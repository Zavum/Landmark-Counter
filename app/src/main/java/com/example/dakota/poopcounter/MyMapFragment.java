package com.example.dakota.poopcounter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A fragment that launches other parts of the demo application.
 */
public class MyMapFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    MapView mMapView;
    private GoogleMap mGoogleMap;
    private GoogleApiClient locationApi;
    private LocationRequest mLocationRequest;
    Location mLastLocation;

    Context activityContext;
    Activity currentActivity;

    double lat, lon;
    private ArrayList<String> latitudes;
    private ArrayList<String> longitudes;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflat and return the layout
        View v = inflater.inflate(R.layout.fragment_map, container,
                false);
        activityContext = getActivity();
        currentActivity = getActivity();
        locationApi = new GoogleApiClient.Builder(activityContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mMapView = (MapView) v.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();

        ArrayList<ArrayList<String>> locationPairs = new ArrayList<ArrayList<String>>(getPastLocations());

        latitudes = locationPairs.get(0);
        longitudes = locationPairs.get(1);

        SupportMapFragment mSupportMapFragment;

        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mSupportMapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mSupportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mSupportMapFragment).commit();
        }

        if (mSupportMapFragment != null) {
            mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    if (googleMap != null) {
                        mGoogleMap = googleMap;

                        MarkerOptions marker = new MarkerOptions();
                        LatLng latLng;
                        for(int i =0; i < latitudes.size();i++){
                            latLng = new LatLng(Double.parseDouble(latitudes.get(i)),Double.parseDouble(longitudes.get(i)));
                            marker.position(latLng).title("You Pooped Here Before!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                            googleMap.addMarker(marker);

                        }
                    }

                }
            });
        }
        return v;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    Toast.makeText(currentActivity, "Permission deny to read your location", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        locationApi.disconnect();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onConnected(Bundle bundle) {


        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(100); // Update location every second


        if (ActivityCompat.checkSelfPermission(activityContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(activityContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(activityContext, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(activityContext,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED ) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(currentActivity,android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    Toast.makeText(activityContext,"TODO: LOCATION PERMISSION STATEMENT",Toast.LENGTH_LONG).show();

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(currentActivity,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION},
                            1);

                }
            }
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(locationApi, mLocationRequest, this);


        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(locationApi);

        lat = mLastLocation.getLatitude();
        lon = mLastLocation.getLongitude();
        mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
        mGoogleMap.setMyLocationEnabled(true);
        LatLng currentLocation = new LatLng(lat, lon);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(currentLocation).zoom(15.0f).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mGoogleMap.moveCamera(cameraUpdate);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }

    synchronized void buildGoogleApiClient() {
        locationApi = new GoogleApiClient.Builder(activityContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    @Override
    public void onStart() {
        super.onStart();
        locationApi.connect();
    }
    public void poopTaken(){
        if(latitudes.contains(String.valueOf(lat)) && longitudes.contains(String.valueOf(lon))){

        }else{
            latitudes.add(0,String.valueOf(lat));
            longitudes.add(0,String.valueOf(lon));
            MarkerOptions marker = new MarkerOptions();
            marker.position(new LatLng(Double.parseDouble(latitudes.get(0)),Double.parseDouble(longitudes.get(0)))).title("You Pooped Here Before!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
            mGoogleMap.addMarker(marker);
        }
    }
    private void updateMap(){

    }
    private ArrayList<ArrayList<String>> getPastLocations(){
        SharedPreferences sp = activityContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        ArrayList<ArrayList<String>> returnedList = new ArrayList<ArrayList<String>>();
        Set<String> emptyList = new HashSet<String>();

        Set<String> setLats = sp.getStringSet("pastLatitudes",emptyList);
        Set<String> setLongs = sp.getStringSet("pastLongitudes",emptyList);

        ArrayList<String> returnedLats = new ArrayList<String>(setLats);
        ArrayList<String> returnedLongs = new ArrayList<String>(setLongs);
        returnedList.add(returnedLats);
        returnedList.add(returnedLongs);

        return returnedList;
    }
    public void savePastLocations(){
        SharedPreferences sp = activityContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();


        Set<String> setLats = new HashSet<String>(latitudes);
        Set<String> setLongs = new HashSet<String>(longitudes);


        editor.putStringSet("pastLatitudes",setLats);
        editor.putStringSet("pastLongitudes",setLongs);

        editor.apply();
    }

}