package com.example.ucmmapapp.view

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ucmmapapp.R

/**
 * `WelcomeScreen` es la pantalla de bienvenida de la aplicación.
 * Ofrece al usuario tres opciones principales: iniciar sesión, registrarse o continuar como invitado.
 * Cada opción navega a la pantalla correspondiente.
 *
 * @param navController El controlador de navegación de Jetpack Compose, utilizado para gestionar
 *                      las transiciones entre las diferentes pantallas de la aplicación.
 */
@Composable
fun WelcomeScreen(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_ucm),
                contentDescription = "Logo UCM",
                modifier = Modifier.size(180.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { navController.navigate("login") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Iniciar sesión", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate("register") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Registrarse", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate("home") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Ingreso de invitado", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
