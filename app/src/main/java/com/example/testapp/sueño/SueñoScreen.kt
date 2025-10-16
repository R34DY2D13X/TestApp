package com.example.testapp.sueño

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.testapp.ui.theme.CardBackgroundColor
import com.example.testapp.ui.theme.DarkBackground
import com.example.testapp.ui.theme.PrimaryTextColor

// --- DATA MODELS ---
enum class EstadoPlan { POR_COMPLETAR, COMPLETADO, NO_COMPLETADO }

data class TareaDiaria(val id: Int, val descripcion: String)

data class PlanDeSueño(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val duracion: String,
    val objetivo: String,
    val estado: EstadoPlan,
    val tareas: List<TareaDiaria>,
    val infoMedica: String // <- CAMPO AÑADIDO
)

// --- MOCK DATA REPOSITORY ---
object SueñoRepository {
    // Simulación de datos que podrían venir de una base de datos
    private val planes = listOf(
        PlanDeSueño(
            id = "plan1",
            nombre = "Dormir mejor poco a poco",
            descripcion = "Para estudiantes que se duermen muy tarde",
            duracion = "2 semanas",
            objetivo = "Ir adelantando gradualmente la hora de dormir para lograr un descanso más profundo.",
            estado = EstadoPlan.POR_COMPLETAR,
            tareas = listOf(
                TareaDiaria(1, "30 min antes: bajar brillo de pantalla y activar modo noche."),
                TareaDiaria(2, "Guardar el celular lejos de la cama."),
                TareaDiaria(3, "Preparar mochila o ropa para el día siguiente.")
            ),
            infoMedica = "La exposición a la luz azul de las pantallas suprime la melatonina, la hormona del sueño. Reducir la exposición 1-2 horas antes de dormir mejora la calidad del descanso, según estudios del Rensselaer Polytechnic Institute."
        ),
        PlanDeSueño(
            id = "plan2",
            nombre = "Rutina constante",
            descripcion = "Para estudiantes con horarios cambiantes",
            duracion = "3 semanas",
            objetivo = "Establecer un horario de sueño fijo para regular el reloj biológico.",
            estado = EstadoPlan.POR_COMPLETAR,
            tareas = listOf(TareaDiaria(1, "Definir hora fija para dormir y despertar."), TareaDiaria(2, "Evitar siestas largas durante el día.")),
            infoMedica = "Mantener un horario de sueño y vigilia constante ayuda a sincronizar el ritmo circadiano. Esto mejora la eficiencia del sueño y reduce el tiempo que se tarda en dormirse, como afirma la Sleep Foundation."
        ),
        PlanDeSueño(
            id = "plan3",
            nombre = "Sueño express para parciales",
            descripcion = "Para semanas de exámenes o alta carga",
            duracion = "1 semana",
            objetivo = "Maximizar la calidad del sueño en periodos de estrés académico.",
            estado = EstadoPlan.COMPLETADO,
            tareas = listOf(TareaDiaria(1, "Realizar una sesión de relajación de 10 min."), TareaDiaria(2, "Asegurar oscuridad total en la habitación.")),
            infoMedica = "Incluso periodos cortos de sueño profundo son vitales para la consolidación de la memoria. Técnicas de relajación y un ambiente oscuro maximizan la eficiencia del sueño, permitiendo una mejor recuperación en menos tiempo."
        ),
        PlanDeSueño(
            id = "plan4",
            nombre = "Sueño saludable completo",
            descripcion = "Para usuarios que quieren adoptar todo el hábito completo",
            duracion = "4 semanas",
            objetivo = "Integrar todos los hábitos clave para un estilo de vida con sueño saludable.",
            estado = EstadoPlan.NO_COMPLETADO,
            tareas = listOf(TareaDiaria(1, "Cenar 2 horas antes de acostarse."), TareaDiaria(2, "Tomar un té relajante.")),
            infoMedica = "Un enfoque integral que incluye dieta, ejercicio y rituales de relajación crea un ciclo de sueño robusto y sostenible. La digestión y la temperatura corporal son factores clave que, al ser regulados, conducen a un descanso óptimo."
        )
    )

    fun obtenerPlanes(): List<PlanDeSueño> = planes
    fun obtenerPlanPorId(id: String): PlanDeSueño? = planes.find { it.id == id }
}

// --- COMPOSABLES ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SueñoScreen(navController: NavController) {
    val planesAgrupados = remember { SueñoRepository.obtenerPlanes().groupBy { it.estado } }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Planes de Sueño", color = PrimaryTextColor) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = PrimaryTextColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            planesAgrupados.forEach { (estado, planes) ->
                item {
                    Header(texto = estado.name.replace('_', ' '))
                }
                items(planes) { plan ->
                    PlanCard(plan = plan, onClick = {
                        navController.navigate("plan_detalle/${plan.id}")
                    })
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun Header(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.headlineSmall,
        color = PrimaryTextColor.copy(alpha = 0.8f),
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlanCard(plan: PlanDeSueño, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(plan.nombre, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PrimaryTextColor)
                Spacer(Modifier.height(4.dp))
                Text(plan.descripcion, style = MaterialTheme.typography.bodyMedium, color = PrimaryTextColor.copy(alpha = 0.9f))
            }
            if (plan.estado == EstadoPlan.COMPLETADO) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Completado", tint = Color.Green, modifier = Modifier.size(32.dp).padding(start = 8.dp))
            }
        }
    }
}