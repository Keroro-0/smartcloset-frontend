package com.angel.smartcloset.ui.navegation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.angel.smartcloset.ui.home.HomeScreen
import com.angel.smartcloset.ui.login.LoginScreen
import com.angel.smartcloset.ui.login.RegisterScreen
import com.angel.smartcloset.ui.wardrobe.WardrobeScreen
import com.angel.smartcloset.ui.wardrobe.WardrobeViewModel
import com.angel.smartcloset.ui.wardrobe.CameraScreen
import com.angel.smartcloset.ui.wardrobe.AddPrendaViewModel
import com.angel.smartcloset.data.repository.SmartClosetRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun AppNavigation() {
    /**
     * Instanciamos navController para navegar entre pantallas.
     * Instanciamos el repositorio y los ViewModels.
     */
    val navController = rememberNavController()
    val smartClosetRepository = SmartClosetRepository()
    val wardrobeViewModel = WardrobeViewModel(smartClosetRepository)
    val addPrendaViewModel = AddPrendaViewModel(smartClosetRepository)

    /**
     * Para mantener la sesión iniciada, la currentUser que nos indicará si hay un usuario logueado.
     *  - En caso de que sea nulo, se mostrará la pantalla de login.
     *  - En caso de que no sea nulo, se mostrará la pantalla de home.
     */
    val currentUser = Firebase.auth.currentUser
    val startDestination = if (currentUser != null) {
        AppScreens.Home.route
    } else {
        AppScreens.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = AppScreens.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(route = AppScreens.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(route = AppScreens.Register.route) {
            RegisterScreen(navController)
        }
        composable(route = AppScreens.Wardrobe.route) {
            WardrobeScreen(
                navController = navController,
                viewModel = wardrobeViewModel,
                onNavigateToAddPrenda = {
                    navController.navigate(AppScreens.Camera.route)
                }
            )
        }
        composable(route = AppScreens.Camera.route) {
            CameraScreen(
                viewModel = addPrendaViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                    wardrobeViewModel.fetchArmario()
                }
            )
        }
    }
}