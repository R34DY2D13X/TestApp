package com.example.testapp.menu

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.testapp.R
import com.example.testapp.auth.UserData
import com.example.testapp.auth.UserRole
import com.example.testapp.chat.ChatWindow
import com.example.testapp.chat.ChatbotButton
import com.example.testapp.chat.ChatbotViewModel
import com.example.testapp.ui.theme.CardBackgroundColor
import com.example.testapp.ui.theme.DarkBackground
import com.example.testapp.ui.theme.PrimaryTextColor
import kotlinx.coroutines.delay

// --- LÓGICA DEL CARRUSEL ---
private val groupMin = listOf(
    R.drawable.img1_min,
    R.drawable.img2_min,
    R.drawable.img3_min,
    R.drawable.img4_min
)

private val groupMin2 = listOf(
    R.drawable.img1_min2,
    R.drawable.img2_min2,
    R.drawable.img3_min2,
    R.drawable.img4_min2
)

private val groupMin3 = listOf(
    R.drawable.img1_min3,
    R.drawable.img2_min3,
    R.drawable.img3_min3
)

private val groupSotf = listOf(
    R.drawable.sotf_img1,
    R.drawable.sotf_img2,
    R.drawable.sotf_img3,
    R.drawable.sotf_img4,
    R.drawable.sotf_img5
)

private val allGroups = listOf(groupMin, groupMin2, groupMin3, groupSotf)
private val placeholderColors = listOf(Color(0xFF64B5F6), Color(0xFFB0C4DE), Color(0xFF2C3E50))

// --- MENÚ ITEMS ---
data class MenuItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String
)

private val menuItems = listOf(
    MenuItem("Sueño", "¿Te desvelas mucho?", Icons.Default.Nightlight, "sueño"),
    MenuItem("Bienestar", "Cuida tu salud", Icons.Default.Favorite, "bienestar"),
    MenuItem("Temporizador", "Gestiona tu tiempo", Icons.Default.Timer, "temporizador"), // Ruta corregida
    MenuItem("Plan de Estudios", "Organiza tu aprendizaje", Icons.Default.Book, "plan_de_estudios")
)

// Data for Bottom Navigation
data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

