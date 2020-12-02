package com.example.capstoneapplication;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.example.capstoneapplication.Utility;

import java.io.File;


public class FileConverter extends AppCompatActivity {

    Uri uri;
    Spinner s;
    String inputPath;
    String[] ffmpegCommand;
    FFmpeg ff;
    TextView txtView;
    Utility util;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_converter);
        getSupportActionBar().setTitle("");
        util = new Utility();
        File destFolder = new File("storage/emulated/0/EditingApeOutput/FileConverter");
        if (!destFolder.exists()) {
            destFolder.mkdir();
        }


    }

    public void configureVideoFileSpinner() {
        String[] arraySpinner = new String[]{
                ".mp4", ".mov", ".mkv", ".avi"
        };
        s = (Spinner) findViewById(R.id.file_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
    }

    public void configureAudioFileSpinner() {
        String[] arraySpinner = new String[]{
                ".mp3", ".wav", ".flac"
        };
        s = (Spinner) findViewById(R.id.file_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
    }

    public void configureImageFileSpinner() {
        String[] arraySpinner = new String[]{
                ".jpg", ".png", ".tif"
        };
        s = (Spinner) findViewById(R.id.file_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
    }

    public void selectAudioFile(View v){
        Toast audioToast = Toast.makeText(getApplicationContext(),"Please choose files from your internal library", Toast. LENGTH_SHORT);
        audioToast.show();
        Intent intent;
        intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/mpeg");
        startActivityForResult(Intent.createChooser(intent, "Pick Audio"), 101);
    }


    public void selectImage(View v) {
        Intent galleryAccess = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryAccess.setType("image/*");
        startActivityForResult(galleryAccess, 102);
    }


    public void selectVideo(View v){
        Intent galleryAccess = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryAccess.setType("video/*");
        startActivityForResult(galleryAccess,100);
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data)
    {
        super.onActivityResult(reqCode,resCode,data);
        if(resCode == RESULT_OK ){

            switch (reqCode){
                case 100:
                    uri = data.getData();
                    inputPath = util.getPathFromUri(getApplicationContext(), uri);
                    configureVideoFileSpinner();
                    break;
                case 101:
                    uri = data.getData();
                    inputPath = getAudioPathFromURI(uri);
                    configureAudioFileSpinner();
                    break;
                case 102:
                    uri = data.getData();
                    inputPath = util.getPathFromUri(getApplicationContext(), uri);
                    configureImageFileSpinner();
            }
            txtView = findViewById(R.id.filepath);
            txtView.setText(inputPath);
        }
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if(menuItem.getItemId()==R.id.save){
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(com.example.capstoneapplication.FileConverter.this);

            LinearLayout linLay = new LinearLayout(com.example.capstoneapplication.FileConverter.this);
            linLay.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layPar = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layPar.setMargins(50, 0, 50, 100 );
            final EditText input = new EditText(com.example.capstoneapplication.FileConverter.this);
            input.setLayoutParams(layPar);
            input.setGravity(Gravity.TOP|Gravity.START);
            input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            linLay.addView(input,layPar);

            alertDialog.setMessage("Enter New File Name");
            alertDialog.setTitle("Converting to " + s.getSelectedItem().toString());
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
                    String fileName = input.getText().toString();
                    File destFolder = new File("storage/emulated/0/EditingApeOutput/FileConverter");
                    if (!destFolder.exists()) {
                        destFolder.mkdir();
                    }
                    String fileExtension = s.getSelectedItem().toString();
                    File destination = new File(destFolder, fileName + fileExtension);
                    if(fileExtension.equals(".avi")){
                        ffmpegCommand = new String[]{"-y", "-i", inputPath, "-b:v", "10M", destination.toString()};
                    }
                    if(fileExtension.equals(".flac")) {
                        ffmpegCommand = new String[]{"-y", "-i", inputPath, "-compression_level", "12 ", destination.toString()};
                    }
                    if(fileExtension.equals(".wav")){
                        ffmpegCommand = new String[]{"-y", "-i", inputPath, "-compression_level", "12", destination.toString()};
                    }
                    else {
                        ffmpegCommand = new String[]{"-y", "-i", inputPath, destination.toString()};
                    }

                    ff = FFmpeg.getInstance(com.example.capstoneapplication.FileConverter.this);
                    try {
                        executeCommand(ffmpegCommand);
                    } catch (FFmpegCommandAlreadyRunningException e) {
                        e.printStackTrace();
                    }

                }
            });
            alertDialog.show();
        }
        return super.onOptionsItemSelected(menuItem);
    }
    private void executeCommand(String [] ffmpegCommand) throws FFmpegCommandAlreadyRunningException {

        ff.execute(ffmpegCommand, new ExecuteBinaryResponseHandler(){

            @Override
            public void onProgress(String message){
                Log.i("FileConverter","Progress");
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
        menuInflater.inflate(R.menu.save_menu,menu);
        return true;
    }
}


