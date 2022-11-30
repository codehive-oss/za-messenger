# `za-messenger`

Eine Erweiterung des messengers, den Schüler des Informatikunterrichts in NRW bearbeiten müssen.

## Features

- [x] sqlite Datenbank Anbindung
- [x] Basic Auth
- [x] Passwort Hashing
- [x] Broadcast Nachrichten
- [x] De(Serialisierung)
- [x] Bilder
- [ ] Videos
- [ ] Ende zu Ende Verschlüsselung

Contributions sind erwünscht 😊

## How to (run Server)

Um den Server auf Port 20017 zu Starten nutze den Befehl:
\
```./gradlew :messenger-server:run```

oder auf Windows:

```gradlew :messenger-server:run```

Optional kann ein anderer Port angegeben werden:
\
```./gradlew :messenger-server:run --args="12345"```

auf Windows:

```gradlew :messenger-server:run --args="12345"```

## How to (run Client)

Um einen Client zu starten, der sich mit localhost:20017 verbindet:
\
```./gradlew :messenger-client:run```

auf Windows:

```gradlew :messenger-client:run```


Optional können eine andere ip und ein anderer Port angegeben werden:
\
```./gradlew :messenger-client:run --args="192.168.0.58 12345"```

auf Windows:

```gradlew :messenger-client:run --args="192.168.0.58 12345"```
