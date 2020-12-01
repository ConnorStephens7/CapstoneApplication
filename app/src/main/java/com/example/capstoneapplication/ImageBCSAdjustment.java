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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import java.io.File;


public class ImageBCSAdjustment extends AppCompatActivity{
    Uri uri;
    String[] command;
    ImageView imageView;
    String fileName;
    String inputVideoAbsolutePath;
    File destination;
    FFmpeg ff;
    RadioButton bButton, cButton, sButton;
    RadioGroup BCSOptions;
    SeekBar adjustmentLevelBar;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_bcs_adjustment);
        getSupportActionBar().setTitle("Image BCS Adjustment");
        Intent passUri = getIntent();
        if (passUri != null) {
            String imagePath = passUri.getStringExtra("uri");
            uri = Uri.parse(imagePath);
            imageView = findViewById(R.id.imageView);
            imageView.setImageURI(uri);
        }
        bButton = findViewById(R.id.brightness_button);
        cButton = findViewById(R.id.contrast_button);
        sButton = findViewById(R.id.saturation_button);
        BCSOptions = findViewById(R.id.radioGroup);
        adjustmentLevelBar = findViewById(R.id.seekBar);
        adjustmentLevelBar.setProgress(50);
        inputVideoAbsolutePath = getPathFromUri(getApplicationContext(), uri);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if(menuItem.getItemId()==R.id.VFI_save) {
            if(BCSOptions.getCheckedRadioButtonId() ==-1){
                final Toast noOptionSelectedWarning = Toast.makeText(getApplicationContext(), "Please select an option", Toast.LENGTH_SHORT);
                noOptionSelectedWarning.show();
            }

            else {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(com.example.capstoneapplication.ImageBCSAdjustment.this);

                LinearLayout linLay = new LinearLayout(com.example.capstoneapplication.ImageBCSAdjustment.this);
                linLay.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams layPar = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layPar.setMargins(50, 0, 50, 100);
                final EditText input = new EditText(com.example.capstoneapplication.ImageBCSAdjustment.this);
                input.setLayoutParams(layPar);
                input.setGravity(Gravity.TOP | Gravity.START);
                input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                linLay.addView(input, layPar);

                alertDialog.setMessage("Enter New Image Name");
                alertDialog.setTitle("New Image Name");
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
                        File destFolder = new File("storage/emulated/0/EditingApeOutput/ImageBCSAdjusted/");
                        if (!destFolder.exists()) {
                            destFolder.mkdir();
                        }
                        String fileExtension = ".jpg";
                        destination = new File(destFolder, fileName + fileExtension);
                        float adjValue = (float)(adjustmentLevelBar.getProgress()-50)/25;
                        if(bButton.isChecked()){
                            command = new String[]{"-y", "-i", inputVideoAbsolutePath, "-vf", "eq=brightness=" + adjValue, "-c:a", "copy", destination.toString()};
                        }
                        if(cButton.isChecked()){
                                command = new String[]{"-y", "-i", inputVideoAbsolutePath, "-vf", "eq=contrast=" + adjValue, "-c:a", "copy", destination.toString()};
                            }
                        if(sButton.isChecked()) {
                            command = new String[]{"-y", "-i", inputVideoAbsolutePath, "-vf", "eq=saturation=" + adjValue, "-c:a", "copy", destination.toString()};
                        }
                        try {
                            executeCommand(command);

                        } catch (FFmpegCommandAlreadyRunningException e) {
                            e.printStackTrace();
                        }
                    }
                });
                alertDialog.show();
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }





    private void executeCommand(String [] ffmpegCommand) throws FFmpegCommandAlreadyRunningException {
        ff= FFmpeg.getInstance(com.example.capstoneapplication.ImageBCSAdjustment.this);
        ff.execute(ffmpegCommand, new ExecuteBinaryResponseHandler(){

            @Override
            public void onProgress(String message){
                Log.i("ImageBCSAdjustment","Progress");
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