package com.angel.smartcloset.ui.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.angel.smartcloset.data.model.OutfitRecomendacion
import com.angel.smartcloset.ui.navegation.AppScreens
import com.angel.smartcloset.ui.theme.*
import com.google.android.gms.location.LocationServices
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel(),
) {
    val outfitState by viewModel.outfitState.collectAsState()

    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = { BottomNavigationBar(navController) },
        containerColor = fondo
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "¡Buenos días!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = primario
                )
                Spacer(modifier = Modifier.height(24.dp))

                WeatherScreen(viewModel)

                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Tus Outfits de hoy",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = primario
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            when (val state = outfitState) {
                is OutfitState.Loading -> {
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = primario)
                        }
                    }
                }
                is OutfitState.EmptyCloset -> {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = superficie
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Tu armario está vacío.\n¡Añade algunas prendas primero para que Gemini pueda ayudarte!",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = secundario
                            )
                        }
                    }
                }
                is OutfitState.Error -> {
                    item {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
                is OutfitState.Success -> {
                    items(state.outfits) { outfit ->
                        OutfitCard(outfit)
                        Spacer(modifier = Modifier.height(28.dp))
                    }
                }
                is OutfitState.Idle -> {
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Obteniendo tu ubicación...",
                                color = secundario
                            )
                        }
                    }
                }
                else -> {}
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun OutfitCard(outfit: OutfitRecomendacion) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = superficie
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "IA",
                    tint = primario,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sugerencia Inteligente",
                    style = MaterialTheme.typography.labelLarge,
                    color = primario,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = outfit.motivo,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = secundario
            )
            Spacer(modifier = Modifier.height(20.dp))
            PrendaItemCard(label = "Camiseta", prenda = outfit.camiseta)
            Spacer(modifier = Modifier.height(12.dp))
            PrendaItemCard(label = "Pantalón", prenda = outfit.pantalon)
            Spacer(modifier = Modifier.height(12.dp))
            PrendaItemCard(label = "Calzado", prenda = outfit.calzado)
        }
    }
}

@Composable
fun PrendaItemCard(label: String, prenda: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = fondo.copy(alpha = 0.6f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        color = secundario.copy(alpha = 0.15f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label.first().toString().uppercase(),
                    color = primario,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = secundario,
                    letterSpacing = 1.sp
                )
                Text(
                    text = prenda,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = primario
                )
            }
        }
    }
}

@Composable
fun WeatherScreen(viewModel: HomeViewModel) {
    val context = LocalContext.current
    val weather by viewModel.weatherState.collectAsState()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted && weather == null) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) viewModel.fetchWeather(location.latitude, location.longitude)
                    else viewModel.fetchWeather(37.1882, -3.6067)
                }
            } else if (!isGranted && weather == null) {
                viewModel.fetchWeather(37.1882, -3.6067)
            }
        }
    )

    LaunchedEffect(Unit) {
        if (weather == null) {
            if (ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) viewModel.fetchWeather(location.latitude, location.longitude)
                    else viewModel.fetchWeather(37.1882, -3.6067)
                }
            } else {
                permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        }
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        if (weather != null) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = superficie
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ubicación Actual",
                        style = MaterialTheme.typography.labelLarge,
                        color = secundario,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${weather!!.temperature} °C",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = primario
                    )
                }
            }
        } else {
            CircularProgressIndicator(color = primario)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavController
) {
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = "SmartCloset",
                fontWeight = FontWeight.Bold,
                color = superficie
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = secundario,
            actionIconContentColor = superficie
        ),
        actions = {
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = "Ajustes",
                        tint = superficie
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(superficie)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Cerrar Sesión",
                                color = primario
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.ExitToApp,
                                contentDescription = null,
                                tint = primario
                            )
                        },
                        onClick = {
                            showMenu = false
                            Firebase.auth.signOut()
                            navController.navigate(AppScreens.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar(
        containerColor = superficie,
        contentColor = primario,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.Home,
                    "Inicio"
                )
            },
            label = { Text("Inicio") },
            selected = true,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = fondo,
                selectedTextColor = primario,
                indicatorColor = primario,
                unselectedIconColor = secundario,
                unselectedTextColor = secundario
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.List,
                    "Armario"
                )
            },
            label = { Text("Armario") },
            selected = false,
            onClick = {
                navController.navigate(AppScreens.Wardrobe.route) {
                    popUpTo(AppScreens.Home.route) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = fondo,
                selectedTextColor = primario,
                indicatorColor = primario,
                unselectedIconColor = secundario,
                unselectedTextColor = secundario
            )
        )
    }
}