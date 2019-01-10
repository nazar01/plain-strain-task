/**
 * Created by Танечка on 23.12.2018.
 */
public class RenumberedNodes {
    private int oldNode;
    private int newNode;

    public RenumberedNodes(int oldNode, int newNode){
        this.oldNode = oldNode;
        this.newNode = newNode;
    }
    public int getOldNode() {
        return oldNode;
    }

    public void setOldNode(int oldNode) {
        this.oldNode = oldNode;
    }

    public int getNewNode() {
        return newNode;
    }

    public void setNewNode(int newNode) {
        this.newNode = newNode;
    }
    public String toString(){
        return String.valueOf(oldNode) + " " + String.valueOf(newNode) ;
    }
}
