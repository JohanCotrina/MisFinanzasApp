package edu.pe.cibertec.MisFinanzas.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class Categoria(
    val nombre: String,
    val icono: ImageVector,
    val color: Color,
    val limiteMensual: Double
) {
    ALIMENTACION(
        nombre = "Alimentación",
        icono = Icons.Default.Restaurant,
        color = Color(0xFF4CAF50),
        limiteMensual = 800.0
    ),
    TRANSPORTE(
        nombre = "Transporte",
        icono = Icons.Default.DirectionsCar,
        color = Color(0xFF2196F3),
        limiteMensual = 300.0
    ),
    ENTRETENIMIENTO(
        nombre = "Entretenimiento",
        icono = Icons.Default.Movie,
        color = Color(0xFF9C27B0),
        limiteMensual = 200.0
    ),
    VIVIENDA(
        nombre = "Vivienda",
        icono = Icons.Default.Home,
        color = Color(0xFFF44336),
        limiteMensual = 1500.0
    ),
    SALUD(
        nombre = "Salud",
        icono = Icons.Default.LocalHospital,
        color = Color(0xFFE91E63),
        limiteMensual = 400.0
    ),
    CAFE_BEBIDAS(
        nombre = "Café/Bebidas",
        icono = Icons.Default.LocalCafe,
        color = Color(0xFF795548),
        limiteMensual = 150.0
    ),
    COMPRAS(
        nombre = "Compras",
        icono = Icons.Default.ShoppingCart,
        color = Color(0xFFFF9800),
        limiteMensual = 500.0
    ),
    OTROS(
        nombre = "Otros",
        icono = Icons.Default.Info,
        color = Color(0xFF9E9E9E),
        limiteMensual = 300.0
    )
}
