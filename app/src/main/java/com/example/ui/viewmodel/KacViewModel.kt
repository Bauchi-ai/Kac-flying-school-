package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.CurriculumModule
import com.example.data.model.FlightBooking
import com.example.data.model.StudentProfile
import com.example.data.model.PaymentTransaction
import com.example.data.repository.KacRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class KacViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = KacRepository(
        database.studentDao(),
        database.flightDao(),
        database.curriculumDao(),
        database.transactionDao()
    )

    val studentProfile: StateFlow<StudentProfile?> = repository.studentProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val flightBookings: StateFlow<List<FlightBooking>> = repository.flightBookings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val curriculumModules: StateFlow<List<CurriculumModule>> = repository.curriculumModules
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val paymentTransactions: StateFlow<List<PaymentTransaction>> = repository.paymentTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            repository.checkAndSeed()
        }
    }

    fun saveProfile(profile: StudentProfile) {
        viewModelScope.launch {
            repository.saveProfile(profile)
        }
    }

    fun bookFlight(booking: FlightBooking) {
        viewModelScope.launch {
            repository.bookFlight(booking)
        }
    }

    fun cancelBooking(id: Int) {
        viewModelScope.launch {
            repository.deleteBooking(id)
        }
    }

    fun completeLesson(module: CurriculumModule) {
        viewModelScope.launch {
            val newCompleted = (module.completedLessons + 1).coerceAtMost(module.totalLessons)
            val isCompletedNow = newCompleted == module.totalLessons
            val percent = ((newCompleted.toFloat() / module.totalLessons.toFloat()) * 100).toInt()
            val score = if (isCompletedNow && module.examScore == -1) 85 else module.examScore
            
            val updated = module.copy(
                completedLessons = newCompleted,
                progressPercent = percent,
                isCompleted = isCompletedNow,
                examScore = score
            )
            repository.updateModuleProgress(updated)
        }
    }

    fun resetCurriculum() {
        viewModelScope.launch {
            val list = listOf(
                CurriculumModule("AIR-101", "Aviation Law & Rules", "Study of international and national regulations, airspace rules, flight licensing, and air traffic control procedures.", 10, 4, 40, false, -1),
                CurriculumModule("MET-101", "Aviation Meteorology", "Understanding weather patterns, wind shear, clouds, microbursts, aviation forecasts, and handling adverse weather conditions safely.", 12, 8, 66, false, -1),
                CurriculumModule("NAV-101", "Flight Navigation & Planning", "Traditional and digital navigation techniques, flight computers, dead reckoning, VOR/GPS systems, and filing operational flight plans.", 15, 10, 66, false, -1),
                CurriculumModule("AGK-101", "Aircraft General Knowledge", "Detailed principles of flight, airframes, engines, fuel systems, flight instruments, and mechanical systems of light trainer aircraft.", 14, 2, 14, false, -1),
                CurriculumModule("HP-101", "Human Performance", "Exploring aeromedical factors, hypoxia, spatial disorientation, fatigue, vision limitations, and cockpit crew resource management (CRM).", 8, 8, 100, true, 88),
                CurriculumModule("OPS-101", "Operational Procedures", "Standard operating procedures, emergency protocols, search and rescue guidelines, aircraft security, and noise abatement rules.", 10, 0, 0, false, -1)
            )
            database.curriculumDao().insertModules(list)
        }
    }

    fun takeExam(module: CurriculumModule, score: Int) {
        viewModelScope.launch {
            val updated = module.copy(
                isCompleted = true,
                completedLessons = module.totalLessons,
                progressPercent = 100,
                examScore = score
            )
            repository.updateModuleProgress(updated)
        }
    }

    fun processPayment(
        amount: Double,
        purpose: String,
        paymentMethod: String,
        billingRef: String,
        status: String = "Success"
    ) {
        viewModelScope.launch {
            val randomLetters = ('A'..'Z').map { it }.shuffled().take(4).joinToString("")
            val randomNums = (1000..9999).random()
            val referenceCode = "${randomLetters}${randomNums}"
            val receiptNumber = "REC-KAC-${(10000..99999).random()}"
            val currentDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())

            val tx = PaymentTransaction(
                referenceCode = referenceCode,
                amount = amount,
                purpose = purpose,
                paymentMethod = paymentMethod,
                date = currentDate,
                status = status,
                receiptNumber = receiptNumber,
                billingPhoneOrEmail = billingRef
            )
            repository.addTransaction(tx)

            if (status == "Success") {
                studentProfile.value?.let { profile ->
                    var updatedPaid = profile.feesPaid
                    var updatedInvoiced = profile.feesInvoiced
                    var updatedHours = profile.totalHours

                    if (purpose.contains("Tuition") || purpose.contains("Registration")) {
                        updatedPaid += amount
                    } else if (purpose.contains("Flight Hours")) {
                        // Assuming 15,000 KES per flight hour
                        val purchasedHours = (amount / 15000.0).toFloat()
                        updatedHours += purchasedHours
                        updatedInvoiced += amount
                        updatedPaid += amount
                    } else {
                        updatedInvoiced += amount
                        updatedPaid += amount
                    }

                    val updatedProfile = profile.copy(
                        feesPaid = updatedPaid,
                        feesInvoiced = updatedInvoiced,
                        totalHours = updatedHours
                    )
                    repository.saveProfile(updatedProfile)
                }
            }
        }
    }
}