private val bottomNavItems = listOf(
    BottomNavItem("Inicio", Icons.Default.Home, "menu"),
    BottomNavItem("Calendario", Icons.Default.CalendarMonth, "calendario"),
    BottomNavItem("Ajustes", Icons.Default.Settings, "ajustes")
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MenuScreen(navController: NavController, chatbotViewModel: ChatbotViewModel = viewModel()) {
    var currentRoute by rememberSaveable { mutableStateOf("menu") }
    var showChat by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    // Selección aleatoria del grupo de imágenes para el carrusel
    val selectedGroup = remember {
        if (allGroups.isNotEmpty()) allGroups.random() else emptyList()
    }
    val displayImages = if (selectedGroup.isNotEmpty()) selectedGroup else emptyList()
    
    // LÓGICA PARA LOOP INFINITO
    val realSize = if (displayImages.isNotEmpty()) displayImages.size else placeholderColors.size
    // Iniciamos en un número muy alto para permitir scroll infinito hacia ambos lados
    val initialIndex = Int.MAX_VALUE / 2
    // Ajustamos para que empiece en el primer item del grupo (índice 0 relativo)
    val startIndex = if (realSize > 0) initialIndex - (initialIndex % realSize) else 0

    val pagerState = rememberPagerState(
        initialPage = startIndex,
        pageCount = { Int.MAX_VALUE } // Cantidad de páginas "infinita"
    )

    // Altura fija ahora que no tenemos las imágenes verticales de WhatsApp
    val carouselHeight = 250.dp

    // Efecto para el desplazamiento automático del carrusel
    LaunchedEffect(Unit) {
        while (true) {
            delay(4000) // Cambio cada 4 segundos
            pagerState.animateScrollToPage(pagerState.currentPage + 1)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = DarkBackground,
            bottomBar = {
                NavigationBar(containerColor = CardBackgroundColor.copy(alpha = 0.8f)) {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                currentRoute = item.route
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label, tint = PrimaryTextColor) },
                            label = { Text(item.label, color = PrimaryTextColor) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryTextColor,
                                unselectedIconColor = PrimaryTextColor.copy(alpha = 0.6f),
                                selectedTextColor = PrimaryTextColor,
                                unselectedTextColor = PrimaryTextColor.copy(alpha = 0.6f),
                                indicatorColor = CardBackgroundColor
                            )
                        )
                    }
                }
            },
            floatingActionButton = {
                if (UserData.role == UserRole.ADMIN) {
                    FloatingActionButton(
                        onClick = { /* TODO: Navegar a una pantalla de creación/edición */ },
                        containerColor = CardBackgroundColor
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir Plan", tint = PrimaryTextColor)
                    }
                }
            }
        ) { innerPadding ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(innerPadding).background(DarkBackground),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Carrusel de Imágenes al inicio (Top)
                item(span = { GridItemSpan(2) }) { 
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(carouselHeight)
                            .padding(bottom = 24.dp) // Separación de los botones
                            .clickable {
                                // Lógica de navegación al hacer clic en el carrusel
                                when (selectedGroup) {
                                    groupMin -> navController.navigate("temporizador")
                                    groupMin2 -> navController.navigate("plan_de_estudios")
                                    groupMin3 -> navController.navigate("bienestar")
                                    groupSotf -> navController.navigate("sueño")
                                }
                            }
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            // Calculamos el índice real usando módulo para ciclo infinito
                            val index = page % realSize
                            
                            if (displayImages.isNotEmpty()) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Image(
                                        painter = painterResource(id = displayImages[index]),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )

                                    // Agregamos estela negra y texto a la izquierda para todos los grupos
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.horizontalGradient(
                                                    colors = listOf(Color.Black.copy(alpha = 0.9f), Color.Transparent),
                                                    startX = 0f,
                                                    endX = Float.POSITIVE_INFINITY
                                                )
                                            )
                                    )
                                    
                                    Column(
                                        modifier = Modifier
                                            .align(Alignment.CenterStart) // Alineación a la izquierda y centrado verticalmente
                                            .padding(16.dp)
                                            .fillMaxWidth(0.6f), // Ocupar solo una parte del ancho para dar efecto de lado izquierdo
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        val textoPrincipal = when (selectedGroup) {
                                            groupMin -> "¿Necesitas ayuda para concentrarte?"
                                            groupMin2 -> "Organizar tu estudio es organizar tus ideas"
                                            groupMin3 -> "¿Tu bienestar está en pausa?"
                                            else -> "El sueño reparador empieza con tu rutina"
                                        }

                                        Text(
                                            text = textoPrincipal,
                                            style = MaterialTheme. typography.headlineSmall.copy( // Texto un poco más grande
                                                shadow = Shadow(
                                                    color = Color.Black,
                                                    offset = Offset(2f, 2f),
                                                    blurRadius = 4f
                                                )
                                            ),
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            textAlign = TextAlign.Start
                                        )
                                        
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        Text(
                                            text = "¡PRESIONA AQUÍ!",
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                shadow = Shadow(
                                                    color = Color.Black,
                                                    offset = Offset(1f, 1f),
                                                    blurRadius = 2f
                                                )
                                            ),
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFFFFD700), // Color dorado llamativo
                                            textAlign = TextAlign.Start
                                        )
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(placeholderColors[index])
                                )
                            }
                        }
                    }
                }

                // Título
                item(span = { GridItemSpan(2) }) { 
                    Text(
                        text = "MEJORA TUS HÁBITOS CON NOSOTROS",
                        style = MaterialTheme.typography.headlineSmall,
                        color = PrimaryTextColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }
                
                // Tarjetas del Menú
                items(menuItems) { item ->
                    MenuCard(item = item) {
                        navController.navigate(item.route)
                    }
                }
            }
        }

        // Lógica del Chatbot integrada
        if (showChat) {
            ChatWindow(
                message = message,
                onMessageChange = { message = it },
                onSend = {
                    if (message.isNotBlank()) {
                        chatbotViewModel.sendMessage(message)
                        message = ""
                    }
                },
                onClose = { showChat = false },
                messages = chatbotViewModel.messages
            )
        } else {
            ChatbotButton(onClick = { showChat = true })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuCard(item: MenuItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.aspectRatio(0.8f),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                modifier = Modifier.size(48.dp),
                tint = PrimaryTextColor
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = item.title.uppercase(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryTextColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.description,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = PrimaryTextColor.copy(alpha = 0.8f),
                lineHeight = 14.sp
            )
        }
    }
}
