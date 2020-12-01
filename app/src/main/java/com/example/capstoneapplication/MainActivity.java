package com.example.capstoneapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.drm.DrmStore;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    FFmpeg ffmpeg;
    Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        requestPermission();

        File destFolder = new File("storage/emulated/0/EditingApeOutput");
        if(!destFolder.exists()) {
            destFolder.mkdir();
        }

        try{
        loadFFmpegLib();
        }
        catch (FFmpegNotSupportedException e){
            e.printStackTrace();
        }
        getSupportActionBar().setTitle("");

    }




    public void configureFileConverterButton(View v){
        Intent toFC = new Intent(MainActivity.this, FileConverter.class);

        startActivity(toFC);
    }
    public void selectImage(View v){
        Intent galleryAccess = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryAccess.setType("image/*");
        startActivityForResult(galleryAccess,101);
    }





    public void selectVideo(View v){
        Intent galleryAccess = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryAccess.setType("video/*");
        startActivityForResult(galleryAccess,100);
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data)
    {
        super.onActivityResult(reqCode,resCode,data);
        if(resCode == RESULT_OK ){
            switch (reqCode){
                case 100:
                    uri = data.getData();
                    Intent toEditor = new Intent(MainActivity.this, VideoEditor.class);
                    toEditor.putExtra("uri", uri.toString());
                    startActivity(toEditor);
                    break;
                case 101:
                    uri = data.getData();
                    Intent toImageEditor = new Intent(MainActivity.this, ImageEditor.class);
                    toImageEditor.putExtra("uri", uri.toString());
                    startActivity(toImageEditor);


            }

        }
    }

    private static final int REQUEST_WRITE_PERMISSION = 786;
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.i("MainActivity","Access Granted");
        }
    }



    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        }
    }


    public void loadFFmpegLib() throws FFmpegNotSupportedException
    {
        ffmpeg = FFmpeg.getInstance(this);
        ffmpeg.loadBinary(new FFmpegLoadBinaryResponseHandler() {
            @Override
            public void onFailure() {
                Toast.makeText(getApplicationContext(), "FFmpegIntegration library didn't load", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "FFmpegIntegration library loaded", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }
        });
    }
}
