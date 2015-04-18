package algorithm;

/**
 * Created by Michael on 3/19/2015.
 */
public class ModificationParams {

    private ModificationType h = null;
    private ModificationType t = null;
    private ModificationType c = null;

    public ModificationParams(){}

    public ModificationParams(ModificationType H, ModificationType T, ModificationType C){
        this.h=H;
        this.t=T;
        this.c=C;
    }
    public ModificationType getH() {
        return h;
    }
    public void setH(ModificationType H) {
        this.h = H;
    }
    public ModificationType getT() {
        return t;
    }
    public void setT(ModificationType t) {
        this.t = t;
    }
    public ModificationType getC() {
        return c;
    }
    public void setC(ModificationType c) {
        this.c = c;
    }
    public boolean isNull(){
        return (h==null&&t==null&&c==null);
    }
}