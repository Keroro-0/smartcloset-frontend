package com.angel.smartcloset

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.angel.smartcloset.ui.navegation.AppNavigation

class MainActivity : ComponentActivity() {

    /**
     * onCreate es el primer método en ejecutarse
     */
        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * enableEdgeToEdge haze que ocupe toda la pantalla
         */
        enableEdgeToEdge()
        setContent {

            /**
             * Llamamos a la función AppNavigation() que se encarga de gestionar las pantllas.
             */
            AppNavigation()

        }
    }
}