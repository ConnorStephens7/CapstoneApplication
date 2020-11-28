package com.example.capstoneapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
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
import android.widget.TextView;
import android.widget.VideoView;

import com.aghajari.axvideotimelineview.AXVideoTimelineView;
import com.aghajari.axvideotimelineview.AXTimelineViewListener;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.io.File;



public class VideoTrimmer extends AppCompatActivity {

        Uri uri;
        ImageView imgView;
        VideoView vidView;
        RangeSeekBar videoDurBar;
        TextView clockLeft, clockRight;
        AXVideoTimelineView axVideoTimeline;
        boolean vidPlaying = false;
        int vidDuration;




    String fileName, inputVideoPath;
        String[] command;
        File destination;
        FFmpeg ff;



    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_video_trimmer);

            clockLeft = (TextView) findViewById(R.id.leftClock);
            clockRight = (TextView) findViewById(R.id.rightClock);

            imgView = (ImageView) findViewById(R.id.pause_icon);
            vidView = (VideoView) findViewById(R.id.videoView);
            axVideoTimeline = findViewById(R.id.axView);
            Intent passUri = getIntent();
            if(passUri != null){

                String videoPath = passUri.getStringExtra("uri");
                uri = Uri.parse(videoPath);
                vidPlaying= true;
                vidView.setVideoURI(uri);
                vidView.start();
                axVideoTimeline.setVideoPath(getPathFromUri(getApplicationContext(),uri));


            }
        clickListeners();


        }

        private void clickListeners(){
            //click listener for the pause button
            imgView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    if(vidPlaying) {
                        imgView.setImageResource(R.drawable.icon_play);//changes icon to play button when paused
                        vidView.pause();
                        vidPlaying = false;
                    }
                    else{//if was paused, play on user click
                        vidView.start();
                        imgView.setImageResource(R.drawable.icon_pause);
                        vidPlaying= true;
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
                            clockLeft.setText(getClockValue(time/1000));

                        }

                        @Override
                        public void onRightProgressChanged(float progress) {
                            int dur = mp.getDuration();
                            float prog = axVideoTimeline.getRightProgress();
                            float seekTo = dur * prog;
                            int time = (int) seekTo;
                            clockRight.setText(getClockValue(time/1000));
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
                    clockLeft.setText("00:00:00");
                    clockRight.setText(getClockValue(vidDuration));
                    mp.setLooping(true);



                }

            });

        }

        private String getClockValue(int sec){
            int hours = sec/3600;
            int hrRem = sec % 3600;
            int minutes = hrRem/60;
            int secondsRem = hrRem %60;
            return String.format("%02d",hours) +":" + String.format("%02d", minutes) +":"+ String.format("%02d", secondsRem);
        }

        //setting up menu for saving files and changing filename
        @Override
        public boolean onOptionsItemSelected(MenuItem menuItem){
            if(menuItem.getItemId()==R.id.trim){
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(com.example.capstoneapplication.VideoTrimmer.this);

                LinearLayout linLay = new LinearLayout(com.example.capstoneapplication.VideoTrimmer.this);
                linLay.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams layPar = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layPar.setMargins(50, 0, 50, 100 );
                final EditText input = new EditText(com.example.capstoneapplication.VideoTrimmer.this);
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

                        float Lprog = axVideoTimeline.getLeftProgress();
                        float LseekTo = vidDuration * Lprog;
                        int Ltime = (int) LseekTo;

                        float Rprog = axVideoTimeline.getRightProgress();
                        float RseekTo = vidDuration * Rprog;
                        int Rtime = (int) RseekTo;


                        try {
                            snipVideo( Ltime, Rtime, fileName);
                        } catch (FFmpegCommandAlreadyRunningException e) {
                            e.printStackTrace();
                        }


                    }
                });
                alertDialog.show();
            }
            return super.onOptionsItemSelected(menuItem);
        }

        private void snipVideo( int min, int max, String fileName) throws FFmpegCommandAlreadyRunningException {

            File destFolder = new File("storage/emulated/0" + "/EditingApeSnippedVideos");
            if (!destFolder.exists()) {
                destFolder.mkdir();
            }
            String fileExtension = ".mp4";
            destination = new File(destFolder, fileName + fileExtension);
            inputVideoPath = getPathFromUri(getApplicationContext(), uri);

            //get new video duration
            vidDuration = (max - min);

            //FFmpeg command
            command = new String[]{"-ss", "" + min , "-y", "-i", inputVideoPath, "-t", "" + (max - min) , "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", destination.toString()};


            ff= FFmpeg.getInstance(com.example.capstoneapplication.VideoTrimmer.this);
            executeCommand();
        }

        private void executeCommand() throws FFmpegCommandAlreadyRunningException {

            ff.execute(command, new ExecuteBinaryResponseHandler(){

                @Override
                public void onProgress(String message){
                    Log.i("VideoTrimmer","Progress");
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
            menuInflater.inflate(R.menu.trimmer_menu,menu);
            return true;
        }
}


