package com.example.testapp

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// Definimos los grupos de imágenes
private val groupMin = listOf(
    R.drawable.img1_min,
    R.drawable.img2_min,
    R.drawable.img3_min,
    R.drawable.img4_min
)

private val groupSotf = listOf(
    R.drawable.sotf_img1,
    R.drawable.sotf_img2,
    R.drawable.sotf_img3,
    R.drawable.sotf_img4,
    R.drawable.sotf_img5
)

private val groupWhatsapp = listOf(
    R.drawable.whatsapp_1,
    R.drawable.whatsapp_2,
    R.drawable.whatsapp_3,
    R.drawable.whatsapp_4,
    R.drawable.whatsapp_5,
    R.drawable.whatsapp_6,
    R.drawable.whatsapp_7
)

// Lista de todos los grupos disponibles para la selección aleatoria
private val allGroups = listOf(groupMin, groupSotf, groupWhatsapp)

private val placeholderColors = listOf(Color(0xFF64B5F6), Color(0xFFB0C4DE), Color(0xFF2C3E50))

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen() {
    // Lógica Random: Selecciona un grupo al azar cada vez que se carga la pantalla
    val selectedGroup = remember {
        if (allGroups.isNotEmpty()) allGroups.random() else emptyList()
    }

    val displayImages = if (selectedGroup.isNotEmpty()) selectedGroup else emptyList()
    val pageCount = if (displayImages.isNotEmpty()) displayImages.size else placeholderColors.size
    
    val pagerState = rememberPagerState(pageCount = { pageCount })

    // Desplazamiento automático (1 por 1)
    LaunchedEffect(Unit) {
        while (true) {
            delay(4000) // Velocidad del pase
            pagerState.animateScrollToPage((pagerState.currentPage + 1) % pagerState.pageCount)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // --- BARRA DE PRESENTACIÓN (CARRUSEL) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp) // Altura de la barra
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                // Imagen limpia, sin textos ni capas oscuras encima
                if (displayImages.isNotEmpty()) {
                    Image(
                        painter = painterResource(id = displayImages[page]),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(placeholderColors[page % placeholderColors.size])
                    )
                }
            }
        }

        // --- RESTO DEL CONTENIDO ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
        ) {
            Text(
                text = "Aquí irá el resto de tu contenido...",
                color = Color.Gray,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
