package edu.pe.cibertec.MisFinanzas.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Transaccion::class], version = 2, exportSchema = false)
abstract class AppDatabase: RoomDatabase(){

    abstract fun transaccionDao(): TranasaccionDao
}