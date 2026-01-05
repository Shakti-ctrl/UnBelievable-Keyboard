# Unexpected Keyboard

## Overview

Unexpected Keyboard is a lightweight, privacy-conscious virtual keyboard for Android. The main feature is swipe-to-corner input, allowing users to type additional characters by swiping keys toward their corners. Originally designed for programmers using Termux, it's now suitable for everyday use. The app contains no ads, makes no network requests, and is fully open source.

## User Preferences

Preferred communication style: Simple, everyday language.

## System Architecture

### Build System
- **Gradle-based Android project** using OpenJDK 17
- **Target SDK**: Android platform 30 with build tools minimum 28.0.1
- **Nix support**: Development environment can be configured via `shell.nix`
- Debug APK output: `build/outputs/apk/debug/app-debug.apk`

### Keyboard Layout System
- **Layout definitions**: XML files in `srcs/layouts/` define keyboard layouts
- **Layout structure**: Each layout uses `<keyboard>`, `<row>`, and `<key>` XML tags
- **Key positioning**: Keys support 9 positions (center + 8 compass directions: nw, n, ne, e, se, s, sw, w)
- **Custom layouts**: Users can create custom layouts via XML with configurable bottom rows, number rows, and scripts

### Compose/Character System
- **Compose sequences**: JSON files in `srcs/compose/` define character compositions (accents, diacritics, special characters)
- **Compile process**: `srcs/compose/compile.py` generates `ComposeKeyData.java` from compose definitions
- **Accent support**: Extensive accent/diacritic support via files like `accent_aigu.json`, `accent_grave.json`, etc.
- **Script-specific numpads**: Separate numpad definitions for Bengali, Devanagari, Arabic, Persian, etc.

### Code Generation
- **Python 3 scripts** generate various resources:
  - `gen_layouts.py`: Generates `res/values/layouts.xml` from layout files
  - `gen_emoji.py`: Downloads and parses Unicode emoji data into `res/raw/emojis.txt`
  - `gen_method_xml.py`: Generates `res/xml/method.xml` for input method configuration
  - `sync_translations.py`: Synchronizes translation files across locales

### Localization
- **Translation management**: Weblate integration for community translations
- **Metadata storage**: `fastlane/metadata/android/{locale}/` contains store descriptions
- **Locale configuration**: `gen_method_xml.py` defines locale-to-layout mappings with extra keys per language

### Key Value System
- Keys can be special key names, character sequences, or complex definitions with legends
- Macro support: Multiple key actions can be combined
- Android keycode support for hardware key simulation

## External Dependencies

### Build Dependencies
- OpenJDK 17
- Android SDK (build tools 28.0.1+, platform 30)
- Python 3 (for code generation scripts only)

### External Data Sources
- Unicode emoji data: Downloaded from `unicode.org/Public/emoji/latest/emoji-test.txt`
- XKB compose sequences: `srcs/compose/en_US_UTF_8_Compose.pre` from xorg project

### Translation Service
- Weblate: `https://hosted.weblate.org/engage/unexpected-keyboard/`

### Distribution Channels
- F-Droid
- Google Play Store

### No Runtime Dependencies
- No network requests at runtime
- No analytics or tracking
- No advertisements