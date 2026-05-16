package com.angel.smartcloset.ui.wardrobe

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors

@Composable
fun CameraScreen(
    viewModel: AddPrendaViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    /**
     * LocalLifecycleOwner: CameraX necesita saber cuándo la pantalla está visible
     * y cuándo se oculta para encender/apagar el sensor de la cámara.
     *
     * Gestionamos los estados con uiState.
     */
    val lifecycleOwner = LocalLifecycleOwner.current

    val uiState by viewModel.uiState.collectAsState()


    /**
     * Comprobamos si tenemos permiso de la cámara.
     */
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    /**
     * permissionLauncher: La forma en Jetpack Compose de lanzar el diálogo del sistema
     * que pregunta al usuario para dar permiso a la cámara.
     * Si el usuario no da permiso a la cámara, volvemos a la pantalla anterior.
     */
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) onNavigateBack()
    }

    /**
     * imageCaptureRef: Necesitamos guardar una referencia , para poder acceder a ella
     * cuando el usuario pulse el botón físico de la UI de Compose.
     *
     * cameraExecutor: El procesamiento de imágenes pesadas no debe bloquear el hilo principal (UI).
     * Creamos un hilo exclusivo ("Worker Thread") solo para procesar la foto al tomarla.
     */
    val imageCaptureRef = remember { mutableStateOf<ImageCapture?>(null) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    /**
     * Pedimos permiso al entrar en la pantalla si no lo tenemos
     * Cuando la IA termina de procesar la imagen, volvemos al armario.
     * DisposableEffect: Se ejecuta justo cuando la pantalla va a ser destruida.
     */

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    LaunchedEffect(uiState) {
        if (uiState is AddPrendaState.Success) {
            viewModel.resetState()
            onNavigateBack()
        }
    }
    DisposableEffect(Unit) {
        onDispose { cameraExecutor.shutdown() }
    }

    /**
     * Comprobamos el estado de la App
     * - Estado de carga: subiendo foto o Gemini analizando la prenda
     * - Estado de error: Volvemos al armario¡
     */
    Box(modifier = Modifier.fillMaxSize()) {

        when (val state = uiState) {
            is AddPrendaState.Loading -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }

            is AddPrendaState.Error -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = {
                        viewModel.resetState()
                        onNavigateBack()
                    }) {
                        Text("Volver al armario")
                    }
                }
            }

            /**
             * En caso de que no haya estado de carga ni error, comprobamos si tenemos permiso de la cámara.
             * - Preview : Podemos ver lo que enfoca la cmara
             * - imageCapture : Capturamos la foto
             * - Guardamos la referencia de la imagen
             */

            else -> {
                if (hasCameraPermission) {

                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx).apply {
                                scaleType = PreviewView.ScaleType.FILL_CENTER
                            }

                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()

                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }

                                val imageCapture = ImageCapture.Builder()
                                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                    .build()

                                imageCaptureRef.value = imageCapture

                                /**
                                 * Vinculamos la cámara al ciclo de vida de la pantalla.
                                 * Si la pantalla se destruye, se destruye la cámara.
                                 */

                                try {
                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        CameraSelector.DEFAULT_BACK_CAMERA,
                                        preview,
                                        imageCapture
                                    )
                                } catch (e: Exception) {
                                    Log.e("CameraScreen", "Error al vincular la cámara: ${e.message}")
                                }

                            }, ContextCompat.getMainExecutor(ctx))

                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    /**
                     * Superponemos botones (Cancelar y Disparador) encima de la vista de la cámara.
                     */

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 48.dp)
                    ) {
                        TextButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = 24.dp)
                        ) {
                            Text(
                                text = "Cancelar",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .size(80.dp)
                                .border(4.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = {
                                    val capture = imageCaptureRef.value ?: return@Button

                                    /**
                                     * Hazemos la foto
                                     * Convertimos la foto a un array de bytes (JPEG)
                                     * Cerramos la imagen para liberar memoria
                                     * Pasamos los bytes al ViewModel para pasarlos al backend para que Gemini la analize en segundo plano
                                     */

                                    capture.takePicture(
                                        cameraExecutor,
                                        object : ImageCapture.OnImageCapturedCallback() {
                                            override fun onCaptureSuccess(image: ImageProxy) {
                                                val bytes = imageProxyToJpegBytes(image)

                                                image.close()

                                                viewModel.uploadAndAnalyzeImage(bytes)
                                            }

                                            override fun onError(exception: ImageCaptureException) {
                                                Log.e("CameraScreen", "Error al capturar: ${exception.message}")
                                            }
                                        }
                                    )
                                },
                                modifier = Modifier.size(64.dp),
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                contentPadding = PaddingValues(0.dp)
                            ) {}
                        }
                    }

                    /**
                     * En caso de no tenga permiso de la cámara, mostramos un mensaje de que no tiene permiso.
                     * Y le dejamos opción de dar permiso o volver a la pantalla anterior.
                     */

                } else {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Se necesita permiso de cámara para añadir prendas.",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }) {
                            Text("Conceder permiso")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = onNavigateBack) {
                            Text("Volver al armario")
                        }
                    }
                }
            }
        }
    }
}

/**
 * imageProxyToJpegBytes convierte una imagen a un array de bytes en formato JPEG.
 * - Convertimos la imagen a un Bitmap
 * - Calculamos la rotación necesaria
 * - Creamos un nuevo Bitmap ya rotado correctamente
 * - Lo comprimimos a formato JPEG y lo convertimos en un array de bytes
 */

private fun imageProxyToJpegBytes(image: ImageProxy): ByteArray {
    val bitmap: Bitmap = image.toBitmap()

    val matrix = Matrix().apply {
        postRotate(image.imageInfo.rotationDegrees.toFloat())
    }

    val rotatedBitmap = Bitmap.createBitmap(
        bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
    )

    return ByteArrayOutputStream().use { stream ->
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        stream.toByteArray()
    }
}