package edu.pe.cibertec.MisFinanzas.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import edu.pe.cibertec.MisFinanzas.data.Transaccion
import edu.pe.cibertec.MisFinanzas.data.Categoria
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransaccionesDialog(
    onAdd: (Transaccion) -> Unit,
    onDismiss: () -> Unit,
    transaccion: Transaccion? = null,
    transaccionesExistentes: List<Transaccion> = emptyList()
) {
    var amount by remember { mutableStateOf(transaccion?.amount?.toString() ?: "0") }
    var description by remember { mutableStateOf(transaccion?.description?.toString() ?: "") }
    var type by remember { mutableStateOf(transaccion?.type?.toString() ?: "Gasto") }
    var selectedDate by remember { mutableStateOf(transaccion?.date ?: System.currentTimeMillis()) }
    var selectedCategoria by remember { mutableStateOf(transaccion?.categoria ?: "OTROS") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showCategoriaPicker by remember { mutableStateOf(false) }

    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val selectedCategoriaEnum = Categoria.values().find { it.name == selectedCategoria } ?: Categoria.OTROS

    fun calcularGastosMensuales(categoria: String, fecha: Long): Double {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = fecha
        val mes = calendar.get(Calendar.MONTH)
        val año = calendar.get(Calendar.YEAR)
        
        return transaccionesExistentes
            .filter { it.categoria == categoria && it.type == "Gasto" }
            .filter { 
                val cal = Calendar.getInstance()
                cal.timeInMillis = it.date
                cal.get(Calendar.MONTH) == mes && cal.get(Calendar.YEAR) == año
            }
            .sumOf { it.amount }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (transaccion == null) "Registrar Nuevo Gasto" else "Editar Gasto",
                    style = MaterialTheme.typography.headlineSmall
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Monto") },
                    prefix = { Text("$") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción (opcional)") },
                    placeholder = { Text("Ej. Café con amigos") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dateFormatter.format(Date(selectedDate)),
                    onValueChange = { },
                    label = { Text("Fecha") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = selectedCategoriaEnum.nombre,
                    onValueChange = { },
                    label = { Text("Categoría") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showCategoriaPicker = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Seleccionar categoría")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }
                    
                    Button(
                        onClick = {
                            val amountValue = amount.toDoubleOrNull() ?: 0.0
                            if (amountValue > 0) {
                                val gastosMensuales = calcularGastosMensuales(selectedCategoria, selectedDate)
                                val nuevoTotal = gastosMensuales + amountValue
                                
                                if (nuevoTotal > selectedCategoriaEnum.limiteMensual) {

                                }
                                
                                onAdd(
                                    Transaccion(
                                        id = transaccion?.id ?: 0,
                                        amount = amountValue,
                                        description = description.ifBlank { selectedCategoriaEnum.nombre },
                                        type = type,
                                        date = selectedDate,
                                        categoria = selectedCategoria
                                    )
                                )
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Guardar Gasto")
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDate = it
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showCategoriaPicker) {
        AlertDialog(
            onDismissRequest = { showCategoriaPicker = false },
            title = { Text("Seleccionar Categoría") },
            text = {
                Column {
                    Categoria.values().forEach { categoria ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    selectedCategoria = categoria.name
                                    showCategoriaPicker = false 
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = categoria.icono,
                                contentDescription = null,
                                tint = categoria.color,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = categoria.nombre)
                                Text(
                                    text = "Límite: $${categoria.limiteMensual}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoriaPicker = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}