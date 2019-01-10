/**
 * Created by Танечка on 15.12.2018.
 */
public class Triangle {
    private int a;
    private int b;
    private int c;
    Triangle(){
        a=0;
        b=0;
        c=0;
    }
    Triangle(int a, int b, int c){
        this.a=a;
        this.b=b;
        this.c=c;
    }
    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    public int getC() {
        return c;
    }

    public void setA(int a) {
        this.a = a;
    }

    public void setB(int b) {
        this.b = b;
    }

    public void setC(int c) {
        this.c = c;
    }
    @Override
    public String toString(){
       return String.valueOf(a) + " " + String.valueOf(b) + " " + String.valueOf(c) ;
    }

}
