# Rescan

Ein Überblick über den Workflow von der Bildaufnahme bis zur Produktverwaltung.

> Kameraaufnahme → Bildverarbeitung → Produktanzeige & Bearbeitung → Speicherung

## Projektbeschreibung

Rescan ist eine Android-App, die Produktinformationen aus Kamerabildern extrahiert, zur Bearbeitung anzeigt und lokal verwaltet.  
Der Fokus liegt auf einem durchgängigen, performanten Workflow von der Aufnahme über die Texterkennung bis zur Speicherung der Produkte.

## Kernfeatures

- Offline-Texterkennung mit ML Kit zur robusten Erkennung von Text auf Quittungen (engl. receipt)
- Moderne, deklarative UI mit Jetpack Compose
- Produktliste mit Bearbeitung direkt in der Listenansicht
- Swipe-Gesten zum schnellen Speichern oder Verwerfen einzelner Produkte
- Simulierte lokale Datenbank (MockDb) für CRUD-Operationen auf Produktdaten

## Architektur und Komponenten

Die App ist in klar getrennte Komponenten strukturiert:

- ScanScreen  
  Startet die Kamera und initiiert die Bildaufnahme.

- ScanAdapter  
  Verarbeitet das aufgenommene Bild, führt die Texterkennung durch und extrahiert Produktdaten.

- ItemSelectScreen  
  Zeigt die erkannten Produkte in einer Liste, ermöglicht Bearbeitung und Kategorisierung.

- ProductRow  
  Einzelne Produktzeile mit Eingabefeldern und Swipe-Gesten für Aktionen (Speichern/Verwerfen).

- MockDb  
  Simulierte Datenbank, die CRUD-Operationen auf Produktobjekten bereitstellt.

## UI und Interaktion

### ItemSelectScreen mit LazyColumn

Die erkannten Produkte werden in einer LazyColumn dargestellt:

- Effiziente Darstellung auch bei langen Listen
- Jeder Eintrag basiert auf einem `Product`-Objekt
- Verwendung von `product.id` als Key für stabile und performante UI-Updates

### SwipeToDismiss in ProductRow

Jede Produktzeile ist mit SwipeToDismiss umschlossen:

- Swipe nach rechts (DismissedToEnd): Produkt wird gespeichert
- Swipe nach links (DismissedToStart): Produkt wird verworfen
- `rememberDismissState` verwaltet den Swipe-Zustand
- Logik in `confirmStateChange` prüft Bedingungen (z. B. Pflichtfelder) und führt die passende Aktion aus
- Visuelles Feedback durch farbige Hintergründe (z. B. grün für Speichern, rot für Verwerfen)

## Ablauf des Workflows

1. Der Nutzer öffnet den ScanScreen und nimmt ein Bild auf.  
2. Der ScanAdapter verarbeitet das Bild, erkennt Text und extrahiert daraus Produktobjekte.  
3. Im ItemSelectScreen werden alle erkannten Produkte in einer scrollbaren Liste angezeigt.  
4. Der Nutzer kann Produkte direkt in der Liste bearbeiten (z. B. Namen, Handel, Kategorie).  
5. Über Swipe-Gesten werden Produkte entweder gespeichert (in MockDb) oder verworfen.  
6. Die Liste aktualisiert sich unmittelbar, indem die betroffene Zeile entfernt wird.

## Technologien

- Kotlin
- Jetpack Compose
- ML Kit (Offline-Texterkennung)
- CameraX (Kameraaufnahme)
- MockDb (lokale Simulation einer Datenbank)

## Installation und Ausführung

1. Repository klonen.
2. Projekt in Android Studio öffnen.
3. Ein Android-Gerät oder Emulator mit Kamerazugriff konfigurieren.
4. App ausführen und den Scan-Workflow durchlaufen.

