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
    int inclination;
    float pitch;
    float roll;
    float[] inclineGravity = new float[3];
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
        sensorManager.registerListener(this, aSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, mSensor,  SensorManager.SENSOR_DELAY_UI);
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

            int up = 0;
            int parameterInclination = 0;
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){

                mGravity = event.values;

            }
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            {
                mGeomagnetic = event.values;

                if (isTiltDownward())
                {
                    up = -1;
                }
                else if (isTiltUpward() )
                {
                    up = 1;
                }
            }


            if (mGravity != null && mGeomagnetic != null) {
                float R[] = new float[9];
                float I[] = new float[9];

                inclineGravity = mGravity.clone();

                double norm_Of_g = Math.sqrt(inclineGravity[0] * inclineGravity[0] + inclineGravity[1] * inclineGravity[1] + inclineGravity[2] * inclineGravity[2]);

                // Normalize the accelerometer vector
                inclineGravity[0] = (float) (inclineGravity[0] / norm_Of_g);
                inclineGravity[1] = (float) (inclineGravity[1] / norm_Of_g);
                inclineGravity[2] = (float) (inclineGravity[2] / norm_Of_g);

                //Checks if device is flat on ground or not
                 inclination = (int) Math.round(Math.toDegrees(Math.acos(inclineGravity[2])));

                if (SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)) {

                    // orientation contains azimut, pitch and roll
                    float orientation[] = new float[3];
                    SensorManager.getOrientation(R, orientation);
                    //SensorManager.getInclination(I);
                    azimut = orientation[0];
                }
            }


            float rotation = (-1.0f) * azimut * 360 / (2 * 3.14159f);
            Log.e(TAG, "Rotation of the device is :" + rotation );
            //Log.e(TAG, "Tilt of the device is up :" + up );

            mRotationListener.onRotationChanged(rotation, inclination, up);

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

    public boolean isTiltUpward()
    {
        if (mGravity != null && mGeomagnetic != null)
        {
            float R[] = new float[9];
            float I[] = new float[9];

            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);

            if (success)
            {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);

                /*
                 * If the roll is positive, you're in reverse landscape (landscape right), and if the roll is negative you're in landscape (landscape left)
                 *
                 * Similarly, you can use the pitch to differentiate between portrait and reverse portrait.
                 * If the pitch is positive, you're in reverse portrait, and if the pitch is negative you're in portrait.
                 *
                 * orientation -> azimut, pitch and roll
                 *
                 *
                 */

                pitch = orientation[1];
                roll = orientation[2];

                inclineGravity = mGravity.clone();

                double norm_Of_g = Math.sqrt(inclineGravity[0] * inclineGravity[0] + inclineGravity[1] * inclineGravity[1] + inclineGravity[2] * inclineGravity[2]);

                // Normalize the accelerometer vector
                inclineGravity[0] = (float) (inclineGravity[0] / norm_Of_g);
                inclineGravity[1] = (float) (inclineGravity[1] / norm_Of_g);
                inclineGravity[2] = (float) (inclineGravity[2] / norm_Of_g);

                //Checks if device is flat on ground or not
                int inclination = (int) Math.round(Math.toDegrees(Math.acos(inclineGravity[2])));
                //Log.e("------------------Tilt inclination:",""+ inclination);

                /*
                 * Float obj1 = new Float("10.2");
                 * Float obj2 = new Float("10.20");
                 * int retval = obj1.compareTo(obj2);
                 *
                 * if(retval > 0) {
                 * System.out.println("obj1 is greater than obj2");
                 * }
                 * else if(retval < 0) {
                 * System.out.println("obj1 is less than obj2");
                 * }
                 * else {
                 * System.out.println("obj1 is equal to obj2");
                 * }
                 */
                Float objPitch = new Float(roll);
                Float objZero = new Float(0.0);
                Float objZeroPointTwo = new Float(0.2);
                Float objZeroPointTwoNegative = new Float(-0.2);

                int objPitchZeroResult = objPitch.compareTo(objZero);
                int objPitchZeroPointTwoResult = objZeroPointTwo.compareTo(objPitch);
                int objPitchZeroPointTwoNegativeResult = objPitch.compareTo(objZeroPointTwoNegative);


                Log.e("------------------- TILT UPWARD if---------------", "inc: " + inclination + " objPitchZeroResult: " + objPitchZeroResult + " objPitchZeroPointTwoResult: " + objPitchZeroPointTwoResult + " objPitchZeroPointTwoNegativeResult: " + objPitchZeroPointTwoNegativeResult );


                if (pitch < 0 && ((objPitchZeroResult > 0 && objPitchZeroPointTwoResult < 0) || (objPitchZeroResult < 0 && objPitchZeroPointTwoNegativeResult < 0)) && (inclination > 100 && inclination < 140))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }

        return false;
    }

    public boolean isTiltDownward()
    {
        if (mGravity != null && mGeomagnetic != null)
        {
            float R[] = new float[9];
            float I[] = new float[9];

            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);

            if (success)
            {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);

                pitch = orientation[1];
                roll = orientation[2];

                inclineGravity = mGravity.clone();

                double norm_Of_g = Math.sqrt(inclineGravity[0] * inclineGravity[0] + inclineGravity[1] * inclineGravity[1] + inclineGravity[2] * inclineGravity[2]);

                // Normalize the accelerometer vector
                inclineGravity[0] = (float) (inclineGravity[0] / norm_Of_g);
                inclineGravity[1] = (float) (inclineGravity[1] / norm_Of_g);
                inclineGravity[2] = (float) (inclineGravity[2] / norm_Of_g);

                //Checks if device is flat on groud or not
                int inclination = (int) Math.round(Math.toDegrees(Math.acos(inclineGravity[2])));
                //Log.e("------------------Tilt inclination:",""+ inclination);

                Float objPitch = new Float(roll);
                Float objZero = new Float(0.0);
                Float objZeroPointTwo = new Float(0.2);
                Float objZeroPointTwoNegative = new Float(-0.2);

                int objPitchZeroResult = objPitch.compareTo(objZero);
                int objPitchZeroPointTwoResult = objZeroPointTwo.compareTo(objPitch);
                int objPitchZeroPointTwoNegativeResult = objPitch.compareTo(objZeroPointTwoNegative);

                Log.e("------------------- TILT DOWNWARD if---------------", "inc: " + inclination + " objPitchZeroResult: " + objPitchZeroResult + " objPitchZeroPointTwoResult: " + objPitchZeroPointTwoResult + " objPitchZeroPointTwoNegativeResult: " + objPitchZeroPointTwoNegativeResult );

                if (pitch < 0 && ((objPitchZeroResult > 0 && objPitchZeroPointTwoResult > 0) || (objPitchZeroResult < 0 && objPitchZeroPointTwoNegativeResult > 0)) && (inclination > 20 && inclination < 50))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }

        return false;
    }

}