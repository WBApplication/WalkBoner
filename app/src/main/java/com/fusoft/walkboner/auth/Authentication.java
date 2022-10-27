package com.fusoft.walkboner.auth;

import android.text.format.DateFormat;
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
import java.util.Calendar;
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
        log("Initialized");

        this.listener = listener;

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (auth.getCurrentUser() != null) {
            log("User Already Logged");
            user = auth.getCurrentUser();

            log("Getting User Data...");
            getUserData(new UserInfoListener() {
                @Override
                public void OnUserDataReceived(User userData) {
                    log("---> Success!");
                    log("Checking if PIN is Required...");
                    firestore.collection("users").whereEqualTo("userUid", user.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                documentSnapshot.getReference().collection("pin").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if (queryDocumentSnapshots.isEmpty()) {
                                            if (isListenerAvailable()) {
                                                if (userData.isUserBanned()) {
                                                    log("---> Success");
                                                    log("---> PIN Is Not Required but user is banned");
                                                    listener.UserAlreadyLoggedIn(true, userData.isUserBanned(), false, "Urządzenie Zbanowane\nPowód:\n" + userData.getUserBanReason() + "\n\nZbanowano do: " + getDate(Long.parseLong(userData.getUserBannedTo())) + "\n\nJeśli uważasz, że nie powinieneś dostać bana, odwołaj się na serwerze Discord\n\nTwój Identyfikator\n(kliknij by skopiować)\n" + userData.getUserUid(), null);
                                                } else {
                                                    log("---> Success");
                                                    log("---> PIN Is Not Required");
                                                    listener.UserAlreadyLoggedIn(true, false, false, null, userData);
                                                }
                                            }
                                        } else {
                                            if (isListenerAvailable()) {
                                                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                                                if (userData.isUserBanned()) {
                                                    log("---> Success");
                                                    log("---> PIN Is Required but user is banned");
                                                    listener.UserAlreadyLoggedIn(true, userData.isUserBanned(), Boolean.parseBoolean(doc.getString("ENABLED")), "Urządzenie Zbanowane\nPowód:\n" + userData.getUserBanReason() + "\n\nZbanowano do: " + getDate(Long.parseLong(userData.getUserBannedTo())) + "\n\nJeśli uważasz, że nie powinieneś dostać bana, odwołaj się na serwerze Discord\n\nTwój Identyfikator\n(kliknij by skopiować)\n" + userData.getUserUid(), null);
                                                } else {
                                                    log("---> Success");
                                                    log("---> PIN Is Required");
                                                    listener.UserAlreadyLoggedIn(true, false, Boolean.parseBoolean(doc.getString("ENABLED")), null, userData);
                                                }
                                            }
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        log("---> Failure");
                                        log("---> Line 83");
                                        if (isListenerAvailable()) {
                                            listener.UserAlreadyLoggedIn(false, false, false, e.getMessage(), null);
                                        }
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            log("---> Failure");
                            log("---> Line 95");
                            if (isListenerAvailable()) {
                                listener.UserAlreadyLoggedIn(false, false, false, e.getMessage(), null);
                            }
                        }
                    });
                }

                @Override
                public void OnUserNotFinded() {
                    listener.UserAlreadyLoggedIn(false, false, false, "Nie znaleźliśmy takiego użytkownika w bazie! Napisz do nas na Discordzie!", null);
                }

                @Override
                public void OnError(String reason) {
                    if (isListenerAvailable()) {
                        listener.UserAlreadyLoggedIn(false, false, false, reason, null);
                    }
                }
            });
        } else {
            if (isListenerAvailable()) {
                listener.UserRequiredToBeLogged();
            }
        }
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void login(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            user = authResult.getUser();

            getUserData(new UserInfoListener() {
                @Override
                public void OnUserDataReceived(User userData) {
                    firestore.collection("users").whereEqualTo("userUid", userData.getUserUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            DocumentSnapshot userSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            userSnapshot.getReference().collection("pin").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    DocumentSnapshot pin = queryDocumentSnapshots.getDocuments().get(0);
                                    if (isListenerAvailable()) {
                                        if (userData.isUserBanned()) {
                                            listener.OnLogin(false, true, Boolean.parseBoolean(pin.getString("ENABLED")), "Urządzenie Zbanowane\nPowód:\n" + userData.getUserBanReason() + "\n\nZbanowano do: " + getDate(Long.parseLong(userData.getUserBannedTo())) + "\n\nJeśli uważasz, że nie powinieneś dostać bana, odwołaj się na serwerze Discord\n\nTwój Identyfikator\n(kliknij by skopiować)\n" + userData.getUserUid());
                                        } else {
                                            listener.OnLogin(true, false, Boolean.parseBoolean(pin.getString("ENABLED")), null);
                                        }
                                    }
                                    return;
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if (isListenerAvailable()) {
                                        listener.OnLogin(false, false, false, e.getMessage());
                                    }
                                }
                            });
                        }
                    });
                }

                @Override
                public void OnUserNotFinded() {
                    if (isListenerAvailable()) {
                        listener.OnLogin(false, false, false, "Nie znaleziono użytkownika w naszej bazie. Daj nam o tym znać na Discordzie!");
                    }
                }

                @Override
                public void OnError(String reason) {
                    if (isListenerAvailable()) {
                        listener.OnLogin(false, false, false, reason);
                    }
                }
            });
        }).addOnFailureListener(e -> {
            if (isListenerAvailable()) {
                listener.OnLogin(false, false, false, e.getMessage());
            }
        });
    }

    /*public void login(String email, String password) {
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
    }*/

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

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        String date = DateFormat.format("HH:mm dd-MM-yyyy", cal).toString();
        return date;
    }

    private void log(String message) {
        Log.d("Authentication", message);
    }
}