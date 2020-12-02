package com.example.capstoneapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.MediaPlayer;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.aghajari.axvideotimelineview.AXTimelineViewListener;
import com.aghajari.axvideotimelineview.AXVideoTimelineView;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

public class VideoResolution extends AppCompatActivity {
    Uri uri;
    ImageView imgView;
    VideoView vidView;
    boolean vidPlaying;
    String fileName, inputVideoPath, inputVideoAbsolutePath;
    File destination;
    ImageView pauseIcon;
    String [] ffmpegCommand;
    AXVideoTimelineView axVideoTimeline;
    FFmpeg ff;
    Utility util;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_resolution);
        getSupportActionBar().setTitle("Video Resolution Adjustment");
        vidView = (VideoView) findViewById(R.id.videoView);
        pauseIcon = findViewById(R.id.pause_icon);
        axVideoTimeline = findViewById(R.id.AXVideoTimelineView3);
        Intent passUri = getIntent();
        util = new Utility();

        if (passUri != null) {

            inputVideoPath = passUri.getStringExtra("uri");
            uri = Uri.parse(inputVideoPath);
            inputVideoAbsolutePath = util.getPathFromUri(getApplicationContext(),uri);
            vidPlaying = true;
            vidView.setVideoURI(uri);
            vidView.start();
            axVideoTimeline.setVideoPath(util.getPathFromUri(getApplicationContext(),uri));
        }
        clickListeners();
    }
    private void clickListeners() {
        //click listener for the pause button
        pauseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vidPlaying) {
                    pauseIcon.setImageResource(R.drawable.icon_play);//changes icon to play button when paused
                    vidView.pause();

                    vidPlaying = false;
                } else {//if was paused, play on user click
                    vidView.start();
                    pauseIcon.setImageResource(R.drawable.icon_pause);
                    vidPlaying = true;
                }
            }
        });
        vidView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {
                vidView.start();
                AXTimelineViewListener axTimelineViewListener = new AXTimelineViewListener() {
                    @Override
                    public void onLeftProgressChanged(float progress) {
                        int dur = mp.getDuration();
                        float prog = axVideoTimeline.getLeftProgress();
                        float seekTo = dur * prog;
                        int time = (int) seekTo;
                        vidView.seekTo(time);
                    }

                    @Override
                    public void onRightProgressChanged(float progress) {

                    }

                    @Override
                    public void onDurationChanged(long Duration) {

                    }

                    @Override
                    public void onPlayProgressChanged(float progress) {
                        int dur = mp.getDuration();
                        float prog = axVideoTimeline.getPlayProgress();
                        float seekTo = dur * prog;
                        int time = (int) seekTo;
                        vidView.seekTo(time);
                    }

                    @Override
                    public void onDraggingStateChanged(boolean isDragging) {

                    }
                };
                axVideoTimeline.setListener(axTimelineViewListener);
                mp.setLooping(true);



            }

        });
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
        String filePath = util.getPathFromUri(getApplicationContext(),uri);
        File destFolder = new File("storage/emulated/0/EditingApeOutput/ResChangedVideos/");
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



}