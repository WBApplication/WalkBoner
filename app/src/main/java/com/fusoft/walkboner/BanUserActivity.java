package com.fusoft.walkboner;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.fusoft.walkboner.auth.UserInfoListener;
import com.fusoft.walkboner.database.funcions.BanDuration;
import com.fusoft.walkboner.database.funcions.BanUser;
import com.fusoft.walkboner.database.funcions.BanUserListener;
import com.fusoft.walkboner.models.User;
import com.fusoft.walkboner.utils.Profile;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.FirebaseFirestore;

import de.dlyt.yanndroid.oneui.dialog.ProgressDialog;
import de.dlyt.yanndroid.oneui.view.Toast;
import de.dlyt.yanndroid.oneui.widget.Spinner;

public class BanUserActivity extends AppCompatActivity {
    private EditText userToBanUidEdittext;
    private LinearLayout userFindedDataLinear, userAlreadyBannedLinear, userNotFindedLinear;
    private ImageView userAvatarImage;
    private MaterialTextView userNameText;
    private EditText banReasonEdittext;
    private Spinner banDurationSpinner;
    private MaterialButton banUserButton;

    private String banReason;
    private String bannedTo;

    private ProgressDialog loading;

    private User currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ban_user);
        initView();
        setup();
    }

    private void initView() {
        userToBanUidEdittext = (EditText) findViewById(R.id.user_to_ban_uid_edittext);
        userFindedDataLinear = (LinearLayout) findViewById(R.id.user_finded_data_linear);
        userAlreadyBannedLinear = findViewById(R.id.user_already_banned_linear);
        userNotFindedLinear = findViewById(R.id.user_not_finded_linear);
        userFindedDataLinear.setVisibility(View.GONE);
        userAvatarImage = (ImageView) findViewById(R.id.user_avatar_image);
        userNameText = (MaterialTextView) findViewById(R.id.user_name_text);
        banReasonEdittext = (EditText) findViewById(R.id.ban_reason_edittext);
        banDurationSpinner = (Spinner) findViewById(R.id.ban_duration_spinner);
        banUserButton = (MaterialButton) findViewById(R.id.ban_user_button);
    }

    private void setup() {
        loading = new ProgressDialog(BanUserActivity.this);
        loading.setIndeterminate(true);
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);
        loading.setProgressStyle(ProgressDialog.STYLE_CIRCLE);

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
                    new Profile().GetProfileForUser(editable.toString(), new UserInfoListener() {
                        @Override
                        public void OnUserDataReceived(User user) {
                            loading.dismiss();

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
                            loading.dismiss();
                            userNotFindedLinear.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void OnError(String reason) {
                            loading.dismiss();
                            Toast.makeText(BanUserActivity.this, reason, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    loading.dismiss();
                    currentUser = null;
                    userFindedDataLinear.setVisibility(View.GONE);
                    userAlreadyBannedLinear.setVisibility(View.GONE);
                    userNotFindedLinear.setVisibility(View.GONE);
                }
            }
        });

        banUserButton.setOnClickListener(v -> {
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
                BanUser.Ban(currentUser.getUserUid(), bannedTo, banReason, new BanUserListener() {
                    @Override
                    public void OnUserBanned() {
                        Toast.makeText(BanUserActivity.this, "Pomyślnie zbanowano użytkownika", Toast.LENGTH_SHORT).show();
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
