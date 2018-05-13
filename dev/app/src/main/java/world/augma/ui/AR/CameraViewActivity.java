package world.augma.ui.AR;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

import java.io.IOException;
import java.util.List;

import world.augma.R;
import world.augma.asset.Note;
import world.augma.ui.note.UINoteDisplay;
import world.augma.work.visual.S3;

import static android.support.constraint.Constraints.TAG;

public class CameraViewActivity extends Activity implements
        SurfaceHolder.Callback, OnLocationChangedListener, OnRotationChangedListener{

    private Camera mCamera;
    private SurfaceHolder mSurfaceHolder;
    private boolean isCameraviewOn = false;

    private double mAzimuthReal = 0;
    private double mRotationReal = 0;
    private double mInclination = 0;
    private double[] mAzimuthTheoretical;
    private double[] degreesOfNotes;
    private static double AZIMUTH_ACCURACY = 5;
    private double mMyLatitude = 0;
    private double mMyLongitude = 0;
    private Location lastLocation;
    private double difference[];
    private double differenceTop[];

    DisplayMetrics displayMetrics;
    public int screenWidth;
    public int screenHeight;

    private MyCurrentAzimuth myCurrentAzimuth;
    private MyCurrentLocation myCurrentLocation;


    private List<Note> filteredNotes;
    TextView descriptionTextView;
    RelativeLayout ARRootLayout;
    private AROnClickListener listener;

    private RelativeLayout[] imageArray;
    private boolean[] imageDrawn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;

        ARRootLayout = findViewById(R.id.ARroot);
        listener = new AROnClickListener();

        setupListeners();
        setupLayout();
        filteredNotes = (List<Note>) getIntent().getExtras().getSerializable("filteredNotes");

        degreesOfNotes = new double[filteredNotes.size()];

        imageArray = new RelativeLayout[filteredNotes.size()];
        imageDrawn = new boolean[filteredNotes.size()];
        LayoutInflater inflater = LayoutInflater.from(this);

        differenceTop = new double[filteredNotes.size()];
        difference = new double[filteredNotes.size()];

        for (int i = 0; i < filteredNotes.size(); i++) {
            imageArray[i] = (RelativeLayout) inflater.inflate(R.layout.ar_item_view, null, false);

            S3.fetchNotePreviewImage(this, ((ImageView) imageArray[i].findViewById(R.id.notePreviewImage)), filteredNotes.get(i).getOwner().getUserID(), filteredNotes.get(i).getNoteID());
            ARRootLayout.addView(imageArray[i]);

            imageArray[i].setTag(filteredNotes.get(i));
            imageArray[i].setOnClickListener(listener);
            imageArray[i].bringToFront();
            imageArray[i].setVisibility(View.INVISIBLE);
            differenceTop[i] = 0;
            difference[i] = 0;

        }

        for (int i = 0; i < imageDrawn.length; i++)
            imageDrawn[i] = false;

    }

    public double calculateDegreeOfTheNote(Note note) {

        double noteLat = note.getLatitude();
        double noteLon = note.getLongitude();

        double usersLat = mMyLatitude;
        double usersLon = mMyLongitude;

        double deltaLon = noteLon - usersLon;

        /*
            θ = atan2( sin Δλ ⋅ cos φ2 , cos φ1 ⋅ sin φ2 − sin φ1 ⋅ cos φ2 ⋅ cos Δλ );

            where φ1,λ1 is the start point, φ2,λ2 the end point (Δλ is the difference in longitude);*/

        double degree = Math.toDegrees(Math.atan2(Math.sin(deltaLon) * Math.cos(noteLat),
                Math.cos(usersLat) * Math.sin(noteLat) - Math.sin(usersLat) * Math.cos(noteLat) * Math.cos(deltaLon)));

        Log.e(TAG, "degree of the note" + note.getNoteID() + "is: " + degree);

        return degree;

    }

    private boolean isBetween(double minAngle, double maxAngle, double azimuth) {
        if (minAngle > maxAngle) {
            if (isBetween(0, maxAngle, azimuth) && isBetween(minAngle, 360, azimuth))
                return true;
        } else {
            if (azimuth > minAngle && azimuth < maxAngle)
                return true;
        }
        return false;
    }

    private void updateDescription() {
        descriptionTextView.setText( " rotation " + mRotationReal + " latitude "
                + mMyLatitude + " longitude " + mMyLongitude);
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = new Location(location);
        lastLocation.setLatitude(mMyLatitude);
        lastLocation.setLongitude(mMyLongitude);
        //Last locationimdan 20 metre hareket ettiysem location degisicek
        if(location.distanceTo(lastLocation) >=200){
            mMyLatitude = location.getLatitude();
            mMyLongitude = location.getLongitude();
            lastLocation.setLatitude(mMyLatitude);
            lastLocation.setLongitude(mMyLongitude);
            for(int i = 0; i < filteredNotes.size(); i++) {
                degreesOfNotes[i] = calculateDegreeOfTheNote(filteredNotes.get(i));
            }
            Toast.makeText(this,"latitude: "+location.getLatitude()+" longitude: "+location.getLongitude(), Toast.LENGTH_SHORT).show();
            updateDescription();
        }

    }

    @Override
    public void onRotationChanged(float newRot,int inclination, int up) {

        float screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        float screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        int intNewRotation = (int) newRot;
        int intOldRotation = (int) mRotationReal;
        Log.e("-------------INCLINATION-------------------", ""+inclination);

        if(Math.abs(intNewRotation - intOldRotation) > 3 || Math.abs(mInclination - inclination) > 3) {

            //TODO burda ne kadar degistigini cek edip thresholddan yuksekse assign edicez gibi?

            mRotationReal = newRot;
            mInclination = inclination;

            for (int i = 0; i < filteredNotes.size(); i++) {
                degreesOfNotes[i] = calculateDegreeOfTheNote(filteredNotes.get(i));

                double minRot = degreesOfNotes[i] - 15.0;
                double maxRot = degreesOfNotes[i] + 15.0;

                if (isBetween(minRot, maxRot, mRotationReal)) {

                    double differenceNew = mRotationReal - degreesOfNotes[i];
                    double differenceTopNew = mInclination;
                    Log.e("-------------differenceTop-------------------", ""+differenceTop);
                    if(Math.abs(differenceNew - difference[i]) > 6 ||Math.abs(differenceTopNew - differenceTop[i]) > 6){
                        difference[i] = differenceNew;
                        differenceTop[i] = differenceTopNew;
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(200, 200);
                        params.leftMargin = 54 * (int) difference[i] + (int) (screenWidth / 2);
                        if(up == -1)
                            params.topMargin = -64 * (int)differenceTop[i] / 3 + (int)(screenHeight + (screenHeight)/2);
                        else if(up == 1)
                            params.topMargin = 64 * (int)differenceTop[i] / 3 - (int)(screenHeight/2);
                        else
                            params.topMargin = 64 * (int)differenceTop[i] / 3 - (int)(screenHeight/2);

                        if(mInclination >= 45){
                            imageArray[i].setLayoutParams(params);
                            imageDrawn[i] = true;
                            imageArray[i].setVisibility(View.VISIBLE);

                            LottieAnimationView animationView = imageArray[i].findViewById(R.id.ArPulseView);
                            animationView.setAnimation(R.raw.pulse);
                            animationView.setRepeatMode(LottieDrawable.INFINITE);
                            animationView.playAnimation();
                        }
                        else{
                            imageArray[i].setVisibility(View.INVISIBLE);
                            imageDrawn[i] = false;
                            //LottieAnimationView animationView = imageArray[i].findViewById(R.id.ArPulseView);
                            //animationView.pauseAnimation();
                        }
                    }


                } else {
                    if (imageDrawn[i]) {
                        imageArray[i].setVisibility(View.INVISIBLE);
                        imageDrawn[i] = false;
                        LottieAnimationView animationView = imageArray[i].findViewById(R.id.ArPulseView);
                        animationView.setVisibility(View.INVISIBLE);
                        animationView.pauseAnimation();
                    }

                }
            }
            ARRootLayout.invalidate();
        }
        updateDescription();

    }

    @Override
    protected void onStop() {
        myCurrentAzimuth.stop();
        myCurrentLocation.stop();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myCurrentAzimuth.start();
        myCurrentLocation.start();
    }

    private void setupListeners() {
        myCurrentLocation = new MyCurrentLocation(this);
        myCurrentLocation.buildGoogleApiClient(this);
        myCurrentLocation.start();

        myCurrentAzimuth = new MyCurrentAzimuth(this, this);
        myCurrentAzimuth.setCameraViewActivity(this);
        myCurrentAzimuth.start();

    }

    private void setupLayout() {

        descriptionTextView = (TextView) findViewById(R.id.cameraTextView);

        getWindow().setFormat(PixelFormat.UNKNOWN);
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.cameraview);
        surfaceView.setZOrderOnTop(false);
        mSurfaceHolder = surfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        if (isCameraviewOn) {
            mCamera.stopPreview();
            isCameraviewOn = false;
        }

        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.startPreview();
                isCameraviewOn = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = Camera.open();
        mCamera.setDisplayOrientation(90);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
        isCameraviewOn = false;
    }

    private class AROnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            RelativeLayout image = (RelativeLayout) v;

            if(image.getTag() != null)
            {
                Note nt = (Note)image.getTag();
                Intent intent = new Intent(CameraViewActivity.this, UINoteDisplay.class);
                intent.putExtra("obj", nt);
                startActivity(intent,
                        ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out).toBundle());
            }
        }
    }

}