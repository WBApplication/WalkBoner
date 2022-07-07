package com.fusoft.walkboner.database.funcions;

public interface BanUserListener {
    void OnUserBanned();

    void OnError(String reason);
}
