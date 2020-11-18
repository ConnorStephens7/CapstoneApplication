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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import java.io.File;


public class ImageCollage extends AppCompatActivity implements View.OnClickListener{
    Uri uri, imageUri;
    String[] command, imagePaths;
    ImageView imageView1, imageView2, imageView3, imageView4;
    String fileName;
    File destination;
    FFmpeg ff;
    RadioButton hButton, vButton, sButton;
    RadioGroup collageOptions;
    int imgCount, imgViewID;
    int [] frameHistory;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_collage);
        Intent passUri = getIntent();
        if (passUri != null) {
            String imagePath = passUri.getStringExtra("uri");
            uri = Uri.parse(imagePath);
        }
        imgCount =0;
        imagePaths = new String[4];
        frameHistory = new int[4];

        imageView1 = findViewById(R.id.image1);
        imageView2 = findViewById(R.id.image2);
        imageView3 = findViewById(R.id.image3);
        imageView4 = findViewById(R.id.image4);
        hButton = findViewById(R.id.horizontal_button);
        vButton = findViewById(R.id.vertical_button);
        sButton = findViewById(R.id.square_button);
        collageOptions = findViewById(R.id.radioGroup);


        imageView1.setOnClickListener(this);
        imageView2.setOnClickListener(this);
        imageView3.setOnClickListener(this);
        imageView4.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image1:
                imgViewID = 1;
                selectAnImage();
                break;
            case R.id.image2:
                imgViewID = 2;
                selectAnImage();
                break;
            case R.id.image3:
                imgViewID = 3;
                selectAnImage();
                break;
            case R.id.image4:
                imgViewID = 4;
                selectAnImage();
                break;

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if(menuItem.getItemId()==R.id.VFI_save) {
            if (imgCount == 0 | imgCount == 1) {
                final Toast tooFewImgsWarning = Toast.makeText(getApplicationContext(), "Need at least 2 videos for collage", Toast.LENGTH_SHORT);
                tooFewImgsWarning.show();
            }
            else if(imgCount != 4 && sButton.isChecked()) {
                final Toast squareWarning = Toast.makeText(getApplicationContext(), "Need 4 videos for square collage", Toast.LENGTH_SHORT);
                squareWarning.show();
            }
            else if(collageOptions.getCheckedRadioButtonId() ==-1){
                final Toast noOptionSelectedWarning = Toast.makeText(getApplicationContext(), "Please select a collage type", Toast.LENGTH_SHORT);
                noOptionSelectedWarning.show();
            }

            else {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(com.example.capstoneapplication.ImageCollage.this);

                LinearLayout linLay = new LinearLayout(com.example.capstoneapplication.ImageCollage.this);
                linLay.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams layPar = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layPar.setMargins(50, 0, 50, 100);
                final EditText input = new EditText(com.example.capstoneapplication.ImageCollage.this);
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
                        File destFolder = new File("storage/emulated/0" + "/EditingApeImageCollages");
                        if (!destFolder.exists()) {
                            destFolder.mkdir();
                        }
                        String fileExtension = ".jpg";
                        destination = new File(destFolder, fileName + fileExtension);
                        if(imgCount ==2) {
                            if(hButton.isChecked()) {
                                command = new String[]{"-y", "-i", imagePaths[0], "-i", imagePaths[1], "-filter_complex", "[0:v]scale=640:480[l];[1:v]scale=640:480[r];[l][r]hstack[v]", "-map", "[v]", "-c:a", "copy", destination.toString()};
                            }
                            if(vButton.isChecked()){
                                command = new String[]{"-y", "-i", imagePaths[0], "-i", imagePaths[1], "-filter_complex", "[0:v]scale=640:480[l];[1:v]scale=640:480[r];[l][r]vstack[v]", "-map", "[v]", "-c:a", "copy", destination.toString()};
                            }
                            try {
                                executeCommand(command);

                            } catch (FFmpegCommandAlreadyRunningException e) {
                                e.printStackTrace();
                            }
                        }
                        else if(imgCount ==3) {
                            if(hButton.isChecked()) {
                                command = new String[]{"-y", "-i", imagePaths[0], "-i", imagePaths[1], "-i", imagePaths[2], "-filter_complex", "[0:v]scale=640:480[0v];[1:v]scale=640:480[1v];[2:v]scale=640:480[2v];[0v][1v]hstack[t];[t][2v]hstack[v]", "-map", "[v]", "-c:a", "copy", destination.toString()};
                            }
                            if(vButton.isChecked()) {
                                command = new String[]{"-y", "-i", imagePaths[0], "-i", imagePaths[1], "-i", imagePaths[2], "-filter_complex", "[0:v]scale=640:480[0v];[1:v]scale=640:480[1v];[2:v]scale=640:480[2v];[0v][1v]vstack[t];[t][2v]vstack[v]", "-map", "[v]", "-c:a", "copy", destination.toString()};
                            }
                            try {
                                executeCommand(command);

                            } catch (FFmpegCommandAlreadyRunningException e) {
                                e.printStackTrace();
                            }

                        }
                        else if(imgCount ==4){
                            if(hButton.isChecked()){
                                command = new String[]{"-y", "-i", imagePaths[0], "-i", imagePaths[1], "-i", imagePaths[2], "-i", imagePaths[3], "-filter_complex", "[0:v]scale=640:480[0v];[1:v]scale=640:480[1v];[2:v]scale=640:480[2v];[3:v]scale=640:480[3v];[0v][1v]hstack[l];[2v][3v]hstack[r];[l][r]hstack[v]","-map", "[v]", "-c:a", "copy", destination.toString()};
                            }
                            if(vButton.isChecked()){
                                command = new String[]{"-y", "-i", imagePaths[0], "-i", imagePaths[1], "-i", imagePaths[2], "-i", imagePaths[3], "-filter_complex", "[0:v]scale=640:480[0v];[1:v]scale=640:480[1v];[2:v]scale=640:480[2v];[3:v]scale=640:480[3v];[0v][1v]vstack[u];[2v][3v]vstack[l];[u][l]vstack[v]", "-map", "[v]", "-c:a", "copy", destination.toString()};
                            }
                            if(sButton.isChecked()) {
                                command = new String[]{"-y", "-i", imagePaths[0], "-i", imagePaths[1], "-i", imagePaths[2], "-i", imagePaths[3], "-filter_complex", "[0:v]scale=640:480[a];[1:v]scale=640:480[b];[2:v]scale=640:480[c];[3:v]scale=640:480[d];[a][b]hstack[u];[c][d]hstack[l];[u][l]vstack[v]",
                                "-map", "[v]", "-c:a", "copy", destination.toString()};
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



    public void selectAnImage(){
        Intent galleryAccess = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryAccess.setType("image/*");
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
            imageUri = data.getData();
            if (!contains(frameHistory, imgViewID)) {
                frameHistory[imgCount] = imgViewID;
                imagePaths[imgCount] = getPathFromUri(getApplicationContext(), imageUri);
                imgCount++;//add to imgCount if an unused imageView is being set by user
            }
            else if(contains(frameHistory, imgViewID)){
                imagePaths[imgViewID -1] = getPathFromUri(getApplicationContext(), imageUri);
            }
        }
        switch (imgViewID){
            case 1:
                imageView1.setImageURI(imageUri);
                break;
            case 2:
                imageView2.setImageURI(imageUri);
                break;
            case 3:
                imageView3.setImageURI(imageUri);
                break;
            case 4:
                imageView4.setImageURI(imageUri);
                break;

        }
    }



    private void executeCommand(String [] ffmpegCommand) throws FFmpegCommandAlreadyRunningException {
        ff= FFmpeg.getInstance(com.example.capstoneapplication.ImageCollage.this);
        ff.execute(ffmpegCommand, new ExecuteBinaryResponseHandler(){

            @Override
            public void onProgress(String message){
                Log.i("ImageCollage","Progress");
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