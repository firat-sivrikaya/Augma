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
    float inclination;
    int con =0;

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
        sensorManager.registerListener(this, aSensor, 1000000);
        sensorManager.registerListener(this, mSensor, 1000000);
    }

    public void stop(){
        sensorManager.unregisterListener(this);
    }

    public void setOnShakeListener(OnRotationChangedListener listener) {
        mRotationListener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //if(con == 1){


            int up =0;
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){

                mGravity = event.values;
                //Understand up or down tilt
                float x = event.values[0];
                float y = event.values[1];

                if (Math.abs(x) > Math.abs(y)) {
            /*if (x < 0) {
                image.setImageResource(R.drawable.right);
                textView.setText("You tilt the device right");
            }
            if (x > 0) {
                image.setImageResource(R.drawable.left);
                textView.setText("You tilt the device left");
            }*/
                } else {
                    if (y < 0) {
                        up = 1;
                        Log.e(TAG, "Tilt of the device is up :" + up );
                        //textView.setText("You tilt the device up");
                    }
                    if (y > 0) {
                        up = 2;
                        Log.e(TAG, "Tilt of the device is up :" + up );
                        //textView.setText("You tilt the device down");
                    }
                }
                if (x > (-2) && x < (2) && y > (-2) && y < (2)) {
                    up = 0;
                    Log.e(TAG, "Tilt of the device is up :" + up );
                    //textView.setText("Not tilt device");
                }
                // End
            }


            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                mGeomagnetic = event.values;

            if (mGravity != null && mGeomagnetic != null) {
                float R[] = new float[9];
                float I[] = new float[9];

                if (SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)) {

                    // orientation contains azimut, pitch and roll
                    float orientation[] = new float[3];
                    SensorManager.getOrientation(R, orientation);
                    inclination = orientation[1];
                    //SensorManager.getInclination(I);
                    azimut = orientation[0];
                }
            }


            float rotation = (-1.0f) * azimut * 360 / (2 * 3.14159f);
            float incl = (-1.0f) * inclination * 360 / (2 * 3.14159f);

            Log.e(TAG, "Rotation of the device is :" + rotation );
            //Log.e(TAG, "Tilt of the device is up :" + up );

            mRotationListener.onRotationChanged(rotation,incl,up);

//        azimuthFrom = azimuthTo;
//
//        float[] orientation = new float[3];
//        float[] rMat = new float[9];
//        SensorManager.getRotationMatrixFromVector(rMat, event.values);
//        azimuthTo = (int) ( Math.toDegrees( SensorManager.getOrientation( rMat, orientation )[0] ) + 360 ) % 360;
//
//        mAzimuthListener.onAzimuthChanged(azimuthFrom, azimuthTo);
        //}

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        /*switch (accuracy) {
            case 0:
                System.out.println("Unreliable");
                con=0;
                break;
            case 1:
                System.out.println("Low Accuracy");
                con=0;
                break;
            case 2:
                System.out.println("Medium Accuracy");
                con=0;

                break;
            case 3:
                System.out.println("High Accuracy");
                con=1;
                break;
        }*/

    }
}