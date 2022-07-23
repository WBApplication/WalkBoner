package com.fusoft.walkboner.moderation;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fusoft.walkboner.R;
import com.fusoft.walkboner.adapters.recyclerview.ModChatAdapter;
import com.fusoft.walkboner.auth.Authentication;
import com.fusoft.walkboner.auth.AuthenticationListener;
import com.fusoft.walkboner.auth.UserInfoListener;
import com.fusoft.walkboner.models.Message;
import com.fusoft.walkboner.models.User;
import com.fusoft.walkboner.utils.UidGenerator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import de.dlyt.yanndroid.oneui.sesl.recyclerview.LinearLayoutManager;
import de.dlyt.yanndroid.oneui.view.RecyclerView;
import de.dlyt.yanndroid.oneui.view.Toast;

public class ModChatActivity extends AppCompatActivity {
    private RecyclerView chatRecyclerView;
    private ImageView pickImageButton;
    private EditText messageEdittext;
    private ImageView sendButton;

    private ArrayList<Message> messagesList = new ArrayList<>();

    private ModChatAdapter adapter;
    private FirebaseFirestore firestore;
    private User userData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_chat);

        initView();
        setup();
    }

    private void initView() {
        chatRecyclerView = (RecyclerView) findViewById(R.id.chat_recycler_view);
        pickImageButton = (ImageView) findViewById(R.id.pick_image_button);
        messageEdittext = (EditText) findViewById(R.id.message_edittext);
        sendButton = (ImageView) findViewById(R.id.send_button);
    }

    private void setup() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(ModChatActivity.this);
        layoutManager.setReverseLayout(true);
        chatRecyclerView.setLayoutManager(layoutManager);

        Authentication authentication = new Authentication(new AuthenticationListener() {
            @Override
            public void UserAlreadyLoggedIn(boolean pinRequired) {

            }

            @Override
            public void UserRequiredToBeLogged() {

            }

            @Override
            public void OnLogin(boolean isSuccess, boolean pinRequired, @Nullable String reason) {

            }

            @Override
            public void OnRegister(boolean isSuccess, @Nullable String reason) {

            }
        });

        authentication.getUserData(new UserInfoListener() {
            @Override
            public void OnUserDataReceived(User user) {
                Log.e("ModChatActivity", "User Data Loaded...");
                adapter = new ModChatAdapter(ModChatActivity.this, messagesList, user.getUserUid());
                chatRecyclerView.setAdapter(adapter);
                userData = user;

                LoadMessages();
            }

            @Override
            public void OnUserNotFinded() {

            }

            @Override
            public void OnError(String reason) {

            }
        });

        sendButton.setOnClickListener(v -> {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            HashMap<String, Object> map = new HashMap<>();
            map.put("senderUid", userData.getUserUid());
            map.put("senderAvatar", userData.getUserAvatar());
            map.put("messageUid", UidGenerator.Generate());
            map.put("message", messageEdittext.getText().toString());
            map.put("image", "null");
            map.put("sendedAt", String.valueOf(timestamp.getTime()));
            map.put("type", Message.TYPE_TEXT);

            firestore.collection("modsChat").add(map);

            messageEdittext.setText("");
        });
    }

    private void LoadMessages() {
        Log.e("ModChatActivity", "Loading Messages...");
        firestore = FirebaseFirestore.getInstance();
        firestore.collection("modsChat").orderBy("sendedAt", Query.Direction.DESCENDING).addSnapshotListener((value, error) -> {
            messagesList.clear();

            if (value == null) {
                Toast.makeText(ModChatActivity.this, "Brak Wiadomości", Toast.LENGTH_SHORT).show();
            } else {
                for (DocumentSnapshot doc : value.getDocuments()) {
                    Message message = new Message();
                    message.setSenderUid(doc.getString("senderUid"));
                    message.setSenderAvatar(doc.getString("senderAvatar"));
                    message.setMessageUid(doc.getString("messageUid"));
                    message.setMessage(doc.getString("message"));
                    message.setImage(doc.getString("image"));
                    message.setSendedAt(doc.getString("sendedAt"));
                    message.setType(doc.getString("type"));

                    messagesList.add(message);

                    Log.e("ModChatActivity", "Message Added To List...");
                }

                Log.e("ModChatActivity", "Messages Loaded...");
                adapter.notifyDataSetChanged();
            }
        });
    }
}
