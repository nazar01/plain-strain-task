#include <iostream>
#include <fstream>
#include <vector>

#ifdef __linux__
#include <eigen3/Eigen/Dense>
#include <eigen3/Eigen/Sparse>
#elif _WIN32
#include <Eigen/Dense>
#include <Eigen/Sparse>
#endif

using namespace std;
struct Element
{
	void CalculateStiffnessMatrix(const Eigen::Matrix3f& D, vector<Eigen::Triplet<float> >& triplets);

	Eigen::Matrix<float, 3, 6> B;
	int nodesIds[3];
};

struct Constraint
{
	/*	enum Type
		{
			UX = 1 << 0,
			UY = 1 << 1,
			UXY = UX | UY
		};*/
	double x, y;
	int node;
	//	Type type;
};

/*
* Добавить ввод х и у из файла. Добавить Халецкого. Создать вектор для правой части
*/

//Количество узлов
int                         nodesCount;
int                         noadsCount;
//Вектор с х - координатой узлов
Eigen::VectorXf             nodesX;
//Вектор с y - координатой узлов
Eigen::VectorXf             nodesY;
Eigen::VectorXf             loads;
Eigen::VectorXf             f;
//Вектор элементов
vector< Element >      elements;
//Вектор закреплений
vector< Constraint >   constraints;

void Element::CalculateStiffnessMatrix(const Eigen::Matrix3f& D, vector<Eigen::Triplet<float> >& triplets)
{
	Eigen::Vector3f x, y;
	x << nodesX[nodesIds[0]], nodesX[nodesIds[1]], nodesX[nodesIds[2]];
	y << nodesY[nodesIds[0]], nodesY[nodesIds[1]], nodesY[nodesIds[2]];

	Eigen::Matrix3f C;
	C << Eigen::Vector3f(1.0f, 1.0f, 1.0f), x, y;

	Eigen::Matrix3f IC = C.inverse();

	for (int i = 0; i < 3; i++)
	{
		B(0, 2 * i + 0) = IC(1, i);
		B(0, 2 * i + 1) = 0.0f;
		B(1, 2 * i + 0) = 0.0f;
		B(1, 2 * i + 1) = IC(2, i);
		B(2, 2 * i + 0) = IC(2, i);
		B(2, 2 * i + 1) = IC(1, i);
	}
	Eigen::Matrix<float, 6, 6> K = B.transpose() * D * B * C.determinant() / 2.0f;

	for (int i = 0; i < 3; i++)
	{
		for (int j = 0; j < 3; j++)
		{
			Eigen::Triplet<float> trplt11(2 * nodesIds[i] + 0, 2 * nodesIds[j] + 0, K(2 * i + 0, 2 * j + 0));
			Eigen::Triplet<float> trplt12(2 * nodesIds[i] + 0, 2 * nodesIds[j] + 1, K(2 * i + 0, 2 * j + 1));
			Eigen::Triplet<float> trplt21(2 * nodesIds[i] + 1, 2 * nodesIds[j] + 0, K(2 * i + 1, 2 * j + 0));
			Eigen::Triplet<float> trplt22(2 * nodesIds[i] + 1, 2 * nodesIds[j] + 1, K(2 * i + 1, 2 * j + 1));

			triplets.push_back(trplt11);
			triplets.push_back(trplt12);
			triplets.push_back(trplt21);
			triplets.push_back(trplt22);
		}
	}
}

void SetConstraints(Eigen::SparseMatrix<float>::InnerIterator& it, int index)
{
	if (it.row() == index || it.col() == index)
	{
		it.valueRef() = it.row() == it.col() ? 1.0f : 0.0f;
	}
}

void ApplyConstraints(Eigen::SparseMatrix<float>& K, const vector<Constraint>& constraints)
{
	vector<int> indicesToConstraint;

	for (vector<Constraint>::const_iterator it = constraints.begin(); it != constraints.end(); ++it)
	{
		//	if (it->type & Constraint::UX)
		//	{
		indicesToConstraint.push_back(2 * it->node + 0);
		loads(2 * it->node + 0) = it->x - nodesX[it->node];
		//	}
		//	if (it->type & Constraint::UY)
			//{
		indicesToConstraint.push_back(2 * it->node + 1);
		loads(2 * it->node + 1) = it->y - nodesY[it->node];
		//}

	}

	f = loads;
	for (int k = 0; k < K.outerSize(); ++k)
	{
		Eigen::VectorXf  col_of_mat = K.col(k) * loads[k];

		f -= col_of_mat;
		for (vector<int>::iterator idit = indicesToConstraint.begin(); idit != indicesToConstraint.end(); ++idit)
			f[*idit] = loads[*idit];

		for (Eigen::SparseMatrix<float>::InnerIterator it(K, k); it; ++it)
		{

			for (vector<int>::iterator idit = indicesToConstraint.begin(); idit != indicesToConstraint.end(); ++idit)
			{
				SetConstraints(it, *idit);
			}
		}
	}
}

