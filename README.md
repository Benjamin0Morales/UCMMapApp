# UCMMapApp - Mapa Offline del Campus UCM

UCMMapApp es una aplicación Android nativa diseñada para proporcionar a los estudiantes y visitantes de la Universidad Católica del Maule (UCM) una herramienta de navegación offline dentro del campus. La aplicación permite visualizar un mapa detallado, ver la ubicación actual del usuario y calcular la ruta más corta entre dos edificios sin necesidad de una conexión a internet.

## ✨ Características Principales

- **Mapa Interactivo Offline:** Visualiza el campus de la UCM con edificios, caminos y zonas de interés, todo disponible sin conexión.
- **Cálculo de Rutas Peatonales:** Selecciona un edificio de origen y uno de destino para calcular y visualizar la ruta peatonal más corta.
- **Geolocalización en Tiempo Real:** Muestra tu ubicación actual en el mapa para una mejor orientación.
- **Interfaz Moderna:** Desarrollada con Jetpack Compose y Material Design 3 para una experiencia de usuario fluida y agradable.
- **Renderizado de Datos GeoJSON:** Carga y dibuja dinámicamente las estructuras del campus (edificios y caminos) a partir de archivos GeoJSON.

## 🛠️ Tecnologías Utilizadas

- **Lenguaje:** [Kotlin](https://kotlinlang.org/)
- **Interfaz de Usuario:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Mapas:** [osmdroid](https://github.com/osmdroid/osmdroid) - Una alternativa de código abierto para mapas en Android.
- **Localización:** [Google Play Services Location API](https://developers.google.com/android/reference/com/google/android/gms/location/package-summary)
- **Corrutinas de Kotlin:** Para la gestión de operaciones asíncronas.
- **Arquitectura:** UI basada en Composables, con integración de vistas de Android (`AndroidView`) para componentes no nativos de Compose.

## 🚀 Cómo Empezar

Para compilar y ejecutar el proyecto en tu propio dispositivo o emulador, sigue estos pasos:

1.  **Clona el repositorio:**
    ```bash
    git clone https://github.com/Benjamin0Morales/UCMMapApp.git
    ```
2.  **Abre el proyecto en Android Studio:**
    -   Inicia Android Studio.
    -   Selecciona `File > Open` y navega hasta la carpeta donde clonaste el repositorio.
3.  **Sincroniza el proyecto con Gradle:**
    -   Android Studio debería sincronizar automáticamente las dependencias de Gradle. Si no es así, puedes hacerlo manualmente desde `File > Sync Project with Gradle Files`.
4.  **Ejecuta la aplicación:**
    -   Selecciona un dispositivo (físico o emulador).
    -   Pulsa el botón `Run 'app'` (o `Shift + F10`).

## 📂 Estructura del Proyecto

El código fuente principal se encuentra en `app/src/main/java/com/example/ucmmapapp/`:

-   `MainActivity.kt`: El punto de entrada de la aplicación. Configura el tema y el grafo de navegación de Compose.
-   `view/`: Contiene los Composables que definen las pantallas de la aplicación.
    -   `HomeScreen.kt`: La pantalla principal que alberga el mapa, la lógica de rutas y la interacción del usuario.
    -   `WelcomeScreen.kt`, `LoginScreen.kt`, `RegisterScreen.kt`: Pantallas para el flujo de autenticación (si aplica).
-   `routing/`: Lógica para el cálculo de rutas offline.
    -   `OfflineRouter.kt`: Implementa el algoritmo para encontrar el camino más corto sobre la red de caminos definida en el GeoJSON.
-   `network/`: (Si aplica) Clases para la comunicación con servicios de red.
-   `ui/theme/`: Archivos de configuración del tema de la aplicación (colores, tipografía).
-   `GeoJsonRenderer.kt`: Clase de utilidad para parsear y renderizar datos desde archivos GeoJSON en el mapa de osmdroid.

Los datos del mapa se encuentran en `app/src/main/assets/`:
-   `campus_zonas.geojson`: Polígonos que representan los edificios y zonas.
-   `campus_caminos.geojson`: Polilíneas que representan la red de caminos peatonales.