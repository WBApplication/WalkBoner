package com.fusoft.walkboner.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fusoft.walkboner.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.ui.StyledPlayerView;

public class Player extends FrameLayout {

    private Context context;

    private ExoPlayer player;
    private StyledPlayerView playerView;

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

    private void init() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mRootView = inflater.inflate(R.layout.view_player, this, false);
        this.addView(mRootView);
        playerView = (StyledPlayerView) mRootView.findViewById(R.id.player_view);
        player = new ExoPlayer.Builder(context).build();

        player.addListener(new com.google.android.exoplayer2.Player.Listener() {
            @Override
            public void onIsLoadingChanged(boolean isLoading) {
                com.google.android.exoplayer2.Player.Listener.super.onIsLoadingChanged(isLoading);
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                com.google.android.exoplayer2.Player.Listener.super.onPlaybackStateChanged(playbackState);

                if (playbackState == ExoPlayer.STATE_BUFFERING) {

                } else if (playbackState == ExoPlayer.STATE_READY) {

                }
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                com.google.android.exoplayer2.Player.Listener.super.onIsPlayingChanged(isPlaying);
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                com.google.android.exoplayer2.Player.Listener.super.onPlayerError(error);
            }

            @Override
            public void onPositionDiscontinuity(com.google.android.exoplayer2.Player.PositionInfo oldPosition, com.google.android.exoplayer2.Player.PositionInfo newPosition, int reason) {
                com.google.android.exoplayer2.Player.Listener.super.onPositionDiscontinuity(oldPosition, newPosition, reason);
            }
        });
    }

    public void playFromUrl(String url) {
        MediaItem mediaItem = MediaItem.fromUri(url);
        player.setMediaItem(mediaItem);
        playerView.setPlayer(player);
        player.prepare();
    }

    public void play() {
        if (playerView.getPlayer() != null) {
            playerView.getPlayer().play();
        }
    }

    public void pause() {
        if (playerView.getPlayer() != null) {
            playerView.getPlayer().pause();
        }
    }

    public void stop() {
        if (playerView.getPlayer() != null) {
            playerView.getPlayer().stop();
        }
    }
}
