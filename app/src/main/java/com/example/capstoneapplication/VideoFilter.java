package com.example.capstoneapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.io.File;

public class VideoFilter extends AppCompatActivity {

    Uri uri;
    Uri previewUri;
    ImageView imgView;
    VideoView vidView;
    String fileName;
    boolean vidPlaying;
    RangeSeekBar videoDurBar;
    String inputVideoPath;
    String inputVideoAbsolutePath;
    String filterPreviewPath = "storage/emulated/0/EditingApeFilteredVideos/preview";
    FFmpeg ff;
    int filterSelection;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_filter);

        videoDurBar = (RangeSeekBar) findViewById(R.id.scrubBar);
        imgView = (ImageView) findViewById(R.id.pause_icon);
        vidView = (VideoView) findViewById(R.id.videoView);

        File destFolder = new File("storage/emulated/0" + "/EditingApeFilteredVideos");
        if(!destFolder.exists()) {
            destFolder.mkdir();
        }

        Intent passUri = getIntent();
        configureGrayScaleButton();
        configureSepiaButton();
        configureInvertColorButton();
        if (passUri != null) {

            inputVideoPath = passUri.getStringExtra("uri");
            uri = Uri.parse(inputVideoPath);
            inputVideoAbsolutePath = getPathFromUri(getApplicationContext(),uri);
            vidPlaying = true;
            vidView.setVideoURI(uri);
            vidView.start();

        }
        clickListeners();
    }

    private void clickListeners() {
        //click listener for the pause button
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vidPlaying) {
                    imgView.setImageResource(R.drawable.icon_play);//changes icon to play button when paused
                    vidView.pause();
                    vidPlaying = false;
                } else {//if was paused, play on user click
                    vidView.start();
                    imgView.setImageResource(R.drawable.icon_pause);
                    vidPlaying = true;
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if(menuItem.getItemId()==R.id.filter){
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(com.example.capstoneapplication.VideoFilter.this);

            LinearLayout linLay = new LinearLayout(com.example.capstoneapplication.VideoFilter.this);
            linLay.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layPar = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layPar.setMargins(50, 0, 50, 100 );
            final EditText input = new EditText(com.example.capstoneapplication.VideoFilter.this);
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
                    fileName = input.getText().toString() +".mp4";
                    File sourceFile =new File("0","0");
                    switch(filterSelection){
                        case 1:
                            sourceFile= new File("storage/emulated/0/EditingApeFilteredVideos","preview1.mp4");
                            break;

                        case 2:
                            sourceFile= new File("storage/emulated/0/EditingApeFilteredVideos","preview2.mp4");
                            break;

                        case 3:
                            sourceFile= new File("storage/emulated/0/EditingApeFilteredVideos","preview3.mp4");
                            break;
                    }

                    File saved = new File("storage/emulated/0/EditingApeFilteredVideos", fileName);
                    sourceFile.renameTo(saved);
                }
            });
            alertDialog.show();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void configureSepiaButton(){
        ImageButton IEButton =  findViewById(R.id.sepia_button);
        IEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ff= FFmpeg.getInstance(com.example.capstoneapplication.VideoFilter.this);
                filterSelection = 1;
                String[] ffmpegCommand;
                ffmpegCommand = new String [] {"-y","-i", inputVideoAbsolutePath, "-filter_complex", "colorchannelmixer=.393:.769:.189:0:.349:.686:.168:0:.272:.534:.131",
                        "-threads", "4","-vcodec", "mpeg4","-r", "18", filterPreviewPath + "1.mp4"};
                try {
                    executeCommand(ffmpegCommand);
                } catch (FFmpegCommandAlreadyRunningException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    public void configureGrayScaleButton(){
        ImageButton IEButton =  findViewById(R.id.grayscale_button);
        IEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ff= FFmpeg.getInstance(com.example.capstoneapplication.VideoFilter.this);
                filterSelection = 2;
                String[] ffmpegCommand;
                ffmpegCommand = new String [] {"-y","-i", inputVideoAbsolutePath, "-threads", "4",
                        "-vf", "hue=s=0", "-vcodec", "mpeg4", "-r", "24", filterPreviewPath + "2.mp4"};
                try {
                    executeCommand(ffmpegCommand);
                } catch (FFmpegCommandAlreadyRunningException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    public void configureInvertColorButton(){
        ImageButton IEButton =  findViewById(R.id.invert_button);
        IEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ff= FFmpeg.getInstance(com.example.capstoneapplication.VideoFilter.this);
                filterSelection = 3;
                String[] ffmpegCommand;
                ffmpegCommand = new String [] {"-y","-i", inputVideoAbsolutePath, "-threads", "4",
                        "-vf", "negate", "-vcodec", "mpeg4", "-r", "24", filterPreviewPath + "3.mp4"};
                try {
                    executeCommand(ffmpegCommand);
                } catch (FFmpegCommandAlreadyRunningException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private void executeCommand(String [] ffmpegCommand) throws FFmpegCommandAlreadyRunningException {
        final Toast grayToast=Toast. makeText(getApplicationContext(),"Please wait, video filtering, preview will display when finished", Toast. LENGTH_SHORT);

        ff.execute(ffmpegCommand, new ExecuteBinaryResponseHandler(){

            @Override
            public void onProgress(String message){
                grayToast.show();
                Log.i("VideoFilter","Progress");
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
                setPreviewToVideoView();
                super.onFinish();
            }
        });

    }

    private void setPreviewToVideoView(){
        previewUri = Uri.parse(filterPreviewPath + filterSelection + ".mp4" );
        vidPlaying = true;
        vidView.setVideoURI(previewUri);
        vidView.start();
    }

    private String getPathFromUri(Context ctxt, Uri uriContent) {
        Cursor cursor = null;
        try {
            String[] project = {MediaStore.Images.Media.DATA};
            cursor = ctxt.getContentResolver().query(uriContent, project, null, null, null);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.filter_menu,menu);
        return true;
    }



}
