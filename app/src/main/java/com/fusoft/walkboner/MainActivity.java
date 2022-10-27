package com.fusoft.walkboner;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fusoft.walkboner.adapters.viewpager.MainViewPager;
import com.fusoft.walkboner.auth.Authentication;
import com.fusoft.walkboner.auth.AuthenticationListener;
import com.fusoft.walkboner.database.funcions.GetNotifications;
import com.fusoft.walkboner.models.Notification;
import com.fusoft.walkboner.models.SavedLink;
import com.fusoft.walkboner.offline.LinksManager;
import com.fusoft.walkboner.settings.Settings;
import com.fusoft.walkboner.uniload.UniloadActivity;
import com.fusoft.walkboner.utils.AppUpdate;
import com.fusoft.walkboner.views.dialogs.CreateLinkDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;

import de.dlyt.yanndroid.oneui.layout.DrawerLayout;
import de.dlyt.yanndroid.oneui.view.Toast;
import de.dlyt.yanndroid.oneui.view.ViewPager2;
import de.dlyt.yanndroid.oneui.widget.OptionButton;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mainActivityToolbar;
    private ViewPager2 viewPager;
    private OptionButton profileButton;
    private OptionButton homeButton;
    private OptionButton celebrityButton;
    private OptionButton logoutButton, premiumButton, savedLinksButton, notificationsButton;
    private OptionButton debugButton, uniloadButton;
    private ExtendedFloatingActionButton createFab;

    private Authentication auth;
    private Settings settings;

    private MainViewPager adapter;

    @Override
    protected void onDestroy() {
        auth = null;
        settings = null;
        adapter = null;

        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setup();
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            finishAndRemoveTask();
        } else if (viewPager.getCurrentItem() > 0) {
            changePage(0);
        }
    }

    private void initView() {
        settings = new Settings(MainActivity.this);

        mainActivityToolbar = findViewById(R.id.mainActivityToolbar);
        viewPager = findViewById(R.id.viewPager);
        profileButton = findViewById(R.id.profile_button);
        homeButton = findViewById(R.id.home_button);
        celebrityButton = findViewById(R.id.celebrity_button);
        logoutButton = findViewById(R.id.logout_button);
        premiumButton = findViewById(R.id.premium_button);
        createFab = findViewById(R.id.create_fab);
        savedLinksButton = findViewById(R.id.saved_links_button);
        uniloadButton = findViewById(R.id.uniload_button);
        notificationsButton = findViewById(R.id.notifications_button);

        adapter = new MainViewPager(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(adapter);

        homeButton.setButtonSelected(true);
        viewPager.setUserInputEnabled(false);

        homeButton.setOnClickListener(v -> {
            changePage(0);
        });

        celebrityButton.setOnClickListener(v -> {
            changePage(1);
        });

        savedLinksButton.setOnClickListener(v -> {
            changePage(2);
        });

        premiumButton.setOnClickListener(v -> {
            openActivity(PremiumActivity.class, false);
        });

        uniloadButton.setOnClickListener(v -> {
            openActivity(UniloadActivity.class, false);
        });

        notificationsButton.setOnClickListener(v -> {
            mainActivityToolbar.setNavigationButtonBadge(0);
            notificationsButton.setCounterEnabled(false);
            openActivity(NotificationsActivity.class, false);
        });

        AppUpdate.checkForUpdate(new AppUpdate.UpdateListener() {
            @Override
            public void OnUpdateAvailable(String version, String changeLog, String downloadUrl, boolean isRequired) {

            }

            @Override
            public void OnError(String reason) {

            }
        });

        auth = new Authentication(null);
        debugButton = (OptionButton) findViewById(R.id.debug_button);
    }

    private void setup() {
        GetNotifications.Get(auth.getCurrentUserUid(), false, new GetNotifications.NotificationsListener() {
            @Override
            public void OnSuccess(@Nullable List<Notification> notifications) {
                if (notifications != null) {
                    int newNotificationsAmount = 0;
                    for (Notification notification : notifications) {
                        if (!notification.isChecked()) {
                            newNotificationsAmount++;
                        }
                    }
                    mainActivityToolbar.setNavigationButtonBadge(newNotificationsAmount);
                    notificationsButton.setCounterEnabled(true);
                    notificationsButton.setCounter(newNotificationsAmount);
                }
            }

            @Override
            public void OnError(String reason) {

            }
        });

        profileButton.setOnClickListener(v -> {
            openActivity(UserProfileActivity.class, false);
        });

        mainActivityToolbar.setDrawerButtonOnClickListener(view -> {
            openActivity(SettingsActivity.class, false);
        });

        logoutButton.setOnClickListener(v -> {
            auth.logout();
            openActivity(AuthActivity.class, true);
        });

        debugButton.setOnClickListener(v -> {
            openActivity(AppDebugActivity.class, false);
        });


        LinksManager linksManager = new LinksManager(MainActivity.this);
        createFab.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() == 0) {
                openActivity(CreatePostActivity.class, false);
            }

            if (viewPager.getCurrentItem() == 2) {
                CreateLinkDialog newLinkDialog = new CreateLinkDialog();
                newLinkDialog.Show(MainActivity.this, new CreateLinkDialog.DialogListener() {
                    @Override
                    public void OnAddClicked(String title, String description, String url, String imageUrl) {
                        Toast.makeText(MainActivity.this, "Added", Toast.LENGTH_SHORT).show();

                        if (!settings.shouldPublishLink()) {
                            SavedLink link = new SavedLink();
                            link.setName(title);
                            link.setDescription(description);
                            link.setImageUrl("");
                            link.setPublic(false);
                            link.setLikes(null);
                            link.setLinkUid("0");
                            link.setLink(url);

                            linksManager.addLink(link);
                        }

                        newLinkDialog.Dismiss();
                    }

                    @Override
                    public void OnDismiss() {
                        Toast.makeText(MainActivity.this, "Dismissed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void openActivity(Class toOpen, boolean finish) {
        startActivity(new Intent(MainActivity.this, toOpen));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        if (finish) {
            finish();
        }
    }

    public void changePage(int page) {
        switch (page) {
            case 0:
                viewPager.setCurrentItem(0, false);
                mainActivityToolbar.setSubtitle("Strona Główna");
                homeButton.setButtonSelected(true);
                celebrityButton.setButtonSelected(false);
                savedLinksButton.setButtonSelected(false);
                toggleFab(true);
                break;
            case 1:
                viewPager.setCurrentItem(1, false);
                mainActivityToolbar.setSubtitle("Influencerki");
                mainActivityToolbar.setExpanded(false, true);
                homeButton.setButtonSelected(false);
                celebrityButton.setButtonSelected(true);
                savedLinksButton.setButtonSelected(false);
                toggleFab(false);
                break;
            case 2:
                viewPager.setCurrentItem(2, false);
                mainActivityToolbar.setSubtitle("Odnośniki");
                mainActivityToolbar.setExpanded(true, true);
                homeButton.setButtonSelected(false);
                celebrityButton.setButtonSelected(false);
                savedLinksButton.setButtonSelected(true);
                toggleFab(true);
                break;
        }

        mainActivityToolbar.setDrawerOpen(false, true);
    }

    private void toggleFab(boolean on) {
        if (on) {
            createFab.show();
        } else {
            createFab.hide();
        }
    }
}