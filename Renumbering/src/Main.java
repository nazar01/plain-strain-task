/*
 * Created by Танечка on 15.12.2018.
 */

import java.io.*;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException {
        boolean logsOn = false;
        final String inputTriangles = "../triangles.txt";
        final String outputTriangles = "../triangles_renumbered.txt";
        final String inputPoints = "../points.txt";
        final String outputPoints = "../points_renumbered.txt";
        final String inputConstraints = "../src_constraints.txt";
        final String outputConstraints = "../constraints.txt";
        final String outputWidth = "../width.txt";
        try {
            ArrayList<Triangle> trianglesArray = new ArrayList<>();
            ArrayList<Point> pointsArray = new ArrayList<>();
            ArrayList<Constraint> constraintsArray = new ArrayList<>();
            parsePoints(inputPoints, pointsArray);
            parseConstraints(inputConstraints, constraintsArray);
            int nodeAmount = parseTrianglesAndGetNodeAmount(inputTriangles, trianglesArray);
            if (logsOn)
                for (Triangle triangle : trianglesArray) {
                    System.out.println(triangle.toString());
                }
            if (logsOn)
                System.out.println("nodeAmount = " + nodeAmount);
            int[][] oldGenMatrix = new int[nodeAmount][nodeAmount];
            initUnitMatrix(oldGenMatrix);
            fillGenMatrix(trianglesArray, oldGenMatrix);
            if (logsOn) {
                printMatrix(oldGenMatrix);
                System.out.println("Ширина ленты старой матрицы: " + matrixBandwidth(oldGenMatrix));
            }
            int rootNode = FirstNodeAlgorithm.findFirstNode(oldGenMatrix,logsOn);
            if (logsOn)
                System.out.println("RootNode = " + rootNode);
            ArrayList<RenumberedNodes> renumberedNodes = new ArrayList<>();

            ArrayList<Integer> newNodes = CuthillMcKeeAlgorithm.bandwidthReduction(oldGenMatrix, rootNode);
            renumberNodes(renumberedNodes, newNodes);
            //renumberNodesTemporary(renumberedNodes,nodeAmount,rootNode);
            if (logsOn) {
                System.out.println("Renumbered Nodes");
                for (int i = 0; i < nodeAmount; i++)
                    System.out.println(renumberedNodes.get(i).toString());
            }
            ArrayList<Triangle> newTrianglesArray = new ArrayList<>();
            outputTrianglesInFile(trianglesArray, outputTriangles, renumberedNodes, newTrianglesArray);

            int[][] newGenMatrix = new int[nodeAmount][nodeAmount];
            initUnitMatrix(newGenMatrix);
            fillGenMatrix(newTrianglesArray, newGenMatrix);
            int newBandWidth = matrixBandwidth(newGenMatrix);
            if (logsOn) {
                printMatrix(newGenMatrix);
                System.out.println("Ширина ленты новой матрицы: " + newBandWidth);
            }
            outputBandWidth(newBandWidth, outputWidth);

            if(logsOn) {
                System.out.println("Points");
                int n = pointsArray.size();
                for (int i = 0; i < n; i++) {
                    System.out.println(pointsArray.get(i).toString());
                }
                System.out.println("Constraints");
                int nConstraints = constraintsArray.size();
                for (int i = 0; i < nConstraints; i++) {
                    System.out.println(constraintsArray.get(i).toString());
                }
            }
            outputPointsInFile(pointsArray, outputPoints, renumberedNodes);
            outputConstraintsInFile(constraintsArray, outputConstraints, renumberedNodes);
        } catch (FileNotFoundException e) {
            System.exit(1);
        }
    }

    public static void initUnitMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++)
            for (int j = 0; j < matrix.length; j++)
                if (i == j)
                    matrix[i][j] = 1;
                else
                    matrix[i][j] = 0;
    }

    public static void printMatrix(int[][] matrix) {
        System.out.print("   ");
        for (int i = 0; i < matrix.length; i++)
            System.out.print(i + " ");
        System.out.println();
        System.out.print("   ");
        for (int i = 0; i < matrix.length; i++)
            System.out.print("_ ");
        System.out.println();
        for (int i = 0; i < matrix.length; i++) {
            System.out.print(i + "| ");
            for (int j = 0; j < matrix.length; j++)
                System.out.print(matrix[i][j] + " ");
            System.out.println();
        }

    }

    public static int parseTrianglesAndGetNodeAmount(String inputTriangles, ArrayList<Triangle> TrianglesArray) throws IOException {
        int nodeAmount = 0;
        try {
            FileInputStream fileInputStream = null; //Создание файлового потока для чтения
            fileInputStream = new FileInputStream(inputTriangles);
            BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream, "Cp1251")); //чтение из файла символов с кирилицей
            String strLine;
            while ((strLine = br.readLine()) != null) { // построчное считываение непустых строк из файла
                int a, b, c;
                strLine.trim();
                String[] nodes = strLine.split(" ");
                a = Integer.parseInt(nodes[0]);
                b = Integer.parseInt(nodes[1]);
                c = Integer.parseInt(nodes[2]);
                if (a > nodeAmount)
                    nodeAmount = a;
                if (b > nodeAmount)
                    nodeAmount = b;
                if (c > nodeAmount)
                    nodeAmount = c;
                Triangle triangle = new Triangle(a, b, c);
                TrianglesArray.add(triangle);
            }
            nodeAmount++;
        } catch (FileNotFoundException e) {
            System.out.println("Файл " + inputTriangles + " не найден.");
            throw e;
        }
        return nodeAmount;
    }

    public static void parsePoints(String inputPoints, ArrayList<Point> PointsArray) throws IOException {
        try {
            FileInputStream fileInputStream = null; //Создание файлового потока для чтения
            fileInputStream = new FileInputStream(inputPoints);
            BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream, "Cp1251")); //чтение из файла символов с кирилицей
            String strLine;
            while ((strLine = br.readLine()) != null) { // построчное считываение непустых строк из файла
                double x, y;
                strLine.trim();
                String[] nodes = strLine.split(" ");
                x = Double.parseDouble(nodes[0]);
                y = Double.parseDouble(nodes[1]);
                PointsArray.add(new Point(x, y));
            }
        } catch (FileNotFoundException e) {
            System.out.println("Файл " + inputPoints + " не найден.");
            throw e;
        }
    }

    public static void parseConstraints(String inputConstraints, ArrayList<Constraint> constraintsArray) throws IOException {
        try {
            FileInputStream fileInputStream = null; //Создание файлового потока для чтения
            fileInputStream = new FileInputStream(inputConstraints);
            BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream, "Cp1251")); //чтение из файла символов с кирилицей
            String strLine;
            while ((strLine = br.readLine()) != null) { // построчное считываение непустых строк из файла
                double x, y;
                int nodeNumber;
                strLine.trim();
                String[] nodes = strLine.split(" ");
                nodeNumber = Integer.parseInt(nodes[0]);
                x = Double.parseDouble(nodes[1]);
                y = Double.parseDouble(nodes[2]);
                constraintsArray.add(new Constraint(nodeNumber, new Point(x, y)));
            }
        } catch (FileNotFoundException e) {
            System.out.println("Файл " + inputConstraints + " не найден.");
            throw e;
        }
    }

    public static void fillGenMatrix(ArrayList<Triangle> TrianglesArray, int[][] matrix) {
        for (Triangle triangle : TrianglesArray) {
            matrix[triangle.getA()][triangle.getB()] = 1;
            matrix[triangle.getB()][triangle.getA()] = 1;
            matrix[triangle.getB()][triangle.getC()] = 1;
            matrix[triangle.getC()][triangle.getB()] = 1;
            matrix[triangle.getA()][triangle.getC()] = 1;
            matrix[triangle.getC()][triangle.getA()] = 1;
        }
    }

    public static void renumberNodes(ArrayList<RenumberedNodes> renumberedNodes, ArrayList<Integer> newNodes) {
        for (int i = 0; i < newNodes.size(); i++) {
            renumberedNodes.add(new RenumberedNodes(i, newNodes.get(i)));
        }
    }

    public static void renumberNodesTemporary(ArrayList<RenumberedNodes> renumberedNodes, int nodeAmount, int rootNode) {
        for (int i = 0; i < nodeAmount; i++) {
            renumberedNodes.add(new RenumberedNodes(i, 0));
        }
        renumberedNodes.get(0).setNewNode(rootNode);
            /*заглушка для ренамберед*/
        int kInd = nodeAmount;
        for (int i = 1; i < nodeAmount; i++) {
            kInd--;
            if (kInd == rootNode)
                kInd--;
            renumberedNodes.get(i).setNewNode(kInd);
        }
    }

    public static void outputTrianglesInFile(ArrayList<Triangle> trianglesArray, String outputTriangles, ArrayList<RenumberedNodes> renumberedNodes, ArrayList<Triangle> newTrianglesArray) throws IOException {
        OutputStreamWriter st = new OutputStreamWriter(new FileOutputStream(outputTriangles, false), "cp1251");
        int trianglesAmount = trianglesArray.size();
        st.write(trianglesAmount + System.lineSeparator());
        for (Triangle triangle : trianglesArray) {
            st.write(renumberedNodes.get(triangle.getA()).getNewNode()
                    + " " + renumberedNodes.get(triangle.getB()).getNewNode()
                    + " " + renumberedNodes.get(triangle.getC()).getNewNode() + System.lineSeparator());
            newTrianglesArray.add(new Triangle(renumberedNodes.get(triangle.getA()).getNewNode(),
                    renumberedNodes.get(triangle.getB()).getNewNode(),
                    renumberedNodes.get(triangle.getC()).getNewNode()));
        }
        st.flush();
        System.out.println("Файл " + outputTriangles + " сгенерирован успешно.");
    }

    public static void outputPointsInFile(ArrayList<Point> pointsArray, String outputPoints, ArrayList<RenumberedNodes> renumberedNodes) throws IOException {
        OutputStreamWriter st = new OutputStreamWriter(new FileOutputStream(outputPoints, false), "cp1251");
        int nodeAmount = renumberedNodes.size();
        ArrayList<RenumberedNodes> sortedByNewNodes = new ArrayList<>();
        for (int i = 0; i < nodeAmount; i++) {
            sortedByNewNodes.add(new RenumberedNodes(0, 0));
        }
        for (RenumberedNodes renumberedNode : renumberedNodes) {
            sortedByNewNodes.set(renumberedNode.getNewNode(), renumberedNode);
        }
        //System.out.println("Sorted Renumbered Nodes");
        //for (int i = 0; i < nodeAmount; i++)
        //    System.out.println(sortedByNewNodes.get(i).toString());
        st.write(nodeAmount + System.lineSeparator());
        for (RenumberedNodes node : sortedByNewNodes) {
            st.write(pointsArray.get(node.getOldNode()).toString()
                    + System.lineSeparator());
        }
        st.flush();
        System.out.println("Файл " + outputPoints + " сгенерирован успешно.");
    }

    public static void outputConstraintsInFile(ArrayList<Constraint> constraintsArray, String outputConstraints, ArrayList<RenumberedNodes> renumberedNodes) throws IOException {
        OutputStreamWriter st = new OutputStreamWriter(new FileOutputStream(outputConstraints, false), "cp1251");
        int constraintsAmount = constraintsArray.size();

        st.write(constraintsAmount + System.lineSeparator());
        for (Constraint constraint : constraintsArray) {
            st.write(renumberedNodes.get(constraint.getNodeNumber()).getNewNode() + " " +
                    constraint.getPoint().toString()
                    + System.lineSeparator());
        }
        st.flush();
        System.out.println("Файл " + outputConstraints + " сгенерирован успешно.");
    }

    public static void outputBandWidth(int bandWidth, String outputWidth) throws IOException {
        OutputStreamWriter st = new OutputStreamWriter(new FileOutputStream(outputWidth, false), "cp1251");
        st.write(bandWidth + System.lineSeparator());
        st.flush();
        System.out.println("Файл " + outputWidth + " сгенерирован успешно.");
    }

    public static int matrixBandwidth(int[][] matrix) {
        int bandwidth = 0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = i + bandwidth; j < matrix.length; j++) {
                if (matrix[i][j] == 1)
                    bandwidth = j - i;
            }
        }
        return bandwidth + 1;
    }
}
