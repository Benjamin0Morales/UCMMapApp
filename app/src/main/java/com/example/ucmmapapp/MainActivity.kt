package com.example.ucmmapapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.*
import com.example.ucmmapapp.ui.theme.UCMMapAppTheme
import com.example.ucmmapapp.view.*

/**
 * `MainActivity` es la única actividad y el punto de entrada principal de la aplicación.
 * Es responsable de configurar el contenido de la interfaz de usuario utilizando Jetpack Compose
 * y de establecer el grafo de navegación que gestiona las transiciones entre las diferentes pantallas.
 */
class MainActivity : ComponentActivity() {

    /**
     * Se llama cuando la actividad se está creando por primera vez.
     * Aquí es donde se realiza toda la inicialización de la UI.
     *
     * @param savedInstanceState Si la actividad se está reinicializando después de haber sido
     * cerrada por el sistema, este Bundle contiene los datos que suministró más recientemente en
     * `onSaveInstanceState(Bundle)`. De lo contrario, es nulo.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // `setContent` establece el contenido de la actividad con un composable de Jetpack Compose.
        setContent {
            // `UCMMapAppTheme` aplica el tema visual definido para la aplicación.
            UCMMapAppTheme {
                // `rememberNavController` crea y recuerda un NavController que sobrevive a las recomposiciones.
                val navController = rememberNavController()

                // `NavHost` es el contenedor que muestra el destino actual del grafo de navegación.
                NavHost(navController, startDestination = "welcome") {
                    // Define la pantalla de bienvenida como el destino inicial.
                    composable("welcome") {
                        WelcomeScreen(navController)
                    }
                    // Define la ruta y el composable para la pantalla de inicio de sesión.
                    composable("login") {
                        LoginScreen(navController)
                    }
                    // Define la ruta y el composable para la pantalla de registro.
                    composable("register") {
                        RegisterScreen(navController)
                    }
                    // Define la ruta y el composable para la pantalla principal del mapa.
                    composable("home") {
                        HomeScreen()
                    }
                }
            }
        }
    }
}
