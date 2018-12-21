package com.example.yasse.wain_nakul.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.yasse.wain_nakul.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Restaurant_Images extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant__images_activity);

        ImageButton backButton = findViewById(R.id.images_backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        final LinearLayout linearLayout = findViewById(R.id.imagesLinearLayout);

        Intent intent = getIntent();
        ArrayList imagesURL = intent.getStringArrayListExtra("images");

        for (int i = 0; i < imagesURL.size(); i++){
            ImageView imageView = new ImageView(getApplicationContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            imageView.setLayoutParams(params);
            imageView.setPadding(8, 8, 8, 8);
            linearLayout.addView(imageView);

            Picasso.get().load(imagesURL.get(i).toString()).into(imageView);
        }
    }
}
