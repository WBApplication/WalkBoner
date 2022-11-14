package com.fusoft.walkboner.moderation;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fusoft.walkboner.R;
import com.fusoft.walkboner.moderation.utils.GetInfluencersToModerateCount;
import com.fusoft.walkboner.moderation.utils.GetReportsToModerateCount;
import com.google.android.material.button.MaterialButton;

public class ModerationActivity extends AppCompatActivity {

    private MaterialButton chatModButton;
    private MaterialButton banUserButton;
    private MaterialButton unbanUserButton;
    private MaterialButton warningUserButton;
    private MaterialButton moderateNewInfluencersButton;
    private MaterialButton deletePostButton;
    private MaterialButton deleteAlbumButton;
    private MaterialButton deleteInfluencerButton;
    private MaterialButton reportedPostsButton;
    private MaterialButton findUserButton;
    private MaterialButton adminListButton;
    private MaterialButton modsListButton;

    private GetInfluencersToModerateCount moderateCount;
    private GetReportsToModerateCount reportsCount;
    private MaterialButton modLogsButton;

    @Override
    protected void onDestroy() {
        moderateCount = null;
        reportsCount = null;

        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moderation);

        initView();
        setup();
    }

    private void initView() {
        moderateCount = new GetInfluencersToModerateCount();
        reportsCount = new GetReportsToModerateCount();

        chatModButton = (MaterialButton) findViewById(R.id.chat_mod_button);
        banUserButton = (MaterialButton) findViewById(R.id.ban_user_button);
        unbanUserButton = (MaterialButton) findViewById(R.id.unban_user_button);
        warningUserButton = (MaterialButton) findViewById(R.id.warning_user_button);
        moderateNewInfluencersButton = (MaterialButton) findViewById(R.id.moderate_new_influencers_button);
        deletePostButton = (MaterialButton) findViewById(R.id.delete_post_button);
        deleteAlbumButton = (MaterialButton) findViewById(R.id.delete_album_button);
        reportedPostsButton = (MaterialButton) findViewById(R.id.reported_posts_button);
        findUserButton = (MaterialButton) findViewById(R.id.find_user_button);
        adminListButton = (MaterialButton) findViewById(R.id.admin_list_button);
        modsListButton = (MaterialButton) findViewById(R.id.mods_list_button);
        deleteInfluencerButton = (MaterialButton) findViewById(R.id.delete_influencer_button);
        modLogsButton = (MaterialButton) findViewById(R.id.mod_logs_button);
    }

    private void setup() {
        /*moderateCount.GetAmount(new CounterListener() {
            @Override
            public void OnResponse(int amountOfInfluencersToModerate) {
                if (amountOfInfluencersToModerate != 0) {
                    moderateNewInfluencersButton.setCounter(amountOfInfluencersToModerate);
                    moderateNewInfluencersButton.setCounterEnabled(true);
                } else {
                    moderateNewInfluencersButton.setCounter(0);
                    moderateNewInfluencersButton.setCounterEnabled(false);
                }
            }

            @Override
            public void OnError(String reason) {

            }
        });*/

        /*reportsCount.GetAmount(new CounterListener() {
            @Override
            public void OnResponse(int amountOfInfluencersToModerate) {
                if (amountOfInfluencersToModerate != 0) {
                    reportedPostsButton.setCounter(amountOfInfluencersToModerate);
                    reportedPostsButton.setCounterEnabled(true);
                } else {
                    reportedPostsButton.setCounter(0);
                    reportedPostsButton.setCounterEnabled(false);
                }
            }

            @Override
            public void OnError(String reason) {

            }
        });*/

        chatModButton.setOnClickListener(v -> {
            openActivity(ModChatActivity.class);
        });

        banUserButton.setOnClickListener(v -> {
            openActivity(BanUserActivity.class);
        });

        unbanUserButton.setOnClickListener(v -> {
            openActivity(UnbanUserActivity.class);
        });

        moderateNewInfluencersButton.setOnClickListener(v -> {
            openActivity(AcceptInfluencersProfilesActivity.class);
        });

        deleteInfluencerButton.setOnClickListener(v -> {
            openActivity(DeleteInfluencerActivity.class);
        });

        modLogsButton.setOnClickListener(v -> {
            openActivity(ModLoggerActivity.class);
        });
    }

    private void openActivity(Class toOpen) {
        startActivity(new Intent(ModerationActivity.this, toOpen));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
