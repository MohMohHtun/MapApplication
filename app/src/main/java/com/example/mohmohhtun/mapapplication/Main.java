package com.example.mohmohhtun.mapapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main extends FragmentActivity implements LocationListener,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,ResultCallback<Status> {
	// Class to do operations on the Map
	GoogleMap googleMap;
    Double currentLatt=0.0,currentLong = 0.0;

    protected static final String TAG = "Main";
    protected GoogleApiClient mGoogleApiClient;
    private boolean mGeofencesAdded;
    private SharedPreferences mSharedPreferences;
    protected ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;

    FloatingActionButton btnenableAlert,btndisableAlert,btnFASave,btnFAList,btnFaAbout;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
        mGeofenceList = new ArrayList<>();
        mGeofencePendingIntent = null;
        btnFASave = (FloatingActionButton)findViewById(R.id.save_location);
        btnFAList = (FloatingActionButton)findViewById(R.id.list);
        btnenableAlert = (FloatingActionButton)findViewById(R.id.enable_alert);
        btndisableAlert = (FloatingActionButton)findViewById(R.id.disable_alert);
        btnFaAbout = (FloatingActionButton)findViewById(R.id.info);


        btnFaAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomAlet();
            }
        });

        btnFASave.setOnClickListener(new OnSaveClicked());
        btnFAList.setOnClickListener(new OnViewSavedClicked());


		// setuping locatiomanager to perfrom location related operations
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// Requesting locationmanager for location updates
		locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1, 1, this);


		// To get map from MapFragment from layout
		//googleMap = ((SupportMapFragment) Main.this.getSupportFragmentManager().findFragmentById(
			//	R.id.map)).getMap();
        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager()
                .findFragmentById(R.id.map);
        googleMap = mapFragment.getMap();

		// To change the map type to Satellite
		// googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

		// To show our current location in the map with dot
        Location location = new GPSTracker(getApplicationContext()).getLocation();
	    //googleMap.setMyLocationEnabled(true);
        if(googleMap!=null) {
            if (location != null) {
                googleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("It's Me!"));
                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
                googleMap.moveCamera(center);
                googleMap.animateCamera(zoom);
            } else {
                //1.284014, 103.858982  // default singapoer if location cannto get.

                googleMap.addMarker(new MarkerOptions().position(new LatLng(1.284014, 103.858982)).title("It's Me!"));
                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(1.284014, 103.858982));
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(17);
                googleMap.moveCamera(center);
                googleMap.animateCamera(zoom);

            }
        }
        mSharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);
        mGeofencesAdded = mSharedPreferences.getBoolean(Constants.GEOFENCES_ADDED_KEY, false);
        setButtonsEnabledState();

        populateGeofenceList();
        buildGoogleApiClient();

	}
    private MapFragment getMapFragment() {
        android.app.FragmentManager fm = null;

        Log.d(TAG, "sdk: " + Build.VERSION.SDK_INT);
        Log.d(TAG, "release: " + Build.VERSION.RELEASE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "using getFragmentManager");
            fm = getFragmentManager();
        } else {
            Log.d(TAG, "using getChildFragmentManager");
           // fm = getChildFragmentManager();
        }

        return (MapFragment) fm.findFragmentById(R.id.map);
    }

    @Override
	public void onLocationChanged(Location location) {
		// To get lattitude value from location object
		double latti = location.getLatitude();
		// To get longitude value from location object
		double longi = location.getLongitude();
        currentLatt = latti;
        currentLong = longi;
		// To hold lattitude and longitude values
		LatLng position = new LatLng(latti, longi);

		// Creating object to pass our current location to the map
		MarkerOptions markerOptions = new MarkerOptions();
		// To store current location in the markeroptions object
		markerOptions.position(position);

        if(googleMap!=null) {
            // Zooming to our current location with zoom level 17.0f
            googleMap.animateCamera(CameraUpdateFactory
                    .newLatLngZoom(position, 15f));

            // adding markeroptions class object to the map to show our current
            // location in the map with help of default marker
            googleMap.clear();
            googleMap.addMarker(markerOptions.title("It's me!"));
            ArrayList<Place> list = new PlaceHandler(Main.this).getPlaces();
            if (list != null && list.size() > 0) {
                addMarkersforGeoGence(list);
            }
        }

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onResult(Status status) {
        if (status.isSuccess()) {
            // Update state and save in shared preferences.
            mGeofencesAdded = !mGeofencesAdded;
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(Constants.GEOFENCES_ADDED_KEY, mGeofencesAdded);
            editor.apply();

            // Update the UI. Adding geofences enables the Remove Geofences button, and removing
            // geofences enables the Add Geofences button.
            setButtonsEnabledState();

            Toast.makeText(
                    this,
                    getString(mGeofencesAdded ? R.string.geofences_added :
                            R.string.geofences_removed),
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
            Log.e(TAG, errorMessage);
        }
    }

    private class OnSaveClicked implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            final AlertDialog dialog = new AlertDialog.Builder(Main.this).create();
            // hide to default title for Dialog
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


            if (currentLatt == 0.0 || currentLong == 0.0){
                dialog.setTitle("Error:");
                dialog.setMessage("Cannot Retrieve exact location, make sure you have good internet connection and enable gps.");
                dialog.show();
                return;
            }


            // inflate the layout dialog_layout.xml and set it as contentView
            Rect displayRectangle = new Rect();
            Window window = Main.this.getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View saveview = inflater.inflate(R.layout.save_dialog, null, false);
            saveview.setMinimumWidth((int) (displayRectangle.width() * 0.9f));

            final EditText name = (EditText)saveview.findViewById(R.id.txtName);
            final EditText latt = (EditText)saveview.findViewById(R.id.latt);
            final EditText longi = (EditText)saveview.findViewById(R.id.longi);

            latt.setText(currentLatt+"");
            longi.setText(currentLong+"");

            Button cancel = (Button)saveview.findViewById(R.id.btnCancel);
            Button btnsave = (Button) saveview.findViewById(R.id.btnSave);
            btnsave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (name.getText().toString().equalsIgnoreCase("")){
                        Toast.makeText(getApplicationContext(),"Please Provide Name",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        return;
                    }else {
                        new PlaceHandler(getApplicationContext()).addPlace(name.getText().toString(),latt.getText().toString(),longi.getText().toString(),"0");
                        Toast.makeText(getApplicationContext(),"Location Saved",Toast.LENGTH_SHORT).show();
                        populateGeofenceList();
                        btnenableAlert.performClick();
                        dialog.dismiss();
                    }
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });



            dialog.setCanceledOnTouchOutside(true);
            dialog.setView(saveview);

            // Display the dialog

            dialog.show();
        }
    }

    private class OnViewSavedClicked implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            ArrayList<Place> list  = new PlaceHandler(getApplicationContext()).getPlaces();

            if (list != null && list.size() == 0){
                Toast.makeText(getApplicationContext(),"Currently No Saved Location",Toast.LENGTH_SHORT).show();
                return;
            }else {
                Intent intent = new Intent(Main.this,LocationList.class);
                intent.putExtra("extra",list);

                startActivity(intent);

            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    private void setButtonsEnabledState() {
        if (mGeofencesAdded) {
            btnenableAlert.setEnabled(false);
            btndisableAlert.setEnabled(true);
        } else {
            btnenableAlert.setEnabled(true);
            btndisableAlert.setEnabled(false);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void populateGeofenceList() {


        ArrayList<Place> list = new PlaceHandler(Main.this).getPlaces();
        if (list == null && list.size() == 0)
            return;
        HashMap<String,LatLng> listMap = new HashMap<>();
        for (int i = 0; i < list.size() ; i++){
            Place place = list.get(i);
            Double latt = Double.valueOf(Location.convert(Double.parseDouble(place.getLatt()), Location.FORMAT_DEGREES));
            Double longi = Double.valueOf(Location.convert(Double.parseDouble(place.getLon()), Location.FORMAT_DEGREES));
            LatLng latLng = new LatLng(latt,longi);

            listMap.put(place.getName(),latLng);

        }

        for (Map.Entry<String, LatLng> entry : listMap.entrySet()) {

            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.getKey())

                            // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )

                            // Set the expiration duration of the geofence. This geofence gets automatically
                            // removed after this period of time.
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                            // Set the transition types of interest. Alerts are only generated for these
                            // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)

                            // Create the geofence.
                    .build());
        }
        if (mGeofenceList.size() > 0 ){
            addMarkersforGeoGence(list);
        }
    }

    private void addMarkersforGeoGence(ArrayList<Place> mGeofenceList) {
        for (Place fence : mGeofenceList){
            showCircle(fence);
        }
    }

    public void removeGeofencesButtonHandler(View view) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            // Remove geofences.
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    // This is the same pending intent that was used in addGeofences().
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    public void addGeofencesButtonHandler(View view) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        if ( new PlaceHandler(Main.this).getPlaces() == null || new PlaceHandler(Main.this).getPlaces().size() == 0){
            Toast.makeText(this,"you have no saved location, please save location first", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }



    private void showCircle(Place place){
        if(place == null){
            // display en error message and return
            return;
        }
        googleMap.addMarker( new MarkerOptions()
                .position( new LatLng(Double.parseDouble(place.getLatt()), Double.parseDouble(place.getLon())) )
                .title(place.getName()));

//Instantiates a new CircleOptions object +  center/radius
        CircleOptions circleOptions = new CircleOptions()
                .center( new LatLng(Double.parseDouble(place.getLatt()), Double.parseDouble(place.getLon())) )
                .radius( Constants.GEOFENCE_RADIUS_IN_METERS )
                .fillColor(0x40ff0000)
                .strokeColor(Color.TRANSPARENT)
                .strokeWidth(2);

// Get back the mutable Circle
        Circle circle = googleMap.addCircle(circleOptions);
// more operations on the circle...
    }

    private void showCustomAlet(){
        final AlertDialog dialog = new AlertDialog.Builder(Main.this).create();
        // hide to default title for Dialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        if (currentLatt == 0.0 || currentLong == 0.0){
            dialog.setTitle("Error:");
            dialog.setMessage("Cannot Retrieve exact location, make sure you have good internet connection and enable gps.");
            dialog.show();
            return;
        }


        // inflate the layout dialog_layout.xml and set it as contentView
        Rect displayRectangle = new Rect();
        Window window = Main.this.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View saveview = inflater.inflate(R.layout.info_dialog, null, false);
        saveview.setMinimumWidth((int) (displayRectangle.width() * 0.9f));
        saveview.setMinimumHeight((int) (displayRectangle.width() * 0.9f));

        Button ok = (Button)saveview.findViewById(R.id.btnOk);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(true);
        dialog.setView(saveview);

        // Display the dialog

        dialog.show();
    }


}
