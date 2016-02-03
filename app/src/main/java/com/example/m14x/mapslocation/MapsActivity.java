package com.example.m14x.mapslocation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.barcode.Barcode;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    protected Location mLastLocation;
    protected MapCoordinate mapCoor = new MapCoordinate();

    @Bind(R.id.TxtLatitude)
    EditText mLatitude;
    @Bind(R.id.TxtLongitude)
    EditText mLongitude;
    @Bind(R.id.TxtV)
    TextView mTxtAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        buildGoogleApiClient();

    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @OnClick(R.id.btnAddress)
    public void getAddress()  {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(mapCoor.getmLatitude(), mapCoor.getmLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            if (addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL



                mTxtAddress.setText(city + " " +
                                    knownName + " "+
                                    state + " " +
                                    country + " "+
                                    postalCode + " " );

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }



    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();

        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    @OnClick(R.id.btnSearch)
    public void Search(){
        LatLng place = new LatLng(mapCoor.getmLatitude(),mapCoor.getmLongitude());
        MarkerOptions marker = new MarkerOptions().position(place).title("Marker");
        marker.draggable(true);
        mMap.addMarker(marker);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place, 13));

    }

    @OnClick(R.id.btnLocate)
    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitude.setText(String.format("%f",
                    mLastLocation.getLatitude()));
            mapCoor.setmLatitude(mLastLocation.getLatitude());
            mLongitude.setText(String.format("%f",
                    mLastLocation.getLongitude()));
            mapCoor.setmLongitude(mLastLocation.getLongitude());
        } else {
            //Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
            mLongitude.setText("No Location Detected");
        }


    }

    @Override

    public void onConnected(Bundle bundle) {
        //Toast.makeText(this, "Test", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
