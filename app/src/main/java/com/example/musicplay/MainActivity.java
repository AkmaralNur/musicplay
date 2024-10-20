package com.example.musicplay;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private int[] tracks = {R.raw.rihanna, R.raw.imagine, R.raw.house_of_memories, R.raw.indila_tourner};
    private int[] albumCovers = {R.drawable.rihanna_diamonds, R.drawable.imaginedragons, R.drawable.house, R.drawable.indiana};
    private String[] trackTitles = {"Rihanna", "Imagine", "House of Memories", "Tourner"};
    private String[] trackArtists = {"Diamonds", "John Lennon", "Panic! At The Disco", "Indila"};
    private int currentTrackIndex = 0;
    private Handler handler = new Handler();

    private ImageView playPauseImg, previousBtn, nextBtn, albumImage;
    private TextView startTime, endTime, trackTitle, trackArtist;
    private SeekBar playerSeekBar;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playPauseImg = findViewById(R.id.playPauseImg);
        previousBtn = findViewById(R.id.previousBtn);
        nextBtn = findViewById(R.id.nextBtn);
        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);
        playerSeekBar = findViewById(R.id.pleyerSeekBar);
        albumImage = findViewById(R.id.albumImage);
        trackTitle = findViewById(R.id.trackTitle);
        trackArtist = findViewById(R.id.trackArtist);

        mediaPlayer = MediaPlayer.create(this, tracks[currentTrackIndex]);
        albumImage.setImageResource(albumCovers[currentTrackIndex]);
        trackTitle.setText(trackTitles[currentTrackIndex]);
        trackArtist.setText(trackArtists[currentTrackIndex]);
        playerSeekBar.setMax(mediaPlayer.getDuration());
        endTime.setText(formatTime(mediaPlayer.getDuration()));

        playPauseImg.setOnClickListener(view -> togglePlayPause());
        nextBtn.setOnClickListener(view -> changeTrack(1));
        previousBtn.setOnClickListener(view -> changeTrack(-1));

        playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    startTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(updateSeekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.postDelayed(updateSeekBar, 1000);
            }
        });

        handler.postDelayed(updateSeekBar, 1000);
        mediaPlayer.setOnCompletionListener(mp -> changeTrack(1));
    }

    private void togglePlayPause() {
        if (isPlaying) {
            mediaPlayer.pause();
            playPauseImg.setImageResource(R.drawable.play_icon);
        } else {
            mediaPlayer.start();
            playPauseImg.setImageResource(R.drawable.pause_ic);
            handler.postDelayed(updateSeekBar, 1000);
        }
        isPlaying = !isPlaying;
    }

    private void changeTrack(int direction) {
        mediaPlayer.stop();
        mediaPlayer.reset();
        currentTrackIndex = (currentTrackIndex + direction + tracks.length) % tracks.length;
        mediaPlayer = MediaPlayer.create(this, tracks[currentTrackIndex]);
        albumImage.setImageResource(albumCovers[currentTrackIndex]);
        trackTitle.setText(trackTitles[currentTrackIndex]);
        trackArtist.setText(trackArtists[currentTrackIndex]);
        playerSeekBar.setMax(mediaPlayer.getDuration());
        endTime.setText(formatTime(mediaPlayer.getDuration()));
        playMusic();
    }

    private void playMusic() {
        mediaPlayer.start();
        playPauseImg.setImageResource(R.drawable.pause_ic);
        handler.post(updateSeekBar);
    }

    private String formatTime(int duration) {
        int minutes = (duration / 1000) / 60;
        int seconds = (duration / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            playerSeekBar.setProgress(mediaPlayer.getCurrentPosition());
            startTime.setText(formatTime(mediaPlayer.getCurrentPosition()));
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacks(updateSeekBar);
    }
}
