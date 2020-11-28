package com.example.capstoneapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ToggleButton;
import android.widget.VideoView;

import com.aghajari.axvideotimelineview.AXTimelineViewListener;
import com.aghajari.axvideotimelineview.AXVideoTimelineView;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import java.io.File;


public class VideoAudioChange extends AppCompatActivity {
    Uri vidUri,audioUri;
    ImageView pauseIcon;
    Button addVideoButton;
    VideoView vidView;
    ToggleButton mergeToggle;
    String [] command;
    boolean vidPlaying = false;
    int vidDuration;
    String fileName, audioUriPath,audioUriPathReal;
    File destination;
    FFmpeg ff;
    AXVideoTimelineView axVideoTimeline;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_audio_change);
        pauseIcon = (ImageView) findViewById(R.id.pause_icon);
        addVideoButton = (Button) findViewById(R.id.addVideoButton);
        vidView = (VideoView) findViewById(R.id.videoView);
        axVideoTimeline = findViewById(R.id.AXVideoTimelineView6);


        Intent passUri = getIntent();
        if (passUri != null) {
            String videoPath = passUri.getStringExtra("uri");
            vidUri = Uri.parse(videoPath);
            vidPlaying = true;
            vidView.setVideoURI(vidUri);
            vidView.start();
            axVideoTimeline.setVideoPath(getPathFromUri(getApplicationContext(),vidUri));

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
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if(menuItem.getItemId()==R.id.audioChange){
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(com.example.capstoneapplication.VideoAudioChange.this);

            LinearLayout linLay = new LinearLayout(com.example.capstoneapplication.VideoAudioChange.this);
            linLay.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layPar = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layPar.setMargins(50, 0, 50, 100 );
            final EditText input = new EditText(com.example.capstoneapplication.VideoAudioChange.this);
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
                            File destFolder = new File("storage/emulated/0" + "/EditingApeAudioChangedVideos");
                            if (!destFolder.exists()) {
                                destFolder.mkdir();
                            }
                            String fileExtension = ".mp4";
                            destination = new File(destFolder, fileName + fileExtension);
                            try {
                                audioUriPath = getAudioPathFromURI(audioUri);

                                createFfmpegCommand(getPathFromUri(getApplicationContext(), vidUri), audioUriPath);
                                executeCommand(command);
                            } catch (FFmpegCommandAlreadyRunningException e) {
                                e.printStackTrace();
                            }
                        }
                    });

            alertDialog.show();
            }
        return super.onOptionsItemSelected(menuItem);
    }


    private void createFfmpegCommand(String vidUriPath, String audioUriPath)  {
        command = new String []{"-y", "-i", vidUriPath, "-i", audioUriPath, "-c:v", "copy", "-map", "0:v:0", "-map", "1:a:0", "-shortest", destination.toString()};

    }



    public void selectAudio(View v){
        Intent intent;
        intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/mpeg");
        startActivityForResult(Intent.createChooser(intent, "Pick Background Sound"), 1);
    }
    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data)
    {
        super.onActivityResult(reqCode,resCode,data);
        if(resCode == RESULT_OK && reqCode == 1){
            audioUri = data.getData();
            vidView.start();
        }

    }

    private void executeCommand(String [] ffmpegCommand) throws FFmpegCommandAlreadyRunningException {
        ff= FFmpeg.getInstance(com.example.capstoneapplication.VideoAudioChange.this);
        ff.execute(ffmpegCommand, new ExecuteBinaryResponseHandler(){

            @Override
            public void onProgress(String message){
                Log.i("VideoAudioChange","Progress");
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
    private String getAudioPathFromURI(Uri contentUri) {
        String[] media = { MediaStore.Audio.Media.DATA };
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, media, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.audio_change_menu,menu);
        return true;
    }

}
