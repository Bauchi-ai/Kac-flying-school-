package com.example.data.repository

import com.example.data.local.StudentDao
import com.example.data.local.FlightDao
import com.example.data.local.CurriculumDao
import com.example.data.local.TransactionDao
import com.example.data.model.StudentProfile
import com.example.data.model.FlightBooking
import com.example.data.model.CurriculumModule
import com.example.data.model.PaymentTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class KacRepository(
    private val studentDao: StudentDao,
    private val flightDao: FlightDao,
    private val curriculumDao: CurriculumDao,
    private val transactionDao: TransactionDao
) {
    val studentProfile: Flow<StudentProfile?> = studentDao.getProfile()
    val flightBookings: Flow<List<FlightBooking>> = flightDao.getAllBookings()
    val curriculumModules: Flow<List<CurriculumModule>> = curriculumDao.getAllModules()
    val paymentTransactions: Flow<List<PaymentTransaction>> = transactionDao.getAllTransactions()

    suspend fun saveProfile(profile: StudentProfile) {
        studentDao.insertOrUpdateProfile(profile)
    }

    suspend fun bookFlight(booking: FlightBooking) {
        flightDao.insertBooking(booking)
    }

    suspend fun deleteBooking(id: Int) {
        flightDao.deleteBookingById(id)
    }

    suspend fun updateModuleProgress(module: CurriculumModule) {
        curriculumDao.updateModule(module)
    }

    suspend fun addTransaction(transaction: PaymentTransaction) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(id: Int) {
        transactionDao.deleteTransactionById(id)
    }

    suspend fun checkAndSeed() {
        val currentModules = curriculumDao.getAllModules().first()
        if (currentModules.isEmpty()) {
            val seedModules = listOf(
                CurriculumModule(
                    id = "AIR-101",
                    title = "Aviation Law & Rules",
                    description = "Study of international and national regulations, airspace rules, flight licensing, and air traffic control procedures.",
                    totalLessons = 10,
                    completedLessons = 4,
                    progressPercent = 40,
                    isCompleted = false,
                    examScore = -1
                ),
                CurriculumModule(
                    id = "MET-101",
                    title = "Aviation Meteorology",
                    description = "Understanding weather patterns, wind shear, clouds, microbursts, aviation forecasts, and handling adverse weather conditions safely.",
                    totalLessons = 12,
                    completedLessons = 8,
                    progressPercent = 66,
                    isCompleted = false,
                    examScore = -1
                ),
                CurriculumModule(
                    id = "NAV-101",
                    title = "Flight Navigation & Planning",
                    description = "Traditional and digital navigation techniques, flight computers, dead reckoning, VOR/GPS systems, and filing operational flight plans.",
                    totalLessons = 15,
                    completedLessons = 10,
                    progressPercent = 66,
                    isCompleted = false,
                    examScore = -1
                ),
                CurriculumModule(
                    id = "AGK-101",
                    title = "Aircraft General Knowledge",
                    description = "Detailed principles of flight, airframes, engines, fuel systems, flight instruments, and mechanical systems of light trainer aircraft.",
                    totalLessons = 14,
                    completedLessons = 2,
                    progressPercent = 14,
                    isCompleted = false,
                    examScore = -1
                ),
                CurriculumModule(
                    id = "HP-101",
                    title = "Human Performance",
                    description = "Exploring aeromedical factors, hypoxia, spatial disorientation, fatigue, vision limitations, and cockpit crew resource management (CRM).",
                    totalLessons = 8,
                    completedLessons = 8,
                    progressPercent = 100,
                    isCompleted = true,
                    examScore = 88
                ),
                CurriculumModule(
                    id = "OPS-101",
                    title = "Operational Procedures",
                    description = "Standard operating procedures, emergency protocols, search and rescue guidelines, aircraft security, and noise abatement rules.",
                    totalLessons = 10,
                    completedLessons = 0,
                    progressPercent = 0,
                    isCompleted = false,
                    examScore = -1
                )
            )
            curriculumDao.insertModules(seedModules)
        }

        val currentProfile = studentDao.getProfile().first()
        if (currentProfile == null) {
            studentDao.insertOrUpdateProfile(StudentProfile())
        }

        val currentBookings = flightDao.getAllBookings().first()
        if (currentBookings.isEmpty()) {
            val seedBookings = listOf(
                FlightBooking(
                    date = "2026-07-10",
                    time = "08:30 AM",
                    aircraft = "Cessna 172 Skyhawk (5Y-KAC)",
                    instructor = "Capt. James Mwangi",
                    durationHours = 1.5f,
                    purpose = "General Handling & Circuits",
                    status = "Completed"
                ),
                FlightBooking(
                    date = "2026-07-12",
                    time = "10:00 AM",
                    aircraft = "Cessna 172 Skyhawk (5Y-KAC)",
                    instructor = "Capt. James Mwangi",
                    durationHours = 2.0f,
                    purpose = "Solo Cross-Country Flight",
                    status = "Completed"
                ),
                FlightBooking(
                    date = "2026-07-16",
                    time = "09:00 AM",
                    aircraft = "Piper PA-28 Archer (5Y-KAS)",
                    instructor = "Capt. Alice Wambui",
                    durationHours = 2.5f,
                    purpose = "Dual Instrument Rating Training",
                    status = "Scheduled"
                ),
                FlightBooking(
                    date = "2026-07-18",
                    time = "14:00 PM",
                    aircraft = "Cessna 172 Skyhawk (5Y-KAC)",
                    instructor = "Capt. James Mwangi",
                    durationHours = 1.0f,
                    purpose = "Pre-Solo Flight Check",
                    status = "Pending Approval"
                )
            )
            for (booking in seedBookings) {
                flightDao.insertBooking(booking)
            }
        }

        val currentTransactions = transactionDao.getAllTransactions().first()
        if (currentTransactions.isEmpty()) {
            val seedTransactions = listOf(
                PaymentTransaction(
                    referenceCode = "QA16T9J8M2",
                    amount = 5000.0,
                    purpose = "Admission Registration Fee",
                    paymentMethod = "M-Pesa Express",
                    date = "2026-07-01",
                    status = "Success",
                    receiptNumber = "REC-KAC-10029",
                    billingPhoneOrEmail = "254712345678"
                )
            )
            for (tx in seedTransactions) {
                transactionDao.insertTransaction(tx)
            }
        }
    }
}
