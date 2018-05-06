package world.augma.ui.note;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.otaliastudios.cameraview.Audio;
import com.otaliastudios.cameraview.CameraException;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraUtils;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Facing;
import com.otaliastudios.cameraview.Flash;
import com.otaliastudios.cameraview.Gesture;
import com.otaliastudios.cameraview.GestureAction;
import com.otaliastudios.cameraview.Grid;
import com.otaliastudios.cameraview.SessionType;
import com.otaliastudios.cameraview.WhiteBalance;

import java.io.File;
import java.util.HashMap;

import world.augma.R;
import world.augma.asset.AugmaCameraIconType;
import world.augma.work.Utils;

public class UINotePost extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    private final String TAG = "[".concat(UINotePost.class.getSimpleName()).concat("]");
    private final HashMap<AugmaCameraIconType, Integer> iconMap = new HashMap<>(14);

    private CameraView camera;
    private ImageView facingButton;
    private ImageView galleryButton;
    private ImageView whiteBalanceButton;
    private ImageView gridButton;
    private ImageView effectsButton;
    private ImageView flashButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_note_post);
        NotePostClickListener listener = new NotePostClickListener();

        iconMap.put(AugmaCameraIconType.FACING_FRONT, R.drawable.note_post_facing_front);
        iconMap.put(AugmaCameraIconType.FACING_BACK, R.drawable.note_post_facing_back);
        iconMap.put(AugmaCameraIconType.FLASH_ON, R.drawable.note_post_flash_on);
        iconMap.put(AugmaCameraIconType.FLASH_OFF, R.drawable.note_post_flash_off);
        iconMap.put(AugmaCameraIconType.FLASH_AUTO, R.drawable.note_post_flash_auto);
        iconMap.put(AugmaCameraIconType.GRID_ON, R.drawable.note_post_grid_on);
        iconMap.put(AugmaCameraIconType.GRID_OFF, R.drawable.note_post_grid_off);
        iconMap.put(AugmaCameraIconType.GALLERY, R.drawable.note_post_gallery);
        iconMap.put(AugmaCameraIconType.EFFECTS, R.drawable.note_post_effect_button_icon);
        iconMap.put(AugmaCameraIconType.WHITE_BALANCE_DAYLIGHT, R.drawable.note_post_wb_daylight);
        iconMap.put(AugmaCameraIconType.WHITE_BALANCE_AUTO, R.drawable.note_post_wb_auto);
        iconMap.put(AugmaCameraIconType.WHITE_BALANCE_CLOUDY, R.drawable.note_post_wb_cloudy);
        iconMap.put(AugmaCameraIconType.WHITE_BALANCE_INCANDESCENT, R.drawable.note_post_wb_incandescent);
        iconMap.put(AugmaCameraIconType.WHITE_BALANCE_FLOURESCENT, R.drawable.note_post_wb_flourescent);

        /* Initialize Camera */
        camera = findViewById(R.id.camera);

        /* Initialize Layouts */
        RelativeLayout bottomLayout =  findViewById(R.id.notePostBottomBackground);
        RelativeLayout topLayout    =  findViewById(R.id.notePostTopBackground);

        bottomLayout.setClickable(false);
        topLayout.setClickable(false);

        /* Top Layout Buttons */
        whiteBalanceButton  = topLayout.findViewById(R.id.notePostWhiteBalanceButton);
        gridButton          = topLayout.findViewById(R.id.notePostGridButton);
        effectsButton       = topLayout.findViewById(R.id.notePostEffectsButton);
        flashButton         = topLayout.findViewById(R.id.notePostFlashButton);

        /* Bottom Layout Buttons */
        facingButton = bottomLayout.findViewById(R.id.notePostFlashButton).findViewById(R.id.notePostFunctionButton);
        galleryButton = bottomLayout.findViewById(R.id.notePostGalleryButton).findViewById(R.id.notePostFunctionButton);

        /* Setting up the Camera */
        camera.setSessionType(SessionType.PICTURE);
        camera.addCameraListener(new NoteCameraListener());
        camera.setFlash(Flash.OFF);
        camera.setFacing(Facing.FRONT);
        camera.setJpegQuality(100);
        camera.setWhiteBalance(WhiteBalance.AUTO);
        camera.setAudio(Audio.OFF);
        camera.setPlaySounds(false);

        facingButton.setOnClickListener(listener);
        galleryButton.setOnClickListener(listener);
        whiteBalanceButton.setOnClickListener(listener);
        gridButton.setOnClickListener(listener);
        effectsButton.setOnClickListener(listener);
        flashButton.setOnClickListener(listener);

        /* Camera Gestures */
        camera.mapGesture(Gesture.PINCH, GestureAction.ZOOM);
        camera.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER);
        camera.mapGesture(Gesture.SCROLL_VERTICAL, GestureAction.EXPOSURE_CORRECTION);
        //camera.mapGesture(Gesture.LONG_TAP, GestureAction.FOCUS_WITH_MARKER); save long tap for note opening

        facingButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), iconMap.get(AugmaCameraIconType.FACING_BACK), null));
        galleryButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), iconMap.get(AugmaCameraIconType.GALLERY), null));
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        camera.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        camera.destroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {

            Intent intent = new Intent(this, UINoteDisplay.class);
            intent.setData(data.getData());
            startActivity(intent,
                    ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out).toBundle());
        }
    }

    public void notePostTakePhoto(View view) {
        camera.capturePicture();
    }

    private class NoteCameraListener extends CameraListener {

        @Override
        public void onPictureTaken(byte[] jpeg) {
            super.onPictureTaken(jpeg);

            CameraUtils.decodeBitmap(jpeg, new CameraUtils.BitmapCallback() {
                @Override
                public void onBitmapReady(Bitmap bitmap) {
                    Utils.storeImage(bitmap, getApplicationContext());
                }
            });
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

    private class NotePostClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(v == facingButton) {
                if(camera.getFacing() == Facing.BACK) {
                    facingButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                            iconMap.get(AugmaCameraIconType.FACING_FRONT), null));
                    camera.setFacing(Facing.FRONT);
                } else {
                    facingButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                            iconMap.get(AugmaCameraIconType.FACING_BACK), null));
                    camera.setFacing(Facing.BACK);
                }
            } else if(v == galleryButton) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            } else if(v == whiteBalanceButton) {

                switch (camera.getWhiteBalance()) {
                    case AUTO:
                        whiteBalanceButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                            iconMap.get(AugmaCameraIconType.WHITE_BALANCE_DAYLIGHT), null));
                        camera.setWhiteBalance(WhiteBalance.DAYLIGHT);
                        break;

                    case DAYLIGHT:
                        whiteBalanceButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                iconMap.get(AugmaCameraIconType.WHITE_BALANCE_CLOUDY), null));
                        camera.setWhiteBalance(WhiteBalance.CLOUDY);
                        break;

                    case CLOUDY:
                        whiteBalanceButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                iconMap.get(AugmaCameraIconType.WHITE_BALANCE_FLOURESCENT), null));
                        camera.setWhiteBalance(WhiteBalance.FLUORESCENT);
                        break;

                    case FLUORESCENT:
                        whiteBalanceButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                iconMap.get(AugmaCameraIconType.WHITE_BALANCE_INCANDESCENT), null));
                        camera.setWhiteBalance(WhiteBalance.INCANDESCENT);
                        break;

                    case INCANDESCENT:
                        whiteBalanceButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                iconMap.get(AugmaCameraIconType.WHITE_BALANCE_AUTO), null));
                        camera.setWhiteBalance(WhiteBalance.AUTO);
                        break;

                    default:
                        whiteBalanceButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                iconMap.get(AugmaCameraIconType.WHITE_BALANCE_AUTO), null));
                        camera.setWhiteBalance(WhiteBalance.AUTO);
                        break;
                }

            } else if(v == gridButton) {
                if(camera.getGrid() == Grid.OFF) {
                    gridButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                            iconMap.get(AugmaCameraIconType.GRID_ON), null));
                    camera.setGrid(Grid.DRAW_3X3);
                } else {
                    gridButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                            iconMap.get(AugmaCameraIconType.GRID_OFF), null));
                    camera.setGrid(Grid.OFF);
                }

            } else if(v == effectsButton) {
                //TODO to be implemented
            } else if(v == flashButton) {

                switch(camera.getFlash()) {
                    case AUTO:
                        flashButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                iconMap.get(AugmaCameraIconType.FLASH_ON), null));
                        camera.setFlash(Flash.ON);
                        break;

                    case ON:
                        flashButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                iconMap.get(AugmaCameraIconType.FLASH_OFF), null));
                        camera.setFlash(Flash.OFF);
                        break;

                    case OFF:
                    default:
                        flashButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                iconMap.get(AugmaCameraIconType.FLASH_AUTO), null));
                        camera.setFlash(Flash.AUTO);
                        break;
                }
            }
        }
    }
}
