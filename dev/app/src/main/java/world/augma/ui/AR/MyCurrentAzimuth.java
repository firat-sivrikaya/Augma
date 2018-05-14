package world.augma.ui.AR;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import world.augma.work.Compatibility;


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
    float[] gravSensorVals;
    float[] mGeomagnetic;
    float[] magSensorVals;
    private CameraViewActivity cameraViewActivity;
    float azimut;
    int inclination;
    float pitch;
    float pitch1;
    float roll;
    float roll1;
    float[] inclineGravity = new float[3];
    int con =0;
    static final float ALPHA = 0.25f;

    public MyCurrentAzimuth(OnRotationChangedListener rotationListener, Context context) {
        mRotationListener = rotationListener;
        mContext = context;
    }

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
        sensorManager.unregisterListener(this , aSensor);
        sensorManager.unregisterListener(this , mSensor);
    }

    public void setOnShakeListener(OnRotationChangedListener listener) {
        mRotationListener = listener;
    }

    protected float[] lowPass( float[] input, float[] output ) {
        if ( output == null ) return input;

        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //if(con == 1){

            int up = 0;
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                gravSensorVals = lowPass(event.values.clone(), gravSensorVals);
                mGravity = event.values;

            }
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            {
                 magSensorVals = lowPass(event.values.clone(), magSensorVals);
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


            if (gravSensorVals != null && magSensorVals != null) {
                float R[] = new float[9];
                float Rot[] = new float[9];
                float I[] = new float[9];
                float orientation[] = new float[3];

                inclineGravity = mGravity.clone();

                double norm_Of_g = Math.sqrt(inclineGravity[0] * inclineGravity[0] + inclineGravity[1] * inclineGravity[1] + inclineGravity[2] * inclineGravity[2]);

                // Normalize the accelerometer vector
                inclineGravity[0] = (float) (inclineGravity[0] / norm_Of_g);
                inclineGravity[1] = (float) (inclineGravity[1] / norm_Of_g);
                inclineGravity[2] = (float) (inclineGravity[2] / norm_Of_g);

                //Checks if device is flat on ground or not
                 inclination = (int) Math.round(Math.toDegrees(Math.acos(inclineGravity[2])));

                if (SensorManager.getRotationMatrix(R, I, gravSensorVals, magSensorVals)) {


                    int rotation = Compatibility.getRotation(cameraViewActivity);

                    if (rotation == 1) {
                        SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Z, Rot);
                    } else {
                        SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_Z, Rot);
                    }

                    SensorManager.getOrientation(Rot, orientation);

                    azimut = (float)(((orientation[0]*180)/Math.PI)+180);
                    pitch1 = (float)(((orientation[1]*180/Math.PI))+90);
                    roll1 = (float)(((orientation[2]*180/Math.PI)));

                }
            }


            mRotationListener.onRotationChanged(azimut,pitch1,roll1, inclination, up);


    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

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

    public CameraViewActivity getCameraViewActivity() {
        return cameraViewActivity;
    }

    public void setCameraViewActivity(CameraViewActivity cameraViewActivity) {
        this.cameraViewActivity = cameraViewActivity;
    }
}