int main()
{

	float poissonRatio, youngModulus;

    ifstream fin;
    fin.open("../poissonRatioAndYoungModulus.txt");
    fin >> poissonRatio >> youngModulus;
    fin.close();

    //Генерация матрицы упругости
    Eigen::Matrix3f D;
    D <<
      1.0f, poissonRatio, 0.0f,
            poissonRatio, 1.0, 0.0f,
            0.0f, 0.0f, (1.0f - poissonRatio) / 2.0f;

    D *= youngModulus / (1.0f - pow(poissonRatio, 2.0f));

    // Далее мы читаем список с координатами узлов.
    // Сначала читаем количество узлов, затем задаем размер динамических векторов х и у.
    // Далее мы просто читаем координаты узлов в цикле, строка за строкой.
    fin.open("../points_renumbered.txt");
    fin >> nodesCount;
    nodesX.resize(nodesCount);
    nodesY.resize(nodesCount);

    for (int i = 0; i < nodesCount; ++i)
    {
        fin >> nodesX[i] >> nodesY[i];
    }
    fin.close();

    // Затем мы читаем список элементов.
    // Все то же самое, читаем количество элементов, а затем индексы узлов для каждого элемента:
    int elementCount;
    fin.open("../triangles_renumbered.txt");
    fin >> elementCount;

    for (int i = 0; i < elementCount; ++i)
    {
        Element element;
        fin >> element.nodesIds[0] >> element.nodesIds[1] >> element.nodesIds[2];
        elements.push_back(element);
    }
    fin.close();
    //Далее читаем список закреплений. Все то же самое:
    fin.open("../constraints.txt");
    int constraintCount;
    fin >> constraintCount;
	for (int i = 0; i < constraintCount; ++i)
	{
		Constraint constraint;
		int type;
		//Вводим координаты начальных условий
		double x, y;
		fin >> constraint.node >> x >> y;
		//Предыдущие ограничения
		/*constraint.type = static_cast<Constraint::Type>(type);*/
		constraint.x = x;
		constraint.y = y;
		constraints.push_back(constraint);
	}

	int loadsCount;
	loads.resize(2 * nodesCount);
	loads.setZero();
	f.resize(2 * nodesCount);
	f.setZero();

	//fin >> loadsCount;

//	for (int i = 0; i < loadsCount; ++i)
	//{
	//	int node;
	//	float x, y;
	//	fin >> node >> x >> y;
	//	loads[2 * node + 0] = x;
	//	loads[2 * node + 1] = y;
	//}

	// Для построения глобальной матрицы жесткости нам понадобится вектор triplets.
	// В цикле, мы пройдемся по каждому элементу и
	// заполним этот вектор значениями матриц жесткости полученными от каждого элемента:
	vector<Eigen::Triplet<float> > triplets;
	for (vector<Element>::iterator it = elements.begin(); it != elements.end(); ++it)
	{
		it->CalculateStiffnessMatrix(D, triplets);
	}
	//Как уже упоминалось ранее, мы можем построить глобальную разреженную матрицу прямо из вектора triplets:
	Eigen::SparseMatrix<float> globalK(2 * nodesCount, 2 * nodesCount);
	globalK.setFromTriplets(triplets.begin(), triplets.end());

	ApplyConstraints(globalK, constraints);
	cout << "Global stiffness matrix:\n";
	cout << static_cast<const Eigen::SparseMatrixBase<Eigen::SparseMatrix<float> >&> (globalK) << endl;

	cout << "Loads vector:" << endl << loads << endl << endl;
	cout << "F vector:" << endl << f << endl << endl;

	Eigen::SimplicialLDLT<Eigen::SparseMatrix<float> > solver(globalK);

	Eigen::VectorXf displacements = solver.solve(f);

    ofstream fout;

    fout.open("../stiffness_matrix_and_f.txt");
    fout << 2 * nodesCount << endl << static_cast<const Eigen::SparseMatrixBase<Eigen::SparseMatrix<float> >&> (globalK) << endl << f;
    fout.close();

    std::cout << "Displacements vector:" << std::endl << displacements << std::endl;

//    fout.open("../lib_solved_u_v.txt");
//    for(int i = 0; i < displacements.size(); i++){
//        if(i%2==0 && i!=0){
//            fout<<endl;
//        }
//        fout << displacements[i] << " ";
//    }
//    fout.close();

    return 0;
}
