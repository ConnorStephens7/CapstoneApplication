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

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import java.io.File;

public class ImageFilter extends AppCompatActivity {

    Uri uri;
    Uri previewUri;
    ImageView imgView;
    String inputImagePath;
    String fileName;
    String inputImageAbsolutePath;
    String filterPreviewPath = "storage/emulated/0/EditingApeFilteredImages/preview";
    String savePathPrefix = "storage/emulated/0/EditingApeFilteredImages";
    FFmpeg ff;
    int filterSelection;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_filter);
        File destFolder = new File("storage/emulated/0" + "/EditingApeFilteredImages");
        if(!destFolder.exists()) {
            destFolder.mkdir();
        }

        Intent passUri = getIntent();
        configureGrayScaleButton();
        configureSepiaButton();
        configureInvertColorButton();
        imgView = findViewById(R.id.ImageView);
        if (passUri != null) {
            inputImagePath = passUri.getStringExtra("uri");
            uri = Uri.parse(inputImagePath);
            inputImageAbsolutePath = getPathFromUri(getApplicationContext(),uri);
            imgView.setImageURI(uri);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if(menuItem.getItemId()==R.id.filter){
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(com.example.capstoneapplication.ImageFilter.this);

            LinearLayout linLay = new LinearLayout(com.example.capstoneapplication.ImageFilter.this);
            linLay.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layPar = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layPar.setMargins(50, 0, 50, 100 );
            final EditText input = new EditText(com.example.capstoneapplication.ImageFilter.this);
            input.setLayoutParams(layPar);
            input.setGravity(Gravity.TOP|Gravity.START);
            input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            linLay.addView(input,layPar);

            alertDialog.setMessage("Enter Image Name");
            alertDialog.setTitle("Change Image Name");
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
                    fileName = input.getText().toString() +".jpg";
                    File sourceFile =new File("0","0");
                    switch(filterSelection){
                        case 1:
                            sourceFile= new File(savePathPrefix,"preview1.jpg");
                            break;

                        case 2:
                            sourceFile= new File(savePathPrefix,"preview2.jpg");
                            break;

                        case 3:
                            sourceFile= new File(savePathPrefix,"preview3.jpg");
                            break;
                    }

                    File saved = new File("storage/emulated/0/EditingApeFilteredImages", fileName);
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
                ff= FFmpeg.getInstance(com.example.capstoneapplication.ImageFilter.this);
                filterSelection = 1;
                String[] ffmpegCommand;
                ffmpegCommand = new String [] {"-y","-i", inputImageAbsolutePath, "-filter_complex", "colorchannelmixer=.393:.769:.189:0:.349:.686:.168:0:.272:.534:.131",
                        "-threads", "4", filterPreviewPath + "1.jpg"};
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
                ff= FFmpeg.getInstance(com.example.capstoneapplication.ImageFilter.this);
                filterSelection = 2;
                String[] ffmpegCommand;
                ffmpegCommand = new String [] {"-y","-i", inputImageAbsolutePath, "-threads", "4",
                        "-vf", "hue=s=0", filterPreviewPath + "2.jpg"};
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
                ff= FFmpeg.getInstance(com.example.capstoneapplication.ImageFilter.this);
                filterSelection = 3;
                String[] ffmpegCommand;
                ffmpegCommand = new String [] {"-y","-i", inputImageAbsolutePath, "-threads", "4",
                        "-vf", "negate", filterPreviewPath + "3.jpg"};
                try {
                    executeCommand(ffmpegCommand);
                } catch (FFmpegCommandAlreadyRunningException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private void executeCommand(String [] ffmpegCommand) throws FFmpegCommandAlreadyRunningException {


        ff.execute(ffmpegCommand, new ExecuteBinaryResponseHandler(){

            @Override
            public void onProgress(String message){
                Log.i("ImageFilter","Progress");
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
                setPreviewToImageView();
                super.onFinish();
            }
        });

    }

    private void setPreviewToImageView(){
        previewUri = Uri.parse(filterPreviewPath + filterSelection + ".jpg" );
        imgView.setImageURI(previewUri);
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
