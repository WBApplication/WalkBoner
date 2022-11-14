package com.fusoft.walkboner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fusoft.walkboner.adapters.recyclerview.AlbumsAdapter;
import com.fusoft.walkboner.adapters.recyclerview.ItemClickListener;
import com.fusoft.walkboner.adapters.recyclerview.ItemLongClickListener;
import com.fusoft.walkboner.database.funcions.GetAlbum;
import com.fusoft.walkboner.models.album.Album;
import com.fusoft.walkboner.moderation.views.AlbumModBottomSheetKt;
import com.fusoft.walkboner.singletons.UserSingletone;
import com.fusoft.walkboner.utils.AnimateChanges;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;

import java.util.List;

public class PersonAlbumsActivity extends AppCompatActivity {
    private ImageView backgroundImage;
    private MaterialTextView personNickText;
    private MaterialTextView personFullNameText;
    private ImageView instagramButton;
    private ImageView youtubeButton;
    private LinearLayout socialsLinear, bottomContentLinear;
    private ProgressBar loadingAlbumsProgress;
    private MaterialTextView noAlbumsText;
    private RecyclerView albumsRecyclerView;
    private ExtendedFloatingActionButton createAlbumButton;
    private GetAlbum getAlbum;
    private AlbumsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_albums);

        initView();
        setup();
    }

    private void initView() {
        backgroundImage = (ImageView) findViewById(R.id.background_image);
        personNickText = (MaterialTextView) findViewById(R.id.person_nick_text);
        personFullNameText = (MaterialTextView) findViewById(R.id.person_full_name_text);
        instagramButton = (ImageView) findViewById(R.id.instagram_button);
        youtubeButton = (ImageView) findViewById(R.id.youtube_button);
        socialsLinear = (LinearLayout) findViewById(R.id.socials_linear);
        loadingAlbumsProgress = (ProgressBar) findViewById(R.id.loading_albums_progress);
        noAlbumsText = (MaterialTextView) findViewById(R.id.no_albums_text);
        albumsRecyclerView = (RecyclerView) findViewById(R.id.albums_recycler_view);
        createAlbumButton = (ExtendedFloatingActionButton) findViewById(R.id.create_album_button);
        bottomContentLinear = findViewById(R.id.bottom_content_linear);
        AnimateChanges.forLinear(bottomContentLinear);

        getAlbum = new GetAlbum();

        instagramButton.setVisibility(View.GONE);
        youtubeButton.setVisibility(View.GONE);

        personNickText.animate().alpha(1f).translationX(0f).setDuration(800).setInterpolator(new DecelerateInterpolator()).start();
        personFullNameText.animate().alpha(1f).translationX(0f).setDuration(1000).setStartDelay(500).setInterpolator(new DecelerateInterpolator()).start();
    }

    private void setup() {
        createAlbumButton.setOnClickListener(v -> {
            Intent intent = new Intent(PersonAlbumsActivity.this, CreateAlbumActivity.class);
            intent.putExtra("influencerUid", getIntent().getStringExtra("influencerUid"));
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        String backgroundImageUrl = getIntent().getStringExtra("influencerAvatar");
        String nickName = getIntent().getStringExtra("influencerNick");
        String fullName = getIntent().getStringExtra("influencerFullName");

        boolean isYouTubeLinkAvailable = !getIntent().getStringExtra("influencerYouTube").contentEquals("");
        boolean isInstagramLinkAvailable = !getIntent().getStringExtra("influencerInstagram").contentEquals("");

        if (isYouTubeLinkAvailable) {
            youtubeButton.setVisibility(View.VISIBLE);
        }

        if (isInstagramLinkAvailable) {
            instagramButton.setVisibility(View.VISIBLE);
        }

        if (!isYouTubeLinkAvailable && !isInstagramLinkAvailable) {
            socialsLinear.setVisibility(View.GONE);
        }

        Glide.with(PersonAlbumsActivity.this)
                .load(backgroundImageUrl)
                .into(backgroundImage);

        personNickText.setText(nickName);
        personFullNameText.setText(fullName);

        getAlbum.ForInfluencer(getIntent().getStringExtra("influencerUid"), new GetAlbum.AlbumListener() {
            @Override
            public void OnReceived(List<Album> albums) {
                adapter = new AlbumsAdapter(PersonAlbumsActivity.this, albums);

                GridLayoutManager layoutManager = new GridLayoutManager(PersonAlbumsActivity.this, 3);

                albumsRecyclerView.setLayoutManager(layoutManager);
                albumsRecyclerView.setAdapter(adapter);

                loadingAlbumsProgress.setVisibility(View.GONE);
                albumsRecyclerView.setVisibility(View.VISIBLE);

                if (!albums.isEmpty()) {
                    albumsRecyclerView.animate().alpha(1f).translationY(0f).setDuration(1800).setInterpolator(new DecelerateInterpolator()).start();
                }

                if (albums.isEmpty()) {
                    loadingAlbumsProgress.setVisibility(View.GONE);
                    albumsRecyclerView.setVisibility(View.GONE);
                    noAlbumsText.setVisibility(View.VISIBLE);
                }

                adapter.setClickListener(new ItemClickListener() {
                    @Override
                    public void onItemClick(Album album, int position) {
                        Intent intent = new Intent(PersonAlbumsActivity.this, MediaViewerActivity.class);
                        intent.putExtra("albumImages", new Gson().toJson(album.getAlbumContent()));
                        startActivity(intent);
                    }
                });

                adapter.setLongClickListener(new ItemLongClickListener() {
                    @Override
                    public void onAlbumLongClicked(Album album) {
                        boolean isUserAdmin = UserSingletone.getInstance().getUser().isUserAdmin();
                        boolean isUserMod = UserSingletone.getInstance().getUser().isUserModerator();
                        if (isUserAdmin || isUserMod) {
                            AlbumModBottomSheetKt.ShowAlbumModBottomSheet(
                                    PersonAlbumsActivity.this,
                                    getLayoutInflater(),
                                    album.getAlbumUid(),
                                    album.getCreatedBy()
                            );
                        }
                    }
                });
            }

            @Override
            public void OnError(String reason) {
                Toast.makeText(PersonAlbumsActivity.this, "Error: " + reason, Toast.LENGTH_LONG).show();
            }
        });
    }
}
