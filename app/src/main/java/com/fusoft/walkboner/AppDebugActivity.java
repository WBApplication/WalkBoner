package com.fusoft.walkboner;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fusoft.walkboner.auth.Authentication;
import com.fusoft.walkboner.database.funcions.SendNotification;
import com.fusoft.walkboner.models.Notification;
import com.fusoft.walkboner.utils.UidGenerator;

import de.dlyt.yanndroid.oneui.widget.OptionButton;

public class AppDebugActivity extends AppCompatActivity {
    private OptionButton crashButton;
    private OptionButton notifyButton;

    private Authentication auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_app);
        initView();
        setup();
    }

    private void initView() {
        crashButton = (OptionButton) findViewById(R.id.crash_button);
        notifyButton = (OptionButton) findViewById(R.id.notify_button);

        auth = new Authentication(null);
    }

    private void setup() {
        crashButton.setOnClickListener(v -> {
            int a = 0;
            int b = 2;
            int c = Integer.parseInt("dick");
            int d = b / a * c + Integer.parseInt("BBC") % Integer.parseInt("PORT");
        });

        notifyButton.setOnClickListener(v -> {
            notifyButton.setButtonEnabled(false);
            Notification notification = new Notification();
            notification.setNotificationUid(UidGenerator.Generate(12));
            notification.setNotificationTitle("Test Notification");
            notification.setNotificationDescription(UidGenerator.Generate(5));
            notification.setNotificationAttribute("");

            new SendNotification().send(auth.getCurrentUserUid(), notification, new SendNotification.NotificationListener() {
                @Override
                public void OnSuccess() {
                    notifyButton.setButtonEnabled(true);
                }

                @Override
                public void OnError(String error) {
                    notifyButton.setButtonEnabled(true);
                }
            });
        });
    }
}
