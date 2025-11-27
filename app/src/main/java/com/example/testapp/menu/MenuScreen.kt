package com.example.testapp.menu

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.testapp.ajustes.SettingsState
import com.example.testapp.ajustes.SettingsViewModel
import com.example.testapp.ui.theme.CardBackgroundColor
import com.example.testapp.ui.theme.DarkBackground
import com.example.testapp.ui.theme.PrimaryTextColor
import kotlinx.coroutines.delay

private val groupMin = listOf(R.drawable.img1_min, R.drawable.img2_min, R.drawable.img3_min, R.drawable.img4_min)
private val groupMin2 = listOf(R.drawable.img1_min2, R.drawable.img2_min2, R.drawable.img3_min2, R.drawable.img4_min2)
private val groupMin3 = listOf(R.drawable.img1_min3, R.drawable.img2_min3, R.drawable.img3_min3)
private val groupSotf = listOf(R.drawable.sotf_img1, R.drawable.sotf_img2, R.drawable.sotf_img3, R.drawable.sotf_img4, R.drawable.sotf_img5)
private val allGroups = listOf(groupMin, groupMin2, groupMin3, groupSotf).filter { it.isNotEmpty() }

data class MenuItem(val title: String, val description: String, val icon: ImageVector, val route: String)
private val menuItems = listOf(
    MenuItem("Sueño", "¿Te desvelas mucho?", Icons.Default.Nightlight, "sueño"),
    MenuItem("Bienestar", "Cuida tu salud", Icons.Default.Favorite, "bienestar"),
    MenuItem("Temporizador", "Gestiona tu tiempo", Icons.Default.Timer, "temporizadorP"),
    MenuItem("Plan de Estudios", "Organiza tu aprendizaje", Icons.Default.Book, "plan_de_estudios")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(navController: NavController, settingsViewModel: SettingsViewModel = viewModel()) {
    val settings by settingsViewModel.uiState.collectAsState()
    val dynamicBg = if (settings.highContrast) Color.Black else DarkBackground
    val dynamicPrimaryText = if (settings.highContrast) Color.White else PrimaryTextColor

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(dynamicBg)
            .verticalScroll(rememberScrollState())
    ) {
        CarouselComponent(navController = navController, settings = settings)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "MEJORA TUS HÁBITOS CON NOSOTROS",
            style = MaterialTheme.typography.headlineSmall.copy(fontSize = MaterialTheme.typography.headlineSmall.fontSize * settings.fontSize),
            color = dynamicPrimaryText,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
        )

        val chunkedItems = menuItems.chunked(2)
        chunkedItems.forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowItems.forEach { item ->
                    Box(modifier = Modifier.weight(1f)) {
                        MenuCard(item = item, settings = settings) {
                            navController.navigate(item.route)
                        }
                    }
                }
                if (rowItems.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CarouselComponent(navController: NavController, settings: SettingsState) {
    val selectedGroup = remember { allGroups.randomOrNull() }
    val fontSize = settings.fontSize
    val highContrast = settings.highContrast
    val dynamicPrimaryText = if (highContrast) Color.White else PrimaryTextColor

    if (selectedGroup != null) {
        val pagerState = rememberPagerState(
            initialPage = Int.MAX_VALUE / 2 - (Int.MAX_VALUE / 2 % selectedGroup.size),
            pageCount = { Int.MAX_VALUE }
        )

        LaunchedEffect(pagerState) {
            while (true) {
                delay(4000)
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable {
                    val route = when (selectedGroup) {
                        groupMin -> "temporizadorP"
                        groupMin2 -> "plan_de_estudios"
                        groupMin3 -> "bienestar"
                        else -> "sueño"
                    }
                    navController.navigate(route)
                }
        ) {
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                val index = page % selectedGroup.size
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = selectedGroup[index]),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier.fillMaxSize().background(
                            Brush.horizontalGradient(colors = listOf(Color.Black.copy(alpha = 0.9f), Color.Transparent))
                        )
                    )
                    Column(
                        modifier = Modifier.align(Alignment.CenterStart).padding(16.dp).fillMaxWidth(0.6f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        val textoPrincipal = when (selectedGroup) {
                            groupMin -> "¿Necesitas ayuda para concentrarte?"
                            groupMin2 -> "Organizar tu estudio es organizar tus ideas"
                            groupMin3 -> "¿Tu bienestar está en pausa?"
                            else -> "El sueño reparador empieza con tu rutina"
                        }.uppercase()

                        Text(
                            text = textoPrincipal,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontSize = MaterialTheme.typography.headlineSmall.fontSize * fontSize,
                                shadow = Shadow(color = Color.Black, offset = Offset(2f, 2f), blurRadius = 4f)
                            ),
                            fontWeight = FontWeight.Bold,
                            color = dynamicPrimaryText,
                            textAlign = TextAlign.Start
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "¡PRESIONA AQUÍ!",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = MaterialTheme.typography.labelLarge.fontSize * fontSize,
                                shadow = Shadow(color = Color.Black, offset = Offset(1f, 1f), blurRadius = 2f)
                            ),
                            fontWeight = FontWeight.ExtraBold,
                            color = if (highContrast) dynamicPrimaryText else Color(0xFFFFD700),
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
        }
    } else {
        Spacer(Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuCard(item: MenuItem, settings: SettingsState, onClick: () -> Unit) {
    val dynamicCardBg = if (settings.highContrast) Color(0xFF1C1C1E) else CardBackgroundColor
    val dynamicPrimaryText = if (settings.highContrast) Color.White else PrimaryTextColor
    val dynamicSecondaryText = if (settings.highContrast) Color.LightGray else PrimaryTextColor.copy(alpha = 0.8f)

    Card(
        modifier = Modifier.aspectRatio(0.8f),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = dynamicCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                modifier = Modifier.size(48.dp),
                tint = dynamicPrimaryText
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = item.title.uppercase(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = MaterialTheme.typography.titleMedium.fontSize * settings.fontSize),
                fontWeight = FontWeight.Bold,
                color = dynamicPrimaryText
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.description,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = MaterialTheme.typography.bodySmall.fontSize * settings.fontSize),
                color = dynamicSecondaryText,
                lineHeight = (14.sp * settings.fontSize)
            )
        }
    }
}
