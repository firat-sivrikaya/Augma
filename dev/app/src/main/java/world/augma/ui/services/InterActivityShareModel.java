package world.augma.ui.services;

import android.app.Activity;

public class InterActivityShareModel {

    private static InterActivityShareModel singletonObj;

    private Activity activity;

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

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
