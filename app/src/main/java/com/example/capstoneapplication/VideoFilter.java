package com.example.capstoneapplication;


import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
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

import com.aghajari.axvideotimelineview.AXTimelineViewListener;
import com.aghajari.axvideotimelineview.AXVideoTimelineView;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import java.io.File;

public class VideoFilter extends AppCompatActivity {

    Uri uri;
    Uri previewUri;
    ImageView imgView;
    VideoView vidView;
    String fileName;
    boolean vidPlaying;
    AXVideoTimelineView axVideoTimeline;
    String inputVideoPath;
    String inputVideoAbsolutePath;
    String filterPreviewPath = "storage/emulated/0/EditingApeOutput/FilteredVideos/preview";
    String savePathPrefix = "storage/emulated/0/EditingApeOutput/FilteredVideos";
    FFmpeg ff;
    int filterSelection;
    Utility util;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_filter);
        getSupportActionBar().setTitle("Video Filter");
        imgView = (ImageView) findViewById(R.id.pause_icon);
        vidView = (VideoView) findViewById(R.id.videoView);
        axVideoTimeline = findViewById(R.id.AXVideoTimelineView);
        util = new Utility();

        File destFolder = new File("storage/emulated/0/EditingApeOutput/FilteredVideos");
        if(!destFolder.exists()) {
            destFolder.mkdir();
        }

        Intent passUri = getIntent();
        configureGrayScaleButton();
        configureSepiaButton();
        configureInvertColorButton();
        configureVignetteButton();
        configureErosionButton();
        configureRedButton();
        configureGreenButton();
        configureBlueButton();
        configureBlueGreenButton();
        configureRedGreenButton();
        configureRedBlueButton();
        configureFastButton();
        configureSlowButton();

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
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if(menuItem.getItemId()==R.id.save){
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
                            sourceFile= new File(savePathPrefix,"preview1.mp4");
                            break;

                        case 2:
                            sourceFile= new File(savePathPrefix,"preview2.mp4");
                            break;

                        case 3:
                            sourceFile= new File(savePathPrefix,"preview3.mp4");
                            break;

                        case 4:
                            sourceFile= new File(savePathPrefix,"preview4.mp4");
                            break;

                        case 5:
                            sourceFile= new File(savePathPrefix,"preview5.mp4");
                            break;

                        case 6:
                            sourceFile= new File(savePathPrefix,"preview6.mp4");
                            break;

                        case 7:
                            sourceFile= new File(savePathPrefix,"preview7.mp4");
                            break;

                        case 8:
                            sourceFile= new File(savePathPrefix,"preview8.mp4");
                            break;
                        case 9:
                            sourceFile= new File(savePathPrefix,"preview9.mp4");
                            break;
                        case 10:
                            sourceFile= new File(savePathPrefix,"preview10.mp4");
                            break;
                        case 11:
                            sourceFile= new File(savePathPrefix,"preview11.mp4");
                            break;
                        case 12:
                            sourceFile= new File(savePathPrefix,"preview12.mp4");
                            break;

                        case 13:
                            sourceFile= new File(savePathPrefix,"preview13.mp4");
                            break;

                        case 14:
                            sourceFile= new File(savePathPrefix,"preview14.mp4");
                            break;


                    }

                    File saved = new File("storage/emulated/0//EditingApeOutput/FilteredVideos/", fileName);
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
                Toast sepiaToast = Toast.makeText(getApplicationContext(),"Sepia", Toast. LENGTH_SHORT);
                sepiaToast.show();
                String[] ffmpegCommand;
                ffmpegCommand = new String [] {"-y","-i", inputVideoAbsolutePath, "-filter_complex",
                        "colorchannelmixer=.393:.769:.189:0:.349:.686:.168:0:.272:.534:.131",
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
                Toast grayToast = Toast.makeText(getApplicationContext(),"Grayscale", Toast. LENGTH_SHORT);
                grayToast.show();
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
        ImageButton IEButton =  findViewById(R.id.negate_button);
        IEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ff= FFmpeg.getInstance(com.example.capstoneapplication.VideoFilter.this);
                filterSelection = 3;
                Toast invertToast = Toast.makeText(getApplicationContext(),"Invert", Toast. LENGTH_SHORT);
                invertToast.show();
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

    public void configureVignetteButton(){
        ImageButton IEButton =  findViewById(R.id.vignette_button);
        IEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ff= FFmpeg.getInstance(com.example.capstoneapplication.VideoFilter.this);
                filterSelection = 4;
                Toast vignetteToast = Toast.makeText(getApplicationContext(),"Vignette", Toast. LENGTH_SHORT);
                vignetteToast.show();
                String[] ffmpegCommand;
                ffmpegCommand = new String [] {"-y","-i", inputVideoAbsolutePath, "-vf",
                        "vignette=angle=PI/4", filterPreviewPath + "4.mp4"};
                try {
                    executeCommand(ffmpegCommand);
                } catch (FFmpegCommandAlreadyRunningException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    public void configureErosionButton(){
        ImageButton IEButton =  findViewById(R.id.erosion_button);
        IEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ff= FFmpeg.getInstance(com.example.capstoneapplication.VideoFilter.this);
                filterSelection = 5;
                Toast erodeToast = Toast.makeText(getApplicationContext(),"erode", Toast. LENGTH_SHORT);
                erodeToast.show();
                String[] ffmpegCommand;
                ffmpegCommand = new String [] {"-y","-i", inputVideoAbsolutePath, "-vf",
                        "erosion", filterPreviewPath + "5.mp4"};
                try {
                    executeCommand(ffmpegCommand);
                } catch (FFmpegCommandAlreadyRunningException e) {
                    e.printStackTrace();
                }
            }

        });
    }


    public void configureRedButton(){
        ImageButton IEButton =  findViewById(R.id.red_button);
        IEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ff= FFmpeg.getInstance(com.example.capstoneapplication.VideoFilter.this);
                filterSelection = 7;
                Toast redToast = Toast.makeText(getApplicationContext(),"Red Shadows", Toast. LENGTH_SHORT);
                redToast.show();
                String[] ffmpegCommand;
                ffmpegCommand = new String [] {"-y","-i", inputVideoAbsolutePath, "-vf", "colorbalance=rs=.8", "-pix_fmt", "yuv420p", filterPreviewPath + "7.mp4"};
                try {
                    executeCommand(ffmpegCommand);
                } catch (FFmpegCommandAlreadyRunningException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    public void configureGreenButton(){
        ImageButton IEButton =  findViewById(R.id.green_button);
        IEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ff= FFmpeg.getInstance(com.example.capstoneapplication.VideoFilter.this);
                filterSelection = 8;
                Toast greenToast = Toast.makeText(getApplicationContext(),"Green Shadows", Toast. LENGTH_SHORT);
                greenToast.show();
                String[] ffmpegCommand;
                ffmpegCommand = new String [] {"-y","-i", inputVideoAbsolutePath, "-vf", "colorbalance=gs=.8", "-pix_fmt", "yuv420p", filterPreviewPath + "8.mp4"};
                try {
                    executeCommand(ffmpegCommand);
                } catch (FFmpegCommandAlreadyRunningException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    public void configureBlueButton(){
        ImageButton IEButton =  findViewById(R.id.blue_button);
        IEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ff= FFmpeg.getInstance(com.example.capstoneapplication.VideoFilter.this);
                filterSelection = 9;
                Toast blueToast = Toast.makeText(getApplicationContext(),"Blue Shadows", Toast. LENGTH_SHORT);
                blueToast.show();
                String[] ffmpegCommand;
                ffmpegCommand = new String [] {"-y","-i", inputVideoAbsolutePath, "-vf", "colorbalance=bs=.8", "-pix_fmt", "yuv420p", filterPreviewPath + "9.mp4"};
                try {
                    executeCommand(ffmpegCommand);
                } catch (FFmpegCommandAlreadyRunningException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    public void configureBlueGreenButton(){
        ImageButton IEButton =  findViewById(R.id.bluegreen_button);
        IEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ff= FFmpeg.getInstance(com.example.capstoneapplication.VideoFilter.this);
                filterSelection = 10;
                Toast BGToast = Toast.makeText(getApplicationContext(),"Green Shadows, Blue Highlights", Toast. LENGTH_SHORT);
                BGToast.show();
                String[] ffmpegCommand;
                ffmpegCommand = new String [] {"-y","-i", inputVideoAbsolutePath, "-vf", "colorbalance=gs=.8:bh=1", "-pix_fmt", "yuv420p", filterPreviewPath + "10.mp4"};
                try {
                    executeCommand(ffmpegCommand);
                } catch (FFmpegCommandAlreadyRunningException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    public void configureRedGreenButton(){
        ImageButton IEButton =  findViewById(R.id.redgreen_button);
        IEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ff= FFmpeg.getInstance(com.example.capstoneapplication.VideoFilter.this);
                filterSelection = 11;
                Toast RGToast = Toast.makeText(getApplicationContext(),"Red Shadows, Green Highlights", Toast. LENGTH_SHORT);
                RGToast.show();
                String[] ffmpegCommand;
                ffmpegCommand = new String [] {"-y","-i", inputVideoAbsolutePath, "-vf", "colorbalance=rs=.8:gh=1", "-pix_fmt", "yuv420p", filterPreviewPath + "11.mp4"};
                try {
                    executeCommand(ffmpegCommand);
                } catch (FFmpegCommandAlreadyRunningException e) {
                    e.printStackTrace();
                }
            }

        });
    }


    public void configureRedBlueButton(){
        ImageButton IEButton =  findViewById(R.id.redblue_button);
        IEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ff= FFmpeg.getInstance(com.example.capstoneapplication.VideoFilter.this);
                filterSelection = 12;
                Toast RBToast = Toast.makeText(getApplicationContext(),"Red Shadows, Blue Highlights", Toast. LENGTH_SHORT);
                RBToast.show();
                String[] ffmpegCommand;
                ffmpegCommand = new String [] {"-y","-i", inputVideoAbsolutePath, "-vf", "colorbalance=rs=.8:bh=1", "-pix_fmt", "yuv420p", filterPreviewPath + "12.mp4"};
                try {
                    executeCommand(ffmpegCommand);
                } catch (FFmpegCommandAlreadyRunningException e) {
                    e.printStackTrace();
                }
            }

        });
    }


    public void configureFastButton(){
        ImageButton IEButton =  findViewById(R.id.fastmo_button);
        IEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ff= FFmpeg.getInstance(com.example.capstoneapplication.VideoFilter.this);
                filterSelection = 13;
                Toast FastToast = Toast.makeText(getApplicationContext(),"Speed up", Toast. LENGTH_SHORT);
                FastToast.show();
                String[] ffmpegCommand;
                ffmpegCommand = new String [] {"-y","-i", inputVideoAbsolutePath, "-filter:v", "setpts=0.5*PTS", filterPreviewPath + "16.mp4"};
                try {
                    executeCommand(ffmpegCommand);
                } catch (FFmpegCommandAlreadyRunningException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    public void configureSlowButton(){
        ImageButton IEButton =  findViewById(R.id.slowmo_button);
        IEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ff= FFmpeg.getInstance(com.example.capstoneapplication.VideoFilter.this);
                filterSelection = 14;
                Toast SlowToast = Toast.makeText(getApplicationContext(),"Slow Down", Toast. LENGTH_SHORT);
                SlowToast.show();
                String[] ffmpegCommand;
                ffmpegCommand = new String [] {"-y","-i", inputVideoAbsolutePath, "-filter:v", "setpts=2*PTS", filterPreviewPath + "17.mp4"};
                try {
                    executeCommand(ffmpegCommand);
                } catch (FFmpegCommandAlreadyRunningException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private void executeCommand(String [] ffmpegCommand) throws FFmpegCommandAlreadyRunningException {
        final Toast grayToast=Toast.makeText(getApplicationContext(),"Please wait, video filtering, preview will display when finished", Toast. LENGTH_SHORT);

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save_menu,menu);
        return true;
    }



}
