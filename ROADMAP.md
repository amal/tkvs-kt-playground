# Transactional Key-Value Store Kotlin Playground


## Features:

- DB Console
  + additional commands (help, reset, etc.)
- DB view.
- undo/redo.
- change log.
- settings (lang, theme, implementation, analytics on/off).
- app info, policies, and 3rd party licenses.

- ads.
  - ads disabling subscription for apps?
  - authorization for subscription (all kinds of authorization including web3)


## Interfaces:

##### CLI
_(interactive, signals handling, colored output)_
- jvm
- GraalVM
- [jvm-cli-compose](https://github.com/JakeWharton/mosaic) (mosaic)
- js-node
- wasm-wasi
- kotlin-native

##### Application
- Android _(Compose, Views, Fragments)_
- iOS _(SwiftUI, Compose)_
- Desktop for all OSes (Win, Linux, Mac)
  - Compose
  - Swing
  - JavaFX
  - AWT
  - SWT
  - [SnapKit](https://github.com/reportmill/SnapKit)
  - [lanterna](https://github.com/mabe02/lanterna)
  - Apache Pivot
  - [GTK](https://gitlab.com/gtk-kt/gtk-kt) [[2](https://gitlab.com/gtk-kn/gtk-kn)]
  - [ImGui](https://github.com/Dominaezzz/kotlin-imgui)
  - Flutter UI?
  - ReactNative UI?

##### Web SPA
- Compose JS
- Compose WASM
- TeaVM
- JS frameworks from Kotlin and vice versa
  - React
  - [Preact](https://preactjs.com/)
  - [Reatom](https://t.me/reatom_ru_news)
  - [Vue](https://vuejs.org/)
  - [Angular](https://angular.io/)
  - [Solid](https://www.solidjs.com/)
  - [Svelte](https://svelte.dev/)
  - [Ember](https://emberjs.com/)
  - [Inferno](https://www.infernojs.org/)
  - [Mithril](https://mithril.js.org/)
  - [Aurelia](https://aurelia.io/)
  - [Backbone](https://backbonejs.org/)
  - jQuery
  - vanilla JS/TS
  - etc.


## Implementations

- switchable different implementations
  - Copy-on-Write with Snapshots
    - _Initially, all transactions point to the same data map. On modification, a copy of the modified part is made._
  - _platform-specific like:_
    - Room-based
    - SQLDelight
- some info for each implementation in the apps


## Architecture perfection

- Layers & feature splits (clean arch, etc.)
- Decompose navigation.
- FluxoMVI vs. other MVI libs (switchable?)
- Kotlin-inject for DI.
- Debug-console
  - time-travel
  - coroutines/jobs monitoring
  - tracking events
  - etc.
- Own hosted deep user analytics.
- App protection and anti-tampering techniques.
- Cross-platform ads with own ads.
- Paid app-subscriptions and one time payment.
- Authorization to distinguish persistent stores and app-subscriptions.
- ...


## Build & CI/CD pipeline perfection

- code quality with git hooks.
- shrinking, minification, optimization.
  - Double (R8 + ProGuard) when possible.
- all types of auto-tests.
  - including final package testing.
- publication of releases everywhere
  - F-Droid
  - GitHub repos.
- ...
