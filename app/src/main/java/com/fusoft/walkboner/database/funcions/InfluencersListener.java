package com.fusoft.walkboner.database.funcions;

import com.fusoft.walkboner.models.Influencer;

import java.util.List;

public interface InfluencersListener {
    void OnDataReceived(List<Influencer> influencers);

    void OnError(String reason);
}
