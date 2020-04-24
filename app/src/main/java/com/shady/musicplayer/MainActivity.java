package com.shady.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MediaPlayer mMediaPlayer;
    private ImageView logo;
    private TextView rightTime, leftTime;
    private Button prev, play, next;
    private SeekBar mSeekBar;
    private Thread mThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpIU();

        mSeekBar.setMax(mMediaPlayer.getDuration());
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mMediaPlayer.seekTo(progress);
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
                int currentPos = mMediaPlayer.getCurrentPosition();
                int duration = mMediaPlayer.getDuration();

                leftTime.setText(dateFormat.format(new Date(currentPos)));
                rightTime.setText(dateFormat.format(new Date(duration - currentPos)));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void setUpIU(){
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.musik);
        logo = findViewById(R.id.logo);
        rightTime = findViewById(R.id.right_time);
        leftTime = findViewById(R.id.left_time);
        mSeekBar = findViewById(R.id.seekBar);
        prev = findViewById(R.id.back);
        play = findViewById(R.id.play);
        next = findViewById(R.id.forward);

        prev.setOnClickListener(this);
        play.setOnClickListener(this);
        next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.back:
                backMusic();
                break;

            case R.id.play:
                if(mMediaPlayer.isPlaying()){
                    pauseMusic();
                }else {
                    startMusic();
                }
                break;
            case R.id.forward:
                nextMusic();
                break;
        }
    }

    public void pauseMusic(){
        if(mMediaPlayer != null){
            mMediaPlayer.pause();
            play.setBackgroundResource(R.drawable.ic_action_play);
        }
    }

    public void startMusic(){
        if(mMediaPlayer != null){
            mMediaPlayer.start();
            updateThread();
            play.setBackgroundResource(R.drawable.ic_action_pause);
        }
    }

    public void backMusic(){
        if(mMediaPlayer.isPlaying()){
            mMediaPlayer.seekTo(0);
        }
    }

    public void nextMusic(){
        if(mMediaPlayer.isPlaying()){
            mMediaPlayer.seekTo(mMediaPlayer.getDuration()-1000);
        }
    }

    public void updateThread(){
        mThread = new Thread(){
            @Override
            public void run() {
                try {
                    while(mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                        Thread.sleep(50);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int newCurrentPostion = mMediaPlayer.getCurrentPosition();
                                int newMaxPosition = mMediaPlayer.getDuration();

                                mSeekBar.setMax(newMaxPosition);
                                mSeekBar.setProgress(newCurrentPostion);

                                leftTime.setText(String.valueOf(new SimpleDateFormat("mm:ss").
                                        format(new Date(mMediaPlayer.getCurrentPosition()))));
                                rightTime.setText(String.valueOf(new SimpleDateFormat("mm:ss").
                                        format(new Date(mMediaPlayer.getDuration()-mMediaPlayer.getCurrentPosition()))));
                            }
                        });
                    }

                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        };
        mThread.start();
    }

    @Override
    protected void onDestroy() {
        if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;

            mThread.interrupt();
            mThread = null;
        }
        super.onDestroy();
    }
}
