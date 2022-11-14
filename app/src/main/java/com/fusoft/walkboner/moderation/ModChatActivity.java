package com.fusoft.walkboner.moderation;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fusoft.walkboner.R;
import com.fusoft.walkboner.adapters.recyclerview.ModChatAdapter;
import com.fusoft.walkboner.auth.Authentication;
import com.fusoft.walkboner.auth.AuthenticationListener;
import com.fusoft.walkboner.auth.UserInfoListener;
import com.fusoft.walkboner.models.Message;
import com.fusoft.walkboner.models.User;
import com.fusoft.walkboner.utils.UidGenerator;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

public class ModChatActivity extends AppCompatActivity {
    private RecyclerView chatRecyclerView;
    private ImageView pickImageButton;
    private EditText messageEdittext;
    private MaterialButton sendButton;

    private ArrayList<Message> messagesList = new ArrayList<>();
    HashMap<String, Object> messagesMap;

    private ModChatAdapter adapter;
    private FirebaseFirestore firestore;
    private User userData;
    private UserInfoListener userInfoListener;

    @Override
    protected void onDestroy() {
        messagesList.clear();
        messagesList = null;
        firestore = null;
        adapter = null;
        messagesMap = null;
        userInfoListener = null;

        super.onDestroy();
    }

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
        sendButton = (MaterialButton) findViewById(R.id.send_button);
    }

    private void setup() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(ModChatActivity.this);
        layoutManager.setReverseLayout(true);
        chatRecyclerView.setLayoutManager(layoutManager);

        Authentication authentication = new Authentication(null);

        userInfoListener = new UserInfoListener() {
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
        };

        authentication.getUserData(userInfoListener);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isTextEmpty = messageEdittext.getText().toString().isEmpty();
                //boolean isTextIsNotOnlyWhiteSpace = messageEdittext.getText().toString().matches("\\w*");

                if (!isTextEmpty) {
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                    messagesMap = new HashMap<>();

                    messagesMap.put("senderUid", userData.getUserUid());
                    messagesMap.put("senderAvatar", userData.getUserAvatar());
                    messagesMap.put("messageUid", UidGenerator.Generate());
                    messagesMap.put("message", messageEdittext.getText().toString());
                    messagesMap.put("image", "null");
                    messagesMap.put("sendedAt", String.valueOf(timestamp.getTime()));
                    messagesMap.put("type", Message.TYPE_TEXT);

                    firestore.collection("modsChat").add(messagesMap).addOnSuccessListener(documentReference -> {
                        messagesMap = null;
                    });

                    messageEdittext.setText("");
                }
            }
        });
    }

    private void LoadMessages() {
        Log.e("ModChatActivity", "Loading Messages...");
        firestore = FirebaseFirestore.getInstance();
        firestore.collection("modsChat").orderBy("sendedAt", Query.Direction.DESCENDING).addSnapshotListener((value, error) -> {
            if (messagesList != null || !messagesList.isEmpty())
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
