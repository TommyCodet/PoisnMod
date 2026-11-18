# PoisnMod

Clientseitige Fabric-Mod für Minecraft 26.2. Sie rendert zusätzliche Zeilen ober- und unterhalb des normalen Spieler-Nametags, ausschließlich wenn die verbundene Serveradresse `hugosmp.net` enthält.

## Build-Voraussetzungen

- JDK 25
- Gradle 9.6.1 (Fabric Loom 1.17.x)
- Minecraft 26.2

**Warum Gradle statt Maven?** Fabric Loom ist offiziell ein Gradle-Plugin und kein Maven-Plugin. Ein Maven-Build mit einem angeblichen Loom-Maven-Plugin wäre nicht unterstützt. Dieses Repository verwendet deshalb den offiziellen Fabric-Buildweg. Fabric dokumentiert für 26.2 Loom 1.17 und Gradle 9.5.1 oder neuer.

## Bauen

Wenn noch kein Wrapper vorhanden ist, einmalig mit installiertem Gradle 9.6.1 ausführen:

```bash
gradle wrapper --gradle-version 9.6.1
```

Danach:

```bash
./gradlew build
```

Unter Windows:

```bat
gradlew.bat build
```

Die Mod-JAR liegt anschließend in `build/libs/`.

## Mitglieder bearbeiten

Die fest eingebettete Liste liegt in:

`src/main/resources/poisn_members.json`

Die Datei wird direkt in die Mod-JAR gepackt. Neue Einträge dort ergänzen und anschließend neu bauen. Es wird keine Config-Datei im Minecraft-config-Ordner angelegt.

Felder:

- `Player`: exakter, case-sensitiver Minecraft-Name
- `UeberNamen`: Text oberhalb des normalen Nametags
- `FarbeUeN`: Farbe des oberen Textes
- `UnterNamen`: Text unterhalb des normalen Nametags
- `FarbeUN`: Farbe des unteren Textes

Farben: Minecraft-Codes wie `&a`, `&2`, `&b`, `&f` oder Hex wie `#00FF7F`. Leere/ungültige Werte werden weiß dargestellt.

Die normale Vanilla-Namensanzeige bleibt erhalten. Die Mod ist client-only; andere Spieler sehen die Zusatzzeilen nur, wenn sie selbst die Mod installiert haben.

## Hinweis

Der Server-Check verwendet die vom Client bekannte Serveradresse, also die Proxy-Adresse, nicht den Backend-Servernamen.
