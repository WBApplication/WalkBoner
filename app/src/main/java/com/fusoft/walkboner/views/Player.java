package com.fusoft.walkboner.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fusoft.walkboner.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.material.textview.MaterialTextView;

public class Player extends FrameLayout {

    private Context context;
    private View mRootView;
    private VideoListener listener;

    private float playbackSpeed = 1f;

    // Player
    private ExoPlayer player;
    private StyledPlayerView playerView;

    // PlayerController
    private ImageView player_state_icon, slowVideoButton, fastVideoButton;
    private MaterialTextView slowMotionText;

    public Player(@NonNull Context context) {
        super(context);
        this.context = context;
        init();
    }

    public Player(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public Player(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public void DestroyPlayer() {
        player.stop();
        player.release();
        player = null;
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.view_player, this, false);
        this.addView(mRootView);

        initializePlayerViews();
        initializePlayerControllerViews();
        setup();
    }

    private void initializePlayerViews() {
        playerView = mRootView.findViewById(R.id.player_view);
        player = new ExoPlayer.Builder(context).build();
    }

    private void initializePlayerControllerViews() {
        player_state_icon = playerView.findViewById(R.id.play_pause_image);
        slowVideoButton = playerView.findViewById(R.id.slow_video_button);
        fastVideoButton = playerView.findViewById(R.id.fast_video_button);
        slowMotionText = playerView.findViewById(R.id.slow_motion_text);
    }

    private void setup() {
        player.setVolume(0f); // Disable Audio

        player.addListener(new com.google.android.exoplayer2.Player.Listener() {
            @Override
            public void onIsLoadingChanged(boolean isLoading) {
                if (!isLoading && isListening()) {
                    listener.OnLoaded();
                    if (isListening()) listener.OnStateChanged(false);
                }
                com.google.android.exoplayer2.Player.Listener.super.onIsLoadingChanged(isLoading);
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                com.google.android.exoplayer2.Player.Listener.super.onIsPlayingChanged(isPlaying);
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                com.google.android.exoplayer2.Player.Listener.super.onPlayerError(error);
            }
        });

        player_state_icon.setOnClickListener(v -> {
            if (player.isPlaying()) {
                player_state_icon.setImageResource(de.dlyt.yanndroid.oneui.R.drawable.ic_oui_pause);
                player.pause();
            } else {
                player_state_icon.setImageResource(de.dlyt.yanndroid.oneui.R.drawable.ic_oui_play);
                player.play();
            }
        });

        // SlowMotion Setup

        slowVideoButton.setOnClickListener(v -> {
            decreasePlaybackSpeed();
            player.setPlaybackSpeed(playbackSpeed);
        });

        fastVideoButton.setOnClickListener(v -> {
            increasePlaybackSpeed();
            player.setPlaybackSpeed(playbackSpeed);
        });

        // SlowMotion Setup
    }

    private void updatePlaybackSpeedText() {
        slowMotionText.setText(playbackSpeed + "x");
    }

    private void decreasePlaybackSpeed() {
        if (playbackSpeed > 0.61) {
            playbackSpeed -= 0.20;
            updatePlaybackSpeedText();
        }
    }

    private void increasePlaybackSpeed() {
        if (playbackSpeed < 1.59) {
            playbackSpeed += 0.20;
            updatePlaybackSpeedText();
        }
    }

    public void playFromUrl(String url) {
        MediaItem mediaItem = MediaItem.fromUri(url);
        player.setPlayWhenReady(true);
        player.setRepeatMode(com.google.android.exoplayer2.Player.REPEAT_MODE_ALL);
        player.setMediaItem(mediaItem);
        playerView.setPlayer(player);
        player.prepare();
    }

    public void play() {
        if (playerView.getPlayer() != null) {
            if (isListening()) listener.OnStateChanged(true);
            playerView.getPlayer().play();
        }
    }

    public void pause() {
        if (playerView.getPlayer() != null) {
            if (isListening()) listener.OnStateChanged(false);
            playerView.getPlayer().pause();
        }
    }

    public void stop() {
        if (playerView.getPlayer() != null) {
            if (isListening()) listener.OnStateChanged(false);
            playerView.getPlayer().stop();
        }
    }

    private boolean isListening() {
        return listener != null;
    }

    public void setListener(VideoListener listener) {
        this.listener = listener;
    }

    public interface VideoListener {
        void OnLoaded();

        void OnStateChanged(boolean isPlaying);

        void OnError(String reason);
    }
}
