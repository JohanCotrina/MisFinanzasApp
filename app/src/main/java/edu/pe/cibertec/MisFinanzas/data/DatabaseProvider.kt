package edu.pe.cibertec.MisFinanzas.data

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseProvider{
    @Volatile
    private var INSTANCE: AppDatabase? = null

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE TRANSACCIONES ADD COLUMN categoria TEXT NOT NULL DEFAULT 'OTROS'")
        }
    }

    fun getDatabase(context: Context): AppDatabase{
        return  INSTANCE ?: synchronized (this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "finantial_tracker_db"
            )
            .addMigrations(MIGRATION_1_2)
            .fallbackToDestructiveMigration()
            .build()
            INSTANCE = instance
            instance
        }
    }

}