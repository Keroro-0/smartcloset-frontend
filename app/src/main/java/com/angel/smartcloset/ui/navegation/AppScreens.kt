package com.angel.smartcloset.ui.navegation

/**
 * Catálogo de todas las rutas de la aplicación.
 * Usamos un String para definir el camino exacto de cada pantalla.
 */
sealed class AppScreens(val route: String) {
    object Login : AppScreens("login_screen")
    object Home : AppScreens("home_screen")
    object Wardrobe : AppScreens("wardrobe_screen")
    object Register : AppScreens("register_screen")
    object Camera : AppScreens("camera_screen")
}