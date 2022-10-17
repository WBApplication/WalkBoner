package com.fusoft.walkboner.moderation;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fusoft.walkboner.R;
import com.fusoft.walkboner.moderation.utils.CounterListener;
import com.fusoft.walkboner.moderation.utils.GetInfluencersToModerateCount;
import com.fusoft.walkboner.moderation.utils.GetReportsToModerateCount;

import de.dlyt.yanndroid.oneui.widget.OptionButton;

public class ModerationActivity extends AppCompatActivity {

    private OptionButton chatModButton;
    private OptionButton banUserButton;
    private OptionButton unbanUserButton;
    private OptionButton warningUserButton;
    private OptionButton moderateNewInfluencersButton;
    private OptionButton deletePostButton;
    private OptionButton deleteAlbumButton;
    private OptionButton reportedPostsButton;
    private OptionButton findUserButton;
    private OptionButton adminListButton;
    private OptionButton modsListButton;

    private GetInfluencersToModerateCount moderateCount;
    private GetReportsToModerateCount reportsCount;

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

        chatModButton = (OptionButton) findViewById(R.id.chat_mod_button);
        banUserButton = (OptionButton) findViewById(R.id.ban_user_button);
        unbanUserButton = (OptionButton) findViewById(R.id.unban_user_button);
        warningUserButton = (OptionButton) findViewById(R.id.warning_user_button);
        moderateNewInfluencersButton = (OptionButton) findViewById(R.id.moderate_new_influencers_button);
        deletePostButton = (OptionButton) findViewById(R.id.delete_post_button);
        deleteAlbumButton = (OptionButton) findViewById(R.id.delete_album_button);
        reportedPostsButton = (OptionButton) findViewById(R.id.reported_posts_button);
        findUserButton = (OptionButton) findViewById(R.id.find_user_button);
        adminListButton = (OptionButton) findViewById(R.id.admin_list_button);
        modsListButton = (OptionButton) findViewById(R.id.mods_list_button);
    }

    private void setup() {
        moderateCount.GetAmount(new CounterListener() {
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
        });

        reportsCount.GetAmount(new CounterListener() {
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
        });

        chatModButton.setOnClickListener(v -> {
            openActivity(ModChatActivity.class);
        });

        banUserButton.setOnClickListener(v -> {
            openActivity(BanUserActivity.class);
        });
        
        moderateNewInfluencersButton.setOnClickListener(v -> {
            openActivity(AcceptInfluencersProfilesActivity.class);
        });
    }

    private void openActivity(Class toOpen) {
        startActivity(new Intent(ModerationActivity.this, toOpen));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
