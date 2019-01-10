import java.util.ArrayList;

/**
 * Created by Танечка on 16.12.2018.
 */
public class LevelInfo {

    private int levelNumber;
    private ArrayList<Integer> neighboringNodes;

    public LevelInfo(){
        neighboringNodes = new ArrayList<>();
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(int levelNumber) {
        this.levelNumber = levelNumber;
    }

    public ArrayList<Integer> getNeighboringNodes() {
        return neighboringNodes;
    }

    public void setNeighboringNodes(ArrayList<Integer> neighboringNodes) {
        this.neighboringNodes = neighboringNodes;
    }
    @Override
    public String toString(){
        String neighbors = "";
        for(Integer node: neighboringNodes){
            neighbors+=node;
            neighbors+=" ";
        }
        return "Level number = " + levelNumber + " NeighboringNodes: " + neighbors;
    }

}
