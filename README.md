# PoisnMod

Clientseitige Fabric-Mod für Minecraft 26.2 und HugoSMP. Die Mod ergänzt Clan-Nametags und zeigt online befindliche eingetragene Clan-Mitglieder in einem kleinen HUD an.

## Features
- Zusätzliche Zeilen ober- und unterhalb des Spieler-Nametags
- Clan-Ränge und Clan-Farben pro Mitglied
- Online-Clan-HUD (nur Mitglieder aus der lokalen Mitgliederliste)
- Groß-/Kleinschreibung bei Spielernamen wird ignoriert
- Eigene lokale Mitgliederliste statt fest eingebetteter Liste
- Aktiv nur auf HugoSMP-Adressen

## Konfiguration
Beim ersten Start wird `config/poisnmod-members.json` aus den Standarddaten erstellt. Diese Datei kannst du bearbeiten, ohne die Mod neu zu bauen. Änderungen werden beim nächsten Spielstart geladen.

Beispiel:
```json
{
  "Player": "SpielerName",
  "Rang": "Leader",
  "Clan": "POISN",
  "UeberNamen": "POISN Leader",
  "FarbeUeN": "#55FF55",
  "UnterNamen": "Dein Zusatztext",
  "FarbeUN": "&b",
  "FarbeClan": "#55FF55"
}
```

Farben: Minecraft-Farbcode wie `&a` oder Hex wie `#55FF55`.

Die Online-Anzeige zeigt nur Mitglieder an, die sich in deiner lokalen Mitgliederliste befinden und gerade in deiner geladenen Welt sichtbar/online sind. Das ist keine serverseitige Clan-Abfrage: HugoSMP stellt der Mod derzeit keine bestätigte öffentliche Clan-API bereit.

## Build
- JDK 25
- Gradle 9.6.1
- Minecraft 26.2 / Fabric Loom 1.17.x

```bash
gradle --no-daemon build
```

Die JAR liegt unter `build/libs/`.
