package com.example.danie.suelos2;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import data.DBHelper;
import data.Floor;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        ArrayList<Floor> suelos = etiquetas();

        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);

        LatLng lugar = null;

        for(Floor suelo: suelos){
            lugar = new LatLng ((double) suelo.getLat(),(double) suelo.getLon());
            String url = suelo.getPath();
            mMap.addMarker(new MarkerOptions().position(lugar).title(suelo.getTipo()).snippet(suelo.getFecha()));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lugar));



    }

    public ArrayList<Floor> etiquetas(){
        DBHelper db = DBHelper.getInstance(this);
        ArrayList<Floor> suelos = db.getAll();
        return suelos;
    }

    public boolean onMarkerClick(Marker marker){
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker){
        Toast.makeText(this, "Hola",Toast.LENGTH_LONG).show();

    }
}
