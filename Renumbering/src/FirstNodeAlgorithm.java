import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Танечка on 16.12.2018.
 */
public class FirstNodeAlgorithm {
    public FirstNodeAlgorithm() {
    }

    public static int findFirstNode(int[][] matrix, boolean logsOn) {
        int rootNode = 0;
        int iter = 0;
        ArrayList<LevelInfo> levelsArray = new ArrayList<>();
        Set<Integer> nodesSet = new HashSet<>();
        int maxLevel = 0;
        int n = matrix.length;
        // цикл
        int prevMaxLevel = -1;
        int currentLevel = 0;
        while (currentLevel <= maxLevel && prevMaxLevel!=currentLevel) {
            prevMaxLevel = currentLevel;
            currentLevel = -1;
            while (n > nodesSet.size()) {
                currentLevel++;
                LevelInfo level = new LevelInfo();
                level.setLevelNumber(currentLevel);
                if(level.getLevelNumber() == 0){
                    ArrayList<Integer> array = new ArrayList<>();
                    array.add(rootNode);
                    level.setNeighboringNodes(array);
                    nodesSet.add(rootNode);
                }
                else{
                    ArrayList<Integer> newNodes = new ArrayList<>();
                    ArrayList<Integer> lastNodes = levelsArray.get(currentLevel - 1).getNeighboringNodes();
                    for(Integer node: lastNodes){
                        for(int j = 0; j<matrix.length; j++)
                        {
                           if (node!=j && matrix[node][j] == 1)  {
                              if (!nodesSet.contains(j))
                               {
                                   newNodes.add(j);
                                   nodesSet.add(j);
                               }
                           }
                        }
                    }
                    level.setNeighboringNodes(newNodes);
                }
                levelsArray.add(level);

            }
            if(maxLevel<currentLevel)
                maxLevel = currentLevel;
            ArrayList<Integer> maxLevelNode = levelsArray.get(currentLevel).getNeighboringNodes();

            int amountNeighbors = n+1;
            for(Integer node: maxLevelNode){
                int currentAmount = 0;
                for(int j = 0; j<n; j++)
                {
                    if (node!=j && matrix[node][j] == 1)  {
                       currentAmount++;
                    }
                }
                if(currentAmount<amountNeighbors){
                    amountNeighbors = currentAmount;
                    rootNode = node;
                }
            }
            if(logsOn) {
                System.out.println("Iteration: " + iter);
                for (LevelInfo level : levelsArray) {
                    System.out.println(level.toString());
                }
            }
            iter++;
            int size = levelsArray.size();
            for(int i = size-1;i>=0;i--)
                levelsArray.remove(i);
            nodesSet.clear();
        }
        return rootNode;
    }
   // private static
}
