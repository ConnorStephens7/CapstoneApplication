package com.example.capstoneapplication;


import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.VideoView;

public class ImageEditor extends AppCompatActivity {
    Uri uri;
    ImageView imgView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_editor);

        imgView = (ImageView) findViewById(R.id.imageView);

        Intent passUri = getIntent();
        if(passUri != null){

            String imagePath = passUri.getStringExtra("uri");
            uri = Uri.parse(imagePath);
            imgView.setImageURI(uri);


        }
    }


}
