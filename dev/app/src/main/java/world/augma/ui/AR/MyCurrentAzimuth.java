package world.augma.ui.AR;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import static android.support.constraint.Constraints.TAG;


public class MyCurrentAzimuth implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor rSensor;
    private Sensor aSensor;
    private Sensor mSensor;
    private int azimuthFrom = 0;
    private int azimuthTo = 0;
    private OnRotationChangedListener mRotationListener;
    Context mContext;
    float[] mGravity;
    float[] mGeomagnetic;
    float azimut;

    public MyCurrentAzimuth(OnRotationChangedListener rotationListener, Context context) {
        mRotationListener = rotationListener;
        mContext = context;
    }

    /*
    <ImageView
android:id="@+id/icon"
android:layout_width="wrap_content"
android:layout_height="wrap_content"
android:layout_centerHorizontal="true"
android:layout_centerVertical="true"
android:src="@drawable/map_marker2"
android:visibility="gone" />
     */

    public void start(){
        sensorManager = (SensorManager) mContext.getSystemService(mContext.SENSOR_SERVICE);
        //rSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        aSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //sensorManager.registerListener(this, rSensor,
        //SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, aSensor, 250000);
        sensorManager.registerListener(this, mSensor, 250000);
    }

    public void stop(){
        sensorManager.unregisterListener(this);
    }

    public void setOnShakeListener(OnRotationChangedListener listener) {
        mRotationListener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;

        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];

            if (SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)) {

                // orientation contains azimut, pitch and roll
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);

                azimut = orientation[0];
            }
        }


        float rotation = (-1.0f) * azimut * 360 / (2 * 3.14159f);

        Log.e(TAG, "rotation of the device is :" + rotation );

        mRotationListener.onRotationChanged(rotation);

//        azimuthFrom = azimuthTo;
//
//        float[] orientation = new float[3];
//        float[] rMat = new float[9];
//        SensorManager.getRotationMatrixFromVector(rMat, event.values);
//        azimuthTo = (int) ( Math.toDegrees( SensorManager.getOrientation( rMat, orientation )[0] ) + 360 ) % 360;
//
//        mAzimuthListener.onAzimuthChanged(azimuthFrom, azimuthTo);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}