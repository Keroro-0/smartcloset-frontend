# SmartCloset - Android Frontend

## Descripción del Proyecto

SmartCloset es una aplicación nativa para Android orientada a la digitalización y gestión eficiente del guardarropa personal. El sistema permite a los usuarios registrar sus prendas, categorizarlas y recibir recomendaciones de atuendos (*outfits*) dinámicas, calculadas en función de las condiciones meteorológicas actuales mediante la integración de servicios de terceros.

Este repositorio contiene exclusivamente el código fuente correspondiente al cliente móvil (Frontend).

## Arquitectura y Patrones de Diseño

El proyecto está estructurado siguiendo los principios de **Clean Architecture** y utiliza el patrón de diseño **MVVM (Model-View-ViewModel)**. Esta decisión arquitectónica garantiza una clara separación de responsabilidades (Separation of Concerns), facilitando la escalabilidad, el mantenimiento y la futura implementación de pruebas unitarias.

La base de código se divide en dos capas principales:

* **Capa de Datos (`data/`)**: Encargada de la gestión y provisión de la información.
    * `model/`: Definición de las entidades de dominio y los Data Transfer Objects (DTOs) para la comunicación en red (ej. `Prenda`, `WeatherModel`, `AddPrendaRequest`).
    * `network/`: Implementación de los clientes HTTP mediante Retrofit (`RetrofitHelper`, `SmartClosetApiService`, `WeatherApiService`).
    * `repository/`: Centralización de la lógica de acceso a datos (`SmartClosetRepository`, `WeatherRepository`), actuando como única fuente de la verdad para la capa de presentación.
* **Capa de Presentación (`ui/`)**: Responsable de la interfaz gráfica y la interacción del usuario.
    * Organizada por flujos de funcionalidad (`home/`, `login/`, `wardrobe/`).
    * Cada flujo cuenta con sus respectivas vistas (Screens construidas de forma declarativa) y su `ViewModel` asociado, el cual gestiona el estado de la UI y la lógica de presentación.
    * `navigation/`: Gestión centralizada del enrutamiento de la aplicación.
    * `theme/`: Definición global de diseño, colores y tipografías basándose en los estándares de Material Design.

## Tecnologías y Librerías

El desarrollo se ha llevado a cabo utilizando el stack tecnológico moderno recomendado por Google para el desarrollo en Android:

* **Lenguaje:** Kotlin.
* **UI Framework:** Jetpack Compose para la construcción de interfaces de usuario nativas mediante un paradigma declarativo.
* **Asincronía y Reactividad:** Kotlin Coroutines y `StateFlow`. Utilizados para operaciones fuera del hilo principal (ej. llamadas a red) y para la actualización reactiva de la interfaz de usuario en base a los cambios de estado.
* **Cliente HTTP:** Retrofit 2 junto con OkHttp y Gson. Empleados para el consumo de la API REST del backend propio y la API meteorológica externa.
* **Navegación:** Jetpack Navigation Compose para la gestión del grafo de navegación entre pantallas.
* **Diseño:** Material Design 3 (Material You).

## Funcionalidades Principales

1.  **Módulo de Autenticación:** Flujo completo de registro e inicio de sesión de usuarios.
2.  **Gestión de Guardarropa (CRUD):** Visualización del inventario de ropa, adición de nuevas prendas, modificación de categorías y eliminación.
3.  **Captura Multimedia:** Integración directa con el hardware del dispositivo para la captura fotográfica de prendas desde la aplicación.
4.  **Motor de Recomendación Climática:** Interfaz que despliega los datos del clima en tiempo real y cruza esta información con el inventario del usuario para sugerir atuendos adecuados.

## Requisitos y Configuración del Entorno

Para la compilación y ejecución local del proyecto, se requiere el siguiente entorno:

* **IDE:** Android Studio (versión recomendada: Jellyfish o superior).
* **SDK:** Nivel de API mínimo 26 (Android 8.0) y Target SDK 34 (Android 14).
* **Configuración de Red:** Es necesario configurar las URL base para el Backend y el proveedor de clima en el archivo `RetrofitHelper.kt` antes de la ejecución.

### Pasos de despliegue local:

1. Clonar el repositorio:
   ```bash
   git clone [URL_DEL_REPOSITORIO]