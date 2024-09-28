package com.moutamid.radiostreamapp;

import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.material.slider.Slider;
import com.moutamid.radiostreamapp.databinding.ActivityMainBinding;

import java.net.URI;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    ActivityMainBinding binding;
    private ExoPlayer player;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                moveTaskToBack(true);
            }
        });

        binding.play.setOnClickListener(v -> {
            if (player.isPlaying()) {
                binding.playIcon.setImageResource(R.drawable.play_solid);
                player.pause();
            } else {
                binding.playIcon.setImageResource(R.drawable.pause_solid);
                player.play();
            }
        });

        binding.link.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://" + getString(R.string.website)))));
    }

    private void setupVolumeControl() {
        // Get the max volume level for STREAM_MUSIC
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        // Set the Slider's max (valueTo) and current position (value)
        binding.volume.setValueTo(maxVolume);
        binding.volume.setValue(currentVolume);

        // Set a listener on the Slider to adjust volume in real-time
        binding.volume.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(Slider slider, float value, boolean fromUser) {
                // Set the volume based on the Slider's value
                if (value == 0){
                    binding.volumeIcon.setImageResource(R.drawable.volume_xmark_solid);
                } else {
                    binding.volumeIcon.setImageResource(R.drawable.volume_high_solid);
                }
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) value, 0);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player == null) {
            player = new ExoPlayer.Builder(this).build();

            audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

            MediaItem mediaItem = MediaItem.fromUri("https://a2.asurahosting.com:6350/radio.aac");
            player.setMediaItem(mediaItem);
            player.prepare();

            player.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int playbackState) {
//                    switch (playbackState) {
//                        case Player.STATE_READY:
//                            // Player is ready to start playing
//                            Toast.makeText(MainActivity.this, "Player is ready", Toast.LENGTH_SHORT).show();
//                            break;
//                        case Player.STATE_ENDED:
//                            // Player finished playing the media
//                            Toast.makeText(MainActivity.this, "Playback finished", Toast.LENGTH_SHORT).show();
//                            break;
//                        case Player.STATE_BUFFERING:
//                            // Player is buffering
//                            Toast.makeText(MainActivity.this, "Buffering...", Toast.LENGTH_SHORT).show();
//                            break;
//                        case Player.STATE_IDLE:
//                            // Player is idle and not ready
//                            Toast.makeText(MainActivity.this, "Player is idle", Toast.LENGTH_SHORT).show();
//                            break;
//                    }
                }

                @Override
                public void onPlayerError(PlaybackException error) {
                    // Handle any errors during playback
                    Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onIsPlayingChanged(boolean isPlaying) {
//                    if (isPlaying) {
//                        // Player has started playing
//                        Toast.makeText(MainActivity.this, "Playback started", Toast.LENGTH_SHORT).show();
//                    } else {
//                        // Player is paused or stopped
//                        Toast.makeText(MainActivity.this, "Playback paused/stopped", Toast.LENGTH_SHORT).show();
//                    }
                }
            });
            setupVolumeControl();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (player != null) {
//            player.pause();
//            binding.playIcon.setImageResource(R.drawable.play_solid);
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing()) {
            if (player != null) {
                player.release();
                player = null;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}