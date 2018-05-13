package world.augma.ui.AR;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import world.augma.R;
import world.augma.asset.Note;
import world.augma.asset.User;
import world.augma.ui.note.UINoteDisplay;
import world.augma.ui.services.InterActivityShareModel;
import world.augma.ui.services.ServiceUIMain;

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

    private MyCurrentAzimuth myCurrentAzimuth;
    private MyCurrentLocation myCurrentLocation;

    private User user;
    private ServiceUIMain serviceUIMain;


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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ARRootLayout = findViewById(R.id.ARroot);
        listener = new AROnClickListener();
        serviceUIMain = (ServiceUIMain) InterActivityShareModel.getInstance().getUiMain();
        user = serviceUIMain.fetchUser();

        setupListeners();
        setupLayout();
        filteredNotes = (List<Note>) getIntent().getExtras().getSerializable("filteredNotes");

        degreesOfNotes = new double[filteredNotes.size()];

        imageArray = new RelativeLayout[filteredNotes.size()];


        imageDrawn = new boolean[filteredNotes.size()];

        for(int i = 0; i < imageDrawn.length; i++)
            imageDrawn[i] = false;


//        mAzimuthTheoretical = new double[filteredNotes.size()];
    }


    /*public double calculateTheoreticalAzimuth(Note note) {

            double dX = note.getLatitude() - mMyLatitude;
            double dY = note.getLongitude() - mMyLongitude;

            double phiAngle;
            double tanPhi;
            double azimuth = 0;

            tanPhi = Math.abs(dY / dX);
            phiAngle = Math.atan(tanPhi);
            phiAngle = Math.toDegrees(phiAngle);

            if (dX > 0 && dY > 0) { // I quater
                return azimuth = phiAngle;
            } else if (dX < 0 && dY > 0) { // II
                return azimuth = 180 - phiAngle;
            } else if (dX < 0 && dY < 0) { // III
                return azimuth = 180 + phiAngle;
            } else if (dX > 0 && dY < 0) { // IV
                return azimuth = 360 - phiAngle;
            }

        return phiAngle;
    }

    private List<Double> calculateAzimuthAccuracy(double azimuth) {
        double minAngle = azimuth - AZIMUTH_ACCURACY;
        double maxAngle = azimuth + AZIMUTH_ACCURACY;
        List<Double> minMax = new ArrayList<Double>();

        if (minAngle < 0)
            minAngle += 360;

        if (maxAngle >= 360)
            maxAngle -= 360;

        minMax.clear();
        minMax.add(minAngle);
        minMax.add(maxAngle);

        return minMax;
    }*/

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

        LayoutInflater inflater = LayoutInflater.from(this);

        int intNewRotation = (int) newRot;
        int intOldRotation = (int) mRotationReal;
        Log.e("-------------INCLINATION-------------------", ""+inclination);

        if(Math.abs(intNewRotation - intOldRotation) > 3 || Math.abs((int)mInclination - inclination) > 3) {

            //TODO burda ne kadar degistigini cek edip thresholddan yuksekse assign edicez gibi?

            mRotationReal = newRot;
            mInclination = inclination;

            for (int i = 0; i < filteredNotes.size(); i++) {
                degreesOfNotes[i] = calculateDegreeOfTheNote(filteredNotes.get(i));

                double minRot = degreesOfNotes[i] - 15.0;
                double maxRot = degreesOfNotes[i] + 15.0;

                if (isBetween(minRot, maxRot, mRotationReal)) {

                    double difference = mRotationReal - degreesOfNotes[i];
                    double differenceTop = Math.abs( mInclination );
                    Log.e("-------------differenceTop-------------------", ""+differenceTop);

                    //TODO her seferinde ortaya cizmicez
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(200, 200);
                    params.leftMargin = 54 * (int) difference + (int) (screenWidth / 2);
                    if(up == -1)
                        params.topMargin = -64 * (int)differenceTop / 3 + (int)(screenHeight + (screenHeight)/2);
                    else if(up == 1)
                        params.topMargin = 64 * (int)differenceTop / 3 - (int)(screenHeight/2);
                    else
                        params.topMargin = 64 * (int)differenceTop / 3 - (int)(screenHeight/2);

                    if(mInclination >= 45){
                        if (!imageDrawn[i]) {
                            imageArray[i] = (RelativeLayout) inflater.inflate(R.layout.ar_item_view, null, false);

                            ((ImageView) imageArray[i].findViewById(R.id.ArItemImage)).setBackgroundResource(R.drawable.note_icon);
                            imageArray[i].setLayoutParams(params);
                            ARRootLayout.addView(imageArray[i]);

                            imageDrawn[i] = true;
                        } else {
                            ARRootLayout.removeView(imageArray[i]);
                            imageDrawn[i] = false;

                            imageArray[i] = (RelativeLayout) inflater.inflate(R.layout.ar_item_view, null, false);
                            ((ImageView) imageArray[i].findViewById(R.id.ArItemImage)).setBackgroundResource(R.drawable.note_icon);
                            imageArray[i].setLayoutParams(params);
                            ARRootLayout.addView(imageArray[i]);
                            imageDrawn[i] = true;
                        }
                        imageArray[i].setTag(filteredNotes.get(i));
                        imageArray[i].setOnClickListener(listener);
                        imageArray[i].bringToFront();
                    }
                    else{
                        ARRootLayout.removeView(imageArray[i]);
                        imageDrawn[i] = false;
                    }

                } else {
                    if (imageDrawn[i]) {
                        ARRootLayout.removeView(imageArray[i]);
                        imageDrawn[i] = false;
                    }

                }
            }
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