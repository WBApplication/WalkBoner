package com.fusoft.walkboner.views.dialogs;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fusoft.walkboner.auth.Authentication;
import com.fusoft.walkboner.auth.AuthenticationListener;
import com.fusoft.walkboner.utils.UidGenerator;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import de.dlyt.yanndroid.oneui.dialog.AlertDialog;
import de.dlyt.yanndroid.oneui.view.Toast;

public class ReportDialog {
    static int prevSelected = 0;

    public static void ReportPostDialog(Context context, String postUid) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
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

        CharSequence[] items = {
                "Nieodpowiedni avatar użytkownika",
                "Nieodpowiedna nazwa użytkownika",
                "Opis postu zawiera nieodpowiednie treści",
                "Nieodpowiednie zdjęcie"};

        AlertDialog.Builder report = new AlertDialog.Builder(context);
        report.setSingleChoiceItems(items, prevSelected, (dialogInterface, i) -> {
            prevSelected = i;
        });
        report.setTitle("Wybierz Powód:");
        report.setNegativeButton("Anuluj", null);
        report.setPositiveButton("Zgłoś", (dialogInterface, i) -> {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            HashMap<String, Object> map = new HashMap<>();
            map.put("reportUid", UidGenerator.Generate());
            map.put("postUid", postUid);
            map.put("reportReason", items[prevSelected].toString());
            map.put("reportedAt", String.valueOf(timestamp.getTime()));
            map.put("reportsAmount", "1");

            firestore.collection("reports").whereEqualTo("postUid", postUid).get().addOnSuccessListener(queryDocumentSnapshots -> {
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        doc.getReference().collection("reportedBy").whereEqualTo("user", authentication.getCurrentUserUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot docUserAlreadyReported : queryDocumentSnapshots.getDocuments()) {
                                    new ErrorDialog().SimpleErrorDialog(context, "Ten post został już przez ciebie zgłoszony!");
                                    return;
                                }

                                // Jeśli użytkownik zgłasza ten post po raz pierwszy, a post został już zgłoszony wcześniej przez innego użytkownikam, dodaj go do listy zgłaszających.
                                HashMap<String, Object> newMap = new HashMap<>();
                                newMap.put("reportsAmount", String.valueOf(Integer.parseInt(doc.get("reportsAmount").toString()) + 1));

                                firestore.collection("reports").document(doc.getId()).update(newMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        HashMap<String, Object> userMap = new HashMap<>();
                                        userMap.put("user", authentication.getCurrentUserUid());
                                        firestore.collection("reports").document(doc.getId()).collection("reportedBy").add(userMap);
                                        Toast.makeText(context, "Dziękujemy za zgłoszenie!", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Nie udało się wysłać zgłoszenia!\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    return;
                }

                // Jeśli Report nie istnieje, utwórz nowy.
                firestore.collection("reports").add(map).addOnSuccessListener(documentReference -> {
                    HashMap<String, Object> userMap = new HashMap<>();
                    userMap.put("user", authentication.getCurrentUserUid());
                    documentReference.collection("reportedBy").add(userMap);
                    Toast.makeText(context, "Dziękujemy za zgłoszenie!", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> Toast.makeText(context, "Nie udało się wysłać zgłoszenia!\n" + e.getMessage(), Toast.LENGTH_SHORT).show());
            }).addOnFailureListener(e -> Toast.makeText(context, "Nie udało się wysłać zgłoszenia!\n" + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
        report.show();
    }
}
