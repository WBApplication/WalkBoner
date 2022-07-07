package com.fusoft.walkboner;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fusoft.walkboner.adapters.viewpager.MainViewPager;
import com.fusoft.walkboner.auth.Authentication;
import com.fusoft.walkboner.auth.AuthenticationListener;

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
    private OptionButton logoutButton, premiumButton;
    private OptionButton debugButton;

    private Authentication auth;

    private MainViewPager adapter;

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
        } else if (viewPager.getCurrentItem() == 1) {
            changePage(0);
        }
    }

    private void initView() {
        mainActivityToolbar = (DrawerLayout) findViewById(R.id.mainActivityToolbar);
        viewPager = (ViewPager2) findViewById(R.id.viewPager);
        profileButton = (OptionButton) findViewById(R.id.profile_button);
        homeButton = (OptionButton) findViewById(R.id.home_button);
        celebrityButton = (OptionButton) findViewById(R.id.celebrity_button);
        logoutButton = (OptionButton) findViewById(R.id.logout_button);
        premiumButton = findViewById(R.id.premium_button);

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

        premiumButton.setOnClickListener(v -> {
            openActivity(PremiumActivity.class, false);
        });

        auth = new Authentication(new AuthenticationListener() {
            @Override
            public void UserAlreadyLoggedIn(boolean pinRequired) {

            }

            @Override
            public void UserRequiredToBeLogged() {
                Toast.makeText(MainActivity.this, "Dane logowania nieaktualne.\nZaloguj się ponownie!", Toast.LENGTH_LONG).show();
                openActivity(AuthActivity.class, true);
            }

            @Override
            public void OnLogin(boolean isSuccess, boolean pinRequired, @Nullable String reason) {

            }

            @Override
            public void OnRegister(boolean isSuccess, @Nullable String reason) {

            }
        });
        debugButton = (OptionButton) findViewById(R.id.debug_button);
    }

    private void setup() {
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
                break;
            case 1:
                viewPager.setCurrentItem(1, false);
                mainActivityToolbar.setSubtitle("Influencerki");
                mainActivityToolbar.setExpanded(false, true);
                homeButton.setButtonSelected(false);
                celebrityButton.setButtonSelected(true);
                break;
        }

        mainActivityToolbar.setDrawerOpen(false, true);
    }
}