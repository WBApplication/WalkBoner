package com.fusoft.walkboner.auth;

import androidx.annotation.Nullable;

import com.fusoft.walkboner.models.User;

public interface AuthenticationListener {

    /**
     * Triggers when user are already logged in
     */
    void UserAlreadyLoggedIn(boolean success, boolean isBanned, boolean pinRequired, @Nullable String reason, @Nullable User userData);

    /**
     * Triggers when user is not logged in - this method can be used in SplashActivity to redirect user to AuthActivity or to MainActivity if is logged.
     */
    void UserRequiredToBeLogged();

    /**
     *
     * @param isSuccess returns true if operation is ended with success or false if something went wrong
     * @param reason if isSuccess = false, then it returns reason why it failed. It will be null when isSuccess is true.
     */
    void OnLogin(boolean isSuccess, boolean isBanned, boolean pinRequired, @Nullable String reason);

    /**
     *
     * @param isSuccess returns true if operation is ended with success or false if something went wrong
     * @param reason if isSuccess = false, then it returns reason why it failed. It will be null when isSuccess is true.
     */
    void OnRegister(boolean isSuccess, @Nullable String reason);
}
