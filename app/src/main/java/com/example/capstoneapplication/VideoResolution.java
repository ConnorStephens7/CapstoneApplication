package com.example.capstoneapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.io.File;

public class VideoResolution extends AppCompatActivity {
    Uri uri;
    ImageView imgView;
    VideoView vidView;
    boolean vidPlaying;
    String fileName, inputVideoPath, inputVideoAbsolutePath;
    CropImageView frame;
    File destination;
    RangeSeekBar videoDurBar;
    String [] ffmpegCommand;
    FFmpeg ff;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_resolution);
        vidView = (VideoView) findViewById(R.id.videoView);
        Intent passUri = getIntent();

        if (passUri != null) {

            inputVideoPath = passUri.getStringExtra("uri");
            uri = Uri.parse(inputVideoPath);
            inputVideoAbsolutePath = getPathFromUri(getApplicationContext(),uri);
            vidPlaying = true;
            vidView.setVideoURI(uri);
            vidView.start();
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
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(com.example.capstoneapplication.VideoResolution.this);
            LinearLayout linLay = new LinearLayout(com.example.capstoneapplication.VideoResolution.this);
            linLay.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layPar = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layPar.setMargins(50, 0, 50, 100 );
            final EditText input = new EditText(com.example.capstoneapplication.VideoResolution.this);
            input.setLayoutParams(layPar);
            input.setGravity(Gravity.TOP|Gravity.START);
            input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            linLay.addView(input,layPar);

            alertDialog.setMessage("Enter Video Name");
            alertDialog.setTitle("Change Video Name");
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
                        changeVideoResolution();
                    } catch (FFmpegCommandAlreadyRunningException e) {
                        e.printStackTrace();
                    }
                }
            });
            alertDialog.show();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void changeVideoResolution() throws FFmpegCommandAlreadyRunningException{
        String filePath = getPathFromUri(getApplicationContext(),uri);
        File destFolder = new File("storage/emulated/0" + "/EditingApeResChangedVideos");
        if (!destFolder.exists()) {
            destFolder.mkdir();
        }
        String fileExtension = ".mp4";
        destination = new File(destFolder, fileName + fileExtension);
        EditText heightEntryBox = findViewById(R.id.editHeight);
        String height = heightEntryBox.getText().toString();
        EditText widthEntryBox = findViewById(R.id.editWidth);
        String width = widthEntryBox.getText().toString();
        ffmpegCommand = new String [] {"-y", "-i", filePath, "-vf", "scale="+ width + ":" + height, destination.toString()};

        ff= FFmpeg.getInstance(com.example.capstoneapplication.VideoResolution.this);
        executeCommand();
    }

    private void executeCommand() throws FFmpegCommandAlreadyRunningException {

        ff.execute(ffmpegCommand, new ExecuteBinaryResponseHandler(){

            @Override
            public void onProgress(String message){
                Log.i("VideoResolution","Progress");
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