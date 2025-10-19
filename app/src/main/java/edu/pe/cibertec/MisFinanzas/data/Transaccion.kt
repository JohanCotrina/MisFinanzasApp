package edu.pe.cibertec.MisFinanzas.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TRANSACCIONES")
data class Transaccion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val description: String,
    val type: String,
    val date: Long,
    val categoria: String
)