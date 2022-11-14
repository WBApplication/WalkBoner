package com.fusoft.walkboner.moderation;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.fusoft.walkboner.R;
import com.fusoft.walkboner.auth.UserInfoListener;
import com.fusoft.walkboner.database.funcions.BanDuration;
import com.fusoft.walkboner.database.funcions.BanUser;
import com.fusoft.walkboner.database.funcions.BanUserListener;
import com.fusoft.walkboner.models.User;
import com.fusoft.walkboner.utils.Profile;
import com.fusoft.walkboner.views.LoadingView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.FirebaseFirestore;

public class BanUserActivity extends AppCompatActivity {
    private TextInputEditText userToBanUidEdittext, banReasonEdittext;
    private LinearLayout userFindedDataLinear, userAlreadyBannedLinear, userNotFindedLinear;
    private ImageView userAvatarImage;
    private MaterialTextView userNameText;
    private Spinner banDurationSpinner;
    private MaterialButton banUserButton;

    private String banReason, bannedTo;

    private LoadingView loading;
    private User currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ban_user);
        initView();
        setup();
    }

    @Override
    protected void onDestroy() {
        loading = null;
        currentUser = null;

        super.onDestroy();
    }

    private void initView() {
        userToBanUidEdittext = findViewById(R.id.user_to_ban_uid_edittext);
        userFindedDataLinear = findViewById(R.id.user_finded_data_linear);
        userAlreadyBannedLinear = findViewById(R.id.user_already_banned_linear);
        userNotFindedLinear = findViewById(R.id.user_not_finded_linear);
        userFindedDataLinear.setVisibility(View.GONE);
        userAvatarImage = findViewById(R.id.user_avatar_image);
        userNameText = findViewById(R.id.user_name_text);
        banReasonEdittext = findViewById(R.id.ban_reason_edittext);
        banDurationSpinner = findViewById(R.id.ban_duration_spinner);
        banUserButton = findViewById(R.id.ban_user_button);
        loading = findViewById(R.id.loadingView);
    }

    private void setup() {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        final String[] choices = {"Jeden Dzień", "Dwa Dni", "Tydzień", "Dwa Tygodnie", "Miesiąc", "Na zawsze lub do czasu nieokreślonego"};
        ArrayAdapter<String> a = new ArrayAdapter<String>(BanUserActivity.this, android.R.layout.simple_spinner_item, choices);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        banDurationSpinner.setAdapter(a);

        userToBanUidEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 28) {
                    loading.show();
                    userToBanUidEdittext.setEnabled(false);
                    new Profile().GetProfileForUser(editable.toString(), new UserInfoListener() {
                        @Override
                        public void OnUserDataReceived(User user) {
                            loading.hide();
                            userToBanUidEdittext.setEnabled(true);

                            if (user.isUserBanned()) {
                                userAlreadyBannedLinear.setVisibility(View.VISIBLE);
                            } else {
                                currentUser = user;
                                userFindedDataLinear.setVisibility(View.VISIBLE);
                                Glide.with(BanUserActivity.this).load(user.getUserAvatar()).into(userAvatarImage);
                                userNameText.setText(user.getUserName());
                            }
                        }

                        @Override
                        public void OnUserNotFinded() {
                            loading.hide();
                            userToBanUidEdittext.setEnabled(true);
                            userNotFindedLinear.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void OnError(String reason) {
                            loading.hide();
                            userToBanUidEdittext.setEnabled(true);
                            Toast.makeText(BanUserActivity.this, reason, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    loading.hide();
                    currentUser = null;
                    userFindedDataLinear.setVisibility(View.GONE);
                    userAlreadyBannedLinear.setVisibility(View.GONE);
                    userNotFindedLinear.setVisibility(View.GONE);
                }
            }
        });

        // If opened from another activity than moderation
        if (getIntent().hasExtra("userUid")) {
            userToBanUidEdittext.setText(getIntent().getStringExtra("userUid"));
        }

        banUserButton.setOnClickListener(v -> {
            loading.show();

            banReason = banReasonEdittext.getText().toString();

            switch (banDurationSpinner.getSelectedItemPosition()) {
                case 0:
                    bannedTo = BanDuration.ONE_DAY();
                    break;
                case 1:
                    bannedTo = BanDuration.TWO_DAY();
                    break;
                case 2:
                    bannedTo = BanDuration.WEEK();
                    break;
                case 3:
                    bannedTo = BanDuration.TWO_WEEKS();
                    break;
                case 4:
                    bannedTo = BanDuration.MONTH();
                    break;
                case 5:
                    bannedTo = BanDuration.FOREVER();
                    break;
            }

            if (bannedTo == null || bannedTo.isEmpty() || banReason.isEmpty()) {
                Toast.makeText(BanUserActivity.this, "Uzupełnij wszystkie dane", Toast.LENGTH_SHORT).show();
            } else {
                BanUser.Ban(currentUser.getUserName(), currentUser.getUserUid(), bannedTo, banReason, new BanUserListener() {
                    @Override
                    public void OnUserBanned() {
                        Toast.makeText(BanUserActivity.this, "Pomyślnie zbanowano użytkownika", Toast.LENGTH_SHORT).show();
                        loading.hide();
                        finish();
                    }

                    @Override
                    public void OnError(String reason) {
                        Toast.makeText(BanUserActivity.this, reason, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
