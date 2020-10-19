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
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

public class VideoCollage extends AppCompatActivity implements View.OnClickListener{
    Uri uri, videoUri;
    String[] command, videoPaths;
    VideoView videoView1, videoView2, videoView3, videoView4;
    String fileName;
    File destination, imagesInput;
    FFmpeg ff;
    int videoCount, vidViewID;
    int [] frameHistory;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_collage);
        Intent passUri = getIntent();
        if (passUri != null) {
            String videoPath = passUri.getStringExtra("uri");
            uri = Uri.parse(videoPath);
        }
        videoCount =0;
        videoPaths = new String[4];
        frameHistory = new int[4];

        videoView1 = findViewById(R.id.video1);
        videoView2 = findViewById(R.id.video2);
        videoView3 = findViewById(R.id.video3);
        videoView4 = findViewById(R.id.video4);

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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video1:
                vidViewID = 1;
                selectAVideo();
                break;
            case R.id.video2:
                vidViewID = 2;
                selectAVideo();
                break;
            case R.id.video3:
                vidViewID = 3;
                selectAVideo();
                break;
            case R.id.video4:
                vidViewID = 4;
                selectAVideo();
                break;

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if(menuItem.getItemId()==R.id.VFI_save) {
            if (videoCount == 0 | videoCount == 1) {
                final Toast toast = Toast.makeText(getApplicationContext(), "Need at least 2 videos for collage", Toast.LENGTH_SHORT);
                toast.show();
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
                        File destFolder = new File("storage/emulated/0" + "/EditingApeVideoCollages");
                        if (!destFolder.exists()) {
                            destFolder.mkdir();
                        }
                        String fileExtension = ".mp4";
                        destination = new File(destFolder, fileName + fileExtension);
                        if(videoCount ==2) {
                            command = new String[]{"-y", "-i",videoPaths[0],"-i",videoPaths[1],"-filter_complex","[0:v]scale=640:480,setsar=1[l];[1:v]scale=640:480,setsar=1[r];[l][r]hstack=shortest=1","-c:v","libx264","-crf","23","-preset","veryfast",destination.toString()};
                            try {
                                executeCommand(command);

                            } catch (FFmpegCommandAlreadyRunningException e) {
                                e.printStackTrace();
                            }
                        }
                        else if(videoCount==3) {

                        }
                        else if(videoCount ==4){

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
            videoPaths[videoCount] = getPathFromUri(getApplicationContext(),videoUri);
            if (!contains(frameHistory, vidViewID)) {
                frameHistory[videoCount] = vidViewID;
                videoCount++;//add to videoCount if an unused videoView is being set by user
                }
            }
        switch (vidViewID){
            case 1:
                videoView1.setVideoURI(videoUri);
                resumeVideoViews();
                break;
            case 2:
                videoView2.setVideoURI(videoUri);
                resumeVideoViews();
                break;
            case 3:
                videoView3.setVideoURI(videoUri);
                resumeVideoViews();
                break;
            case 4:
                videoView4.setVideoURI(videoUri);
                resumeVideoViews();
                break;

        }
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
                Log.i("VideoFromImages","Progress");
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
        menuInflater.inflate(R.menu.video_from_images_menu,menu);
        return true;
    }
}
