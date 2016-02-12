package com.example.mohmohhtun.mapapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShowLocation extends Activity {
    // Class to do operations on the Map
    GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showlocation);
        // To get map from MapFragment from layout
        googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                R.id.map)).getMap();


        Bundle b = getIntent().getExtras();
        Place place = (Place) b.getParcelable("extra");
        Double latt = Double.parseDouble(place.getLatt());
        Double longi = Double.parseDouble(place.getLon());
       // Location location = new GPSTracker(getApplicationContext()).getLocation();
        //googleMap.setMyLocationEnabled(true);



        LatLng position = new LatLng(latt, longi);

        // Creating object to pass our current location to the map
        MarkerOptions markerOptions = new MarkerOptions();
        // To store current location in the markeroptions object
        markerOptions.position(position);

        // Zooming to our current location with zoom level 17.0f
        googleMap.animateCamera(CameraUpdateFactory
                .newLatLngZoom(position, 17f));
        googleMap.addMarker(new MarkerOptions().position(new LatLng(latt, longi)).title(place.getName()));
        // To listen action whenever we click on the map
        googleMap.setOnMapClickListener(new OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
				/*
				 * LatLng:Class will give us selected position lattigude and
				 * longitude values
				 */
                //googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(new LatLng(latLng.latitude, latLng.longitude)).title("jfal;sfj"));
                CameraUpdate center=CameraUpdateFactory.newLatLng(new LatLng(latLng.latitude, latLng.longitude));
                //CameraUpdate zoom=CameraUpdateFactory.zoomTo(11);
                googleMap.moveCamera(center);
                //googleMap.animateCamera(zoom);
                Toast.makeText(getApplicationContext(), latLng.toString(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

}
