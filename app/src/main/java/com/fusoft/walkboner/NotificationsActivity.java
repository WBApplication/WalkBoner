package com.fusoft.walkboner;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fusoft.walkboner.adapters.recyclerview.NotificationsAdapter;
import com.fusoft.walkboner.auth.Authentication;
import com.fusoft.walkboner.database.funcions.GetNotifications;
import com.fusoft.walkboner.models.Notification;

import java.util.List;

import de.dlyt.yanndroid.oneui.dialog.ProgressDialog;
import de.dlyt.yanndroid.oneui.layout.ToolbarLayout;
import de.dlyt.yanndroid.oneui.sesl.recyclerview.LinearLayoutManager;
import de.dlyt.yanndroid.oneui.view.RecyclerView;
import de.dlyt.yanndroid.oneui.view.Toast;

public class NotificationsActivity extends AppCompatActivity {
    private ToolbarLayout notificationsToolbar;
    private RecyclerView notificationsRecyclerView;

    private Authentication authentication;
    private NotificationsAdapter adapter;
    private LinearLayoutManager layoutManager;

    private ProgressDialog loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        initView();
        setup();
    }

    @Override
    protected void onDestroy() {
        authentication = null;
        adapter = null;
        layoutManager = null;

        super.onDestroy();
    }

    private void initView() {
        notificationsToolbar = (ToolbarLayout) findViewById(R.id.notifications_toolbar);
        notificationsRecyclerView = (RecyclerView) findViewById(R.id.notifications_recycler_view);

        loading = new ProgressDialog(NotificationsActivity.this);
        loading.setIndeterminate(true);
        loading.setProgressStyle(ProgressDialog.STYLE_CIRCLE);
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);
        loading.show();

        authentication = new Authentication(null);
    }

    private void setup() {
        layoutManager = new LinearLayoutManager(NotificationsActivity.this);
        notificationsRecyclerView.setLayoutManager(layoutManager);

        GetNotifications.Get(authentication.getCurrentUserUid(), true, new GetNotifications.NotificationsListener() {
            @Override
            public void OnSuccess(@Nullable List<Notification> notifications) {
                loading.dismiss();
                if (notifications != null) {
                    adapter = new NotificationsAdapter(NotificationsActivity.this, notifications);

                    adapter.setClickListener((notification, position) -> {
                        loading.show();
                        GetNotifications.Remove(authentication.getCurrentUserUid(), notification.getNotificationUid(), new GetNotifications.NotificationsDeleteListener() {
                            @Override
                            public void OnDeleted() {
                                loading.dismiss();
                                adapter.deleteAt(position);
                            }

                            @Override
                            public void OnError(String reason) {
                                loading.dismiss();
                                Toast.makeText(NotificationsActivity.this, reason, Toast.LENGTH_LONG).show();
                            }
                        });
                    });

                    notificationsRecyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(NotificationsActivity.this, "puste", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void OnError(String reason) {
                Toast.makeText(NotificationsActivity.this, reason, Toast.LENGTH_SHORT).show();
            }
        });
    }
}