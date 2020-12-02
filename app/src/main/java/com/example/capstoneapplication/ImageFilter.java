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
    String filterPreviewPath = "storage/emulated/0/EditingApeOutput/FilteredImages/preview";
    String savePathPrefix = "storage/emulated/0/EditingApeOutput/FilteredImages";
    FFmpeg ff;
    int filterSelection;
    Utility util;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_filter);
        getSupportActionBar().setTitle("Image Filter");
        File destFolder = new File("storage/emulated/0/EditingApeOutput/FilteredImages");
        util = new Utility();
        if(!destFolder.exists()) {
            destFolder.mkdir();
        }

        Intent passUri = getIntent();
        configureGrayScaleButton();
        configureSepiaButton();
        configureInvertColorButton();
        configureVignetteButton();
        configureErosionButton();
        configureSketchButton();
        configureRedButton();
        configureGreenButton();
        configureBlueButton();
        configureBlueGreenButton();
        configureRedGreenButton();
        configureRedBlueButton();
        configureRedHButton();
        configureGreenHButton();
        configureBlueHButton();
        imgView = findViewById(R.id.ImageView);
        if (passUri != null) {
            inputImagePath = passUri.getStringExtra("uri");
            uri = Uri.parse(inputImagePath);
            inputImageAbsolutePath = util.getPathFromUri(getApplicationContext(),uri);
            imgView.setImageURI(uri);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if(menuItem.getItemId()==R.id.save){
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

                        case 4:
                            sourceFile= new File(savePathPrefix,"preview4.jpg");
                            break;

                        case 5:
                            sourceFile= new File(savePathPrefix,"preview5.jpg");
                            break;

                        case 6:
                            sourceFile= new File(savePathPrefix,"preview6.jpg");
                            break;

                        case 7:
                            sourceFile= new File(savePathPrefix,"preview7.jpg");
                            break;

                        case 8:
                            sourceFile= new File(savePathPrefix,"preview8.jpg");
                            break;
                        case 9:
                            sourceFile= new File(savePathPrefix,"preview9.jpg");
                            break;
                        case 10:
                            sourceFile= new File(savePathPrefix,"preview10.jpg");
                            break;
                        case 11:
                            sourceFile= new File(savePathPrefix,"preview11.jpg");
                            break;
                        case 12:
                            sourceFile= new File(savePathPrefix,"preview12.jpg");
                            break;
                        case 13:
                            sourceFile= new File(savePathPrefix,"preview13.jpg");
                            break;
                        case 14:
                            sourceFile= new File(savePathPrefix,"preview14.jpg");
                            break;
                        case 15:
                            sourceFile= new File(savePathPrefix,"preview15.jpg");
                            break;
                    }

                    File saved = new File("storage/emulated/0/EditingApeOutput/FilteredImages", fileName);
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
                Toast sepiaToast = Toast.makeText(getApplicationContext(),"Sepia", Toast. LENGTH_SHORT);
                sepiaToast.show();
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
                Toast grayToast = Toast.makeText(getApplicationContext(),"Grayscale", Toast. LENGTH_SHORT);
                grayToast.show();
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
                Toast invertToast = Toast.makeText(getApplicationContext(),"Invert", Toast. LENGTH_SHORT);
                invertToast.show();
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

    public void configureVignetteButton(){
        ImageButton IEButton =  findViewById(R.id.vignette_button);
        IEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ff= FFmpeg.getInstance(com.example.capstoneapplication.ImageFilter.this);
                filterSelection = 4;
                Toast vignetteToast = Toast.makeText(getApplicationContext(),"Vignette", Toast. LENGTH_SHORT);
                vignetteToast.show();
                String[] ffmpegCommand;
                ffmpegCommand = new String [] {"-y","-i", inputImageAbsolutePath, "-vf",
                        "vignette=angle=PI/4", filterPreviewPath + "4.jpg"};
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
                ff= FFmpeg.getInstance(com.example.capstoneapplication.ImageFilter.this);
                filterSelection = 5;
                Toast erodeToast = Toast.makeText(getApplicationContext(),"erode", Toast. LENGTH_SHORT);
                erodeToast.show();
                String[] ffmpegCommand;
                ffmpegCommand = new String [] {"-y","-i", inputImageAbsolutePath, "-vf",
                        "erosion", filterPreviewPath + "5.jpg"};
                try {
                    executeCommand(ffmpegCommand);
                } catch (FFmpegCommandAlreadyRunningException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    public void configureSketchButton(){
        ImageButton IEButton =  findViewById(R.id.sketch_button);
        IEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ff= FFmpeg.getInstance(com.example.capstoneapplication.ImageFilter.this);
                filterSelection = 6;
                Toast sketchToast = Toast.makeText(getApplicationContext(),"Sketch", Toast. LENGTH_SHORT);
                sketchToast.show();
                String[] ffmpegCommand;
                ffmpegCommand = new String [] {"-y","-i", inputImageAbsolutePath, "-vf",
                        "edgedetect=enable='gt(mod(t,60),57)',negate", "-c:a", "copy", filterPreviewPath + "6.jpg"};
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
                ff= FFmpeg.getInstance(com.example.capstoneapplication.ImageFilter.this);
                filterSelection = 7;
                Toast redToast = Toast.makeText(getApplicationContext(),"Red Shadows", Toast. LENGTH_SHORT);
                redToast.show();
                String[] ffmpegCommand;
                ffmpegCommand = new String [] {"-y","-i", inputImageAbsolutePath, "-vf", "colorbalance=rs=.8", "-pix_fmt", "yuv420p", filterPreviewPath + "7.jpg"};
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
                ff= FFmpeg.getInstance(com.example.capstoneapplication.ImageFilter.this);
                filterSelection = 8;
                Toast greenToast = Toast.makeText(getApplicationContext(),"Green Shadows", Toast. LENGTH_SHORT);
                greenToast.show();
                String[] ffmpegCommand;
                ffmpegCommand = new String [] {"-y","-i", inputImageAbsolutePath, "-vf", "colorbalance=gs=.8", "-pix_fmt", "yuv420p", filterPreviewPath + "8.jpg"};
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
                ff= FFmpeg.getInstance(com.example.capstoneapplication.ImageFilter.this);
                filterSelection = 9;
                Toast blueToast = Toast.makeText(getApplicationContext(),"Blue Shadows", Toast. LENGTH_SHORT);
                blueToast.show();
                String[] ffmpegCommand;
                ffmpegCommand = new String [] {"-y","-i", inputImageAbsolutePath, "-vf", "colorbalance=bs=.8", "-pix_fmt", "yuv420p", filterPreviewPath + "9.jpg"};
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
                ff= FFmpeg.getInstance(com.example.capstoneapplication.ImageFilter.this);
                filterSelection = 10;
                Toast BGToast = Toast.makeText(getApplicationContext(),"Green Shadows, Blue Highlights", Toast. LENGTH_SHORT);
                BGToast.show();
                String[] ffmpegCommand;
                ffmpegCommand = new String [] {"-y","-i", inputImageAbsolutePath, "-vf", "colorbalance=gs=.8:bh=1", "-pix_fmt", "yuv420p", filterPreviewPath + "10.jpg"};
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
                ff= FFmpeg.getInstance(com.example.capstoneapplication.ImageFilter.this);
                filterSelection = 11;
                Toast RGToast = Toast.makeText(getApplicationContext(),"Red Shadows, Green Highlights", Toast. LENGTH_SHORT);
                RGToast.show();
                String[] ffmpegCommand;
                ffmpegCommand = new String [] {"-y","-i", inputImageAbsolutePath, "-vf", "colorbalance=rs=.8:gh=1", "-pix_fmt", "yuv420p", filterPreviewPath + "11.jpg"};
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
                ff= FFmpeg.getInstance(com.example.capstoneapplication.ImageFilter.this);
                filterSelection = 12;
                Toast RBToast = Toast.makeText(getApplicationContext(),"Red Shadows, Blue Highlights", Toast. LENGTH_SHORT);
                RBToast.show();
                String[] ffmpegCommand;
                ffmpegCommand = new String [] {"-y","-i", inputImageAbsolutePath, "-vf", "colorbalance=rs=.8:bh=1", "-pix_fmt", "yuv420p", filterPreviewPath + "12.jpg"};
                try {
                    executeCommand(ffmpegCommand);
                } catch (FFmpegCommandAlreadyRunningException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    public void configureRedHButton(){
        ImageButton IEButton =  findViewById(R.id.redh_button);
        IEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ff= FFmpeg.getInstance(com.example.capstoneapplication.ImageFilter.this);
                filterSelection = 13;
                Toast RHToast = Toast.makeText(getApplicationContext(),"Red Highlights", Toast. LENGTH_SHORT);
                RHToast.show();
                String[] ffmpegCommand;
                ffmpegCommand = new String [] {"-y","-i", inputImageAbsolutePath, "-vf", "colorbalance=rh=1", "-pix_fmt", "yuv420p", filterPreviewPath + "13.jpg"};
                try {
                    executeCommand(ffmpegCommand);
                } catch (FFmpegCommandAlreadyRunningException e) {
                    e.printStackTrace();
                }
            }

        });
    }



    public void configureGreenHButton(){
        ImageButton IEButton =  findViewById(R.id.greenh_button);
        IEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ff= FFmpeg.getInstance(com.example.capstoneapplication.ImageFilter.this);
                filterSelection = 14;
                Toast GHToast = Toast.makeText(getApplicationContext(),"Green Highlights", Toast. LENGTH_SHORT);
                GHToast.show();
                String[] ffmpegCommand;
                ffmpegCommand = new String [] {"-y","-i", inputImageAbsolutePath, "-vf", "colorbalance=gh=1", "-pix_fmt", "yuv420p", filterPreviewPath + "14.jpg"};
                try {
                    executeCommand(ffmpegCommand);
                } catch (FFmpegCommandAlreadyRunningException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    public void configureBlueHButton(){
        ImageButton IEButton =  findViewById(R.id.blueh_button);
        IEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ff= FFmpeg.getInstance(com.example.capstoneapplication.ImageFilter.this);
                filterSelection = 15;
                Toast BHToast = Toast.makeText(getApplicationContext(),"Blue Highlights", Toast. LENGTH_SHORT);
                BHToast.show();
                String[] ffmpegCommand;
                ffmpegCommand = new String [] {"-y","-i", inputImageAbsolutePath, "-vf", "colorbalance=bh=1", "-pix_fmt", "yuv420p", filterPreviewPath + "15.jpg"};
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save_menu,menu);
        return true;
    }



}
