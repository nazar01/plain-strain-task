/**
 * Created by Танечка on 27.12.2018.
 */
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CuthillMcKeeAlgorithm {
    public CuthillMcKeeAlgorithm(){
    }
    public static ArrayList<Integer> bandwidthReduction(int[][] matrix, int rootNode){
        int currentLevel=0;
        int levelNumber=0;
        Set<Integer> nodesSet = new HashSet<>();
        int n = matrix.length;
        ArrayList<LevelInfo> levelsArray = new ArrayList<>();
        ArrayList<Integer> reverseNodes = new ArrayList<>();
        int count=0;
        while(reverseNodes.size()<n) {
            if (reverseNodes.isEmpty()) {
                reverseNodes.add(rootNode);
                nodesSet.add(rootNode);
                ArrayList<Integer> newNodes = new ArrayList<>();
                LevelInfo level = new LevelInfo();
                for (int j = 0; j < matrix.length; j++) {
                    if (rootNode != j && matrix[rootNode][j] == 1) {
                        newNodes.add(j);
                    }
                }
                level.setLevelNumber(levelNumber);
                levelNumber++;
                level.setNeighboringNodes(newNodes);
                levelsArray.add(level);
                for (Integer elem : newNodes)
                    nodesSet.add(elem);

            }
            else {
                ArrayList<Integer> lastNodes = levelsArray.get(currentLevel).getNeighboringNodes();
                //ArrayList<Integer> newNodes = new ArrayList<>();
                if (lastNodes.size()==1){
                    ArrayList<Integer> newNodes = new ArrayList<>();
                    reverseNodes.add(lastNodes.get(0));
                    int node = lastNodes.get(0);
                    for(int j = 0; j<matrix.length; j++) {
                        if (node!=j && matrix[node][j] == 1 && !nodesSet.contains(j))  {
                            newNodes.add(j);
                        }
                    }
                    if (!newNodes.isEmpty()){
                        LevelInfo level = new LevelInfo();
                        level.setLevelNumber(levelNumber);
                        level.setNeighboringNodes(newNodes);
                        levelsArray.add(level);
                        levelNumber++;
                        for (Integer elem : newNodes)
                            nodesSet.add(elem);
                    }
                }
                else {
                    int amountNodes = lastNodes.size();
                    int baseRoot=0;
                    //основной цикл
                    for (int i=0; i<amountNodes; i++) {
                        ArrayList<Integer> newNodes = new ArrayList<>();
                        int amountNeighbors = n+1;
                        //подцикл
                        for (int node : lastNodes){
                            //считаем соседей элемента
                            if(!reverseNodes.contains(node)){
                                for(int j = 0; j<matrix.length; j++) {
                                    if (node!=j && matrix[node][j] == 1 && !nodesSet.contains(j))  {
                                        newNodes.add(j);
                                    }
                                }
                                if (newNodes.size()<amountNeighbors) {
                                    amountNeighbors = newNodes.size();
                                    baseRoot=node;
                                }
                            }
                        }
                        newNodes.clear();
                        //в конце подцикла получили элемент и с ним фигачим:
                        reverseNodes.add(baseRoot);
                        for(int j = 0; j<matrix.length; j++) {
                            if (baseRoot!=j && matrix[baseRoot][j] == 1 && !nodesSet.contains(j))  {
                                newNodes.add(j);
                            }
                        }
                        if (!newNodes.isEmpty()){
                            LevelInfo level = new LevelInfo();
                            level.setLevelNumber(levelNumber);
                            level.setNeighboringNodes(newNodes);
                            levelsArray.add(level);
                            for (Integer elem : newNodes)
                                nodesSet.add(elem);
                            levelNumber++;
                        }
                    }
                }
                currentLevel++;
            }
           /* System.out.println("levels CM");
            for(LevelInfo level1: levelsArray){
            System.out.println(level1.toString());
            }
*/
        }
        ArrayList<Integer> newNodes = new ArrayList<>();
        for (int i=0; i<reverseNodes.size(); i++){
            newNodes.add(reverseNodes.get(reverseNodes.size()-i-1));
        }
        return newNodes;
    }
}

