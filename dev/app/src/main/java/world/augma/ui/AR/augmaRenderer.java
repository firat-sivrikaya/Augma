package world.augma.ui.AR;

import android.content.res.Resources;
import android.location.Location;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import world.augma.asset.Note;
import world.augma.ui.map.UIMap;
import world.augma.work.AWS;

public class augmaRenderer implements GLSurfaceView.Renderer{
    private static final String TAG = "augmaRenderer";
    private List<Square> mSquare;
    private List<Note> nearbyNotes;
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];

    private float mAngle;


    public augmaRenderer()
    {
        mSquare = new ArrayList<Square>();
    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);


        nearbyNotes = augmaGLActivity.nearbyNotes;


    }

    public void onDrawFrame(GL10 unused) {
        float[] scratch = new float[16];
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mSquare = new ArrayList<Square>();
        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);


        Location loc = UIMap.mLastKnownLocation;


        /*AWS aws = new AWS();
        try {
            aws.execute(AWS.Service.GET_NOTE_WITH_FILTER, "" + loc.getLatitude(),
                    "" + loc.getLongitude()).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        nearbyNotes = aws.getMatchedNotes();*/

        // initialize a square
        double scaleAmount = 1.0f;
        double translateX = 0.0f;
        double translateY = 0.0f;
        double deltaLat = 0.0f;
        double deltaLon = 0.0f;
        double distanceToNote = 0.0f;
        // Initialize multiple squares with different scales and translates
        for ( int i = 0 ; i < nearbyNotes.size() ; i++ )
        {
            // Calculate lat and lon difference between the device and the note
            deltaLat = Math.abs(loc.getLatitude() - nearbyNotes.get(i).getLatitude());
            deltaLon = Math.abs(loc.getLongitude() - nearbyNotes.get(i).getLongitude());

            // Log output to trace the locations
            Log.e("DEVICELAT", loc.getLatitude() + "");
            Log.e("DEVICELON", loc.getLongitude() + "");
            Log.e("NOTELAT", nearbyNotes.get(i).getLatitude() + "");
            Log.e("NOTELON", nearbyNotes.get(i).getLongitude() + "");
            Log.e("DELTALAT", deltaLat + "");
            Log.e("DELTALON", deltaLon+ "");

            // Calculate the distance to note
            distanceToNote = Math.sqrt(deltaLat*deltaLat + deltaLon*deltaLon);
            Log.e("DISTANCETONOTE", distanceToNote + "");

            // Arrange the scale amount with the given distance
            scaleAmount = scaleAmount * (1/distanceToNote) / 7000;
            Log.e("SCALEAMOUNT", scaleAmount + "");

            // Add the square to the list to get it drawn later
            mSquare.add(new Square(scaleAmount, translateX, translateY));
            //scaleAmount += -0.3f;

            // Translate the next square
            translateX += 0.2f;
            translateY += 0.2f;
        }

        // Draw square
        for ( int i = 0 ; i < nearbyNotes.size() ; i++ )
            mSquare.get(i).draw(mMVPMatrix);

    }


    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;
        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     *
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }
    /**
     * Returns the rotation angle of the triangle shape (mTriangle).
     *
     * @return - A float representing the rotation angle.
     */
    public float getAngle() {
        return mAngle;
    }
    /**
     * Sets the rotation angle of the triangle shape (mTriangle).
     */
    public void setAngle(float angle) {
        mAngle = angle;
    }

    public void processTouchEvent(MotionEvent event)
    {
        float x = event.getX();
        float y = event.getY();
        float screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        float screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

        Log.e(TAG, "width" + screenWidth + ", height " + screenHeight);

        float sceneX = (x/screenWidth)*2.0f - 1.0f;
        float sceneY = (y/screenHeight)*-2.0f + 1.0f; //if bottom is at -1. Otherwise same as X

        for(int i = 0; i < mSquare.size(); i++)
        {
            Log.e(TAG, "sceneX: " + sceneX + ", " + "sceneY: " + sceneY);
            if ( mSquare.get(i).squareCoords[0] <= sceneX && mSquare.get(i).squareCoords[6] >= sceneX && mSquare.get(i).squareCoords[1] >= sceneY && mSquare.get(i).squareCoords[7] <= sceneY )
            {
                Log.e(TAG, "kareye basıldım, i:" + i);
            }
        }

    }


}
