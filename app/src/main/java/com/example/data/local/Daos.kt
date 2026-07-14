package com.example.data.local

import androidx.room.*
import com.example.data.model.StudentProfile
import com.example.data.model.FlightBooking
import com.example.data.model.CurriculumModule
import com.example.data.model.PaymentTransaction
import com.example.data.model.FlightLog
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM student_profile WHERE id = 1 LIMIT 1")
    fun getProfile(): Flow<StudentProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: StudentProfile)
}

@Dao
interface FlightDao {
    @Query("SELECT * FROM flight_bookings ORDER BY date DESC, time DESC")
    fun getAllBookings(): Flow<List<FlightBooking>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: FlightBooking)

    @Update
    suspend fun updateBooking(booking: FlightBooking)

    @Query("DELETE FROM flight_bookings WHERE id = :id")
    suspend fun deleteBookingById(id: Int)
}

@Dao
interface CurriculumDao {
    @Query("SELECT * FROM curriculum_modules ORDER BY id ASC")
    fun getAllModules(): Flow<List<CurriculumModule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModules(modules: List<CurriculumModule>)

    @Update
    suspend fun updateModule(module: CurriculumModule)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM payment_transactions ORDER BY date DESC, id DESC")
    fun getAllTransactions(): Flow<List<PaymentTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: PaymentTransaction)

    @Query("DELETE FROM payment_transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Int)
}

@Dao
interface FlightLogDao {
    @Query("SELECT * FROM flight_logs ORDER BY date DESC, id DESC")
    fun getAllLogs(): Flow<List<FlightLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: FlightLog)

    @Query("DELETE FROM flight_logs WHERE id = :id")
    suspend fun deleteLogById(id: Int)
}
