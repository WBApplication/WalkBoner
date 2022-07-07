package com.fusoft.walkboner.auth;

import com.fusoft.walkboner.models.User;

public interface UserInfoListener {
    void OnUserDataReceived(User user);

    void OnUserNotFinded();

    void OnError(String reason);
}
