# Notebook Games — Proyecto DAM

Proyecto de final de grado de Desarrollo de Aplicaciones Multiplataforma (DAM)  
Una app Android en Jetpack Compose que reúne varios juegos clásicos de lápiz y papel con una estética de cuaderno escolar.

---

## Descripción

Esta aplicación consiste en la implementación de varios juegos tradicionales como parte del Proyecto Final de Grado de DAM. Inspirado en el cuaderno de toda la vida, ofrece una experiencia visual atractiva con interfaz moderna y fuente personalizada, diseñada 100% en **Jetpack Compose**.

---

## Juegos incluidos

- **3 en raya** (PvP y contra IA)
- **Hundir la flota**
- **Ahorcado**
- **Sopa de Letras**

---

## Características principales

- Interfaz 100% Jetpack Compose
- Modo claro forzado (sin dark mode)
- Estética "cuaderno": fondo y tipografía personalizada
- Navegación con `NavHost` y `NavController`
- Inyección de dependencias con **Hilt**
- Arquitectura basada en `ViewModel` y `StateFlow`
- IA sencilla integrada en el Tres en Raya
- Palabras definidas con descripciones (en el Ahorcado y la Sopa de Letras)
- Animaciones básicas y organización modular

---

## Tecnologías utilizadas

- **Kotlin**
- **Jetpack Compose**
- **Hilt** (para DI)
- **ViewModel + StateFlow**
- **Material3**
- **Custom Fonts y Resources**
- **Android Studio Electric Eel+**

---

## Estructura del proyecto
...

notebook-games/
-│
-├── app/
-│ ├── MainActivity.kt
-│ ├── ui/
-│ │ ├── theme/ # Colores, Tipografías y Temas
-│ │ └── components/ # AppBar, Scaffold personalizado, etc.
-│ ├── screen/
-│ │ ├── MainScreen.kt # Menú principal
-│ │ ├── BattleshipScreen.kt
-│ │ ├── HangmanScreen.kt
-│ │ ├── TicTacToeScreen.kt
-│ │ └── WordSoupScreen.kt
-│ ├── model/
-│ │ └── lógica y datos de los juegos
-│ ├── navigation/
-│ │ └── AppNavigation.kt
-│ └── resources/
-│ ├── font/
-│ ├── drawable/
-│ └── values/
-│
-└── README.md

... 

## Instalación y ejecución

Instala la apk y ejecutala

## Licencia

Este proyecto se distribuye bajo la licencia MIT.

## Autor

- Antonio Puerto Fernández