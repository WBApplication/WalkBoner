package com.fusoft.walkboner.security;

import android.content.Context;

import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.fusoft.walkboner.UnlockAppActivity;

import java.util.concurrent.Executor;

public class BiometricUnlock {
    public static void requestUnlock(Context context, BiometricPrompt.AuthenticationCallback callback) {
        BiometricPrompt.PromptInfo.Builder promptInfo = new BiometricPrompt.PromptInfo.Builder();
        promptInfo.setTitle("Autoryzacja");
        promptInfo.setSubtitle("Dokonaj autoryzacji aby przejść do aplikacji.");
        promptInfo.setConfirmationRequired(false);
        promptInfo.setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK);
        promptInfo.setNegativeButtonText("Anuluj");

        new BiometricPrompt(((UnlockAppActivity) context), ContextCompat.getMainExecutor(context), callback).authenticate(promptInfo.build());
    }

    public static void checkForHardware(Context context, HardwareListener listener) {
        BiometricManager biometricManager = BiometricManager.from(context);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                listener.OnResponse(true, "");
                return;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                listener.OnResponse(false, "Funkcje biometryczne są aktualnie niedostępne na twoim telefonie!");
                return;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                listener.OnResponse(false, "Aby używać funkcji biometrycznych musisz pierw ustawić blokadę ekranu na odcisk palca lub skan twarzy!");
                return;
            case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                listener.OnResponse(false, "Zainstaluj ostatnią aktualizacje bezpieczeństwa!");
                return;
            case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                listener.OnResponse(false, "Nieznany stan funkcji biometrycznych!");
                return;
            case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                listener.OnResponse(false, "Funkcje biometryczne w twoim telefonie nie są obsługiwane!");
                return;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                listener.OnResponse(false, "Twój telefon nie posiada funkcji biometrycznych!");
                return;
            default:
                listener.OnResponse(false, "Nieznany błąd\n" + biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK));
        }
    }

    public interface HardwareListener {
        void OnResponse(Boolean success, String response);
    }
}
