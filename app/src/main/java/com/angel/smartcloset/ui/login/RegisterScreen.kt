package com.angel.smartcloset.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.angel.smartcloset.ui.navegation.AppScreens
import com.angel.smartcloset.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = viewModel()
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthRes.Success) {
            navController.navigate(AppScreens.Home.route) {
                popUpTo(AppScreens.Login.route) { inclusive = true }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = fondo
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = primario
            )

            Text(
                text = "Únete para organizar tu armario",
                style = MaterialTheme.typography.bodyLarge,
                color = secundario,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primario,
                    unfocusedBorderColor = secundario.copy(alpha = 0.5f),
                    focusedLabelColor = primario,
                    unfocusedLabelColor = secundario,
                    focusedTextColor = primario,
                    unfocusedTextColor = secundario,
                    cursorColor = primario,
                    focusedContainerColor = superficie.copy(alpha = 0.3f),
                    unfocusedContainerColor = fondo
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primario,
                    unfocusedBorderColor = secundario.copy(alpha = 0.5f),
                    focusedLabelColor = primario,
                    unfocusedLabelColor = secundario,
                    focusedTextColor = primario,
                    unfocusedTextColor = secundario,
                    cursorColor = primario,
                    focusedContainerColor = superficie.copy(alpha = 0.3f),
                    unfocusedContainerColor = fondo
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { viewModel.onConfirmPasswordChange(it) },
                label = { Text("Repetir Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primario,
                    unfocusedBorderColor = secundario.copy(alpha = 0.5f),
                    focusedLabelColor = primario,
                    unfocusedLabelColor = secundario,
                    focusedTextColor = primario,
                    unfocusedTextColor = secundario,
                    cursorColor = primario,
                    focusedContainerColor = superficie.copy(alpha = 0.3f),
                    unfocusedContainerColor = fondo
                )
            )

            Spacer(modifier = Modifier.height(40.dp))

            if (authState is AuthRes.Loading) {
                CircularProgressIndicator(color = primario)
            } else {
                Button(
                    onClick = { viewModel.signUp() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primario,
                        contentColor = fondo
                    )
                ) {
                    Text(
                        text = "Registrarse",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Text(
                        text = "¿Ya tienes cuenta? Inicia sesión",
                        color = terciario,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (authState is AuthRes.Error) {
                Text(
                    text = (authState as AuthRes.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}