package world.augma.ui.map;

import android.os.Bundle;
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

import world.augma.R;

/** Created by Burak Şahin */

public class UIMap extends Fragment {

    private GoogleMap mMap;
    private MapView mMapView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.ui_map, container, false);

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
