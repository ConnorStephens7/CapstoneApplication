package com.example.capstoneapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.content.Intent;

import android.net.Uri;

import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;


import java.io.File;
import java.io.IOException;

public class ImageRotate extends AppCompatActivity implements View.OnClickListener {
    Uri uri;
    ImageView imgView;
    String fileName, inputImagePath, inputVideoAbsolutePath;
    File destination;
    String [] ffmpegCommand;
    FFmpeg ff;
    RadioButton clockWButton, counterCWButton, flipVButton, flipHButton;
    RadioGroup rotateOptions;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_rotate);
        imgView =  findViewById(R.id.imageView);
        getSupportActionBar().setTitle("Image Rotation");
        Intent passUri = getIntent();
        clockWButton = findViewById(R.id.clockwise_button);
        counterCWButton = findViewById(R.id.counterclockwise_button);
        flipVButton = findViewById(R.id.flipv_button);
        flipHButton = findViewById(R.id.fliph_button);
        rotateOptions = findViewById(R.id.group);
        clockWButton.setOnClickListener(this);
        counterCWButton.setOnClickListener(this);
        flipVButton.setOnClickListener(this);
        flipHButton.setOnClickListener(this);


        if (passUri != null) {

            inputImagePath = passUri.getStringExtra("uri");
            uri = Uri.parse(inputImagePath);
            inputVideoAbsolutePath = getPathFromUri(getApplicationContext(),uri);
            imgView.setImageURI(uri);
        }
    }

    @Override
    public void onClick(View v) {
        imgView.setRotation(0);
        Matrix defaultMatrix = new Matrix();
        defaultMatrix.preScale(1.0f,1.0f);
        Bitmap defaultBitmap = null;
        try {
             defaultBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imgView.setImageBitmap(Bitmap.createBitmap(defaultBitmap, 0, 0, defaultBitmap.getWidth(), defaultBitmap.getHeight(), defaultMatrix, true));
        switch (v.getId()) {
            case R.id.clockwise_button:
                imgView.setRotation(imgView.getRotationX()+90);
                break;
            case R.id.counterclockwise_button:
                imgView.setRotation(imgView.getRotationX()-90);
                break;
            case R.id.flipv_button:
                imgView.setRotation(imgView.getRotationX()+180);
                break;
            case R.id.fliph_button:
                Matrix matrix = new Matrix();
                matrix.preScale(-1.0f, 1.0f);
                Bitmap bitmap = ((BitmapDrawable)imgView.getDrawable()).getBitmap();
                imgView.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true));
                break;


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.res_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if(menuItem.getItemId()==R.id.res){
            if(rotateOptions.getCheckedRadioButtonId() ==-1){
                final Toast noOptionSelectedWarning = Toast.makeText(getApplicationContext(), "Please select a function", Toast.LENGTH_SHORT);
                noOptionSelectedWarning.show();
            }
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(com.example.capstoneapplication.ImageRotate.this);
            LinearLayout linLay = new LinearLayout(com.example.capstoneapplication.ImageRotate.this);
            linLay.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layPar = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layPar.setMargins(50, 0, 50, 100 );
            final EditText input = new EditText(com.example.capstoneapplication.ImageRotate.this);
            input.setLayoutParams(layPar);
            input.setGravity(Gravity.TOP|Gravity.START);
            input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            linLay.addView(input,layPar);

            alertDialog.setMessage("Enter Image Name");
            alertDialog.setTitle("Change Image Name");
            alertDialog.setView(linLay);
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    fileName = input.getText().toString();

                    try
                    {
                        File destFolder = new File("storage/emulated/0/EditingApeOutput/RotatedImages");
                        if (!destFolder.exists()) {
                            destFolder.mkdir();
                        }
                        String fileExtension = ".jpg";
                        destination = new File(destFolder, fileName + fileExtension);
                        if(clockWButton.isChecked()) {
                            ffmpegCommand = new String[]{"-i", inputVideoAbsolutePath, "-vf", "transpose=1", destination.toString()};
                        }
                        if(counterCWButton.isChecked()) {
                            ffmpegCommand = new String[]{"-i", inputVideoAbsolutePath, "-vf", "transpose=2", destination.toString()};
                        }
                        if(flipVButton.isChecked()) {
                            ffmpegCommand = new String[]{"-i", inputVideoAbsolutePath, "-vf", "transpose=2,transpose=2", destination.toString()};
                        }
                        if(flipHButton.isChecked()) {
                            ffmpegCommand = new String[]{"-i", inputVideoAbsolutePath, "-vf", "hflip", "-c:a", "copy", destination.toString()};
                        }
                        ff = FFmpeg.getInstance(getApplicationContext());
                        executeCommand(ffmpegCommand);

                    } catch (FFmpegCommandAlreadyRunningException e) {
                        e.printStackTrace();
                    }
                }
            });
            alertDialog.show();
        }
        return super.onOptionsItemSelected(menuItem);
    }



    private void executeCommand(String [] ffmpegCommand) throws FFmpegCommandAlreadyRunningException {

        ff.execute(ffmpegCommand, new ExecuteBinaryResponseHandler(){

            @Override
            public void onProgress(String message){
                Log.i("ImageRotate","Progress");
            }

            @Override
            public void onSuccess(String message){
                super.onSuccess(message);
            }

            @Override
            public void onFailure(String message){
                super.onFailure(message);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });

    }

    private String getPathFromUri(Context ctxt, Uri uriData) {
        Cursor cursor = null;
        try {
            String[] project = {MediaStore.Images.Media.DATA};
            cursor = ctxt.getContentResolver().query(uriData, project, null, null, null);
            int col_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            cursor.moveToFirst();
            return cursor.getString(col_index);
        } catch (Exception exception) {
            exception.printStackTrace();
            return "";
        }
        finally{
            if (cursor!=null){
                cursor.close();
            }
        }
    }

}