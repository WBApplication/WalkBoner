package com.fusoft.walkboner.database.funcions;

import androidx.annotation.NonNull;

import com.fusoft.walkboner.models.Influencer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GetInfluencers {
    public static String ALPHABET_A_Z = "ALPHABET_A_Z";
    public static String ALPHABET_Z_A = "ALPHABET_Z_A";
    public static String ORDER_MOST_VIEWED = "MOST_VIEWED";

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private FirebaseUser user;

    public GetInfluencers(String sort, InfluencersListener listener) {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        List<Influencer> influencers = new ArrayList<>();

        firestore = FirebaseFirestore.getInstance();
        firestore.collection("influencers").orderBy("viewsCount", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    Influencer influencer = new Influencer();
                    influencer.setInfluencerUid(doc.getString("influencerUid"));
                    influencer.setInfluencerAddedBy(doc.getString("influencerAddedBy"));
                    influencer.setInfluencerFirstName(doc.getString("influencerFirstName"));
                    influencer.setInfluencerLastName(doc.getString("influencerLastName"));
                    influencer.setInfluencerNickName(doc.getString("influencerNickName"));
                    influencer.setInfluencerDescription(doc.getString("influencerDescription"));
                    influencer.setInfluencerAvatar(doc.getString("influencerAvatar"));
                    influencer.setInfluencerInstagramLink(doc.getString("influencerInstagramLink"));
                    influencer.setInfluencerYouTubeLink(doc.getString("influencerYouTubeLink"));
                    influencer.setInfluencerTikTokLink(doc.getString("influencerTikTokLink"));
                    influencer.setInfluencerModeratorUid(doc.getString("influencerModeratorUid"));
                    influencer.setVerified(Boolean.parseBoolean(doc.get("isVerified").toString()));
                    influencer.setPremium(Boolean.parseBoolean(doc.get("isPremium").toString()));
                    influencer.setHidden(Boolean.parseBoolean(doc.get("isHidden").toString()));
                    influencer.setInfluencerHasInstagram(Boolean.parseBoolean(doc.get("hasInstagram").toString()));
                    influencer.setInfluencerHasYouTube(Boolean.parseBoolean(doc.get("hasYouTube").toString()));
                    influencer.setInfluencerHasTikTok(Boolean.parseBoolean(doc.get("hasTikTok").toString()));
                    influencer.setMaintained(Boolean.parseBoolean(doc.get("isMaintained").toString()));
                    influencer.setInfluencerAddedAt(Long.parseLong(doc.get("influencerAddedAt").toString()));
                    influencer.setMaintainedTo(Long.parseLong(doc.get("maintainedTo").toString()));
                    influencer.setUserFollowing(false);
                    influencer.setViewsCount(Integer.parseInt(doc.get("viewsCount").toString()));

                    List<String> followersList = new ArrayList<>();
                    firestore.collection("influencers").document(doc.getId()).collection("followers").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                followersList.add(documentSnapshot.getString("userUid"));
                                if (documentSnapshot.getString("userUid").contentEquals(user.getUid())) {
                                    influencer.setUserFollowing(true);
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            listener.OnError(e.getMessage());
                        }
                    });

                    influencer.setFollowers(followersList);

                    if (!influencer.isHidden()) influencers.add(influencer);
                }

                listener.OnDataReceived(influencers);
            }
        });
    }
}
