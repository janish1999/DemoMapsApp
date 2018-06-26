package com.example.user.testproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.NumberKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.testproject.models.Placeinfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    private static final String CoarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String FineLocation = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final LatLngBounds LAT_LNG_BOUNDS=new LatLngBounds(new LatLng(-40,-168),new LatLng(71,136));
    private Boolean locationpermmission = false;
    private GoogleMap Map;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private Placeinfo mplace;
    private AutoCompleteTextView search;
    private ImageView gps,info,placePicker;
    private PlaceAutocompleteAdapter adapter;
    GoogleApiClient client;
    private Marker marker;
    private static final int PLACE_PICKER_REQUEST = 1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        gps=(ImageView)findViewById(R.id.gps);
        search=(AutoCompleteTextView) findViewById(R.id.search);
        info=(ImageView)findViewById(R.id.placeinfo);
        placePicker=(ImageView)findViewById(R.id.placePicker);
        getPermission();

    }
    private void init(){
        client = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this ,this)
                .build();

        search.setOnItemClickListener(itemClickListener);

        adapter=new PlaceAutocompleteAdapter(this,client,LAT_LNG_BOUNDS,null);

        search.setAdapter(adapter);
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

               if(actionId== EditorInfo.IME_ACTION_SEARCH || actionId== EditorInfo.IME_ACTION_DONE || event.getAction()==KeyEvent.ACTION_DOWN || event.getAction()==KeyEvent.KEYCODE_ENTER){
                   geolocate();
               }
                return false;
            }
        });
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(marker.isInfoWindowShown()){
                        marker.hideInfoWindow();
                    }else {
                        marker.showInfoWindow();
                    }
                }catch (NullPointerException e){
                    Log.e("error","error");
                }
            }
        });
        placePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(MapActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException ex) {
                    Log.e("error","error");
                }
            }
        });
        hideKeyboard();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this,data);
                PendingResult<PlaceBuffer> placeBufferPendingResult=Places.GeoDataApi.getPlaceById(client,place.getId());
                placeBufferPendingResult.setResultCallback(updatecallback);
            }
        }
    }

    private void moveCamera(LatLng latLng, float zoom,Placeinfo placeinfo) {
        Map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        Map.clear();
        Map.setInfoWindowAdapter(new customadapter(MapActivity.this));
        if(placeinfo !=null){
            try{
                String snippet ="Address "+ placeinfo.getAddress()+ "\n"+
                        "phoneNo "+ placeinfo.getPhoneno()+ "\n"+
                        "Website "+ placeinfo.getWebUri()+ "\n"+
                        "Price Rating "+ placeinfo.getRating()+ "\n";
                MarkerOptions options=new MarkerOptions()
                        .position(latLng)
                        .title(placeinfo.getName())
                        .snippet(snippet);
                marker=Map.addMarker(options);
            }catch (NullPointerException e){
                Log.e("error","error");
            }
        }else {
            Map.addMarker(new MarkerOptions().position(latLng));
        }

    }
    private void moveCamera(LatLng latLng, float zoom,String title) {
        Map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        MarkerOptions options=new MarkerOptions().position(latLng).title(title);
        Map.addMarker(options);

    }

    private void initmap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Toast.makeText(getApplicationContext(), "Map is Ready", Toast.LENGTH_SHORT).show();
                Map = googleMap;

                if (locationpermmission) {
                    getLocation();
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Map.setMyLocationEnabled(true);
                    Map.getUiSettings().setMyLocationButtonEnabled(false);
                   init();
                }
            }

        });
    }
    private void geolocate(){
        String searchString=search.getText().toString();
        Geocoder geocoder=new Geocoder(getApplicationContext());
        List<Address> list=new ArrayList<>();
        try{
            list=geocoder.getFromLocationName(searchString,1);
        }catch(IOException ex){
            Log.e("GeoLocate","Error locating");
        }
        if(list.size()>0){
            Address address=list.get(0);
            Log.i("locate",address.toString());
            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),15f,address.getAddressLine(0));
        }
    }
    private void getPermission() {
        String[] permission = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FineLocation) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), CoarseLocation) == PackageManager.PERMISSION_GRANTED) {
                locationpermmission = true;
                initmap();
            }
        }
        else{
            ActivityCompat.requestPermissions(this,permission,1234);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationpermmission=false;
        switch (requestCode){
            case 1234:{
                if (grantResults.length>0){
                    for(int i=0;i<grantResults.length;i++){
                        if(grantResults[i]!=PackageManager.PERMISSION_GRANTED){
                    locationpermmission=false;
                        return;}
                }
                locationpermmission=true;
                    initmap();
                }
            }
        }
    }
    private  void  getLocation(){
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        try{
            if(locationpermmission){
                Task location =fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Location current=(Location)task.getResult();
                            moveCamera(new LatLng(current.getLatitude(),current.getLongitude()),15f,"Current Location");
                        }
                        else{
                            Log.d("onComplete","Location is null");
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e("ERROR","Cannot get last location");

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private void hideKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }


    private AdapterView.OnItemClickListener itemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideKeyboard();
            final AutocompletePrediction item=adapter.getItem(position);
            final String placeid=item.getPlaceId();
            PendingResult<PlaceBuffer> placeBufferPendingResult=Places.GeoDataApi.getPlaceById(client,placeid);
            placeBufferPendingResult.setResultCallback(updatecallback);
        }

    };
    private ResultCallback<PlaceBuffer> updatecallback=new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Log.d("onResult","not Successful");
                places.release();
                return;
            }
            final Place place=places.get(0);
            try {
                mplace=new Placeinfo();
                mplace.setName(place.getName().toString());
                mplace.setAddress(place.getAddress().toString());
                mplace.setAttributions(place.getAttributions().toString());
                mplace.setId(place.getId());
                mplace.setLatLng(place.getLatLng());
                mplace.setRating(place.getRating());
                mplace.setPhoneno(place.getPhoneNumber().toString());
                mplace.setWebUri(place.getWebsiteUri());
            }catch (NullPointerException e){
                Log.e("Error","NullPointerException");
            }
            try {
                moveCamera(new LatLng(place.getViewport().getCenter().latitude, place.getViewport().getCenter().longitude), 15f, mplace);
            }
            catch (NullPointerException ex){
                Log.e("Error","Coudnt move camera");
            }
        }
    };
}