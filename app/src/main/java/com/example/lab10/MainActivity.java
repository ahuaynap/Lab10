package com.example.lab10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button btnStartRecording, btnStopRecording, btnPlay, btnStop;
    String pathFile = "";
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    boolean permissionToRecordAccepted = false;
    final int PERMISSIONS_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!checkPermissionFromDevice())
            requestPermission();

        btnStartRecording = (Button) findViewById(R.id.buttonStartRecording);
        btnStopRecording = (Button) findViewById(R.id.buttonStopRecording);
        btnPlay = (Button) findViewById(R.id.buttonPlay);
        btnStop = (Button) findViewById(R.id.buttonStop);


            btnStartRecording.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkPermissionFromDevice()) {
                        pathFile = getExternalCacheDir().getAbsolutePath() + "/" +
                                UUID.randomUUID().toString() + "_audio_record.3gp";
                        setupMediaRecorder();
                        try {
                            mediaRecorder.prepare();
                            mediaRecorder.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        btnPlay.setEnabled(false);
                        btnStop.setEnabled(false);
                        btnStartRecording.setEnabled(false);
                        btnStopRecording.setEnabled(true);
                        Toast.makeText(MainActivity.this, "Grabando", Toast.LENGTH_SHORT).show();
                    } else {
                        requestPermission();
                    }
                }
            });

            btnStopRecording.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaRecorder.stop();
                    btnPlay.setEnabled(true);
                    btnStop.setEnabled(false);
                    btnStartRecording.setEnabled(true);
                    btnStopRecording.setEnabled(false);
                }
            });

            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnStop.setEnabled(true);
                    btnStopRecording.setEnabled(false);
                    btnStartRecording.setEnabled(false);
                    btnPlay.setEnabled(false);

                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(pathFile);
                        mediaPlayer.prepare();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                    mediaPlayer.start();
                    Toast.makeText(MainActivity.this, "Reproduicendo grabacion", Toast.LENGTH_SHORT).show();
                }
            });

            btnStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnStopRecording.setEnabled(false);
                    btnStartRecording.setEnabled(true);
                    btnStop.setEnabled(false);
                    btnPlay.setEnabled(true);

                    if(mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        setupMediaRecorder();
                    }
                }
            });
    }

    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(pathFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, PERMISSIONS_CODE);
    }

    private boolean checkPermissionFromDevice() {
        int write_external_storage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int audio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage == PackageManager.PERMISSION_GRANTED &&
                audio == PackageManager.PERMISSION_GRANTED;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSIONS_CODE:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

}