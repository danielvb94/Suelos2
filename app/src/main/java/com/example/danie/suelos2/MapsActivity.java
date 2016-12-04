package com.example.danie.suelos2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
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

        for (Floor suelo : suelos) {
            lugar = new LatLng((double) suelo.getLat(), (double) suelo.getLon());
            String titulo = suelo.getTipo();
            titulo = titulo.replaceAll("\n","|| ");
            titulo = titulo.replaceAll(" \t\t ", "");
            mMap.addMarker(new MarkerOptions().position(lugar).title(titulo).snippet(suelo.getFecha() + "\n" + suelo.getPath()));
        }
        if (lugar != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(lugar));
            CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
            mMap.animateCamera(zoom);
        }


    }

    public ArrayList<Floor> etiquetas() {
        DBHelper db = DBHelper.getInstance(this);
        ArrayList<Floor> suelos = db.getAll();

        for (Floor suelo : suelos) {
            String path = getRealPathFromURI(this,Uri.parse(suelo.getPath()));
            File file = new File(path);

            if (!file.exists()) {
                db.elimina(suelo.getId());
            }
        }
        suelos = db.getAll();

        return suelos;
    }

    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //Toast.makeText(this, "Hola",Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(marker.getSnippet().replaceFirst(".*\n","")), "image/*");
        startActivity(intent);
    }


    public String getRealPathFromURI(Context context, Uri contentUri) {

        String res = "";
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                res = cursor.getString(column_index);
            }
            cursor.close();
        } else {
            Log.d("E:", "Cursor is null");
            return contentUri.getPath();
        }
        return res;
    }




}
