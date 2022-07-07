package com.fusoft.walkboner.moderation.utils;

public interface CounterListener {
    void OnResponse(int amountOfInfluencersToModerate);

    void OnError(String reason);
}
