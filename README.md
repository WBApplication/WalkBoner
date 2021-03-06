<img src="https://github.com/WBApplication/WalkBoner/blob/master/WalkBoner_Presentation.png?raw=true"/>

<div align="center">
<h1>WalkBoner</h1>

馃敒

**Soft / Kontent Dla Doros艂ych**

WalkBoner jest to aplikacja na systemy Android, czerpi膮ca inspiracj臋 z Instagrama, a tak偶e ze [strony Reddit](https://www.reddit.com/r/SexyPolishYoutuber/), kt贸ra zyska艂a spore community.

Celem aplikacji jest archiwizacja, publikacja lub tworzenie album贸w zdj臋膰 znanych influencer贸w.
</div>

Aplikacja korzysta z nast臋puj膮cych bibliotek:

- [OneUI | Yanndroid](https://github.com/OneUIProject/OneUI-Design-Library) (UI)

- [Firebase | Google](https://firebase.google.com/) (Serwer)

- [Glide | Bumptech](https://github.com/bumptech/glide) (艁adowanie Obraz贸w)

- [Lottie | Airbnb](https://github.com/airbnb/lottie-android) (Animowane Grafiki)

- [RollingText | YvesCheung](https://github.com/YvesCheung/RollingText) (
Animacja Zmiany Tekstu)

- [ReadMoreTextView | PRND](https://github.com/PRNDcompany/ReadMoreTextView) (Zwijanie D艂ugiego Tekstu)

# Raportowanie B艂臋d贸w

Zwykle b艂臋dy s膮 raportowane automatycznie - jednak aby si臋 to sta艂o, po wyst膮pieniu b艂臋du musisz uruchomi膰 aplikacj臋 ponownie, aby zg艂oszenie zosta艂o wys艂ane na nasz serwer. Mo偶esz jednak to te偶 zrobi膰 r臋cznie przechodz膮c do zak艂adki [Issues](https://github.com/WBApplication/WalkBoner/issues) i tam utworzy膰 nowy w膮tek lub zg艂o艣 problem w ju偶 istniej膮cym, podobnym w膮tku.

# Pomoc w usprawnianiu aplikacji

Je艣li znasz poprawk臋 dla b艂edu w zak艂adce [Issues](https://github.com/WBApplication/WalkBoner/issues) ch臋tnie jej si臋 przyjrzymy. W przypadku pomys艂u na now膮 funkcj臋, utw贸rz w膮tek w zak艂adce [Issues](https://github.com/WBApplication/WalkBoner/issues) i opisz dzia艂anie funkcji.

# Kompilacja

<details><summary>Uwaga</summary>

`Kontent dost臋pny w oryginalnej aplikacji WalkBoner nie b臋dzie dost臋pny w aplikacji skompilowanej przez ciebie jednak funkcje pozostan膮 te same!`

</details>

Aby skompilowa膰 aplikacje, potrzebujesz <a href="https://developer.android.com/">Android Studio</a>, nast臋pnie <a href="https://www.geeksforgeeks.org/how-to-clone-android-project-from-github-in-android-studio/">przeczytaj ten artyku艂</a>, aby dowiedzie膰 si臋 jak sklonowa膰 aplikacj臋 na urz膮dzenie lokalne, by m贸c edytowa膰 lub skompilowa膰 aplikacje.
<br><br>
Aplikacja po sklonowaniu nie skompiluje si臋 poniewa偶 z projektu dost臋pnym na GitHub zosta艂 usuni臋ty plik **__google-services.json__** - plik ten zawiera klucze pozwalaj膮ce na dost臋p do bazy danych Firebase. Aby utworzy膰 plik <b><i>google-services.json</b></i> musisz utworzy膰 konto <a href="https://firebase.google.com/">Firebase</a>, a nast臋pnie utworzy膰 tam nowy projekt, podaj膮c te informacje:

Nazwa Projektu:
`
Dowolna
`

Google Analytics:
`
W艂膮czone
`

Domy艣lne konto Google Analytics:
`
Domy艣lne Konto Firebase
`

**Po utworzeniu projektu** kliknij ikonk臋 **Androida** i dalej:

Nazwa Pakietu Aplikacji:
`
com.fusoft.walkboner
`

Przejd藕 dalej i pobierz plik **google-services.json**, po pobraniu wklej go w folderze `WalkBoner/app` (np **C:\Users\nazwa_uzytkownika\AndroidStudioProjects\WalkBoner\app**). Klikaj `dalej`.

Nast臋pnie po lewej stronie kliknij `Build`, klikaj i w艂膮czaj kolejno:

- Authentication (i w艂膮cz logowanie poprzez Email i Has艂o)

- Firestore Database, po w艂膮czeniu kliknij `Rules` i wklej tam:

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

Jest to niebezpieczne poniewa偶 twoja baza danych jest dost臋pna dla ka偶dego kto posiada klucze i mo偶e edytowa膰 oraz czyta膰 zawarto艣膰.

- Storage, po w艂膮czeniu kliknij `Rules` i wklej tam:

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

Jest to niebezpieczne poniewa偶 twoja baza danych plik贸w jest dost臋pna dla ka偶dego kto posiada klucze i mo偶e usuwa膰, pobiera膰 oraz czyta膰 zawarto艣膰.

**Nast臋pnie** w sekcji `Storage` wr贸膰 z powrotem do zak艂adki `Files` i skopiuj URL do bazy plik贸w (np. gs://walkboner-ba89b.appspot.com)

Wklej ten link w pliku strings.xml (w Android Studio), tak aby plik wygl膮da艂 w nast臋puj膮cy spos贸b:

```xml
<resources>
    <string name="app_name">WalkBoner</string>

    <string name="discord_invite_url">EMPTY</string>

    <string name="storage_url">gs://walkboner-ba89b.appspot.com</string> <!-- Wklej tutaj URL do bazy danych -->

    <string name="sesl_font_family_regular">sans-serif</string>
</resources>
```

Teraz mo偶esz skompilowa膰 aplikacj臋.
