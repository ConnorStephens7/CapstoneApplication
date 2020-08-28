package com.example.capstoneapplication;

import android.app.Activity;
import android.app.Service;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;


public class FFmpegIntegration extends Service {

    String [] command;
    FFmpeg ffmpeg;
    int duration;
    Calls activity;

    public MutableLiveData<Integer> percentDone;
    IBinder newBinder = new LocalBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            loadFFmpegBin();
        }
        catch (FFmpegNotSupportedException e){
            e.printStackTrace();

        }
        percentDone = new MutableLiveData<>();
    }

    private void loadFFmpegBin() throws FFmpegNotSupportedException {
        if(ffmpeg == null){
            ffmpeg = FFmpeg.getInstance(this);
        }
        ffmpeg.loadBinary(new LoadBinaryResponseHandler(){
            @Override
            public void onSuccess(){
                super.onSuccess();
            }
            @Override
            public void onFailure(){
                super.onFailure();
            }
        });

    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent!=null){
            command =intent.getStringArrayExtra("command");
            duration = Integer.parseInt(intent.getStringExtra("duration"));

            try{
                loadFFmpegBin();
                executeCommand();
            }
             catch (FFmpegNotSupportedException e) {
                e.printStackTrace();
            }
            catch (FFmpegCommandAlreadyRunningException e){
                e.printStackTrace();
            }

        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void executeCommand() throws FFmpegCommandAlreadyRunningException {
        ffmpeg.execute(command, new ExecuteBinaryResponseHandler(){
            @Override
            public void onProgress(String message){
                String arr[];
                if(message.contains("time=")){
                    arr = message.split("time=");
                    String n = arr[1];
                    String stringSplitter [] = n.split(":");
                    String []stringSplitter2 = stringSplitter[2].split(" ");
                    int timeSeconds = Integer.parseInt(stringSplitter2[0]);
                    int timeHours = Integer.parseInt(stringSplitter[0]);
                    timeHours= timeHours*3600;
                    int timeMinutes = Integer.parseInt(stringSplitter[1]);
                    timeMinutes= timeMinutes*60;

                    float totalTimeInSec = timeHours+timeMinutes+timeSeconds;

                    percentDone.setValue((int)((totalTimeInSec/duration)*100));



                }
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
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFinish() {
                percentDone.setValue(100);
            }
        });

    }

    public FFmpegIntegration(){
        super();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return newBinder;
    }

    public void registerActivity(Activity activity){
        this.activity = (Calls)activity;
    }




    public MutableLiveData<Integer>getPercent(){
        return percentDone;
    }

    public class LocalBinder extends Binder {
        public FFmpegIntegration getServiceInstance(){
            return FFmpegIntegration.this;
        }
    }



    public interface Calls{
        void updateClient(float input);
    }
}
