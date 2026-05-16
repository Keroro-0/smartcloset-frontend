package com.angel.smartcloset.ui.wardrobe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.angel.smartcloset.data.model.Prenda
import com.angel.smartcloset.ui.navegation.AppScreens
import com.angel.smartcloset.ui.theme.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardrobeScreen(
    navController: NavController,
    viewModel: WardrobeViewModel,
    onNavigateToAddPrenda: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.fetchArmario() }

    Scaffold(
        topBar = { WardrobeTopBar() },
        bottomBar = { WardrobeBottomNavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddPrenda,
                containerColor = primario,
                contentColor = fondo,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Añadir")
            }
        },
        containerColor = fondo
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is WardrobeState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = primario
                    )
                }
                is WardrobeState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is WardrobeState.Success -> {
                    if (state.prendas.isEmpty()) {
                        Text(
                            text = "Tu armario está vacío.\n¡Añade algo nuevo!",
                            color = secundario,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        // Definición de las categorías y el estado
                        val categorias = listOf("Todo", "Camiseta", "Pantalon", "Calzado")
                        var categoriaSeleccionada by remember { mutableStateOf("Todo") }

                        // Filtrado de la ropa según lo que esté seleccionado
                        val prendasFiltradas = if (categoriaSeleccionada == "Todo") {
                            state.prendas
                        } else {
                            state.prendas.filter { it.categoria.equals(categoriaSeleccionada, ignoreCase = true) }
                        }

                        Column(modifier = Modifier.fillMaxSize()) {

                            // Fila de filtros
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(categorias) { categoria ->
                                    val isSelected = categoriaSeleccionada == categoria

                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { categoriaSeleccionada = categoria },
                                        label = {
                                            Text(
                                                text = categoria,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            // Colores cuando está seleccionado
                                            selectedContainerColor = primario,
                                            selectedLabelColor = fondo,
                                            // Colores cuando NO está seleccionado
                                            containerColor = superficie,
                                            labelColor = secundario
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = isSelected,
                                            borderColor = secundario.copy(alpha = 0.3f),
                                            selectedBorderWidth = 0.dp
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                }
                            }


                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                items(prendasFiltradas) { prenda ->
                                    PrendaItem(
                                        prenda = prenda,
                                        onDelete = { viewModel.deletePrenda(prenda.id) },
                                        onEdit = { cat, col -> viewModel.updatePrenda(prenda.id, cat, col) }
                                    )
                                }
                            }
                        }
                    }
                }
                }
            }
        }
    }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrendaItem(prenda: Prenda, onDelete: () -> Unit, onEdit: (String, String) -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = superficie
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = prenda.urlImagen,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    IconButton(
                        onClick = { showEditDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = primario,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Borrar",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = prenda.categoria,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = primario
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = prenda.color,
                    style = MaterialTheme.typography.bodyMedium,
                    color = secundario
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = superficie,
            title = {
                Text("¿Borrar prenda?", color = primario, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Esta acción no se puede deshacer.",
                    color = secundario
                )
            },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteDialog = false }) {
                    Text("Borrar", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar", color = secundario)
                }
            }
        )
    }

    if (showEditDialog) {
        EditPrendaDialog(
            initialCategoria = prenda.categoria,
            initialColor = prenda.color,
            onDismiss = { showEditDialog = false },
            onConfirm = { cat, col -> onEdit(cat, col); showEditDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPrendaDialog(
    initialCategoria: String,
    initialColor: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var color by remember { mutableStateOf(initialColor) }
    var expanded by remember { mutableStateOf(false) }
    val categorias = listOf("Camiseta", "Pantalon", "Calzado")
    var selectedCategoria by remember { mutableStateOf(initialCategoria) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = superficie,
        title = {
            Text("Editar Prenda", color = primario, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategoria,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primario,
                            unfocusedBorderColor = secundario.copy(alpha = 0.5f),
                            focusedLabelColor = primario,
                            unfocusedLabelColor = secundario,
                            focusedTextColor = primario,
                            unfocusedTextColor = secundario,
                            focusedContainerColor = fondo,
                            unfocusedContainerColor = fondo
                        ),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(superficie)
                    ) {
                        categorias.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option, color = primario) },
                                onClick = {
                                    selectedCategoria = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = color,
                    onValueChange = { color = it },
                    label = { Text("Color") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primario,
                        unfocusedBorderColor = secundario.copy(alpha = 0.5f),
                        focusedLabelColor = primario,
                        unfocusedLabelColor = secundario,
                        focusedTextColor = primario,
                        unfocusedTextColor = secundario,
                        focusedContainerColor = fondo,
                        unfocusedContainerColor = fondo
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedCategoria, color) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = primario,
                    contentColor = fondo
                )
            ) {
                Text("Guardar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = secundario)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardrobeTopBar() {
    TopAppBar(
        title = {
            Text(
                "SmartCloset",
                fontWeight = FontWeight.Bold,
                color = superficie
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = secundario,
            titleContentColor = superficie
        )
    )
}

@Composable
fun WardrobeBottomNavigationBar(navController: NavController) {
    NavigationBar(
        containerColor = superficie,
        contentColor = primario,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = {
                Icon(Icons.Filled.Home, "Inicio")
            },
            label = { Text("Inicio") },
            selected = false,
            onClick = { navController.navigate(AppScreens.Home.route) },
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
                Icon(Icons.Filled.List, "Armario")
            },
            label = { Text("Armario") },
            selected = true,
            onClick = {},
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