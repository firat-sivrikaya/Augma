package world.augma.ui.note;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.otaliastudios.cameraview.CameraException;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Gesture;
import com.otaliastudios.cameraview.GestureAction;

import java.io.File;

import world.augma.R;

public class UINotePost extends AppCompatActivity {

    private final String TAG = "[".concat(UINotePost.class.getSimpleName()).concat("]");

    private CameraView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_note_post);

        cameraView = (CameraView) findViewById(R.id.camera);
        cameraView.addCameraListener(new NoteCameraListener());

        /* Camera Gestures */
        cameraView.mapGesture(Gesture.PINCH, GestureAction.ZOOM);
        cameraView.mapGesture(Gesture.TAP, GestureAction.FOCUS);
        cameraView.mapGesture(Gesture.LONG_TAP, GestureAction.FOCUS_WITH_MARKER);
        cameraView.mapGesture(Gesture.SCROLL_VERTICAL, GestureAction.EXPOSURE_CORRECTION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraView.destroy();
    }

    public void takePhoto(View view) {
        cameraView.capturePicture();
    }

    private class NoteCameraListener extends CameraListener {

        @Override
        public void onPictureTaken(byte[] jpeg) {
            super.onPictureTaken(jpeg);
        }

        @Override
        public void onCameraError(@NonNull CameraException exception) {
            super.onCameraError(exception);
            Log.e(TAG, "ERROR: An exception is thrown during Camera setup or configuration. \n\t"
                    + exception.getClass().getSimpleName() + ":" + exception.getMessage());
        }

        @Override
        public void onCameraOpened(CameraOptions options) {
            super.onCameraOpened(options);
        }

        @Override
        public void onCameraClosed() {
            super.onCameraClosed();
        }

        @Override
        public void onVideoTaken(File video) {
            super.onVideoTaken(video);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            super.onOrientationChanged(orientation);
        }

        @Override
        public void onFocusStart(PointF point) {
            super.onFocusStart(point);
        }

        @Override
        public void onFocusEnd(boolean successful, PointF point) {
            super.onFocusEnd(successful, point);
        }

        @Override
        public void onZoomChanged(float newValue, float[] bounds, PointF[] fingers) {
            super.onZoomChanged(newValue, bounds, fingers);
        }

        @Override
        public void onExposureCorrectionChanged(float newValue, float[] bounds, PointF[] fingers) {
            super.onExposureCorrectionChanged(newValue, bounds, fingers);
        }
    }
}
