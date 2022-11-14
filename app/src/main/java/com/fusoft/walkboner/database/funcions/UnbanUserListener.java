package com.fusoft.walkboner.database.funcions;

public interface UnbanUserListener {
    void OnUserUnbanned();

    default void OnError(String reason) {}
}
