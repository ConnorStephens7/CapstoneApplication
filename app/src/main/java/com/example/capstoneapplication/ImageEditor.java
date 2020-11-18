package com.example.capstoneapplication;


import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

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

        configureImageCropperButton();
        configureImageFilterButton();
        configureImageRotateButton();
        configureImageCollageButton();
        configureImageBCSAdjustmentButton();

        }
    }
    public void configureImageCropperButton(){
        ImageButton IEButton =  findViewById(R.id.image_cropper_button);
        IEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toCropper =new Intent(ImageEditor.this, ImageCropper.class);
                toCropper.putExtra("uri", uri.toString());
                startActivity(toCropper);

            }
        });
    }

    public void configureImageFilterButton(){
        ImageButton IEButton =  findViewById(R.id.image_filter_button);
        IEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toFilter =new Intent(ImageEditor.this, ImageFilter.class);
                toFilter.putExtra("uri", uri.toString());
                startActivity(toFilter);

            }
        });
    }

    public void configureImageRotateButton(){
        ImageButton IEButton =  findViewById(R.id.image_rotate_button);
        IEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toRotate =new Intent(ImageEditor.this, ImageRotate.class);
                toRotate.putExtra("uri", uri.toString());
                startActivity(toRotate);

            }
        });
    }

    public void configureImageCollageButton(){
        ImageButton IEButton =  findViewById(R.id.image_collage_button);
        IEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toCollage =new Intent(ImageEditor.this, ImageCollage.class);
                toCollage.putExtra("uri", uri.toString());
                startActivity(toCollage);

            }
        });
    }

    public void configureImageBCSAdjustmentButton(){
        ImageButton IEButton =  findViewById(R.id.image_bcs_button);
        IEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toBCS =new Intent(ImageEditor.this, ImageBCSAdjustment.class);
                toBCS.putExtra("uri", uri.toString());
                startActivity(toBCS);

            }
        });
    }



}
