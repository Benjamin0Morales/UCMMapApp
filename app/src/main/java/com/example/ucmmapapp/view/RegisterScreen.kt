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
 * `RegisterScreen` proporciona una interfaz para que un nuevo usuario se registre en la aplicación.
 * Incluye campos para el nombre, correo electrónico y contraseña.
 *
 * Realiza una validación básica:
 * 1. Comprueba que todos los campos estén completos.
 * 2. Verifica que el correo electrónico contenga el carácter '@'.
 * En una aplicación real, se debería implementar una validación más robusta y una llamada a un
 * servicio de backend para crear la cuenta de usuario.
 *
 * @param navController El controlador de navegación para redirigir al usuario a la pantalla
 *                      principal (`home`) después de un registro exitoso.
 */
@Composable
fun RegisterScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var showEmailError by remember { mutableStateOf(false) }

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
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    showEmailError = false
                },
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
                Spacer(modifier = Modifier.height(8.dp))
                Text("Debes completar todos los campos", color = MaterialTheme.colorScheme.error)
            } else if (showEmailError) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Correo inválido. Ejemplo: persona2@mail.com", color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = {
                when {
                    name.isBlank() || email.isBlank() || password.isBlank() -> {
                        showError = true
                        showEmailError = false
                    }
                    !email.contains("@") -> {
                        showError = false
                        showEmailError = true
                    }
                    else -> {
                        showError = false
                        showEmailError = false
                        navController.navigate("home")
                    }
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Registrarse")
            }
        }
    }
}
