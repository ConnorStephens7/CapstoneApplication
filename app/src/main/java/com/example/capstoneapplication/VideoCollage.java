package com.example.capstoneapplication;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class VideoCollage extends AppCompatActivity implements View.OnClickListener{
    Uri uri, videoUri;
    String[] command, videoPaths;
    VideoView videoView1, videoView2, videoView3, videoView4;
    String fileName;
    File destination;
    FFmpeg ff;
    RadioButton hButton, vButton, sButton;
    RadioGroup collageOptions;
    int videoCount, vidViewID;
    int [] frameHistory;
    Utility util;
    Map<Integer, Integer> clickMap;
    Map<Integer,VideoView> viewMap;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_collage);
        getSupportActionBar().setTitle("Video Collage");
        Intent passUri = getIntent();
        util = new Utility();
        if (passUri != null) {
            String videoPath = passUri.getStringExtra("uri");
            uri = Uri.parse(videoPath);
        }
        videoCount = 0;
        videoPaths = new String[4];
        frameHistory = new int[4];

        videoView1 = findViewById(R.id.video1);
        videoView2 = findViewById(R.id.video2);
        videoView3 = findViewById(R.id.video3);
        videoView4 = findViewById(R.id.video4);
        hButton = findViewById(R.id.horizontal_button);
        vButton = findViewById(R.id.vertical_button);
        sButton = findViewById(R.id.square_button);
        collageOptions = findViewById(R.id.radioGroup);


        String path = "android.resource://" + getPackageName() + "/" + R.raw.add_video_symbol;
        videoView1.setVideoURI(Uri.parse(path));
        videoView1.seekTo(1);
        videoView2.setVideoURI(Uri.parse(path));
        videoView2.seekTo(1);
        videoView3.setVideoURI(Uri.parse(path));
        videoView3.seekTo(1);
        videoView4.setVideoURI(Uri.parse(path));
        videoView4.seekTo(1);

        videoView1.setOnClickListener(this);
        videoView2.setOnClickListener(this);
        videoView3.setOnClickListener(this);
        videoView4.setOnClickListener(this);

        //need 2 hash maps to maintain array of used videoViews (frameHistory)
        clickMap = new HashMap<>();
        clickMap.put(R.id.video1, 1);
        clickMap.put(R.id.video2, 2);
        clickMap.put(R.id.video3,3);
        clickMap.put(R.id.video4,4);

        viewMap = new HashMap<>();
        viewMap.put(1, videoView1);
        viewMap.put(2, videoView2);
        viewMap.put(3, videoView3);
        viewMap.put(4, videoView4);



    }

    @Override
    public void onClick(View v) {
        Integer viewIdXML = v.getId();
        try {
            vidViewID = clickMap.get(viewIdXML);
        }
        catch (java.lang.NullPointerException e){
            e.printStackTrace();
        }
        selectAVideo();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if(menuItem.getItemId()==R.id.VFI_save) {
            if (videoCount == 0 | videoCount == 1) {
                final Toast tooFewVidsWarning = Toast.makeText(getApplicationContext(), "Need at least 2 videos for collage", Toast.LENGTH_SHORT);
                tooFewVidsWarning.show();
            }
            else if(videoCount != 4 && sButton.isChecked()) {
                final Toast squareWarning = Toast.makeText(getApplicationContext(), "Need 4 videos for square collage", Toast.LENGTH_SHORT);
                squareWarning.show();
            }
            else if(collageOptions.getCheckedRadioButtonId() ==-1){
                final Toast noOptionSelectedWarning = Toast.makeText(getApplicationContext(), "Please select a collage type", Toast.LENGTH_SHORT);
                noOptionSelectedWarning.show();
            }

            else {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(com.example.capstoneapplication.VideoCollage.this);

                LinearLayout linLay = new LinearLayout(com.example.capstoneapplication.VideoCollage.this);
                linLay.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams layPar = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layPar.setMargins(50, 0, 50, 100);
                final EditText input = new EditText(com.example.capstoneapplication.VideoCollage.this);
                input.setLayoutParams(layPar);
                input.setGravity(Gravity.TOP | Gravity.START);
                input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                linLay.addView(input, layPar);

                alertDialog.setMessage("Enter New Video Name");
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
                        File destFolder = new File("storage/emulated/0/EditingApeOutput/VideoCollages");
                        if (!destFolder.exists()) {
                            destFolder.mkdir();
                        }
                        String fileExtension = ".mp4";
                        destination = new File(destFolder, fileName + fileExtension);
                        if(videoCount ==2) {
                            if(hButton.isChecked()) {
                                command = new String[]{"-y", "-i", videoPaths[0], "-i", videoPaths[1], "-filter_complex", "[0:v]scale=640:480,setsar=1[l];[1:v]scale=640:480,setsar=1[r];[l][r]hstack=shortest=1", "-c:v", "libx264", "-crf", "23", "-preset", "veryfast", "-shortest", destination.toString()};
                            }
                            if(vButton.isChecked()){
                                command = new String[]{"-y", "-i", videoPaths[0], "-i", videoPaths[1], "-filter_complex", "[0:v]scale=720:480,setsar=1[l];[1:v]scale=720:480,setsar=1[r];[l][r]vstack=shortest=1,scale=720:960", "-c:v", "libx264", "-crf", "23", "-preset", "veryfast", "-shortest", destination.toString()};
                            }
                            try {
                                executeCommand(command);

                            } catch (FFmpegCommandAlreadyRunningException e) {
                                e.printStackTrace();
                            }
                        }
                        else if(videoCount==3) {
                            if(hButton.isChecked()) {
                                command = new String[]{"-y", "-i", videoPaths[0], "-i", videoPaths[1], "-i", videoPaths[2], "-filter_complex", "[0:v]scale=640:480,setsar=1[0v];[1:v]scale=640:480,setsar=1[1v];[2:v]scale=640:480,setsar=1[2v];[0v][1v][2v]hstack=inputs=3:shortest=1,scale=1920:480", "-c:v", "libx264", "-crf", "23", "-preset", "veryfast", "-shortest", destination.toString()};
                            }
                            if(vButton.isChecked()) {
                                command = new String[]{"-y", "-i", videoPaths[0], "-i", videoPaths[1], "-i", videoPaths[2], "-filter_complex", "[0:v]scale=640:480,setsar=1[0v];[1:v]scale=640:480,setsar=1[1v];[2:v]scale=640:480,setsar=1[2v];[0v][1v][2v]vstack=inputs=3:shortest=1,scale=640:1440", "-c:v", "libx264", "-crf", "23", "-preset", "veryfast", "-shortest", destination.toString()};
                            }
                            try {
                                executeCommand(command);

                            } catch (FFmpegCommandAlreadyRunningException e) {
                                e.printStackTrace();
                            }

                        }
                        else if(videoCount ==4){
                            if(hButton.isChecked()){
                                command = new String[]{"-y", "-i", videoPaths[0], "-i", videoPaths[1], "-i", videoPaths[2], "-i", videoPaths[3], "-filter_complex", "[0:v]scale=640:480,setsar=1[0v];[1:v]scale=640:480,setsar=1[1v];[2:v]scale=640:480,setsar=1[2v];[3:v]scale=640:480,setsar=1[3v];[0v][1v][2v][3v]hstack=inputs=4:shortest=1,scale=2560:480", "-c:v", "libx264", "-crf", "23", "-preset", "veryfast", "-shortest", destination.toString()};
                            }
                            if(vButton.isChecked()){
                                command = new String[]{"-y", "-i", videoPaths[0], "-i", videoPaths[1], "-i", videoPaths[2], "-i", videoPaths[3], "-filter_complex", "[0:v]scale=640:480,setsar=1[0v];[1:v]scale=640:480,setsar=1[1v];[2:v]scale=640:480,setsar=1[2v];[3:v]scale=640:480,setsar=1[3v];[0v][1v][2v][3v]vstack=inputs=4:shortest=1,scale=640:1920", "-c:v", "libx264", "-crf", "23", "-preset", "veryfast", "-shortest", destination.toString()};
                            }
                            if(sButton.isChecked()) {
                                command = new String[]{"-y", "-i", videoPaths[0], "-i", videoPaths[1], "-i", videoPaths[2], "-i", videoPaths[3], "-filter_complex",
                                        "nullsrc=size=640x480 [base]; [0:v] setpts=PTS-STARTPTS, scale=320x240 [upperleft]; [1:v] setpts=PTS-STARTPTS, scale=320x240 [upperright]; " +
                                                "[2:v] setpts=PTS-STARTPTS, scale=320x240 [lowerleft]; [3:v] setpts=PTS-STARTPTS, scale=320x240 [lowerright]; [base][upperleft] overlay=shortest=1 [tmp1];" +
                                                " [tmp1][upperright] overlay=shortest=1:x=320 [tmp2]; [tmp2][lowerleft] overlay=shortest=1:y=240 [tmp3]; [tmp3][lowerright] overlay=shortest=1:x=320:y=240",
                                        "-c:v", "libx264", "-shortest", destination.toString()};
                            }
                            try {
                                executeCommand(command);

                            } catch (FFmpegCommandAlreadyRunningException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
                alertDialog.show();
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }



    public void selectAVideo(){
        Intent galleryAccess = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryAccess.setType("video/*");
        startActivityForResult(galleryAccess,100);
    }


    public boolean contains(final int[] array, final int value) {
        boolean result = false;
        for(int i : array) {
            if (i == value) {
                result = true;
                break;
            }
        }

        return result;
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data)
    {
        super.onActivityResult(reqCode,resCode,data);
        if(resCode == RESULT_OK && reqCode == 100){
            videoUri = data.getData();
            if (!contains(frameHistory, vidViewID)) {
                frameHistory[videoCount] = vidViewID;
                videoPaths[videoCount] = util.getPathFromUri(getApplicationContext(),videoUri);
                videoCount++;//add to videoCount if an unused videoView is being set by user
                }
            else if(contains(frameHistory, vidViewID)){
                videoPaths[vidViewID-1] = util.getPathFromUri(getApplicationContext(),videoUri);
            }
        }
        VideoView currentVideoView = viewMap.get(vidViewID);
        currentVideoView.setVideoURI(videoUri);
        resumeVideoViews();
    }

    public void resumeVideoViews(){
        videoView1.start();
        videoView2.start();
        videoView3.start();
        videoView4.start();
    }

    private void executeCommand(String [] ffmpegCommand) throws FFmpegCommandAlreadyRunningException {
        ff= FFmpeg.getInstance(com.example.capstoneapplication.VideoCollage.this);
        ff.execute(ffmpegCommand, new ExecuteBinaryResponseHandler(){

            @Override
            public void onProgress(String message){
                Log.i("VideoCollage","Progress");
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



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.video_from_images_menu,menu);
        return true;
    }
}
