package com.example.ucmmapapp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ucmmapapp.R

/**
 * `LoginScreen` presenta una interfaz para que el usuario inicie sesión.
 * Contiene campos para el correo electrónico y la contraseña, y un botón para enviar.
 *
 * La lógica de validación actual es básica: comprueba que los campos no estén vacíos.
 * En una implementación real, aquí se realizaría una llamada a un servicio de autenticación.
 *
 * @param navController El controlador de navegación para redirigir al usuario a la pantalla
 *                      principal (`home`) después de un inicio de sesión exitoso.
 */
@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

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
                modifier = Modifier.size(140.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth()
            )

            if (showError) {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Debes completar ambos campos", color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = {
                showError = email.isBlank() || password.isBlank()
                if (!showError) {
                    navController.navigate("home")
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Entrar")
            }
        }
    }
}
