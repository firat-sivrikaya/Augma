package world.augma.ui.map;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ramotion.circlemenu.CircleMenuView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import world.augma.R;
import world.augma.asset.Circle;
import world.augma.asset.Note;
import world.augma.asset.User;
import world.augma.ui.AR.ARView;
import world.augma.ui.note.UINoteDisplay;
import world.augma.ui.note.UINotePost;
import world.augma.ui.services.InterActivityShareModel;
import world.augma.ui.services.ServiceUIMain;
import world.augma.work.AWS;

/** Created by Burak */

public class UIMap extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback ,
        GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback{

    private static final String TAG = UIMap.class.getSimpleName();

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int NOTE_POST = 0;
    private static final int OPEN_CAMERA = 1;
    private static final int GET_NOTES = 2;

    private static final String KEY_LOCATION = "location";
    private static final String KEY_CAMERA_POSITION = "camera_position";

    private GoogleMap mMap;
    private MapView mMapView;
    private CircleMenuView circleMenu;

    public static FusedLocationProviderClient mFusedLocationProviderClient;

    private final LatLng mDefaultLocation = new LatLng(39.871291, 32.749957);
    private static final int DEFAULT_ZOOM = 15;

    private boolean mLocationPermissionGranted;

    private CameraPosition mCameraPosition;
    public static Location mLastKnownLocation;

    private LocationCallback mLocationCallback;
    public static LocationRequest mLocationRequest;

    private List<Note> nearbyNotes;
    private List<Note> filteredNotes;
    private User user;
    private ServiceUIMain serviceUIMain;
    // The entry points to the Places API.
//    private GeoDataClient mGeoDataClient;
//    private PlaceDetectionClient mPlaceDetectionClient;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.ui_map, container, false);

        serviceUIMain = (ServiceUIMain) InterActivityShareModel.getInstance().getUiMain();
        user = serviceUIMain.fetchUser();

        circleMenu = (CircleMenuView) root.findViewById(R.id.circleMenu);

        circleMenu.setEventListener(new CircleMenuView.EventListener() {

            @Override
            public void onButtonClickAnimationEnd(@NonNull CircleMenuView view, int buttonIndex) {
                switch(buttonIndex) {
                    case NOTE_POST:
                        Intent intent2 = new Intent(getActivity(), UINotePost.class);
                        //intent2.putExtra("lastLocation",myL)
                        startActivity(intent2, ActivityOptions.makeCustomAnimation(getContext(),
                                R.anim.fade_in, R.anim.fade_out).toBundle());
                        return;
                    case OPEN_CAMERA:
                        //startActivity(new Intent(getActivity(), UICamera.class));
                        Intent intent = new Intent(getActivity(), ARView.class);
//                        Bundle bundle = new Bundle();
//                        bundle.putParcelable("mLastKnownLocation", mLastKnownLocation);
//                        intent.putExtras(bundle);
                        intent.putExtra("filteredNotes", (Serializable) filteredNotes);
                        //Log.e("ASLKDJAKSDAD", "My Lat: " + mLastKnownLocation.getLatitude() +
                        //" My Lon: " + mLastKnownLocation.getLongitude());
                        startActivity(intent, ActivityOptions.makeCustomAnimation(getContext(),
                                R.anim.fade_in, R.anim.fade_out).toBundle());
                        return;
                    case GET_NOTES:
                        getDeviceLocation();
                        AWS aws = new AWS();
                        try {
                            aws.execute(AWS.Service.GET_NOTE_WITH_FILTER, "" + mLastKnownLocation.getLatitude(),
                                    "" + mLastKnownLocation.getLongitude()).get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                        nearbyNotes = aws.getMatchedNotes();
                        filteredNotes = putMarker(mMap, nearbyNotes);
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

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(2000)
                .setFastestInterval(1000);

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



                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        Note nt= (Note)marker.getTag();
                        Intent intent = new Intent(getActivity(), UINoteDisplay.class);
                        intent.putExtra("obj", nt);
                        startActivity(intent,
                                ActivityOptionsCompat.makeCustomAnimation(getContext(), R.anim.fade_in, R.anim.fade_out).toBundle());
                        return false;
                    }
                });
            }

        });





        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    mLastKnownLocation = location;
                    updateLocationUI();
                }
            }
        };




        return root;
    }



    public List<Note> putMarker(GoogleMap mMap, List<Note> nearbyNotes)
    {
        List<Note> filtered = new ArrayList<>();

        float[] result = new float[1];
        mMap.clear();
        Marker mPerth;
        for(Note n : nearbyNotes)
        {
            boolean added = false;
            Location.distanceBetween(mLastKnownLocation.getLatitude(),
                    mLastKnownLocation.getLongitude(), n.getLatitude(), n.getLongitude(), result);
            if(result[0] <= 1000) // Notes within the 100 meter radius are shown.
            {
                for(Circle c1 : n.getCircleList())
                {
                    for(Circle c2 : user.getMemberships())
                    {
                        if(c1.getName().equals(c2.getName()))
                        {

                            Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.map_marker, null);
                            Bitmap b = ((BitmapDrawable) icon).getBitmap();
                            Bitmap resized = b.createScaledBitmap(b, 60, 94, false);

                            //Log.e("AAAAAAAAAAAAAAAAAAAA", c1.getCircleID());
                            //We deduce that user can see this note.
                            mPerth = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(n.getLatitude(), n.getLongitude()))
                                    .title(c1.getName())
                                    .icon(BitmapDescriptorFactory.fromBitmap(resized))
                            );
                            //mMap.setOnMarkerClickListener(this);
                            mPerth.setTag(n);
                            filtered.add(n);
                            added = true;

                            break;
                        }
                    }
                    if(added)
                        break;
                }
            }
        }
        return filtered;
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
        stopLocationUpdates();

    }

    private void stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
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
        if (mLocationPermissionGranted) {
            startLocationUpdates();
        }

    }



    private void startLocationUpdates() {
        try
        {
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
//            updateCameraBearing(mMap, mLastKnownLocation.getBearing());
        }
        catch(SecurityException e)
        {
            Log.e(TAG, "No location updates for you my friend.");
        }
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
//                            updateCameraBearing(mMap, mLastKnownLocation.getBearing());
                            AWS aws = new AWS();
                            try {
                                aws.execute(AWS.Service.GET_NOTE_WITH_FILTER, "" + mLastKnownLocation.getLatitude(),
                                        "" + mLastKnownLocation.getLongitude()).get();
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                            nearbyNotes = aws.getMatchedNotes();
                            filteredNotes = putMarker(mMap, nearbyNotes);
                        }
                        else
                        {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
//                            updateCameraBearing(mMap, mLastKnownLocation.getBearing());
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
//    private void updateCameraBearing(GoogleMap googleMap, float bearing) {
//        if ( googleMap == null) return;
//        CameraPosition camPos = CameraPosition
//                .builder(
//                        googleMap.getCameraPosition() // current Camera
//                )
//                .bearing(bearing)
//                .build();
//        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
//    }
}
