package com.example.yasse.wain_nakul.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.yasse.wain_nakul.R;
import com.example.yasse.wain_nakul.managers.Api;
import com.example.yasse.wain_nakul.ui.classes.HorizontalDotsProgress;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class Mapped_Suggestion extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    private static final String TAG = "Mapped_Suggestion";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    private GoogleMap mMap;
    private ImageButton backButton;
    private TextView restaurantName;
    private TextView restaurantCategoryAndRating;
    private Button anotherSuggestion;
    private Button showPictures;
    private Button openInGoogle;
    private ArrayList<String> imagesList = new ArrayList<>();
    private RequestQueue mQueue;
    private Boolean mLocationPermissionGranted = false;
    private LatLng coordinates;
    private Intent intent;
    private Marker marker;
    private HorizontalDotsProgress progressDots;

    @Override
    protected void onStart(){
        super.onStart();
        anotherSuggestion.setVisibility(View.VISIBLE);
        progressDots.setVisibility(View.GONE);
        imagesList.clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapped__suggestion);

        intent = getIntent();
        getLocationPermission();

        restaurantName = findViewById(R.id.restaurantName);
        restaurantCategoryAndRating = findViewById(R.id.restaurantCategoryAndRating);
        anotherSuggestion = findViewById(R.id.another_suggestion_button);
        showPictures = findViewById(R.id.show_pictures_button);
        openInGoogle = findViewById(R.id.open_in_google_button);
        backButton = findViewById(R.id.backButton);
        mQueue = Volley.newRequestQueue(this);
        progressDots = findViewById(R.id.mappedProgressDots);

        setListeners();
    }

    private void setListeners() {
        backButton.setOnClickListener(this);
        anotherSuggestion.setOnClickListener(this);
        showPictures.setOnClickListener(this);
        openInGoogle.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backButton:
                onBackPressed();
                break;

            case R.id.another_suggestion_button:
                if (imagesList != null) {imagesList.clear();}
                changeToLoadingMode();
                break;

            case R.id.show_pictures_button:
                showPictures();
                break;

            case R.id.open_in_google_button:
                openGoogleApp();
                break;
        }
    }

    private void changeToLoadingMode(){
        anotherSuggestion.setVisibility(View.INVISIBLE);
        progressDots.setVisibility(View.VISIBLE);
        parseJSON();
    }

    private void showPictures(){
        imagesList = intent.getStringArrayListExtra("images");
        Intent newIntent = new Intent(Mapped_Suggestion.this, Restaurant_Images.class);
        newIntent.putExtra("images", imagesList);
        startActivity(newIntent);
    }

    private void openGoogleApp(){
        String label = restaurantName.getText().toString();
        String uriStart = "geo:" + coordinates.latitude + "," + coordinates.longitude;
        String query = coordinates.latitude + "," + coordinates.longitude + "(" + label + ")";
        String encodedQuery = Uri.encode(query);
        String uriString = uriStart + "?q=" + encodedQuery + "&z=15";
        Uri uri = Uri.parse(uriString);
        Intent googleIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(googleIntent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(false);
        }
            intentAsFirstSuggestion();
    }

    private void moveCamera(LatLng latLng, String markerTitle){
        Log.d(TAG, "moveCamera: moving the camera to Lat: " + latLng.latitude + ", Lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Mapped_Suggestion.DEFAULT_ZOOM));

        if (marker != null){
            marker.remove();
        }

        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(markerTitle)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.forground));
        marker = mMap.addMarker(markerOptions);

        anotherSuggestion.setVisibility(View.VISIBLE);
        progressDots.setVisibility(View.GONE);
    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map ");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(Mapped_Suggestion.this);
        }
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permission = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
                initMap();
            } else{
                ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else{
            ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionResult: called.");
        mLocationPermissionGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if (grantResults.length > 0){
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionResult: permission failed.");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionResult: permission granted.");
                    mLocationPermissionGranted = true;
                    // Permissions are all cool.
                    initMap();
                }
            }
        }
    }

   private void parseJSON(){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Api.API_URL, null, response -> {
            try {
                String suggestionName = response.getString("name");
                restaurantName.setText(suggestionName);

                String suggestionCategory = response.getString("cat");
                String suggestionRating = response.getString("rating");
                restaurantCategoryAndRating.setText(suggestionCategory + " - " + suggestionRating + "/10");

                String suggestionLatitude = response.getString("lat");
                String suggestionLongitude = response.getString("lon");
                coordinates = new LatLng(Double.parseDouble(suggestionLatitude), Double.parseDouble(suggestionLongitude));

                JSONArray suggestionImages = response.getJSONArray("image");
                for (int i = 0; i < suggestionImages.length(); i++){
                    String image = suggestionImages.getString(i);
                    imagesList.add(image);
                }
                moveCamera(coordinates, suggestionName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {

        });
        mQueue.add(request);
   }

    private void intentAsFirstSuggestion(){
        String name = intent.getStringExtra("name");
        restaurantName.setText(name);

        String category = intent.getStringExtra("category");
        String rating = intent.getStringExtra("rating");
        restaurantCategoryAndRating.setText(category + " - " + rating + "/10");

        String latitude = intent.getStringExtra("lat");
        String longitude = intent.getStringExtra("lon");
        coordinates = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

        imagesList = intent.getStringArrayListExtra("images");

        moveCamera(coordinates, name);
    }
}
