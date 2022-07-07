package com.fusoft.walkboner.auth;

import android.util.Log;

import androidx.annotation.NonNull;

import com.fusoft.walkboner.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SnapshotMetadata;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

public class Authentication {

    FirebaseFirestore firestore;

    FirebaseAuth auth;
    FirebaseUser user;

    AuthenticationListener listener;

    private boolean isListenerAvailable() {
        return listener != null;
    }

    public Authentication(AuthenticationListener listener) {
        this.listener = listener;

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (auth.getCurrentUser() != null) {
            user = auth.getCurrentUser();

            firestore.collection("users").whereEqualTo("userUid", user.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        documentSnapshot.getReference().collection("pin").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                    if (isListenerAvailable()) {
                                        listener.UserAlreadyLoggedIn(Boolean.parseBoolean(doc.getString("ENABLED")));
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        } else {
            if (isListenerAvailable()) {
                listener.UserRequiredToBeLogged();
            }
        }
    }

    public void login(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            user = authResult.getUser();
            firestore.collection("users").whereEqualTo("userUid", user.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        documentSnapshot.getReference().collection("pin").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                    if (isListenerAvailable()) {
                                        listener.OnLogin(true, Boolean.parseBoolean(doc.getString("ENABLED")), null);
                                    }
                                    return;
                                }

                                if (isListenerAvailable()) {
                                    listener.OnLogin(true, false, null);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if (isListenerAvailable()) {
                                    listener.OnLogin(false, false, "Dane logowania są poprawne, ale nie udało się uzyskać informacji czy na koncie jest ustalony numer PIN.");
                                }
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (isListenerAvailable()) {
                        listener.OnLogin(false, false, "Dane logowania są poprawne, ale nie udało się uzyskać informacji czy na koncie jest ustalony numer PIN.");
                    }
                }
            });
        }).addOnFailureListener(e -> {
            if (isListenerAvailable()) {
                listener.OnLogin(false, false, e.getMessage());
            }
        });
    }

    public void register(String email, String username, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            firestore.collection("users").add(defaultMap(auth.getUid(), email, username)).addOnSuccessListener(documentReference -> {
                if (isListenerAvailable()) {
                    listener.OnRegister(true, null);
                }
                user = authResult.getUser();
            }).addOnFailureListener(e -> {
                if (isListenerAvailable()) {
                    listener.OnRegister(false, e.getMessage());
                }

                if (auth.getCurrentUser() != null) {
                    auth.getCurrentUser().delete();
                    // TODO: Wysyłać do bazy danych informacje jeśli nie uda się usunąć użytkownika i informować o tym moderacje aby spróbowali zrobić to ręcznie.
                }
            });
        }).addOnFailureListener(e -> {
            if (isListenerAvailable()) {
                listener.OnRegister(false, e.getMessage());
            }
        });
    }

    public String getCurrentUserUid() {
        return user.getUid();
    }

    public void getUserData(UserInfoListener listener) {
        if (user == null && auth.getCurrentUser() == null) {
            listener.OnError("Użytkownik nie zalogowany!");
        } else {
            firestore.collection("users").whereEqualTo("userUid", user.getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    User userData = new User();
                    userData.setUserUid(user.getUid());
                    userData.setUserEmail(doc.get("email").toString());
                    userData.setUserName(doc.get("username").toString());
                    userData.setUserAvatar(doc.get("avatar").toString());
                    userData.setUserDescription(doc.get("description").toString());
                    userData.setUserBanReason(doc.get("banReason").toString());
                    userData.setUserVerified(Boolean.parseBoolean(doc.get("isVerified").toString()));
                    userData.setUserModerator(Boolean.parseBoolean(doc.get("isMod").toString()));
                    userData.setUserAdmin(Boolean.parseBoolean(doc.get("isAdmin").toString()));
                    userData.setUserBanned(Boolean.parseBoolean(doc.get("isBanned").toString()));
                    userData.setUserBannedTo(doc.get("bannedTo").toString());
                    userData.setShowFirstTimeTip(Boolean.parseBoolean(doc.getString("showFirstTimeTip")));
                    userData.setCreatedAt(Long.parseLong(doc.get("createdAt").toString()));
                    listener.OnUserDataReceived(userData);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("doc", e.getMessage());
                    listener.OnError(e.getMessage());
                }
            });
        }
    }

    public void logout() {
        auth.signOut();
    }

    private HashMap<String, Object> defaultMap(String userUid, String email, String username) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        HashMap<String, Object> map = new HashMap<>();
        map.put("userUid", userUid);
        map.put("email", email);
        map.put("username", username);
        map.put("avatar", "default");
        map.put("description", "Jeszcze nie ustaliłem opisu.");
        map.put("isVerified", "false");
        map.put("isMod", "false");
        map.put("isAdmin", "false");
        map.put("isBanned", "false");
        map.put("showFirstTimeTip", "true"); // fragment_home.xml -> tip_linear
        map.put("banReason", "");
        map.put("bannedTo", "");
        map.put("createdAt", String.valueOf(timestamp.getTime()));

        return map;
    }
}