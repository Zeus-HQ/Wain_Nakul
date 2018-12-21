package com.example.yasse.wain_nakul.ui.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.yasse.wain_nakul.R;
import com.example.yasse.wain_nakul.managers.Api;
import com.example.yasse.wain_nakul.ui.classes.HorizontalDotsProgress;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class Start_Suggestions extends AppCompatActivity{
    private static final String TAG = "Start_Suggestions";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private HorizontalDotsProgress progressDots;
    public Button suggestButton;
    private RequestQueue mQueue;
    private ArrayList imagesList = new ArrayList();
    private Boolean mLocationPermissionGranted = false;

    @Override
    protected void onStart(){
        super.onStart();
        suggestButton.setVisibility(View.VISIBLE);
        progressDots.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_suggestions);

        progressDots = findViewById(R.id.progressDots);
        suggestButton = findViewById(R.id.suggest_button);
        mQueue = Volley.newRequestQueue(this);

        if (isServicesOK()){
            getLocationPermission();
            if (mLocationPermissionGranted) {
                suggestButton.setOnClickListener(v -> changeToLoadingMode());
            }
        }
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permission = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
            } else{
                ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else{
            ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
            mLocationPermissionGranted = true;
        }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(Start_Suggestions.this);

        if (available == ConnectionResult.SUCCESS){
            // User can make map requests, all gucci
            Log.d(TAG, "isServicesOK: Google Play Services is working.");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            // The error occurred can be resolvable.
            Log.d(TAG, "isServicesOK: an error occurred, but we can fix it.");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(Start_Suggestions.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else{
            Toast.makeText(this, "You cannot make map requests.", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    public void changeToLoadingMode(){
        suggestButton.setVisibility(View.INVISIBLE);
        progressDots.setVisibility(View.VISIBLE);

        parseFirstJSONCall();
    }

    private void parseFirstJSONCall(){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Api.API_URL, null, response -> {
            try {
                String suggestionName = response.getString("name");
                String suggestionCategory = response.getString("cat");
                String suggestionRating = response.getString("rating");
                String suggestionLatitude = response.getString("lat");
                String suggestionLongitude = response.getString("lon");
                JSONArray suggestionImages = response.getJSONArray("image");

                for (int i = 0; i < suggestionImages.length(); i++){
                    String image = suggestionImages.getString(i);
                    imagesList.add(image);
                }

                createIntent(suggestionName, suggestionCategory, suggestionRating,
                        suggestionLatitude, suggestionLongitude, imagesList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {

        });
        mQueue.add(request);
    }

    private void createIntent(String name, String category, String rating, String lat, String lon, ArrayList images){
        Intent intent = new Intent(Start_Suggestions.this, Mapped_Suggestion.class);
        intent.putExtra("name", name);
        intent.putExtra("category", category);
        intent.putExtra("rating", rating);
        intent.putExtra("lat", lat);
        intent.putExtra("lon", lon);
        intent.putExtra("images", images);

        startActivity(intent);
    }
}
