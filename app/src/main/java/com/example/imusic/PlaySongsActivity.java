package com.example.imusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlaySongsActivity extends AppCompatActivity {

    private TextView textView;
    private ImageView next,play,previous;
    private MediaPlayer mediaPlayer;
    private ArrayList<File> songs;
    private SeekBar seekBar;
    int[] position;
    private Thread updateSeek;
    private MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_songs);
        textView = findViewById(R.id.songname);
        next = findViewById(R.id.next);
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        seekBar = findViewById(R.id.seekBar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList) bundle.getParcelableArrayList("songs");
        String songName = intent.getStringExtra("currentSong");
        textView.setText(songName);
        textView.setSelected(true);
        position = new int[]{intent.getIntExtra("position", 0)};

        Uri uri = Uri.parse(songs.get(position[0]).toString());
        mediaPlayer = MediaPlayer.create(this,uri);
        mediaPlayer.start();

        seekBar.setMax(mediaPlayer.getDuration());


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        updateSeek = new Thread(){
            @Override
            public void run() {
                int currentProgress = 0;
                try{
                    while(currentProgress<mediaPlayer.getDuration()) {
                        currentProgress = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentProgress);
                        sleep(800);
                    }
                }catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        updateSeek.start();


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }else {
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }

            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position[0] !=0){
                    position[0] = position[0] - 1;
                }else{
                    position[0] = songs.size() - 1;
                }
                Uri uri = Uri.parse(songs.get(position[0]).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                String song = songs.get(position[0]).getName().toString();
                textView.setText(song);
                int currentProgress = 0;
                seekBar.setProgress(currentProgress);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position[0]!=songs.size()-1){
                    position[0] = position[0] + 1;
                }else{
                    position[0] = 0;
                }
                Uri uri = Uri.parse(songs.get(position[0]).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                String song = songs.get(position[0]).getName().toString();
                textView.setText(song);
                int currentProgress = 0;
                seekBar.setProgress(currentProgress);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}