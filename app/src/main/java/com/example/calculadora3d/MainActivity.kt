package com.example.calculadora3d

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 1. Estado para controlar el tema (Modo Oscuro por defecto)
            var isDarkTheme by remember { mutableStateOf(true) }

            // 2. Definición de colores
            val colors = if (isDarkTheme) {
                darkColorScheme(
                    primary = Color(0xFFD0BCFF), // Lila más claro para mejor contraste
                    onPrimary = Color(0xFF381E72),
                    primaryContainer = Color(0xFF4F378B),
                    onPrimaryContainer = Color(0xFFEADDFF),
                    background = Color(0xFF1C1B1F),
                    surface = Color(0xFF1C1B1F),
                    onBackground = Color(0xFFE6E1E5),
                    onSurface = Color(0xFFE6E1E5)
                )
            } else {
                lightColorScheme(
                    primary = Color(0xFF6750A4),
                    onPrimary = Color.White,
                    primaryContainer = Color(0xFFEADDFF), // Fondo suave para los títulos
                    onPrimaryContainer = Color(0xFF21005D), // Texto oscuro para los títulos
                    background = Color(0xFFFFFBFE),
                    surface = Color(0xFFFFFBFE),
                    onBackground = Color(0xFF1C1B1F),
                    onSurface = Color(0xFF1C1B1F)
                )
            }

            MaterialTheme(colorScheme = colors) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalculadoraScreen(
                        isDarkTheme = isDarkTheme,
                        onThemeChange = { isDarkTheme = it }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculadoraScreen(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    // --- VARIABLES DE ESTADO ---
    var precioBobina by remember { mutableStateOf("") }
    var pesoBobina by remember { mutableStateOf("1000") }
    var gramosUsados by remember { mutableStateOf("") }
    var horasImpresion by remember { mutableStateOf("") }
    var costeKwh by remember { mutableStateOf("0.15") }
    var potenciaImpresora by remember { mutableStateOf("300") }
    var horasDiseno by remember { mutableStateOf("0") }
    var precioHoraDiseno by remember { mutableStateOf("0") }
    var margenBeneficio by remember { mutableStateOf("20") }

    // --- CÁLCULOS ---
    val pBobina = precioBobina.toDoubleOrNull() ?: 0.0
    val wBobina = pesoBobina.toDoubleOrNull() ?: 1.0
    val gUsados = gramosUsados.toDoubleOrNull() ?: 0.0
    val hImpresion = horasImpresion.toDoubleOrNull() ?: 0.0
    val cKwh = costeKwh.toDoubleOrNull() ?: 0.0
    val potImpresora = potenciaImpresora.toDoubleOrNull() ?: 0.0
    val hDiseno = horasDiseno.toDoubleOrNull() ?: 0.0
    val pHoraDiseno = precioHoraDiseno.toDoubleOrNull() ?: 0.0
    val margen = margenBeneficio.toDoubleOrNull() ?: 0.0


// Lógica de costes
    val costeMaterial = (pBobina / wBobina) * gUsados
    val costeLuz = (potImpresora / 1000) * hImpresion * cKwh
    val costeManoObra = hDiseno * pHoraDiseno
    val costeTotal = costeMaterial + costeLuz + costeManoObra

    // Lógica de venta + IVA
    val precioVentaBase = costeTotal * (1 + (margen / 100))
    val iva = precioVentaBase * 0.21
    val precioFinal = precioVentaBase + iva

    // Formateador
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "ES"))

    // --- INTERFAZ ---
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // CABECERA
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "💰 Calculadora 3D",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black, // Más grueso
                    color = MaterialTheme.colorScheme.primary
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isDarkTheme) "Oscuro" else "Claro",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { onThemeChange(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- BLOQUE 1: MATERIAL ---
            SectionHeader(title = "1. Material", icon = "🧵")
            InputNumber(value = precioBobina, onValueChange = { precioBobina = it }, label = "Precio Bobina (€)")
            InputNumber(value = pesoBobina, onValueChange = { pesoBobina = it }, label = "Peso Bobina (gramos)")
            InputNumber(value = gramosUsados, onValueChange = { gramosUsados = it }, label = "Gramos a usar")

            Spacer(modifier = Modifier.height(16.dp))

            // --- BLOQUE 2: ELECTRICIDAD ---
            SectionHeader(title = "2. Energía y Tiempo", icon = "⚡")
            InputNumber(value = horasImpresion, onValueChange = { horasImpresion = it }, label = "Horas de impresión")
            InputNumber(value = costeKwh, onValueChange = { costeKwh = it }, label = "Coste electricidad (€/kWh)")
            InputNumber(value = potenciaImpresora, onValueChange = { potenciaImpresora = it }, label = "Potencia Impresora (Watts)")

            Spacer(modifier = Modifier.height(16.dp))

            // --- BLOQUE 3: DISEÑO ---
            SectionHeader(title = "3. Mano de Obra", icon = "🛠️")
            InputNumber(value = horasDiseno, onValueChange = { horasDiseno = it }, label = "Horas de diseño")
            InputNumber(value = precioHoraDiseno, onValueChange = { precioHoraDiseno = it }, label = "Precio por hora (€)")

            Spacer(modifier = Modifier.height(16.dp))

            // --- BLOQUE 4: BENEFICIO ---
            SectionHeader(title = "4. Beneficio", icon = "📈")
            InputNumber(value = margenBeneficio, onValueChange = { margenBeneficio = it }, label = "Margen de ganancia (%)")


            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(16.dp))

            // --- RESULTADOS ---
            ResultRow(label = "Coste Material:", value = currencyFormat.format(costeMaterial))
            ResultRow(label = "Coste Luz:", value = currencyFormat.format(costeLuz))
            ResultRow(label = "Coste Mano Obra:", value = currencyFormat.format(costeManoObra))

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Coste Producción: ${currencyFormat.format(costeTotal)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(20.dp))

            // --- TARJETA FINAL ---
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("Base Imponible:", color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(currencyFormat.format(precioVentaBase), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("IVA (21%):", color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(currencyFormat.format(iva), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("PRECIO FINAL (PVP)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                    Text(
                        text = currencyFormat.format(precioFinal),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

// --- COMPONENTE DE CABECERA MEJORADO ---
@Composable
fun SectionHeader(title: String, icon: String) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer, // Color de fondo del bloque
        shape = RoundedCornerShape(12.dp), // Bordes redondeados
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 18.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer // Texto con alto contraste
            )
        }
    }
}

@Composable
fun InputNumber(value: String, onValueChange: (String) -> Unit, label: String) {


    OutlinedTextField(
        value = value,
        onValueChange = { input ->
            if (input.all { char -> char.isDigit() || char == '.' } && input.count { it == '.' } <= 1) {
                onValueChange(input)
            }
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        singleLine = true,
        // Mejoramos los colores del input para que se vea más limpio
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}
