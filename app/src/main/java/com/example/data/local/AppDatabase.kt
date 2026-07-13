package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.StudentProfile
import com.example.data.model.FlightBooking
import com.example.data.model.CurriculumModule
import com.example.data.model.PaymentTransaction

@Database(
    entities = [StudentProfile::class, FlightBooking::class, CurriculumModule::class, PaymentTransaction::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun flightDao(): FlightDao
    abstract fun curriculumDao(): CurriculumDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kac_aviation_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
