package com.moutamid.radiostreamapp;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
            if (isServiceRunning(RadioForegroundService.class)) {
                binding.playIcon.setImageResource(R.drawable.play_solid);
                stopService(new Intent(this, RadioForegroundService.class).setAction(RadioForegroundService.ACTION_STOP));
            } else {
                binding.playIcon.setImageResource(R.drawable.pause_solid);
                ContextCompat.startForegroundService(this, new Intent(this, RadioForegroundService.class).setAction(RadioForegroundService.ACTION_PLAY));
            }
        });

        binding.link.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://" + getString(R.string.website)))));
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
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
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        setupVolumeControl();
        if (isServiceRunning(RadioForegroundService.class)) {
            binding.playIcon.setImageResource(R.drawable.pause_solid);
        } else {
            binding.playIcon.setImageResource(R.drawable.play_solid);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}