<img src="https://github.com/WBApplication/WalkBoner/blob/master/WalkBoner_Presentation.png?raw=true"/>

<div align="center">
<h1>WalkBoner</h1>

WalkBoner jest to aplikacja na systemy Android, czerpiąca inspirację z Instagrama, a także ze [strony Reddit](https://www.reddit.com/r/SexyPolishYoutuber/), która zyskała spore community.

Celem aplikacji jest archiwizacja, publikacja lub tworzenie albumów zdjęć znanych influencerów.
</div>

Aplikacja korzysta z następujących bibliotek:

- [OneUI | Yanndroid](https://github.com/OneUIProject/OneUI-Design-Library) (UI)

- [Firebase | Google](https://firebase.google.com/) (Serwer)

- [Glide | Bumptech](https://github.com/bumptech/glide) (Ładowanie Obrazów)

- [Lottie | Airbnb](https://github.com/airbnb/lottie-android) (Animowane Grafiki)

- [RollingText | YvesCheung](https://github.com/YvesCheung/RollingText) (
Animacja Zmiany Tekstu)

- [ReadMoreTextView | PRND](https://github.com/PRNDcompany/ReadMoreTextView) (Zwijanie Długiego Tekstu)

# Raportowanie Błędów

Zwykle błędy są raportowane automatycznie - jednak aby się to stało, po wystąpieniu błędu musisz uruchomić aplikację ponownie, aby zgłoszenie zostało wysłane na nasz serwer. Możesz jednak to też zrobić ręcznie przechodząc do zakładki [Issues](https://github.com/WBApplication/WalkBoner/issues) i tam utworzyć nowy wątek lub zgłoś problem w już istniejącym, podobnym wątku.

# Pomoc w usprawnianiu aplikacji

Jeśli znasz poprawkę dla błedu w zakładce [Issues](https://github.com/WBApplication/WalkBoner/issues) chętnie jej się przyjrzymy. W przypadku pomysłu na nową funkcję, utwórz wątek w zakładce [Issues](https://github.com/WBApplication/WalkBoner/issues) i opisz działanie funkcji.
Aktualnie aplikacją zawiera sporo tzw. Memory Leaks.

# Kompilacja

<details><summary>Uwaga</summary>

`Kontent dostępny w oryginalnej aplikacji WalkBoner nie będzie dostępny w aplikacji skompilowanej przez ciebie jednak funkcje pozostaną te same!`

</details>

Aby skompilować aplikacje, potrzebujesz <a href="https://developer.android.com/">Android Studio</a>, następnie <a href="https://www.geeksforgeeks.org/how-to-clone-android-project-from-github-in-android-studio/">przeczytaj ten artykuł</a>, aby dowiedzieć się jak sklonować aplikację na urządzenie lokalne, by móc edytować lub skompilować aplikacje.
<br><br>
Aplikacja po sklonowaniu nie skompiluje się ponieważ z projektu dostępnym na GitHub został usunięty plik **__google-services.json__** - plik ten zawiera klucze pozwalające na dostęp do bazy danych Firebase. Aby utworzyć plik <b><i>google-services.json</b></i> musisz utworzyć konto <a href="https://firebase.google.com/">Firebase</a>, a następnie utworzyć tam nowy projekt, podając te informacje:

Nazwa Projektu:
`
Dowolna
`

Google Analytics:
`
Włączone
`

Domyślne konto Google Analytics:
`
Domyślne Konto Firebase
`

**Po utworzeniu projektu** kliknij ikonkę **Androida** i dalej:

Nazwa Pakietu Aplikacji:
`
com.fusoft.walkboner
`

Przejdź dalej i pobierz plik **google-services.json**, po pobraniu wklej go w folderze `WalkBoner/app` (np **C:\Users\nazwa_uzytkownika\AndroidStudioProjects\WalkBoner\app**). Klikaj `dalej`.

Następnie po lewej stronie kliknij `Build`, klikaj i włączaj kolejno:

- Authentication (i włącz logowanie poprzez Email i Hasło)

- Firestore Database, po włączeniu kliknij `Rules` i wklej tam:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write;
    }
  }
}
```

Jest to niebezpieczne ponieważ twoja baza danych jest dostępna dla każdego kto posiada klucze i może edytować oraz czytać zawartość.

- Storage, po włączeniu kliknij `Rules` i wklej tam:

```
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write;
    }
  }
}
```

Jest to niebezpieczne ponieważ twoja baza danych plików jest dostępna dla każdego kto posiada klucze i może usuwać, pobierać oraz czytać zawartość.

**Następnie** w sekcji `Storage` wróć z powrotem do zakładki `Files` i skopiuj URL do bazy plików (np. gs://walkboner-ba89b.appspot.com)

Wklej ten link w pliku strings.xml (w Android Studio), tak aby plik wyglądał w następujący sposób:

```xml
<resources>
    <string name="app_name">WalkBoner</string>

    <string name="discord_invite_url">EMPTY</string>

    <string name="storage_url">gs://walkboner-ba89b.appspot.com</string> <!-- Wklej tutaj URL do bazy danych -->

    <string name="sesl_font_family_regular">sans-serif</string>
</resources>
```

Teraz możesz skompilować aplikację.
