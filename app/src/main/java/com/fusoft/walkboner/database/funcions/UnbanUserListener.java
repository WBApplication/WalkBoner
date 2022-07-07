package com.fusoft.walkboner.database.funcions;

public interface UnbanUserListener {
    void OnUserUnbanned();

    void OnError(String reason);
}
