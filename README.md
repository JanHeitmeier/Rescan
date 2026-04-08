# Rescan

An overview of the workflow from image capture to product management.

> Camera capture → Image processing → Product display & editing → Persistence

## Project description

Rescan is an Android app that extracts product information from camera images, presents it for editing, and manages it locally.  
The focus is on an end-to-end, performant workflow from image capture through text recognition to product storage.

## Key features

- Offline text recognition with ML Kit for robust extraction of text from receipts
- Modern, declarative UI built with Jetpack Compose
- Product list with inline editing directly in the list view
- Swipe gestures for quickly saving or discarding individual products
- Simulated local database (MockDb) providing CRUD operations for product data

## Architecture and components

The app is structured into clearly separated components:

- ScanScreen  
  Starts the camera and initiates image capture.

- ScanAdapter  
  Processes the captured image, performs text recognition, and extracts product data.

- ItemSelectScreen  
  Displays the recognized products in a list and allows editing and categorization.

- ProductRow  
  Single product row with input fields and swipe gestures for actions (save/discard).

- MockDb  
  Simulated database providing CRUD operations for product objects.

## UI and interaction

### ItemSelectScreen with LazyColumn

Recognized products are displayed in a LazyColumn:

- Efficient rendering even for long lists
- Each entry is based on a `Product` object
- Uses `product.id` as the key to ensure stable and performant UI updates

### SwipeToDismiss in ProductRow

Each product row is wrapped with SwipeToDismiss:

- Swipe to the right (DismissedToEnd): product is saved
- Swipe to the left (DismissedToStart): product is discarded
- `rememberDismissState` manages the swipe state
- Logic in `confirmStateChange` validates conditions (e.g., required fields) and triggers the appropriate action
- Visual feedback via colored backgrounds (e.g., green for save, red for discard)

## Workflow

1. The user opens the ScanScreen and captures an image.  
2. The ScanAdapter processes the image, recognizes text, and extracts product objects.  
3. The ItemSelectScreen displays all recognized products in a scrollable list.  
4. The user can edit products directly in the list (e.g., name, store, category).  
5. Using swipe gestures, products are either saved (in MockDb) or discarded.  
6. The list updates immediately by removing the affected row.

## Technologies

- Kotlin
- Jetpack Compose
- ML Kit (offline text recognition)
- CameraX (camera capture)
- MockDb (local database simulation)

## Installation and usage

1. Clone the repository.  
2. Open the project in Android Studio.  
3. Configure an Android device or emulator with camera access.  
4. Run the app and go through the scan workflow.

---

# Rescan (Deutsch)

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
