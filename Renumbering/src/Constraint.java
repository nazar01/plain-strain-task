/**
 * Created by Танечка on 25.12.2018.
 */
public class Constraint {
    private int nodeNumber;
    private Point point;

    public Constraint(int nodeNumber, Point point) {
        this.nodeNumber = nodeNumber;
        this.point = point;
    }

    public int getNodeNumber() {
        return nodeNumber;
    }

    public void setNodeNumber(int nodeNumber) {
        this.nodeNumber = nodeNumber;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return String.valueOf(nodeNumber) + " "+ String.valueOf(point.getX()) + " " + String.valueOf(point.getY());
    }
}
