package com.example.capstoneapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
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

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.io.File;

public class VideoCropper extends AppCompatActivity {
    Uri uri;
    ImageView imgView;
    String duration;
    VideoView vidView;
    String fileName;
    CropImageView frame;
    File destination;
    AXVideoTimelineView axVideoTimeline;
    String [] ffmpegCommand;
    FFmpeg ff;
    MediaMetadataRetriever metaRetriever;
    Utility util;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_cropper);
        getSupportActionBar().setTitle("Video Cropper");
        axVideoTimeline = findViewById(R.id.AXVideoTimelineView4);
        Intent passUri = getIntent();
        util = new Utility();

        if (passUri != null) {
            String videoPath = passUri.getStringExtra("uri");
            uri = Uri.parse(videoPath);
            frame = findViewById(R.id.cropImageView);
            axVideoTimeline.setVideoPath(util.getPathFromUri(getApplicationContext(),uri));
            metaRetriever = new MediaMetadataRetriever();
            metaRetriever.setDataSource(getApplicationContext(),uri);
            duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            Bitmap bitmap = metaRetriever.getFrameAtTime(1);
            if(bitmap!= null){
                frame.setImageBitmap(bitmap);
            }
        }
        clickListeners();
    }
    private void clickListeners() {

        AXTimelineViewListener axTimelineViewListener = new AXTimelineViewListener() {
            @Override
            public void onLeftProgressChanged(float progress) {
                float prog = axVideoTimeline.getLeftProgress();
                float seekTo = Integer.parseInt(duration) * prog;
                long time = ((int) seekTo) *1000;
                Bitmap bitmap = metaRetriever.getFrameAtTime(time);
                if(bitmap!= null){
                    frame.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onRightProgressChanged(float progress) {

            }

            @Override
            public void onDurationChanged(long Duration) {

            }

            @Override
            public void onPlayProgressChanged(float progress) {
                float prog = axVideoTimeline.getPlayProgress();
                float seekTo = Integer.parseInt(duration) * prog;
                long time = ((int) seekTo) *1000;
                Bitmap bitmap = metaRetriever.getFrameAtTime(time);
                if(bitmap!= null){
                    frame.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onDraggingStateChanged(boolean isDragging) {

            }
        };
        axVideoTimeline.setListener(axTimelineViewListener);
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
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(com.example.capstoneapplication.VideoCropper.this);
            LinearLayout linLay = new LinearLayout(com.example.capstoneapplication.VideoCropper.this);
            linLay.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layPar = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layPar.setMargins(50, 0, 50, 100 );
            final EditText input = new EditText(com.example.capstoneapplication.VideoCropper.this);
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
        String filePath = util.getPathFromUri(getApplicationContext(),uri);
        File destFolder = new File("storage/emulated/0/EditingApeOutput/CroppedVideos");
        if (!destFolder.exists()) {
            destFolder.mkdir();
        }
        String fileExtension = ".mp4";
        destination = new File(destFolder, fileName + fileExtension);
        String cropBounds = ("crop=" + String.valueOf(width) + ":" + String.valueOf(height)+
                ":"+ String.valueOf(leftBound) + ":" + String.valueOf(topBound));
        ffmpegCommand = new String [] {"-i", filePath, "-filter:v", cropBounds, "-threads",
                "5", "-preset", "ultrafast", "-strict", "-2", "-c:a", "copy", destination.toString()};

        ff= FFmpeg.getInstance(com.example.capstoneapplication.VideoCropper.this);
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


}

