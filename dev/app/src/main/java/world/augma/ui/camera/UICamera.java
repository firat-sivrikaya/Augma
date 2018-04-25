package world.augma.ui.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;

import android.media.Image;
import android.media.ImageReader;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.util.SizeF;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import world.augma.R;

import static java.lang.Math.atan;

public class UICamera extends AppCompatActivity implements SensorEventListener{

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }


    private Button takePhotoButton;
    private TextureView cameraView;

    private String camId;
    private GLSurfaceView ARView;
    private GLClearRenderer GLClearRenderer;
    private CameraDevice cam;
    private CameraCaptureSession cameraCaptureSession;
    private CaptureRequest.Builder captureBuilder;
    private Size imgDimen;
    private ImageReader imgReader;
    private CameraTextureListener textureListener;
    private File file;
    private boolean mFlashSupported;
    private Handler handler;
    private HandlerThread handlerThread;

    private Sensor deviceSensor;
    private SensorManager SM;




    //TODO kodu sonra toparla
    CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cam = camera;
            preview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            cam.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            cam.close();
            cam = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_camera);


        // Initialize GL Clear Renderer
        GLClearRenderer = new GLClearRenderer();

        // Create sensor manager
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);

        // Initialize accelerometer sensor
        deviceSensor = SM.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        // Register sensor listener
        SM.registerListener(this, deviceSensor, SensorManager.SENSOR_DELAY_FASTEST);

        //takePhotoButton = (Button) findViewById(R.id.takePhotoButton);
        cameraView = (TextureView) findViewById(R.id.cameraView);
        ARView = new GLSurfaceView(this);

        addContentView( ARView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        textureListener = new CameraTextureListener();

        /** Set the camera to be translucent **/

        ARView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        ARView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        ARView.setRenderer(GLClearRenderer);
        ARView.setZOrderOnTop(true);


        //TODO JUnit testi ile degistir!!!
        assert cameraView != null;

        cameraView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
        /*takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });*/
    }

    private void takePicture() {
        if(cam != null) {
            CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            try {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cam.getId());
                Size[] jpegSizes = null;

                if(characteristics != null) {
                    jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
                }

                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);



                //TODO Currently we are gettings
                ImageReader reader = ImageReader.newInstance(metrics.widthPixels, metrics.heightPixels, ImageFormat.JPEG, 1);
                List<Surface> outputSurface = new ArrayList<>(2);
                outputSurface.add(reader.getSurface());
                outputSurface.add(new Surface(cameraView.getSurfaceTexture()));

                //Currently we only capture still pictures
                final CaptureRequest.Builder captureBuilder = cam.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                captureBuilder.addTarget(reader.getSurface());
                captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

                //Check orientation
                int rotation = getWindowManager().getDefaultDisplay().getRotation();
                captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

                file = new File(Environment.getExternalStorageDirectory() + "/" + UUID.randomUUID().toString() + ".jpg");
                ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                    @Override
                    public void onImageAvailable(ImageReader reader) {
                        Image image = null;

                        try {
                            image = reader.acquireLatestImage();
                            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                            byte[] bytes = new byte[buffer.capacity()];
                            buffer.get(bytes);
                            save(bytes);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        finally {
                            if(image != null) {
                                image.close();
                            }
                        }
                    }
                };

                reader.setOnImageAvailableListener(readerListener, handler);
                final CameraCaptureSession.CaptureCallback captureCallbackListener = new CameraCaptureSession.CaptureCallback() {
                    @Override
                    public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                        super.onCaptureCompleted(session, request, result);
                        Toast.makeText(UICamera.this, "Successfully taken.", Toast.LENGTH_SHORT).show();
                        preview();
                    }
                };

                cam.createCaptureSession(outputSurface, new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        try {
                            cameraCaptureSession.capture(captureBuilder.build(), captureCallbackListener, handler);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                    }
                }, handler);

            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void preview() {
        try {
            SurfaceTexture texture = cameraView.getSurfaceTexture();

            //TODO junit ile degistir
            assert texture != null;
            texture.setDefaultBufferSize(imgDimen.getWidth(), imgDimen.getHeight());
            Surface surface = new Surface(texture);
            captureBuilder = cam.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureBuilder.addTarget(surface);

            cam.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    if(cam != null) {
                        cameraCaptureSession = session;
                        updatePreview();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Toast.makeText(UICamera.this, "Preview", Toast.LENGTH_SHORT).show();
                }
            }, null);
        }
        catch(CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        if(cam == null) {
            Toast.makeText(UICamera.this, "Error!", Toast.LENGTH_SHORT).show();
        }

        captureBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);

        try {
            cameraCaptureSession.setRepeatingRequest(captureBuilder.build(), null, handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void save(byte[] bytes) throws IOException {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            os.write(bytes);
        }
        finally {
            if(os != null) {
                os.close();
            }
        }
    }

    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            camId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(camId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            //TODO change with junit
            imgDimen = map.getOutputSizes(SurfaceTexture.class)[0];

            //Check if permitted
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(camId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CAMERA_PERMISSION && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Camera permission request denied!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        startThread();
        if(cameraView.isAvailable()) {
            openCamera();
        } else {
            cameraView.setSurfaceTextureListener(textureListener);
        }
    }

    @Override
    protected void onPause() {
        stopThread();
        super.onPause();

    }

    private void stopThread() {
        handlerThread.quitSafely();

        try {
            handlerThread.join();
            handlerThread = null;
            handler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startThread() {
        handlerThread = new HandlerThread("CamThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            GLClearRenderer.onSensorEvent(sensorEvent);
        }

        // TODO
        Log.e("Azimuth Value:", String.valueOf(sensorEvent.values[0]));
        Log.e("Pitch Value:", String.valueOf(sensorEvent.values[1]));
        Log.e("Roll Value:", String.valueOf(sensorEvent.values[2]));

        ARView.requestRender();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Will not be used in our case
    }

    private class CameraTextureListener implements TextureView.SurfaceTextureListener {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    }

    public class GLClearRenderer implements GLSurfaceView.Renderer, SensorEventListener {

        public float azimuth;
        public float pitch;
        public float roll;
        public float theta;

        volatile boolean sensorRead= false;

        // Sensor variables
        float[] rotationMatrix = new float[16];
        float[] orientations = new float[3];

        private float getHFOV(CameraCharacteristics info) {
            SizeF sensorSize = info.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
            float[] focalLengths = info.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);

            if (focalLengths != null && focalLengths.length > 0) {
                return (float) (2.0f * atan(sensorSize.getWidth() / (2.0f * focalLengths[0])));
            }

            return 1.1f;
        }

        public void setVerticesAndDraw(Float value, GL10 gl, byte color, float azimuth, float pitch, float roll) {
            FloatBuffer vertexbuffer;
            ByteBuffer indicesBuffer;
            ByteBuffer mColorBuffer;

            float objectAzimuth = 0.0f;
            float objectPitch = 0.0f;
            float objectRoll = 0.0f;

            byte indices[] = {0, 1, 2, 0, 2, 3};

            float vetices[] = {//
                    -value, value, 0.0f,
                    value, value, 0.0f,
                    value, -value, 0.0f,
                    -value, -value, 0.0f
            };

            byte colors[] = //3
                    {color, color, 0, color,
                            0, color, color, color,
                            0, 0, 0, color,
                            color, 0, color, color
                    };


            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vetices.length * 4);
            byteBuffer.order(ByteOrder.nativeOrder());
            vertexbuffer = byteBuffer.asFloatBuffer();
            vertexbuffer.put(vetices);
            vertexbuffer.position(0);

            indicesBuffer = ByteBuffer.allocateDirect(indices.length);
            indicesBuffer.put(indices);
            indicesBuffer.position(0);

            mColorBuffer = ByteBuffer.allocateDirect(colors.length);
            mColorBuffer.put(colors);
            mColorBuffer.position(0);


            gl.glClear(gl.GL_COLOR_BUFFER_BIT | gl.GL_DEPTH_BUFFER_BIT);
            gl.glLoadIdentity();

            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexbuffer);
            gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, mColorBuffer);

            gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_BYTE, indicesBuffer);
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

            gl.glPushMatrix();
            gl.glRotatef(objectAzimuth, azimuth, 0.0f, 0.0f);
            gl.glPopMatrix();

        }

        public void onSurfaceChanged( GL10 gl, int width, int height ) {
            // This is called whenever the dimensions of the surface have changed.
            // We need to adapt this change for the GL viewport.
            gl.glViewport( 0, 0, width, height );
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            //float c = 1.0f / 256 * ( System.currentTimeMillis() % 256 );
            //gl.glClearColor( c, c, c, 0.5f );
            //gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
            //Log.e("Camera","Pitch value: " + pitch);
            //Log.e("Camera","Roll value: " + pitch);
            //Log.e("Camera","Yaw value: " + pitch);

            setVerticesAndDraw(azimuth/5.0f, gl, (byte) 255, azimuth, pitch, roll);
        }

        public void onSurfaceCreated( GL10 gl, EGLConfig config ) {
            // No need to do anything here.
        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            // TODO
       /*     if ( this != null )
            {
                if ( sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR )
                {
                    sensorRead = true;
                    SensorManager.getRotationMatrixFromVector(rotationMatrix , sensorEvent.values);
                    SensorManager.getOrientation(rotationMatrix, orientations);


                    float theta = (float) (Math.acos(sensorEvent.values[3])*2);
                    float sinv = (float) Math.sin(theta/2);

                    roll = sensorEvent.values[2]/sinv;     //x
                    pitch = sensorEvent.values[1]/sinv;   //y
                    yaw = sensorEvent.values[0]/sinv;     //z
                    ARView.requestRender();
                }
            }*/
        }

        public void onSensorEvent (SensorEvent event) {

            azimuth = (int) event.values[0];
            pitch = (int) event.values[1];
            roll = (int) event.values[2];
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
            // TODO
        }
    }


}
