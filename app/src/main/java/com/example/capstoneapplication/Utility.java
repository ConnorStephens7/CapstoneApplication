package com.example.capstoneapplication;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

public class Utility extends AppCompatActivity {

    public String getPathFromUri(Context ctxt, Uri uriData) {
        Cursor cursor = null;
        try {
            String[] project = {MediaStore.Images.Media.DATA};
            cursor = ctxt.getContentResolver().query(uriData, project, null, null, null);
            int col_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            cursor.moveToFirst();
            return cursor.getString(col_index);
        } catch (Exception exception) {
            exception.printStackTrace();
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}
