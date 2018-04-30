package world.augma.ui.map;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ramotion.circlemenu.CircleMenuView;

import world.augma.R;
import world.augma.ui.camera.UICamera;

/** Created by Burak */

public class UIMap extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback{

    private static final String TAG = UIMap.class.getSimpleName();

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int NOTE_POST = 0;
    private static final int OPEN_CAMERA = 1;

    private static final String KEY_LOCATION = "location";
    private static final String KEY_CAMERA_POSITION = "camera_position";

    private GoogleMap mMap;
    private MapView mMapView;
    private CircleMenuView circleMenu;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private final LatLng mDefaultLocation = new LatLng(39.871291, 32.749957);
    private static final int DEFAULT_ZOOM = 15;

    private boolean mLocationPermissionGranted;

    private CameraPosition mCameraPosition;
    private Location mLastKnownLocation;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.ui_map, container, false);

        circleMenu = (CircleMenuView) root.findViewById(R.id.circleMenu);

        circleMenu.setEventListener(new CircleMenuView.EventListener() {

            @Override
            public void onButtonClickAnimationEnd(@NonNull CircleMenuView view, int buttonIndex) {
                switch(buttonIndex) {
                    case NOTE_POST:
                        return;
                    case OPEN_CAMERA:
                        Intent intent = new Intent(getActivity(), UICamera.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("mLastKnownLocation", mLastKnownLocation);
                        intent.putExtras(bundle);

                        startActivity(intent, ActivityOptions.makeCustomAnimation(getContext(), R.anim.fade_in, R.anim.fade_out).toBundle());
                        return;
                }
            }
        });

        mMapView = root.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getActivity());

        MapsInitializer.initialize(getActivity().getApplicationContext());

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                try {
                    if(!googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.mapstyle))) {
                        Log.e("UIMap", "Style parsing FAILED.");
                    }
                } catch (Resources.NotFoundException e) {
                    Log.e("UIMap", "Failed while trying to find the style.");
                }

//                LatLng anitkabir = new LatLng(39.928344, 32.837697);
//                mMap.addMarker(new MarkerOptions().position(anitkabir).title("<3"));
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(anitkabir));
//
//                CameraPosition camPos = new CameraPosition.Builder().target(anitkabir).zoom(10).build();
//                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));

                getLocationPermission();

                updateLocationUI();

                getDeviceLocation();
            }
        });
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                circleMenu.setVisibility(View.INVISIBLE);
                circleMenu.invalidate();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                circleMenu.setVisibility(View.VISIBLE);
                circleMenu.invalidate();
            }
        });
    }

    private void getLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;

        } else {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        mLocationPermissionGranted = false;
        if (requestCode != PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            return;
        }

        //if user cancels the request grantResult is empty.
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            mLocationPermissionGranted = true;

        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try
        {
            if(mLocationPermissionGranted)
            {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
            else
            {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                getLocationPermission();
            }
        }
        catch(SecurityException e)
        {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation()
    {
        try{
            if(mLocationPermissionGranted)
            {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this.getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if(task.isSuccessful() && task.getResult() != null)
                        {
                            //Setting map to the current location.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                        }
                        else
                        {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        }
        catch(SecurityException e)
        {
            Log.e("Exception: %s", e.getMessage());
        }
    }
}
