package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_profile")
data class StudentProfile(
    @PrimaryKey val id: Int = 1,
    val fullName: String = "Aviation Cadet",
    val studentId: String = "KAC-2026-0042",
    val licenseType: String = "Private Pilot License (PPL)",
    val medicalStatus: String = "Class 2 Active",
    val totalHours: Float = 14.5f,
    val email: String = "cadet@kac.ac.ke",
    val phone: String = "+254 712 345 678",
    val isRegistered: Boolean = true,
    val feesPaid: Double = 5000.0,
    val feesInvoiced: Double = 125000.0
)

@Entity(tableName = "flight_bookings")
data class FlightBooking(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val time: String,
    val aircraft: String,
    val instructor: String,
    val durationHours: Float,
    val purpose: String,
    val status: String // "Scheduled", "Completed", "Pending Approval"
)

@Entity(tableName = "curriculum_modules")
data class CurriculumModule(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val totalLessons: Int,
    val completedLessons: Int,
    val progressPercent: Int,
    val isCompleted: Boolean = false,
    val examScore: Int = -1 // -1 means exam not taken yet
)

@Entity(tableName = "payment_transactions")
data class PaymentTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val referenceCode: String,
    val amount: Double,
    val purpose: String, // e.g. "Registration Fee", "Tuition Fee", "Flight Hours (5 hrs Cessna 172)", "Meteorology Exam Fee"
    val paymentMethod: String, // "M-Pesa Express", "Stripe (Visa/Mastercard)", "PayPal Secure", "KCB Direct Transfer", "Equity Bank Paybill"
    val date: String,
    val status: String, // "Success", "Pending Verification", "Failed"
    val receiptNumber: String,
    val billingPhoneOrEmail: String
)
