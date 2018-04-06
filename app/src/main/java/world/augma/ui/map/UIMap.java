package world.augma.ui.map;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ramotion.circlemenu.CircleMenuView;

import world.augma.R;
import world.augma.ui.camera.UICamera;

/** Created by Burak */

public class UIMap extends Fragment {

    private static final int NOTE_POST = 0;
    private static final int OPEN_CAMERA = 1;

    private GoogleMap mMap;
    private MapView mMapView;
    private CircleMenuView circleMenu;

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
                        startActivity(new Intent(getActivity(), UICamera.class));
                        return;
                }
            }
        });

        mMapView =   root.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        MapsInitializer.initialize(getActivity().getApplicationContext());

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                LatLng anitkabir = new LatLng(39.928344, 32.837697);
                mMap.addMarker(new MarkerOptions().position(anitkabir).title("<3"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(anitkabir));

                CameraPosition camPos = new CameraPosition.Builder().target(anitkabir).zoom(10).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
            }
        });
        return root;
    }
}
