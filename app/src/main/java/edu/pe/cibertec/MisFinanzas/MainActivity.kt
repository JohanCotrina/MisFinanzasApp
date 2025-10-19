package edu.pe.cibertec.MisFinanzas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider


import edu.pe.cibertec.MisFinanzas.data.DatabaseProvider
import edu.pe.cibertec.MisFinanzas.data.Transaccion
import edu.pe.cibertec.MisFinanzas.data.Categoria
import edu.pe.cibertec.MisFinanzas.repository.TransaccionRepository
import edu.pe.cibertec.MisFinanzas.screens.AddTransaccionesDialog
import edu.pe.cibertec.MisFinanzas.ui.theme.Financial_track_appTheme
import edu.pe.cibertec.MisFinanzas.viewmodel.TransaccionViewModel
import edu.pe.cibertec.MisFinanzas.viewmodel.TransaccionViewModelFactory
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val db = DatabaseProvider.getDatabase(applicationContext)
        val repository = TransaccionRepository(db.transaccionDao())
        val factory = TransaccionViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)[TransaccionViewModel::class.java]

        setContent {
            Financial_track_appTheme {
                MainScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: TransaccionViewModel) {
    val transaccciones by viewModel.trnasaacciones.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var transaccionEditar by remember { mutableStateOf<Transaccion?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mis Gastos") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text("+", style = MaterialTheme.typography.headlineSmall)
            }
        }
    ) { paddingVal ->

        Column(
            modifier = Modifier
                .padding(paddingVal)
                .fillMaxSize()
        ) {
            LazyColumn {
                items(transaccciones) { transaccion ->
                    TransaccionItem(
                        transaccion = transaccion,
                        onDelete = { viewModel.delete(it) },
                        onClick = { transaccionEditar = it }
                    )
                }

                item {
                    val totalGastos = transaccciones.filter { it.type == "Gasto" }.sumOf { it.amount }
                    val totalIngresos = transaccciones.filter { it.type == "Ingreso" }.sumOf { it.amount }
                    val balance = totalIngresos - totalGastos
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Total: -$${String.format("%.2f", totalGastos)}",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                            if (totalIngresos > 0) {
                                Text(
                                    text = "Ingresos: +$${String.format("%.2f", totalIngresos)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Balance: ${if (balance >= 0) "+" else ""}$${String.format("%.2f", balance)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (balance >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AddTransaccionesDialog(
                onAdd = { transaccion ->
                    viewModel.insert(transaccion)
                    showDialog = false
                },
                onDismiss = {
                    showDialog = false
                },
                transaccionesExistentes = transaccciones
            )
        }
        if (transaccionEditar != null) {
            AddTransaccionesDialog(
                transaccion =  transaccionEditar,
                onAdd = { UpdateTransaccion ->
                    viewModel.insert(UpdateTransaccion)
                    transaccionEditar = null
                },
                onDismiss = {
                    transaccionEditar = null
                },
                transaccionesExistentes = transaccciones
            )
        }

    }

}

@Composable
fun TransaccionItem(
    transaccion: Transaccion,
    onDelete: (Transaccion) -> Unit,
    onClick: (Transaccion) -> Unit
) {
    val categoria = Categoria.values().find { it.name == transaccion.categoria } ?: Categoria.OTROS
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val isGasto = transaccion.type == "Gasto"
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick(transaccion) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = categoria.icono,
                contentDescription = null,
                tint = categoria.color,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaccion.description.ifBlank { categoria.nombre },
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = dateFormatter.format(Date(transaccion.date)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "${if (isGasto) "-" else "+"}$${String.format("%.2f", transaccion.amount)}",
                style = MaterialTheme.typography.bodyLarge,
                color = if (isGasto) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        }
    }
}