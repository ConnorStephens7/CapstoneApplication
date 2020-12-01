package com.example.capstoneapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

import com.aghajari.axvideotimelineview.AXTimelineViewListener;
import com.aghajari.axvideotimelineview.AXVideoTimelineView;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class VideoMerge extends AppCompatActivity {
    Uri uri, uri2;
    ImageView pauseIcon;
    Button addVideoButton;
    VideoView vidView;
    ToggleButton mergeToggle;
    String [] command;
    boolean vidPlaying = false;
    int vidDuration;
    String fileName, inputVideoPath;
    AXVideoTimelineView axVideoTimeline;
    File destination;
    FFmpeg ff;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_merge);
        getSupportActionBar().setTitle("Merge Videos");
        pauseIcon = (ImageView) findViewById(R.id.pause_icon);
        addVideoButton = (Button) findViewById(R.id.addVideoButton);
        vidView = (VideoView) findViewById(R.id.videoView);
        mergeToggle = (ToggleButton) findViewById(R.id.mergeToggle);
        axVideoTimeline = findViewById(R.id.AXVideoTimelineView2);

        Intent passUri = getIntent();
        if (passUri != null) {
            String videoPath = passUri.getStringExtra("uri");
            uri = Uri.parse(videoPath);
            vidPlaying = true;
            vidView.setVideoURI(uri);
            vidView.start();
            axVideoTimeline.setVideoPath(getPathFromUri(getApplicationContext(),uri));

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
        mergeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mergeToggle.setChecked(true);
                }
                else{
                    mergeToggle.setChecked(false);
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
                        int dur = mp.getDuration();
                        float prog = axVideoTimeline.getRightProgress();
                        float seekTo = dur * prog;
                        int time = (int) seekTo;

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
                vidDuration =mp.getDuration()/1000; //get vid time in seconds since getDuration returns ms
                mp.setLooping(true);



            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if(menuItem.getItemId()==R.id.merge){
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(com.example.capstoneapplication.VideoMerge.this);

            LinearLayout linLay = new LinearLayout(com.example.capstoneapplication.VideoMerge.this);
            linLay.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layPar = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layPar.setMargins(50, 0, 50, 100 );
            final EditText input = new EditText(com.example.capstoneapplication.VideoMerge.this);
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
                    File destFolder = new File("storage/emulated/0/EditingApeOutput/MergedVideos");
                    if (!destFolder.exists()) {
                        destFolder.mkdir();
                    }
                    String fileExtension = ".mp4";
                    destination = new File(destFolder, fileName + fileExtension);

                    try {

                        boolean ToggleButtonState = mergeToggle.isChecked();

                        if(!ToggleButtonState){
                            createInputTxtFile(getPathFromUri(getApplicationContext(), uri),getPathFromUri(getApplicationContext(),uri2));
                            executeCommand(command);
                        }
                        else if(ToggleButtonState){
                            createInputTxtFile(getPathFromUri(getApplicationContext(), uri2),getPathFromUri(getApplicationContext(),uri));
                            executeCommand(command);
                        }
                    } catch (FFmpegCommandAlreadyRunningException | IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            alertDialog.show();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void createInputTxtFile(String startUriPath, String endUriPath) throws IOException {
        File mergeInput = new File("storage/emulated/0/EditingApeOutput/MergedVideos/mergeInput.txt");
        FileWriter writer = new FileWriter(mergeInput);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        bufferedWriter.write("file " + startUriPath);
        bufferedWriter.newLine();
        bufferedWriter.write("file " + endUriPath);
        bufferedWriter.close();
        writer.close();
        command = new String []{"-y", "-f", "concat", "-safe", "0", "-i", "storage/emulated/0/EditingApeOutput/MergedVideos/mergeInput.txt", "-c", "copy", destination.toString()};

    }



    public void selectSecondVideo(View v){
        Intent galleryAccess = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryAccess.setType("video/*");
        startActivityForResult(galleryAccess,100);
    }
    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data)
    {
        super.onActivityResult(reqCode,resCode,data);
        if(resCode == RESULT_OK && reqCode == 100){
            uri2 = data.getData();
            vidView.start();
        }

    }

    private void executeCommand(String [] ffmpegCommand) throws FFmpegCommandAlreadyRunningException {
        ff= FFmpeg.getInstance(com.example.capstoneapplication.VideoMerge.this);
        ff.execute(ffmpegCommand, new ExecuteBinaryResponseHandler(){

            @Override
            public void onProgress(String message){
                Log.i("VideoMerge","Progress");
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
                setPreview();
                super.onFinish();
            }


        });

    }

    public void setPreview(){
        Uri preview = Uri.parse(destination.toString());
        vidView.setVideoURI(preview);
        vidPlaying =true;
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
        menuInflater.inflate(R.menu.merge_menu,menu);
        return true;
    }



}
