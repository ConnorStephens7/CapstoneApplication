package com.example.capstoneapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
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
import android.widget.EditText;
import android.widget.LinearLayout;


import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;

public class ImageCropper extends AppCompatActivity {
    Uri uri;
    String fileName;
    CropImageView frame;
    File destination;
    String [] ffmpegCommand;
    FFmpeg ff;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_cropper);
        Intent passUri = getIntent();

        if (passUri != null) {
            String imagePath = passUri.getStringExtra("uri");
            uri = Uri.parse(imagePath);
            frame = findViewById(R.id.cropImageView);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(bitmap!= null){
                frame.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.cropper_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if(menuItem.getItemId()==R.id.crop){
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(com.example.capstoneapplication.ImageCropper.this);
            LinearLayout linLay = new LinearLayout(com.example.capstoneapplication.ImageCropper.this);
            linLay.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layPar = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layPar.setMargins(50, 0, 50, 100 );
            final EditText input = new EditText(com.example.capstoneapplication.ImageCropper.this);
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
                        cropVideo();
                    } catch (FFmpegCommandAlreadyRunningException e) {
                        e.printStackTrace();
                    }
                }
            });
            alertDialog.show();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void cropVideo() throws FFmpegCommandAlreadyRunningException{
        Rect cropShape = frame.getCropRect();
        int width = Math.abs(cropShape.left - cropShape.right);
        int height = Math.abs(cropShape.top - cropShape.bottom);
        int leftBound = cropShape.left;
        int topBound = cropShape.top;
        String filePath = getPathFromUri(getApplicationContext(),uri);
        File destFolder = new File("storage/emulated/0" + "/EditingApeCroppedImages");
        if (!destFolder.exists()) {
            destFolder.mkdir();
        }
        String fileExtension = ".jpg";
        destination = new File(destFolder, fileName + fileExtension);
        String cropBounds = ("crop=" + String.valueOf(width) + ":" + String.valueOf(height)+
                ":"+ String.valueOf(leftBound) + ":" + String.valueOf(topBound));
        ffmpegCommand = new String [] {"-i", filePath, "-vf", cropBounds, "-threads",
                "4", "-preset", "ultrafast", "-strict", "-2", "-c:a", "copy", destination.toString()};

        ff= FFmpeg.getInstance(com.example.capstoneapplication.ImageCropper.this);
        executeCommand();
    }

    private void executeCommand() throws FFmpegCommandAlreadyRunningException {

        ff.execute(ffmpegCommand, new ExecuteBinaryResponseHandler(){

            @Override
            public void onProgress(String message){
                Log.i("VideoCropper","Progress");
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
