package com.fusoft.walkboner;

import android.app.Application;

import com.fusoft.walkboner.security.Encrypt;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.liulishuo.filedownloader.FileDownloader;

public class WalkBoner extends Application {
    @Override
    public void onCreate() {
        Encrypt.setContext(this);
        FileDownloader.setup(this);
        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());
        super.onCreate();
    }
}
