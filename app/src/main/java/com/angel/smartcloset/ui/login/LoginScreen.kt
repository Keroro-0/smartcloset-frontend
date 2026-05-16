package com.angel.smartcloset.ui.login

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.angel.smartcloset.R
import com.angel.smartcloset.ui.navegation.AppScreens
import com.angel.smartcloset.ui.theme.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = viewModel()
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current

    /**
     * Launcher para Google Sign-In:
     * Abre un "Intent" (la ventanita nativa de Google para elegir cuenta) y espera el resultado.
     * Si todo va bien (RESULT_OK), extrae las credenciales y se las pasa al ViewModel para
     * autenticar en Firebase.
     */
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                viewModel.signInWithGoogle(credential)
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * LaunchedEffect: Escucha los cambios de authState.
     * Cuando el estado cambia a "Success" (login exitoso), navega al Home
     * y borra la pantalla de login del historial para que el usuario no pueda
     * volver atrás dándole al botón de retroceso del móvil.
     */
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
                text = "Bienvenido",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = primario // Destacamos el título principal
            )

            Text(
                text = "Inicia sesión para continuar",
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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

            var passwordVisible by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = image,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = secundario // Icono del ojito en color secundario
                        )
                    }
                },
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
                    onClick = { viewModel.loginWithEmail() },
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
                        text = "Iniciar Sesión",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(modifier = Modifier.weight(1f), color = superficie)
                    Text(
                        text = " O ",
                        color = secundario,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Divider(modifier = Modifier.weight(1f), color = superficie)
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedButton(
                    onClick = {
                        val gso = GoogleSignInOptions
                            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(context.getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build()
                        val client = GoogleSignIn.getClient(context, gso)
                        launcher.launch(client.signInIntent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, primario),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = primario,
                        containerColor = fondo
                    )
                ) {
                    Text(
                        text = "Continuar con Google",
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = { navController.navigate(AppScreens.Register.route) }
                ) {
                    Text(
                        text = "¿No tienes cuenta? Regístrate aquí",
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