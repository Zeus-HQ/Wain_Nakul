package com.example.yasse.wain_nakul.ui.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.yasse.wain_nakul.R;

public class Splash_Screen_Activity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_activity);

        splashScreenDuration();
    }

    private void splashScreenDuration(){
        int SPLASH_DISPLAY_DURATION = 3333;
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(Splash_Screen_Activity.this, Start_Suggestions.class);
            Splash_Screen_Activity.this.startActivity(intent);
            Splash_Screen_Activity.this.finish();
        }, SPLASH_DISPLAY_DURATION);
    }

}
