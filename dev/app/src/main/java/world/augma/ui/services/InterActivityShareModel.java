package world.augma.ui.services;


public class InterActivityShareModel {

    private static InterActivityShareModel singletonObj;

    private ServiceUIMain uiMain;

    private InterActivityShareModel() {}

    public static synchronized InterActivityShareModel getInstance() {
        if(singletonObj == null) {
            singletonObj = new InterActivityShareModel();
        }
        return singletonObj;
    }

    public void gc() {
        singletonObj = null;
    }

    public Object clone() throws CloneNotSupportedException {
        return new CloneNotSupportedException();
    }


    public ServiceUIMain getUiMain() {
        return uiMain;
    }

    public void setUiMain(ServiceUIMain uiMain) {
        this.uiMain = uiMain;
    }
}
