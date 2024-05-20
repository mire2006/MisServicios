package cl.mferrer.android.misservicios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import cl.mferrer.android.misservicios.ui.theme.MisServiciosTheme
import java.text.DecimalFormat
import java.time.LocalDate

fun ComponentActivity.obtenerDatabase(): AppDatabase {
    return Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java,
        "mis_servicios_db"
    ).build()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val database = obtenerDatabase()
            val viewModelFactory = ListadoViewModelFactory(database)

            MisServiciosTheme {
                AppNavigation(viewModelFactory)
            }
        }
    }
}

@Composable
fun PantallaFormulario(navController: NavController, viewModel: ListadoViewModel) {
    var tipoServicio by remember { mutableStateOf("Agua") }
    var valorMedidor by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf(LocalDate.now().toString()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.registro_medidor),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = valorMedidor,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.matches(Regex("[0-9]+(\\.[0-9]+)?"))) {
                    valorMedidor = newValue
                }
            },
            label = { Text(stringResource(id = R.string.medidor)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = fecha,
            onValueChange = { fecha = it },
            label = { Text(stringResource(id = R.string.fecha)) },
            modifier = Modifier.fillMaxWidth()
        )

        Text(stringResource(id = R.string.medidor_de))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = tipoServicio == "Agua",
                    onClick = { tipoServicio = "Agua" })
                Text(stringResource(id = R.string.agua))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = tipoServicio == "Luz",
                    onClick = { tipoServicio = "Luz" })
                Text(stringResource(id = R.string.luz))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = tipoServicio == "Gas",
                    onClick = { tipoServicio = "Gas" })
                Text(stringResource(id = R.string.gas))
            }
        }

        Button(
            onClick = {
                val nuevoRegistroEntity = RegistroEntity(
                    tipo = tipoServicio,
                    valor = valorMedidor.toDouble(),
                    fecha = fecha
                )
                viewModel.agregarRegistro(nuevoRegistroEntity)
                navController.navigate("listado")
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6E39CA))
        ) {
            Text(stringResource(id = R.string.registrar_medicion))
        }
    }
}

@Composable
fun ItemRegistro(registro: RegistroEntity) {
    val decimalFormat = DecimalFormat("#.##")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = when (registro.tipo) {
                    "Agua" -> Icons.Default.WaterDrop
                    "Luz" -> Icons.Default.Lightbulb
                    "Gas" -> Icons.Default.LocalFireDepartment
                    else -> Icons.Filled.QuestionMark
                },
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(text = registro.tipo)
        }

        Text(
            text = decimalFormat.format(registro.valor),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        Text(text = registro.fecha)
    }

    Divider(modifier = Modifier.fillMaxWidth())
}

@Composable
fun PantallaListado(navController: NavController, viewModel: ListadoViewModel) {

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("formulario") },
                modifier = Modifier
                    .padding(16.dp)
                    .width(70.dp)
                    .height(70.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar registro")
            }
        },
        floatingActionButtonPosition = FabPosition.End

    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier.fillMaxSize()
        ) {
            items(viewModel.listaRegistros) { registro ->
                ItemRegistro(registro = registro)
            }
        }
    }
}

@Composable
fun AppNavigation(viewModelFactory: ListadoViewModelFactory) {
    val navController = rememberNavController()
    val viewModel: ListadoViewModel = viewModel(factory = viewModelFactory)

    NavHost(navController = navController, startDestination = "listado") {
        composable("listado") {
            PantallaListado(navController = navController, viewModel = viewModel)
        }
        composable("formulario") {
            PantallaFormulario(navController = navController, viewModel = viewModel)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    val context = LocalContext.current
    val database = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "mis_servicios_db"
    ).build()
    val viewModelFactory = ListadoViewModelFactory(database)

    MisServiciosTheme {
        AppNavigation(viewModelFactory)
    }
}