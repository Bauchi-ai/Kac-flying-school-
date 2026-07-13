package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.CurriculumModule
import com.example.data.model.FlightBooking
import com.example.data.model.StudentProfile
import com.example.data.model.PaymentTransaction
import com.example.ui.viewmodel.KacViewModel

// Custom Theme Color Tokens
private val NavyPrimary = Color(0xFF0F2C59)
private val BlueAccent = Color(0xFF3B82F6)
private val GoldAccent = Color(0xFFDAC0A3)
private val LightBg = Color(0xFFF8FAFC)
private val CardBg = Color(0xFFFFFFFF)
private val DarkText = Color(0xFF1E293B)
private val MutedText = Color(0xFF64748B)

sealed class PortalTab(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : PortalTab("dashboard", "Home", Icons.Default.Dashboard)
    object Catalog : PortalTab("catalog", "Courses", Icons.Default.Book)
    object Registration : PortalTab("registration", "Profile", Icons.Default.HowToReg)
    object Scheduling : PortalTab("scheduling", "Flights", Icons.Default.FlightTakeoff)
    object Curriculum : PortalTab("curriculum", "Study", Icons.Default.School)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KacPortalScreen(
    viewModel: KacViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf<PortalTab>(PortalTab.Dashboard) }
    
    val profileState by viewModel.studentProfile.collectAsStateWithLifecycle()
    val bookingsState by viewModel.flightBookings.collectAsStateWithLifecycle()
    val modulesState by viewModel.curriculumModules.collectAsStateWithLifecycle()
    val transactionsState by viewModel.paymentTransactions.collectAsStateWithLifecycle()

    var activeQuizModule by remember { mutableStateOf<CurriculumModule?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AirplanemodeActive,
                            contentDescription = "KAC Wings Logo",
                            tint = Color.White,
                            modifier = Modifier
                                .size(28.dp)
                                .padding(end = 6.dp)
                        )
                        Text(
                            text = "KAC FLYING SCHOOL",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            fontSize = 18.sp
                        )
                    }
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(36.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "KAC",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            Toast.makeText(context, "KAC Aviation Student Portal v1.0", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.Info, contentDescription = "Portal Info", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = NavyPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = NavyPrimary,
                tonalElevation = 8.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                val tabs = listOf(
                    PortalTab.Dashboard,
                    PortalTab.Catalog,
                    PortalTab.Registration,
                    PortalTab.Scheduling,
                    PortalTab.Curriculum
                )
                tabs.forEach { tab ->
                    val selected = currentTab == tab
                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                tint = if (selected) NavyPrimary else Color.White.copy(alpha = 0.6f)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                color = if (selected) Color.White else Color.White.copy(alpha = 0.6f),
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = GoldAccent
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.route}")
                    )
                }
            }
        },
        containerColor = LightBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(200))
                },
                label = "TabTransition"
            ) { targetTab ->
                when (targetTab) {
                    PortalTab.Dashboard -> DashboardTab(
                        profile = profileState,
                        bookings = bookingsState,
                        modules = modulesState,
                        onNavigate = { tab -> currentTab = tab }
                    )
                    PortalTab.Registration -> RegistrationTab(
                        profile = profileState,
                        onSave = { updatedProfile ->
                            viewModel.saveProfile(updatedProfile)
                            Toast.makeText(context, "Cadet Profile Saved Successfully!", Toast.LENGTH_LONG).show()
                        }
                    )
                    PortalTab.Scheduling -> SchedulingTab(
                        profile = profileState,
                        bookings = bookingsState,
                        onBookFlight = { newBooking ->
                            viewModel.bookFlight(newBooking)
                            Toast.makeText(context, "Flight Booking Submitted for Approval!", Toast.LENGTH_LONG).show()
                        },
                        onCancelBooking = { bookingId ->
                            viewModel.cancelBooking(bookingId)
                            Toast.makeText(context, "Booking Cancelled", Toast.LENGTH_SHORT).show()
                        }
                    )
                    PortalTab.Curriculum -> CurriculumTab(
                        modules = modulesState,
                        onCompleteLesson = { module ->
                            viewModel.completeLesson(module)
                            Toast.makeText(context, "Studied lesson in ${module.id}! Progress saved.", Toast.LENGTH_SHORT).show()
                        },
                        onTakeExam = { module ->
                            activeQuizModule = module
                        },
                        onResetAll = {
                            viewModel.resetCurriculum()
                            Toast.makeText(context, "Curriculum Progress Reset to Seed Defaults", Toast.LENGTH_SHORT).show()
                        }
                    )
                    PortalTab.Catalog -> {
                        CatalogTab(
                            profile = profileState,
                            onInquire = { courseName, message ->
                                Toast.makeText(context, "Inquiry for $courseName submitted! Our admissions team will email you.", Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                }
            }

            // Interactive Quiz Dialog
            activeQuizModule?.let { module ->
                AviationQuizDialog(
                    module = module,
                    onDismiss = { activeQuizModule = null },
                    onFinishExam = { score ->
                        viewModel.takeExam(module, score)
                        activeQuizModule = null
                        Toast.makeText(context, "Exam Submitted! Score: $score%", Toast.LENGTH_LONG).show()
                    }
                )
            }
        }
    }
}

// ==========================================
// 1. DASHBOARD PORTAL TAB
// ==========================================
@Composable
fun DashboardTab(
    profile: StudentProfile?,
    bookings: List<FlightBooking>,
    modules: List<CurriculumModule>,
    onNavigate: (PortalTab) -> Unit
) {
    val context = LocalContext.current
    val isRegistered = profile?.isRegistered ?: false
    val totalHours = profile?.totalHours ?: 0.0f
    val nextFlight = bookings.firstOrNull { it.status == "Scheduled" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Banner Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Render image_aviation_hero (seeded at setup)
                    Image(
                        painter = painterResource(id = R.drawable.img_aviation_hero_1783983361908),
                        contentDescription = "KAC Airfield Hero",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentScale = ContentScale.Crop
                    )
                    // Gradient overlay to make text highly legible
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                                    startY = 100f
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Surface(
                            color = GoldAccent,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Text(
                                text = "OFFICIAL CADET PORTAL",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "KAC Flying Academy",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isRegistered) {
                                "Welcome back, Cadet ${profile?.fullName ?: "Aviation Student"}"
                            } else {
                                "Welcome to Kenya Aviation Center Flying School"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }

        // Quick Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Logged Flight Hours",
                    value = "${totalHours} hrs",
                    icon = Icons.Default.Timelapse,
                    tint = BlueAccent,
                    modifier = Modifier.weight(1f)
                )
                
                // Calculate average curriculum progress
                val avgProgress = if (modules.isNotEmpty()) {
                    modules.sumOf { it.progressPercent } / modules.size
                } else 0
                
                StatCard(
                    title = "Ground Progress",
                    value = "$avgProgress%",
                    icon = Icons.Default.Analytics,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Registration Alert / Status
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isRegistered) Color(0xFFECFDF5) else Color(0xFFFFF7ED)
                ),
                border = BorderStroke(1.dp, if (isRegistered) Color(0xFFA7F3D0) else Color(0xFFFED7AA)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(PortalTab.Registration) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isRegistered) Icons.Default.VerifiedUser else Icons.Default.Warning,
                        contentDescription = "Status Icon",
                        tint = if (isRegistered) Color(0xFF059669) else Color(0xFFEA580C),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isRegistered) "Cadet Status: Active & Registered" else "Action Required: Student Registration",
                            fontWeight = FontWeight.Bold,
                            color = if (isRegistered) Color(0xFF065F46) else Color(0xFF9A3412),
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (isRegistered) {
                                "ID: ${profile?.studentId} | Goal: ${profile?.licenseType}"
                            } else {
                                "Please complete the mandatory registration portal to schedule flights."
                            },
                            color = if (isRegistered) Color(0xFF047857) else Color(0xFFC2410C),
                            fontSize = 12.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Go",
                        tint = if (isRegistered) Color(0xFF059669) else Color(0xFFEA580C)
                    )
                }
            }
        }

        // Next Flight Countdown & Sched Preview
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Next Scheduled Training Flight",
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    if (nextFlight != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(NavyPrimary.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.FlightTakeoff,
                                    contentDescription = "Flight",
                                    tint = NavyPrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = nextFlight.aircraft,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = DarkText
                                )
                                Text(
                                    text = "Instructor: ${nextFlight.instructor}",
                                    fontSize = 12.sp,
                                    color = MutedText
                                )
                                Text(
                                    text = "${nextFlight.date} at ${nextFlight.time} (${nextFlight.durationHours} hrs duration)",
                                    fontSize = 12.sp,
                                    color = BlueAccent,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Surface(
                                color = Color(0xFFEFF6FF),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Approved",
                                    color = BlueAccent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        ) {
                            Icon(Icons.Default.EventBusy, contentDescription = "No Flight", tint = MutedText, modifier = Modifier.size(40.dp))
                            Text(
                                text = "No upcoming flight scheduled",
                                color = MutedText,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { onNavigate(PortalTab.Scheduling) },
                                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                            ) {
                                Text("Book Flight Now", fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Campus Announcements Section (UoN Style)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Academy Announcements",
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                AnnouncementItem(
                    tag = "Notice",
                    tagColor = Color(0xFFDC2626),
                    title = "Wilson Airport Runway 06 Closed for Repavement",
                    date = "Today",
                    description = "Runway 06 will be closed from 12:00 UTC to 18:00 UTC for patching. Flight plans must divert to Runway 14 or plan schedules accordingly."
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                AnnouncementItem(
                    tag = "Ground School",
                    tagColor = Color(0xFFF59E0B),
                    title = "Radio Communication Practical Exams On Saturday",
                    date = "July 15, 2026",
                    description = "Mandatory VHF Phraseology checkride prep seminar at the lecture hall 3. Open to all students preparing for first solo flights."
                )

                Spacer(modifier = Modifier.height(8.dp))

                AnnouncementItem(
                    tag = "Academic",
                    tagColor = Color(0xFF10B981),
                    title = "Meteorology Theory Exams Online Release",
                    date = "July 12, 2026",
                    description = "Theoretical tests for MET-101 course modules are now available in the curriculum section. High scores are required for PPL signoff."
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    color = MutedText,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(imageVector = icon, contentDescription = title, tint = tint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = NavyPrimary,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp
            )
        }
    }
}

@Composable
fun AnnouncementItem(
    tag: String,
    tagColor: Color,
    title: String,
    date: String,
    description: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = tagColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = tag.uppercase(),
                        color = tagColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = date,
                    color = MutedText,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = DarkText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = MutedText,
                lineHeight = 16.sp
            )
        }
    }
}


// ==========================================
// 2. REGISTRATION PORTAL TAB
// ==========================================
@Composable
fun RegistrationTab(
    profile: StudentProfile?,
    onSave: (StudentProfile) -> Unit
) {
    val currentProfile = profile ?: StudentProfile()

    var name by remember(profile) { mutableStateOf(currentProfile.fullName) }
    var studentId by remember(profile) { mutableStateOf(currentProfile.studentId) }
    var email by remember(profile) { mutableStateOf(currentProfile.email) }
    var phone by remember(profile) { mutableStateOf(currentProfile.phone) }
    var licenseType by remember(profile) { mutableStateOf(currentProfile.licenseType) }
    var medicalStatus by remember(profile) { mutableStateOf(currentProfile.medicalStatus) }
    var totalHoursText by remember(profile) { mutableStateOf(currentProfile.totalHours.toString()) }

    var expandedLicense by remember { mutableStateOf(false) }
    var expandedMedical by remember { mutableStateOf(false) }

    val licenseOptions = listOf(
        "Private Pilot License (PPL)",
        "Commercial Pilot License (CPL)",
        "Airline Transport Pilot License (ATPL)",
        "Flight Instructor Rating"
    )

    val medicalOptions = listOf(
        "Class 1 Active (Commercial/ATP)",
        "Class 2 Active (Private Pilot)",
        "Expired Medical",
        "None (Ground Theory Only)"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Student Pilot Registration Portal",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = NavyPrimary
            )
            Text(
                text = "Keep your academic and pilot profile credentials up to date. This regulates your flight scheduling permissions.",
                fontSize = 13.sp,
                color = MutedText
            )
        }

        // Pilot Badge visualization
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(20.dp)) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = "KENYA AVIATION CENTER",
                                    color = GoldAccent,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "FLIGHT CADET BADGE",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 10.sp
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.AirplanemodeActive,
                                contentDescription = "Wings",
                                tint = GoldAccent,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Avatar box placeholder
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(Color.White.copy(alpha = 0.15f), CircleShape)
                                    .border(1.5.dp, GoldAccent, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Pilot Avatar",
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = name.ifEmpty { "Aviation Cadet" },
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "ID: ${studentId.ifEmpty { "PENDING REGISTRATION" }}",
                                    color = GoldAccent,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = licenseType,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Divider(color = Color.White.copy(alpha = 0.2f))
                        
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "MEDICAL CERTIFICATE",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = medicalStatus,
                                    color = if (medicalStatus.contains("Expired") || medicalStatus.contains("None")) Color(0xFFFCA5A5) else Color(0xFF6EE7B7),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "LOGGED HOURS",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$totalHoursText hrs",
                                    color = GoldAccent,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        // Form Fields
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Edit Cadet Profile",
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary,
                        style = MaterialTheme.typography.titleMedium
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth().testTag("reg_name_input"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = studentId,
                        onValueChange = { studentId = it },
                        label = { Text("KAC Student ID") },
                        modifier = Modifier.fillMaxWidth().testTag("reg_id_input"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Academy Email") },
                        modifier = Modifier.fillMaxWidth().testTag("reg_email_input"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth().testTag("reg_phone_input"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    // License Dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = licenseType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("License Objective") },
                            modifier = Modifier.fillMaxWidth().clickable { expandedLicense = true },
                            leadingIcon = { Icon(Icons.Default.Flight, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { expandedLicense = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            }
                        )
                        DropdownMenu(
                            expanded = expandedLicense,
                            onDismissRequest = { expandedLicense = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            licenseOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt) },
                                    onClick = {
                                        licenseType = opt
                                        expandedLicense = false
                                    }
                                )
                            }
                        }
                    }

                    // Medical Dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = medicalStatus,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Medical Certificate Status") },
                            modifier = Modifier.fillMaxWidth().clickable { expandedMedical = true },
                            leadingIcon = { Icon(Icons.Default.LocalHospital, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { expandedMedical = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            }
                        )
                        DropdownMenu(
                            expanded = expandedMedical,
                            onDismissRequest = { expandedMedical = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            medicalOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt) },
                                    onClick = {
                                        medicalStatus = opt
                                        expandedMedical = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = totalHoursText,
                        onValueChange = { totalHoursText = it },
                        label = { Text("Total Certified Flight Hours") },
                        modifier = Modifier.fillMaxWidth().testTag("reg_hours_input"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Timelapse, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            val hoursVal = totalHoursText.toFloatOrNull() ?: currentProfile.totalHours
                            val updated = StudentProfile(
                                id = 1,
                                fullName = name,
                                studentId = studentId,
                                licenseType = licenseType,
                                medicalStatus = medicalStatus,
                                totalHours = hoursVal,
                                email = email,
                                phone = phone,
                                isRegistered = name.isNotBlank() && studentId.isNotBlank()
                            )
                            onSave(updated)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("save_profile_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Cadet Credentials", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}


// ==========================================
// 3. FLIGHT SCHEDULING PORTAL TAB
// ==========================================
@Composable
fun SchedulingTab(
    profile: StudentProfile?,
    bookings: List<FlightBooking>,
    onBookFlight: (FlightBooking) -> Unit,
    onCancelBooking: (Int) -> Unit
) {
    val context = LocalContext.current
    var date by remember { mutableStateOf("2026-07-16") }
    var time by remember { mutableStateOf("09:00 AM") }
    var aircraft by remember { mutableStateOf("Cessna 172 Skyhawk (5Y-KAC)") }
    var instructor by remember { mutableStateOf("Capt. James Mwangi") }
    var durationText by remember { mutableStateOf("2.0") }
    var purpose by remember { mutableStateOf("General Handling & Circuits") }

    var expandedAircraft by remember { mutableStateOf(false) }
    var expandedInstructor by remember { mutableStateOf(false) }

    val aircraftOptions = listOf(
        "Cessna 172 Skyhawk (5Y-KAC)",
        "Piper PA-28 Archer (5Y-KAS)",
        "Cessna 152 Trainer (5Y-KAB)",
        "Beechcraft Baron G58 (5Y-KAN)"
    )

    val instructorOptions = listOf(
        "Capt. James Mwangi (Senior Flight Instructor)",
        "Capt. Alice Wambui (Chief Pilot Instructor)",
        "Senior Capt. Peter Omondi",
        "Capt. Sylvia Njeri"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Flight Training Scheduler",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = NavyPrimary
            )
            Text(
                text = "Book Cessna/Piper aircraft and schedule dual or solo flights. All scheduling requires a valid Cadet Medical Certificate.",
                fontSize = 13.sp,
                color = MutedText
            )
        }

        // Real-time Booking form
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Schedule a Flight Session",
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it },
                            label = { Text("Flight Date") },
                            placeholder = { Text("YYYY-MM-DD") },
                            modifier = Modifier.weight(1f).testTag("flight_date_input"),
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) }
                        )

                        OutlinedTextField(
                            value = time,
                            onValueChange = { time = it },
                            label = { Text("Departure Time") },
                            placeholder = { Text("e.g. 09:00 AM") },
                            modifier = Modifier.weight(1f).testTag("flight_time_input"),
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null) }
                        )
                    }

                    // Aircraft Dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = aircraft,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Trainer Aircraft") },
                            modifier = Modifier.fillMaxWidth().clickable { expandedAircraft = true },
                            leadingIcon = { Icon(Icons.Default.Flight, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { expandedAircraft = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            }
                        )
                        DropdownMenu(
                            expanded = expandedAircraft,
                            onDismissRequest = { expandedAircraft = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            aircraftOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt) },
                                    onClick = {
                                        aircraft = opt
                                        expandedAircraft = false
                                    }
                                )
                            }
                        }
                    }

                    // Instructor Dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = instructor,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Assign Flight Instructor") },
                            modifier = Modifier.fillMaxWidth().clickable { expandedInstructor = true },
                            leadingIcon = { Icon(Icons.Default.Face, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { expandedInstructor = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            }
                        )
                        DropdownMenu(
                            expanded = expandedInstructor,
                            onDismissRequest = { expandedInstructor = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            instructorOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt) },
                                    onClick = {
                                        instructor = opt.substringBefore(" (")
                                        expandedInstructor = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = durationText,
                        onValueChange = { durationText = it },
                        label = { Text("Planned Flight Duration (Hours)") },
                        modifier = Modifier.fillMaxWidth().testTag("flight_duration_input"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Timer, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )

                    OutlinedTextField(
                        value = purpose,
                        onValueChange = { purpose = it },
                        label = { Text("Flight Training Objective/Purpose") },
                        modifier = Modifier.fillMaxWidth().testTag("flight_purpose_input"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Task, contentDescription = null) }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            val isReg = profile?.isRegistered ?: false
                            val med = profile?.medicalStatus ?: ""
                            
                            if (!isReg) {
                                Toast.makeText(context, "ERROR: Please register Cadet credentials in the Registration Portal first!", Toast.LENGTH_LONG).show()
                            } else if (med.contains("Expired") || med.contains("None")) {
                                Toast.makeText(context, "ERROR: Cannot schedule. Cadet requires an ACTIVE Class 1 or 2 Medical Certificate!", Toast.LENGTH_LONG).show()
                            } else {
                                val dur = durationText.toFloatOrNull() ?: 1.5f
                                val newBooking = FlightBooking(
                                    date = date,
                                    time = time,
                                    aircraft = aircraft,
                                    instructor = instructor,
                                    durationHours = dur,
                                    purpose = purpose,
                                    status = "Pending Approval"
                                )
                                onBookFlight(newBooking)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_flight_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.FlightTakeoff, contentDescription = "Schedule")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Request Flight Schedule", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }

        // List of Active and Historical flight records
        item {
            Text(
                text = "Cadet Flight Bookings & Training Log",
                fontWeight = FontWeight.Bold,
                color = NavyPrimary,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (bookings.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No flight bookings logged.", color = MutedText, fontSize = 13.sp)
                }
            }
        } else {
            items(bookings) { b ->
                BookingLogCard(booking = b, onCancel = { onCancelBooking(b.id) })
            }
        }
    }
}

@Composable
fun BookingLogCard(
    booking: FlightBooking,
    onCancel: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = booking.aircraft,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = DarkText
                    )
                    Text(
                        text = "${booking.date} at ${booking.time}",
                        fontSize = 12.sp,
                        color = MutedText
                    )
                }
                
                val (statusColor, statusBg) = when (booking.status) {
                    "Completed" -> Color(0xFF059669) to Color(0xFFECFDF5)
                    "Scheduled" -> Color(0xFF2563EB) to Color(0xFFEFF6FF)
                    else -> Color(0xFFD97706) to Color(0xFFFFF7ED) // Pending
                }
                
                Surface(
                    color = statusBg,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = booking.status,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = LightBg)
            Spacer(modifier = Modifier.height(10.dp))
            
            Text(
                text = "Objective: ${booking.purpose}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = DarkText
            )
            Text(
                text = "Instructor: ${booking.instructor} | Duration: ${booking.durationHours} hrs",
                fontSize = 12.sp,
                color = MutedText
            )

            if (booking.status != "Completed") {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onCancel,
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Red),
                        modifier = Modifier.testTag("cancel_booking_${booking.id}")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Cancel", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cancel Request", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


// ==========================================
// 4. COURSE CURRICULUM PORTAL TAB
// ==========================================
@Composable
fun CurriculumTab(
    modules: List<CurriculumModule>,
    onCompleteLesson: (CurriculumModule) -> Unit,
    onTakeExam: (CurriculumModule) -> Unit,
    onResetAll: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Ground School Curriculum",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                    Text(
                        text = "Theoretical syllabus portal. Compete ground training modules to authorize solo flights.",
                        fontSize = 13.sp,
                        color = MutedText
                    )
                }
                
                IconButton(onClick = onResetAll) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset Progress", tint = NavyPrimary)
                }
            }
        }

        // Hero image banner for learning center
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_aviation_classroom_1783983378961),
                        contentDescription = "Classroom Flight Simulator",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                    )
                    Text(
                        text = "KAC Theoretical Air Ground Center",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(12.dp)
                    )
                }
            }
        }

        // Overall progress overview
        if (modules.isNotEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                    elevation = CardDefaults.cardElevation(3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val completedCount = modules.count { it.isCompleted }
                        val totalCount = modules.size
                        val totalCompletedLessons = modules.sumOf { it.completedLessons }
                        val totalSyllabusLessons = modules.sumOf { it.totalLessons }
                        val overallPercent = ((totalCompletedLessons.toFloat() / totalSyllabusLessons.toFloat()) * 100).toInt()

                        Text(
                            text = "Cadet Theoretical Milestones",
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Course Modules Completed: $completedCount of $totalCount",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        LinearProgressIndicator(
                            progress = overallPercent / 100f,
                            color = GoldAccent,
                            trackColor = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Syllabus Progress",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            )
                            Text(
                                text = "$overallPercent% Done ($totalCompletedLessons / $totalSyllabusLessons Lessons)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Course List
        if (modules.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = NavyPrimary)
                }
            }
        } else {
            items(modules) { module ->
                ModuleCurriculumCard(
                    module = module,
                    onStudyLesson = { onCompleteLesson(module) },
                    onTakeExam = { onTakeExam(module) }
                )
            }
        }
    }
}

@Composable
fun ModuleCurriculumCard(
    module: CurriculumModule,
    onStudyLesson: () -> Unit,
    onTakeExam: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Course Code Badge
                Box(
                    modifier = Modifier
                        .background(NavyPrimary, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = module.id,
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = module.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = DarkText
                    )
                    Text(
                        text = "${module.completedLessons} of ${module.totalLessons} Lessons",
                        fontSize = 12.sp,
                        color = MutedText
                    )
                }
                
                // Completed indicator or exam mark
                if (module.isCompleted) {
                    Surface(
                        color = Color(0xFFD1FAE5),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (module.examScore >= 0) "Exam: ${module.examScore}%" else "Done",
                            color = Color(0xFF065F46),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    Text(
                        text = "${module.progressPercent}%",
                        color = NavyPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = module.progressPercent / 100f,
                color = NavyPrimary,
                trackColor = LightBg,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )

            // Expanded view with actions
            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = module.description,
                    fontSize = 12.sp,
                    color = MutedText,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Divider(color = LightBg)

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Study lesson button
                    OutlinedButton(
                        onClick = onStudyLesson,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("study_lesson_btn_${module.id}"),
                        enabled = !module.isCompleted,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyPrimary)
                    ) {
                        Icon(Icons.Default.MenuBook, contentDescription = "Study", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Study Lesson", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // Attempt Exam button
                    Button(
                        onClick = onTakeExam,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("take_exam_btn_${module.id}"),
                        colors = ButtonDefaults.buttonColors(containerColor = if (module.isCompleted) Color(0xFF10B981) else NavyPrimary)
                    ) {
                        Icon(Icons.Default.Assignment, contentDescription = "Exam", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (module.examScore >= 0) "Re-take Exam" else "Attempt Exam",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}


// ==========================================
// 5. EDUCATION AVIATION QUIZ SYSTEM DIALOG
// ==========================================
@Composable
fun AviationQuizDialog(
    module: CurriculumModule,
    onDismiss: () -> Unit,
    onFinishExam: (Int) -> Unit
) {
    // Generate questions based on module ID
    val questions = remember(module.id) {
        when (module.id) {
            "MET-101" -> listOf(
                QuizQuestion(
                    text = "What type of cloud is highly indicative of violent convective updrafts, turbulence, and severe lightning?",
                    options = listOf("Cumulonimbus (CB)", "Cirrostratus (CS)", "Altocumulus (AC)", "Nimbostratus (NS)"),
                    correctAnswerIndex = 0
                ),
                QuizQuestion(
                    text = "Which of the following describes a 'Microburst' wind shear event?",
                    options = listOf(
                        "An intense, localized downdraft of air that spreads outward upon hitting the ground",
                        "A steady gentle warm breeze rising up valleys during sunrise",
                        "A horizontal rotation of high-velocity atmospheric jetstream winds",
                        "High density cloud formations that block flight cockpit visibility"
                    ),
                    correctAnswerIndex = 0
                ),
                QuizQuestion(
                    text = "In aviation weather reports, what does the abbreviation 'CAVOK' represent?",
                    options = listOf(
                        "Clear Air and Visibility OK (No cloud below 5000ft, no active weather)",
                        "Cabin Altitude Velocity Oxygen Key checklist",
                        "Caution: Severe Convective Volcanic ash alert",
                        "Co-Pilot Altitude Vectoring Operation Key"
                    ),
                    correctAnswerIndex = 0
                )
            )
            "AIR-101" -> listOf(
                QuizQuestion(
                    text = "What is the standard radio transponder squawk code designated for a complete Radio Communications Failure?",
                    options = listOf("7500", "7600", "7700", "2000"),
                    correctAnswerIndex = 1
                ),
                QuizQuestion(
                    text = "In visual flight rules (VFR) operations, which aircraft has the absolute right-of-way over all other air traffic?",
                    options = listOf(
                        "An aircraft in distress or during an emergency landing",
                        "A commercial passenger Boeing airliner on approach",
                        "A military jet trainer flying in formation",
                        "The faster aircraft catching up from behind"
                    ),
                    correctAnswerIndex = 0
                ),
                QuizQuestion(
                    text = "What is the age requirement and minimum flight hour log to obtain a Commercial Pilot License (CPL) in Kenya?",
                    options = listOf("16 years and 40 hours", "18 years and 200 hours", "21 years and 1500 hours", "17 years and 100 hours"),
                    correctAnswerIndex = 1
                )
            )
            "NAV-101" -> listOf(
                QuizQuestion(
                    text = "What is 'Dead Reckoning' flight navigation?",
                    options = listOf(
                        "Navigating solely by calculating time, speed, distance, and wind vector drift from a known position",
                        "Following landmarks like roads, rivers, and mountains visibly from the cockpit window",
                        "Using satellite-based GPS coordinates exclusively to steer autopilot flight routes",
                        "Relying on radar vector directives issued directly by air traffic controllers"
                    ),
                    correctAnswerIndex = 0
                ),
                QuizQuestion(
                    text = "A VOR navigation transmitter frequency operates in which radio frequency band?",
                    options = listOf("Very High Frequency (VHF)", "Medium Frequency (MF)", "High Frequency (HF)", "Ultra High Frequency (UHF)"),
                    correctAnswerIndex = 0
                ),
                QuizQuestion(
                    text = "On aviation sectional charts, what do contour lines and shaded relief represent?",
                    options = listOf("Airspace control ceilings", "Terrain elevations and obstacles", "Average magnetic wind variation", "Major high-voltage power grids"),
                    correctAnswerIndex = 1
                )
            )
            else -> listOf(
                QuizQuestion(
                    text = "In aerodynamics, what force opposes thrust to slow down an aircraft?",
                    options = listOf("Drag", "Lift", "Weight / Gravity", "Centrifugal"),
                    correctAnswerIndex = 0
                ),
                QuizQuestion(
                    text = "What cockpit instrument monitors the pressure difference to indicate current flying speed?",
                    options = listOf("Airspeed Indicator", "Altimeter", "Vertical Speed Indicator", "Attitude Indicator"),
                    correctAnswerIndex = 0
                ),
                QuizQuestion(
                    text = "Which control surface controls the lateral bank and rolling rotation of the aircraft?",
                    options = listOf("Ailerons", "Rudder", "Elevators", "Flaps"),
                    correctAnswerIndex = 0
                )
            )
        }
    }

    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswerIndex by remember { mutableStateOf<Int?>(null) }
    var correctAnswersCount by remember { mutableStateOf(0) }
    var quizCompleted by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            color = CardBg
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Quiz,
                        contentDescription = "Quiz",
                        tint = NavyPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${module.id} - Theory Evaluation",
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MutedText)
                    }
                }

                Divider(color = LightBg, modifier = Modifier.padding(vertical = 12.dp))

                if (!quizCompleted) {
                    val question = questions[currentQuestionIndex]
                    
                    // Question progress indicator
                    Text(
                        text = "Question ${currentQuestionIndex + 1} of ${questions.size}",
                        color = BlueAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = question.text,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = DarkText,
                        modifier = Modifier.fillMaxWidth().align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Option Buttons
                    question.options.forEachIndexed { index, optionText ->
                        val isSelected = selectedAnswerIndex == index
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) NavyPrimary.copy(alpha = 0.08f) else Color.White
                            ),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) NavyPrimary else Color(0xFFE2E8F0)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable { selectedAnswerIndex = index }
                                .testTag("quiz_option_${index}")
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedAnswerIndex = index },
                                    colors = RadioButtonDefaults.colors(selectedColor = NavyPrimary)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = optionText,
                                    fontSize = 13.sp,
                                    color = DarkText,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action buttons
                    Button(
                        onClick = {
                            if (selectedAnswerIndex != null) {
                                if (selectedAnswerIndex == question.correctAnswerIndex) {
                                    correctAnswersCount++
                                }
                                
                                if (currentQuestionIndex + 1 < questions.size) {
                                    currentQuestionIndex++
                                    selectedAnswerIndex = null
                                } else {
                                    quizCompleted = true
                                }
                            }
                        },
                        enabled = selectedAnswerIndex != null,
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("quiz_next_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (currentQuestionIndex + 1 < questions.size) "Next Question" else "Submit Evaluation",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    // Quiz results
                    val scorePercent = ((correctAnswersCount.toFloat() / questions.size.toFloat()) * 100).toInt()
                    val passed = scorePercent >= 70

                    Icon(
                        imageVector = if (passed) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = "Quiz Result",
                        tint = if (passed) Color(0xFF10B981) else Color(0xFFEF4444),
                        modifier = Modifier.size(72.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (passed) "Theory Exam Cleared!" else "Exam Mark Deficit",
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Score Achieved: $scorePercent% ($correctAnswersCount of ${questions.size} Correct)",
                        fontWeight = FontWeight.ExtraBold,
                        color = if (passed) Color(0xFF10B981) else Color(0xFFEF4444),
                        fontSize = 22.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (passed) {
                            "Outstanding Cadet! This theory qualification is registered in your logbook and advances your flight training clearance."
                        } else {
                            "Minimum pass mark is 70%. Review course study guidelines and re-attempt theoretical flight training clearance."
                        },
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        color = MutedText,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { onFinishExam(scorePercent) },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("quiz_finish_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Register Theory Record", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

data class QuizQuestion(
    val text: String,
    val options: List<String>,
    val correctAnswerIndex: Int
)

@Composable
fun CatalogTab(
    profile: StudentProfile?,
    onInquire: (courseName: String, message: String) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var expandedCourseId by remember { mutableStateOf<String?>(null) }
    
    // Inquiry Dialog State
    var inquiringCourse by remember { mutableStateOf<CatalogCourse?>(null) }
    var inquiryEmail by remember { mutableStateOf(profile?.email ?: "") }
    var inquiryPhone by remember { mutableStateOf(profile?.phone ?: "") }
    var inquiryMessage by remember { mutableStateOf("") }

    LaunchedEffect(profile) {
        if (profile != null) {
            inquiryEmail = profile.email
            inquiryPhone = profile.phone
        }
    }

    val courses = remember {
        listOf(
            CatalogCourse(
                id = "PPL",
                name = "Private Pilot License (PPL)",
                category = "Flight Training",
                duration = "4 - 6 Months",
                intake = "Jan, May, Sep",
                cost = "KES 850,000",
                icon = Icons.Default.Flight,
                shortDesc = "The core foundation of aviation. Safely pilot single-engine aircraft under Visual Flight Rules (VFR).",
                description = "The Private Pilot License (PPL) program is the essential starting point for all aviators. It equips students with the fundamental aerodynamics, ground instruction, and practical flight skills required to operate a single-engine aircraft safely and competently as Pilot-In-Command under Visual Flight Rules (VFR). Perfect for private aviation, and a mandatory prerequisite for career pilot pathways.",
                prerequisites = listOf(
                    "Minimum age of 17 years",
                    "Class 2 Aviation Medical Certificate from an approved KCAA medical examiner",
                    "KCSE Mean Grade C- or equivalent with passing grades in English, Mathematics, and Physics"
                ),
                objectives = listOf(
                    "Master safe takeoff, precision cruise flight, and smooth landing procedures.",
                    "Develop emergency maneuvers, forced landings, and critical risk assessment skills.",
                    "Command single-engine flights during cross-country navigation tasks.",
                    "Pass the official KCAA theoretical exams and final flight skills test."
                ),
                modules = listOf(
                    "Module 1: Air Law & Operational Procedures",
                    "Module 2: Human Performance & Aviation Physiology",
                    "Module 3: Meteorology & Navigation Calculations",
                    "Module 4: Aircraft General Knowledge & Principles of Flight",
                    "Module 5: Radiotelephony (RT) Communications & Phrases",
                    "Practical: 45 Minimum flying hours (including 10 hours solo flight training)"
                )
            ),
            CatalogCourse(
                id = "CPL",
                name = "Commercial Pilot License (CPL) with Instrument Rating (IR)",
                category = "Flight Training",
                duration = "12 - 18 Months",
                intake = "Jan, Jul",
                cost = "KES 2,400,000",
                icon = Icons.Default.FlightTakeoff,
                shortDesc = "Advanced commercial aviation career course. Master multi-engine flight under Instrument Flight Rules (IFR).",
                description = "This intensive, professional course prepares pilots to operate as commercial airline co-pilots or captains. The program covers complex multi-engine aircraft systems, advanced navigation technology, and commercial-grade air law. Training is conducted in glass-cockpit airplanes and advanced Flight Simulation Training Devices (FSTD) under all weather conditions.",
                prerequisites = listOf(
                    "Valid Private Pilot License (PPL) with completed logbook",
                    "Class 1 Aviation Medical Certificate from certified KCAA doctor",
                    "High school grades with credit passes in English, Mathematics, and Physics"
                ),
                objectives = listOf(
                    "Fly multi-engine complex aircraft under Instrument Flight Rules (IFR) safely.",
                    "Exhibit professional-grade situational awareness, decision-making, and airmanship.",
                    "Apply commercial CRM (Crew Resource Management) and safety standards.",
                    "Qualify for modern multi-crew airline type-ratings."
                ),
                modules = listOf(
                    "Module 1: Advanced Flight Planning & Flight Monitoring",
                    "Module 2: Instrumentation, Radio Navigation, & GNSS Avionics",
                    "Module 3: Jet/Turbine Systems & Aerodynamic Principles",
                    "Module 4: General Air Navigation & Dead-Reckoning Planning",
                    "Module 5: Meteorology & Advanced Weather Pattern Analysis",
                    "Practical: 150 Additional flying hours (totalling 200+ hours with multi-engine time)"
                )
            ),
            CatalogCourse(
                id = "AERO_CERT",
                name = "Aeronautical Engineering - Level 5 (Certificate)",
                category = "Engineering",
                duration = "1 Year",
                intake = "Jan, May",
                cost = "KES 150,000",
                icon = Icons.Default.Build,
                shortDesc = "Foundational aircraft maintenance, workshop safety, and airframe structural technology.",
                description = "This entry-level engineering certificate course introduces students to aircraft structures, general workshops, hand-tools, safety management, and basic electricity. It is designed to produce skilled aircraft maintenance assistants and lay the academic foundation for higher diplomas.",
                prerequisites = listOf(
                    "KCSE Mean Grade D+ (Plus) or equivalent international qualification",
                    "Pass grades in Mathematics, Physics, and English",
                    "A strong technical aptitude and passion for mechanics"
                ),
                objectives = listOf(
                    "Understand and enforce strict aviation workshop safety protocols.",
                    "Identify and utilize precision engineering hand tools and measuring instruments.",
                    "Understand basic metallic, composite, and chemical structures used in aircraft.",
                    "Perform basic aircraft servicing and component removal under supervision."
                ),
                modules = listOf(
                    "Module 1: Workshop Technology, Safety, and First Aid",
                    "Module 2: Aircraft General Engineering Drawing & Schematics",
                    "Module 3: Basic Electrical Principles & Avionics Wiring",
                    "Module 4: Airframe Materials, Riveting, and Sheet Metal Work",
                    "Module 5: Elementary Aerodynamics & Turbine Engine Basics",
                    "Practical: Comprehensive laboratory sessions and local hangar attachment"
                )
            ),
            CatalogCourse(
                id = "AERO_DIP",
                name = "Aeronautical Engineering - Level 6 (Diploma)",
                category = "Engineering",
                duration = "2 Years",
                intake = "Jan, May, Sep",
                cost = "KES 320,000",
                icon = Icons.Default.Build,
                shortDesc = "Professional license preparation program. Repair, inspect, and certify structural and propulsion systems.",
                description = "The premier Aeronautical Engineering Diploma. This rigorous course covers structural repair, hydraulics, turbine engines, electrical circuits, and avionics. It is specifically structured around the Kenya Civil Aviation Authority (KCAA) syllabus, preparing graduates to successfully challenge the Aircraft Maintenance Engineer (AME) License exams.",
                prerequisites = listOf(
                    "KCSE Mean Grade C- (Minus) or equivalent, OR Level 5 Certificate in Aeronautical Engineering",
                    "Strong grades in Mathematics, Physics, and English",
                    "Passing score in KAC technical assessment interview"
                ),
                objectives = listOf(
                    "Perform advanced inspection, diagnostic, and troubleshooting routines on aircraft systems.",
                    "Repair aircraft metal, structures, engine parts, and pneumatic valves.",
                    "Interpret airworthiness directives, maintenance logs, and official manuals.",
                    "Apply safety management systems and understand human factors in maintenance."
                ),
                modules = listOf(
                    "Module 1: Gas Turbine Propulsion & Piston Engines",
                    "Module 2: Aircraft Hydraulic, Pneumatic, Fuel, and Landing Gear Systems",
                    "Module 3: Avionics Systems, Radar, Communications, and Instruments",
                    "Module 4: Civil Aviation Regulations & Airworthiness Documentation",
                    "Module 5: Quality Assurance, Safety Audits, & Human Factors in Hangar",
                    "Practical: Multi-month internship in line maintenance, engine overhaul, or avionics workshops"
                )
            ),
            CatalogCourse(
                id = "AERO_ADV",
                name = "Aeronautical Engineering - Level 7 (Advanced Diploma)",
                category = "Engineering",
                duration = "1.5 Years",
                intake = "Sep Only",
                cost = "KES 280,000",
                icon = Icons.Default.Build,
                shortDesc = "Advanced engineering management, composite flight structures, and fleet airworthiness planning.",
                description = "Tailored for senior maintenance staff, technical directors, and aviation managers, this course focuses on structural failure analysis, composite materials fabrication, advanced digital autopilot integration, fleet planning, and regulatory compliance leadership.",
                prerequisites = listOf(
                    "Level 6 Diploma in Aeronautical Engineering or equivalent from a recognized institution",
                    "Minimum of 1 year verified work experience on a certified airfield or maintenance hangar",
                    "Recommendation from active chief engineer or maintenance manager"
                ),
                objectives = listOf(
                    "Formulate scheduled maintenance programs, reliability indexes, and weight logs.",
                    "Lead investigations into composite repairs, structural stress, fatigue, and cracks.",
                    "Design quality management audits aligned with KCAA and EASA global aviation rules.",
                    "Manage technical teams, engineering budgets, and environmental safety regulations."
                ),
                modules = listOf(
                    "Module 1: Advanced Aero-Structures, Stress, Fatigue, & Composite Maintenance",
                    "Module 2: Digital EFIS Cockpits, Fly-By-Wire, and Auto-Flight Computers",
                    "Module 3: Reliability Engineering, Fleet Optimization, and AD/SB Compliance",
                    "Module 4: Maintenance Facility Quality Control & Regulatory Audits",
                    "Module 5: Propulsion Performance, Jet Thermodynamics, & Fuel Efficiency",
                    "Practical: Design project representing a major structural modification or engine upgrade proposal"
                )
            ),
            CatalogCourse(
                id = "CARGO",
                name = "Air Cargo Operations & Management",
                category = "Ground Operations",
                duration = "3 Months",
                intake = "Every Month",
                cost = "KES 75,000",
                icon = Icons.Default.LocalShipping,
                shortDesc = "Supply chain logistics, dangerous goods handling (DGR), and aircraft weight & balance.",
                description = "A fast-track certificate program designed to launch careers in air cargo terminals, shipping agencies, and logistics departments. Covers air freight booking, pricing, customs procedures, and essential weight and balance load control guidelines.",
                prerequisites = listOf(
                    "KCSE Mean Grade D (Plain) or equivalent",
                    "Competent level of computer literacy and communication in English",
                    "Attention to detail and mathematical accuracy"
                ),
                objectives = listOf(
                    "Correctly classify, label, package, and price standard and special cargo.",
                    "Apply international IATA Dangerous Goods Regulations (DGR) correctly.",
                    "Prepare correct airway bills, commercial invoices, and customs manifests.",
                    "Draft safe cargo load distribution sheets for commercial cargo freighters."
                ),
                modules = listOf(
                    "Module 1: Introduction to global supply chain and Air Freight industry",
                    "Module 2: Air Cargo booking, routing, rating, and charging rules",
                    "Module 3: IATA Dangerous Goods (DGR) handling and safety regulations",
                    "Module 4: Special Cargo handling (live animals, high-value, perishables, pharmaceuticals)",
                    "Module 5: Aircraft Loading, load sheets, center-of-gravity, and weight constraints",
                    "Practical: Simulation of warehouse terminal operations and load sheet generation"
                )
            ),
            CatalogCourse(
                id = "CABIN",
                name = "Cabin Crew Ab-Initio & Emergency Procedures",
                category = "Cabin Crew",
                duration = "6 Months",
                intake = "Jan, May, Sep",
                cost = "KES 180,000",
                icon = Icons.Default.SupervisedUserCircle,
                shortDesc = "Elite flight attendant preparation. First aid, survival drills, and elite hospitality.",
                description = "Become a highly sought-after cabin crew professional. This course blends rigorous safety and survival training with world-class hospitality and service principles. It covers cabin evacuations, fire fighting, in-flight first aid, crew resources, and commercial service standards.",
                prerequisites = listOf(
                    "KCSE Mean Grade C- or equivalent (Grade C Plain and above is an advantage)",
                    "Excellent physical health, vision, and swimming proficiency",
                    "Minimum height of 5'4\" (Females) or 5'7\" (Males) for cabin overhead reach",
                    "Warm, service-oriented personality with excellent language articulation"
                ),
                objectives = listOf(
                    "Direct emergency cabin operations including slide deployments, fire fighting, and water survival.",
                    "Provide life-saving First Aid, CPR, and automated external defibrillator (AED) operation.",
                    "Deliver exceptional culinary, service, cultural, and passenger care experiences.",
                    "Demonstrate high-class grooming, presentation, and airline etiquette standards."
                ),
                modules = listOf(
                    "Module 1: Civil Aviation Law, Cabin Operations, and Passenger Psychology",
                    "Module 2: Aircraft Type Familiarization, Galley Systems, & Safety Equipment",
                    "Module 3: Emergency Drills, Smoke Hoods, Evacuation Procedures, and Survival Training",
                    "Module 4: Aviation Medicine, Advanced First Aid, Cardiopulmonary Resuscitation (CPR)",
                    "Module 5: Luxury Airline Service, Wine Pairing, Grooming, and Cultural Etiquette",
                    "Practical: Full-scale cabin mock-up escape simulations, fire chamber practice, and swimming pool ditching drills"
                )
            )
        )
    }

    val filteredCourses = remember(searchQuery, selectedCategory) {
        courses.filter { course ->
            val matchesCategory = selectedCategory == "All" || course.category == selectedCategory
            val matchesSearch = searchQuery.isBlank() ||
                course.name.contains(searchQuery, ignoreCase = true) ||
                course.shortDesc.contains(searchQuery, ignoreCase = true) ||
                course.description.contains(searchQuery, ignoreCase = true) ||
                course.id.contains(searchQuery, ignoreCase = true) ||
                course.modules.any { it.contains(searchQuery, ignoreCase = true) } ||
                course.prerequisites.any { it.contains(searchQuery, ignoreCase = true) }

            matchesCategory && matchesSearch
        }
    }

    val categories = listOf("All", "Flight Training", "Engineering", "Ground Operations", "Cabin Crew")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
    ) {
        // Search & Header Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavyPrimary)
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "Catalog Icon",
                        tint = GoldAccent,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "KAC Course Catalog",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "UoN Standard-Class Flight & Technical Academics",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search course name, prerequisites, topics...", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White.copy(alpha = 0.8f)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.White)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("catalog_search_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                        focusedContainerColor = Color.White.copy(alpha = 0.12f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.12f),
                        focusedPlaceholderColor = Color.White.copy(alpha = 0.6f),
                        unfocusedPlaceholderColor = Color.White.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }
        }

        // Category Filter Chips Row
        Surface(
            color = Color.White,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = { Text(category, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NavyPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = LightBg,
                            labelColor = MutedText
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            selectedBorderColor = NavyPrimary,
                            borderColor = Color(0xFFE2E8F0)
                        )
                    )
                }
            }
        }

        // Course List
        if (filteredCourses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "No Results",
                        tint = MutedText.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Courses Match Your Query",
                        fontWeight = FontWeight.Bold,
                        color = DarkText,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Try refining your search keyword or clearing the search text.",
                        color = MutedText,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            searchQuery = ""
                            selectedCategory = "All"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                    ) {
                        Text("Reset Search Filters")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredCourses, key = { it.id }) { course ->
                    val isExpanded = expandedCourseId == course.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(animationSpec = tween(300))
                            .testTag("course_card_${course.id}"),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column {
                            // Header Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expandedCourseId = if (isExpanded) null else course.id
                                    }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = NavyPrimary.copy(alpha = 0.08f),
                                    shape = CircleShape,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = course.icon,
                                            contentDescription = course.name,
                                            tint = NavyPrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Surface(
                                        color = BlueAccent.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = course.category.uppercase(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = BlueAccent,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = course.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = DarkText
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = course.shortDesc,
                                        fontSize = 12.sp,
                                        color = MutedText,
                                        maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                                    tint = MutedText,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }

                            // Quick Stats Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(LightBg)
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Timer, contentDescription = "Duration", tint = MutedText, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = course.duration, fontSize = 11.sp, color = DarkText, fontWeight = FontWeight.Bold)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CalendarToday, contentDescription = "Intake", tint = MutedText, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Intake: ${course.intake}", fontSize = 11.sp, color = DarkText, fontWeight = FontWeight.Bold)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Payment, contentDescription = "Cost", tint = MutedText, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = course.cost, fontSize = 11.sp, color = NavyPrimary, fontWeight = FontWeight.ExtraBold)
                                }
                            }

                            // Expanded Detailed Information
                            if (isExpanded) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(bottom = 12.dp))

                                    // Detailed Description
                                    Text(text = "Detailed Description", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = course.description, fontSize = 12.sp, color = DarkText, lineHeight = 18.sp)

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Prerequisites
                                    Text(text = "Admission Prerequisites", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    course.prerequisites.forEach { prerequisite ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 3.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Prerequisite",
                                                tint = Color(0xFF10B981),
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .padding(top = 2.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(text = prerequisite, fontSize = 12.sp, color = DarkText, lineHeight = 16.sp)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Objectives
                                    Text(text = "Academic Learning Objectives", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    course.objectives.forEach { objective ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 3.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.School,
                                                contentDescription = "Objective",
                                                tint = BlueAccent,
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .padding(top = 2.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(text = objective, fontSize = 12.sp, color = DarkText, lineHeight = 16.sp)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Curriculum Modules / Topics
                                    Text(text = "Curriculum Topics & Syllabus", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    course.modules.forEachIndexed { idx, topic ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(18.dp)
                                                    .background(GoldAccent, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "${idx + 1}",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = NavyPrimary
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(text = topic, fontSize = 12.sp, color = DarkText, lineHeight = 16.sp)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    // Register Interest / Inquire Button
                                    Button(
                                        onClick = {
                                            inquiringCourse = course
                                            inquiryMessage = "Hi KAC Admissions, I am interested in enrolling in ${course.name}. Please provide more information about the upcoming ${course.intake} intake registration timeline."
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(44.dp)
                                            .testTag("inquire_btn_${course.id}")
                                    ) {
                                        Icon(Icons.Default.Mail, contentDescription = "Inquire", modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Register Interest & Inquire", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Inquiry Dialog Form
    inquiringCourse?.let { course ->
        Dialog(onDismissRequest = { inquiringCourse = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Mail, contentDescription = "Email Inquire", tint = NavyPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Course Inquiry Form",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { inquiringCourse = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = MutedText)
                        }
                    }

                    HorizontalDivider(color = LightBg, modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        text = "You are inquiring about:",
                        fontSize = 11.sp,
                        color = MutedText,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Text(
                        text = course.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(bottom = 12.dp)
                    )

                    // Contact Email
                    OutlinedTextField(
                        value = inquiryEmail,
                        onValueChange = { inquiryEmail = it },
                        label = { Text("Your Contact Email") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = MutedText) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    // Contact Phone
                    OutlinedTextField(
                        value = inquiryPhone,
                        onValueChange = { inquiryPhone = it },
                        label = { Text("Your Mobile Number") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone", tint = MutedText) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    // Message Box
                    OutlinedTextField(
                        value = inquiryMessage,
                        onValueChange = { inquiryMessage = it },
                        label = { Text("Your Question / Message") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (inquiryEmail.isBlank() || inquiryPhone.isBlank()) {
                                Toast.makeText(context, "Please complete your contact email and phone number", Toast.LENGTH_SHORT).show()
                            } else {
                                onInquire(course.name, inquiryMessage)
                                inquiringCourse = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("submit_inquiry_button")
                    ) {
                        Text("Submit Interest Inquiry", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

data class CatalogCourse(
    val id: String,
    val name: String,
    val category: String,
    val duration: String,
    val intake: String,
    val cost: String,
    val icon: ImageVector,
    val shortDesc: String,
    val description: String,
    val prerequisites: List<String>,
    val objectives: List<String>,
    val modules: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceTab(
    profile: StudentProfile?,
    transactions: List<PaymentTransaction>,
    onPay: (amount: Double, purpose: String, paymentMethod: String, billingRef: String) -> Unit
) {
    val context = LocalContext.current

    // Payment Setup States
    var selectedPurpose by remember { mutableStateOf("Standard Tuition Instalment") }
    var customAmountStr by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf("M-Pesa Express (STK)") }

    // Forms
    var mpesaPhone by remember { mutableStateOf(profile?.phone ?: "0700000000") }
    var cardName by remember { mutableStateOf(profile?.fullName ?: "") }
    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvv by remember { mutableStateOf("") }
    var bankRefCode by remember { mutableStateOf("") }
    var selectedBankName by remember { mutableStateOf("KCB Bank") }
    var paypalEmail by remember { mutableStateOf(profile?.email ?: "") }

    // Dropdowns
    var purposeDropdownExpanded by remember { mutableStateOf(false) }
    var methodDropdownExpanded by remember { mutableStateOf(false) }
    var bankDropdownExpanded by remember { mutableStateOf(false) }

    // Simulated Gateway Loading Screen
    var isProcessing by remember { mutableStateOf(false) }
    var processingProgress by remember { mutableStateOf(0f) }
    var processingMessage by remember { mutableStateOf("") }

    // Selected Transaction for Receipt View
    var selectedTxForReceipt by remember { mutableStateOf<PaymentTransaction?>(null) }

    // Derive amounts
    val purposesMap = mapOf(
        "Admission Registration Fee" to 5000.0,
        "Standard Tuition Instalment" to 50000.0,
        "Flight Training (5 hrs Cessna 172)" to 75000.0,
        "Flight Training (10 hrs Cessna 172)" to 150000.0,
        "KCAA Licensing & Exam Fee" to 10000.0,
        "Custom Payment Amount" to -1.0
    )

    val currentFixedAmount = purposesMap[selectedPurpose] ?: 50000.0
    val finalAmount = if (currentFixedAmount < 0) {
        customAmountStr.toDoubleOrNull() ?: 0.0
    } else {
        currentFixedAmount
    }

    // Launch simulated gateway timer
    if (isProcessing) {
        LaunchedEffect(isProcessing) {
            processingProgress = 0.1f
            if (selectedMethod.contains("M-Pesa")) {
                processingMessage = "Sending secure STK Push request to Safaricom network..."
                kotlinx.coroutines.delay(1500)
                processingProgress = 0.4f
                processingMessage = "STK Push sent! Please unlock your mobile phone ($mpesaPhone) and enter your M-Pesa PIN."
                kotlinx.coroutines.delay(2500)
                processingProgress = 0.8f
                processingMessage = "PIN verified by Safaricom. Securing bank transaction tokens..."
                kotlinx.coroutines.delay(1500)
            } else if (selectedMethod.contains("Stripe")) {
                processingMessage = "Connecting to Stripe secure endpoint..."
                kotlinx.coroutines.delay(1000)
                processingProgress = 0.5f
                processingMessage = "Verifying credit card checksum (Luhn) & CVV tokenization..."
                kotlinx.coroutines.delay(2000)
                processingProgress = 0.8f
                processingMessage = "Authenticating transaction with 3D-Secure 2.0 gateway..."
                kotlinx.coroutines.delay(1500)
            } else if (selectedMethod.contains("PayPal")) {
                processingMessage = "Redirecting to PayPal secure authorization..."
                kotlinx.coroutines.delay(1200)
                processingProgress = 0.6f
                processingMessage = "Authenticating user credential session token..."
                kotlinx.coroutines.delay(1800)
            } else {
                processingMessage = "Submitting bank ref code to $selectedBankName API clearance..."
                kotlinx.coroutines.delay(2000)
            }
            processingProgress = 1.0f
            isProcessing = false

            // Complete actual payment trigger
            val finalRef = when {
                selectedMethod.contains("M-Pesa") -> "MPESA-TX-" + (100000 + (Math.random() * 900000).toInt())
                selectedMethod.contains("Stripe") -> "STRIPE-CC-" + (100000 + (Math.random() * 900000).toInt())
                selectedMethod.contains("PayPal") -> "PAYPAL-ID-" + (100000 + (Math.random() * 900000).toInt())
                else -> bankRefCode.uppercase()
            }

            onPay(finalAmount, selectedPurpose, selectedMethod, finalRef)

            // Reset inputs
            customAmountStr = ""
            cardNumber = ""
            cardExpiry = ""
            cardCvv = ""
            bankRefCode = ""
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Fee Account Summary Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("finance_summary_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "KAC STUDENT LEDGER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent,
                                letterSpacing = 1.5.sp
                            )
                            Text(
                                text = profile?.fullName ?: "Aviation Cadet",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = "Wallet",
                            tint = GoldAccent,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "TOTAL INVOICED", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
                            Text(
                                text = "KES " + String.format("%,.2f", profile?.feesInvoiced ?: 850000.0),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "TOTAL PAID", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
                            Text(
                                text = "KES " + String.format("%,.2f", profile?.feesPaid ?: 0.0),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }
                    }

                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    val outstandingBalance = (profile?.feesInvoiced ?: 850000.0) - (profile?.feesPaid ?: 0.0)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "OUTSTANDING FEE BALANCE:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "KES " + String.format("%,.2f", outstandingBalance),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (outstandingBalance <= 0) Color(0xFF10B981) else GoldAccent
                        )
                    }
                }
            }
        }

        // 2. Make a Payment Form Panel
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("payment_form_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Icon(Icons.Default.Payment, contentDescription = "Payment Gateways", tint = NavyPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Make a Secure Payment",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    }

                    // Purpose selector
                    Text(text = "Payment Purpose", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { purposeDropdownExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkText)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(selectedPurpose, fontSize = 13.sp)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand")
                            }
                        }
                        DropdownMenu(
                            expanded = purposeDropdownExpanded,
                            onDismissRequest = { purposeDropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            purposesMap.keys.forEach { purpose ->
                                val price = purposesMap[purpose]!!
                                val priceTag = if (price < 0) "(Enter Custom)" else "KES " + String.format("%,.0f", price)
                                DropdownMenuItem(
                                    text = { Text("$purpose — $priceTag") },
                                    onClick = {
                                        selectedPurpose = purpose
                                        purposeDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Custom amount input
                    if (currentFixedAmount < 0) {
                        Text(text = "Enter Custom KES Amount", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = customAmountStr,
                            onValueChange = { customAmountStr = it },
                            placeholder = { Text("Enter Amount in Shillings") },
                            leadingIcon = { Icon(Icons.Default.Payments, contentDescription = "Amount") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Payment Method selector
                    Text(text = "Gateway / Payment Method", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { methodDropdownExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkText)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(selectedMethod, fontSize = 13.sp)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand")
                            }
                        }
                        DropdownMenu(
                            expanded = methodDropdownExpanded,
                            onDismissRequest = { methodDropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            listOf(
                                "M-Pesa Express (STK)",
                                "Stripe (Credit/Debit Card)",
                                "PayPal Secure Checkout",
                                "Kenyan Bank Transfer (Paybill)"
                            ).forEach { method ->
                                DropdownMenuItem(
                                    text = { Text(method) },
                                    onClick = {
                                        selectedMethod = method
                                        methodDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Gateway specific fields
                    when {
                        selectedMethod.contains("M-Pesa") -> {
                            Text(text = "Safaricom Phone Number for STK Push", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = mpesaPhone,
                                onValueChange = { mpesaPhone = it },
                                placeholder = { Text("e.g. 0712345678") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )
                        }
                        selectedMethod.contains("Stripe") -> {
                            Text(text = "Secure Card Details (Stripe Gateway)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = cardName,
                                onValueChange = { cardName = it },
                                label = { Text("Cardholder Name") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Person") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = cardNumber,
                                onValueChange = { if (it.length <= 16) cardNumber = it },
                                label = { Text("Credit Card Number (16 digits)") },
                                leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = "Card") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = cardExpiry,
                                    onValueChange = { if (it.length <= 5) cardExpiry = it },
                                    label = { Text("Expiry (MM/YY)") },
                                    placeholder = { Text("08/28") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = cardCvv,
                                    onValueChange = { if (it.length <= 3) cardCvv = it },
                                    label = { Text("CVV (3 digits)") },
                                    placeholder = { Text("123") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true
                                )
                            }
                        }
                        selectedMethod.contains("PayPal") -> {
                            Text(text = "PayPal Account Email", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = paypalEmail,
                                onValueChange = { paypalEmail = it },
                                placeholder = { Text("your-paypal@email.com") },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )
                        }
                        selectedMethod.contains("Bank") -> {
                            // Bank Details instructions
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = LightBg,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "KAC Flying School Official Bank Accounts",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = NavyPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "KCB Bank Paybill: 522522\nAccount No: 1104859321 (KAC Tuition)\n\nEquity Bank Paybill: 247247\nAccount No: 1284592039201",
                                        fontSize = 11.sp,
                                        color = DarkText,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Bank Name Paid To", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedButton(
                                    onClick = { bankDropdownExpanded = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkText)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(selectedBankName, fontSize = 13.sp)
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand")
                                    }
                                }
                                DropdownMenu(
                                    expanded = bankDropdownExpanded,
                                    onDismissRequest = { bankDropdownExpanded = false },
                                    modifier = Modifier.fillMaxWidth(0.9f)
                                ) {
                                    listOf("KCB Bank", "Equity Bank", "Cooperative Bank", "NCBA Bank").forEach { bank ->
                                        DropdownMenuItem(
                                            text = { Text(bank) },
                                            onClick = {
                                                selectedBankName = bank
                                                bankDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Bank Reference Transaction Code", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = bankRefCode,
                                onValueChange = { bankRefCode = it },
                                placeholder = { Text("e.g. KCB8305928K") },
                                leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = "Bank") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Final Amount summary and pay button
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = LightBg,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Amount to Pay", fontSize = 10.sp, color = MutedText)
                                Text(
                                    text = "KES " + String.format("%,.2f", finalAmount),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NavyPrimary
                                )
                            }

                            Button(
                                onClick = {
                                    // Validations
                                    if (finalAmount <= 0) {
                                        Toast.makeText(context, "Please enter a valid payment amount.", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    if (selectedMethod.contains("M-Pesa") && (mpesaPhone.isBlank() || mpesaPhone.length < 9)) {
                                        Toast.makeText(context, "Please enter a valid Safaricom phone number.", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    if (selectedMethod.contains("Stripe") && (cardNumber.length < 16 || cardExpiry.isBlank() || cardCvv.length < 3)) {
                                        Toast.makeText(context, "Please fill in all credit card fields correctly.", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    if (selectedMethod.contains("PayPal") && !paypalEmail.contains("@")) {
                                        Toast.makeText(context, "Please enter a valid PayPal account email.", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    if (selectedMethod.contains("Bank") && bankRefCode.isBlank()) {
                                        Toast.makeText(context, "Please enter your bank transfer reference code.", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }

                                    isProcessing = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("process_payment_button")
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = "Secure", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pay Securely", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 3. Transaction History Title
        item {
            Text(
                text = "Student Ledger Transactions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = NavyPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // 4. Ledger History List
        if (transactions.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Info, contentDescription = "No History", tint = MutedText.copy(alpha = 0.5f), modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No transaction logs found", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Initiate a payment above to see your receipts and statements.", fontSize = 12.sp, color = MutedText, textAlign = TextAlign.Center)
                    }
                }
            }
        } else {
            items(transactions.reversed()) { tx ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedTxForReceipt = tx }
                        .testTag("transaction_item_${tx.id}"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icon circle
                        Surface(
                            color = if (tx.status == "Success") Color(0xFFE6F4EA) else Color(0xFFFEF7E0),
                            shape = CircleShape,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (tx.status == "Success") Icons.Default.CheckCircle else Icons.Default.Info,
                                    contentDescription = tx.status,
                                    tint = if (tx.status == "Success") Color(0xFF137333) else Color(0xFFB06000),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = tx.purpose,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = DarkText
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${tx.paymentMethod} • Ref: ${tx.referenceCode}",
                                fontSize = 11.sp,
                                color = MutedText
                            )
                            Text(
                                text = tx.date,
                                fontSize = 10.sp,
                                color = MutedText
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "KES " + String.format("%,.2f", tx.amount),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = NavyPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Surface(
                                color = if (tx.status == "Success") Color(0xFF10B981).copy(alpha = 0.12f) else Color(0xFFF59E0B).copy(alpha = 0.12f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = tx.status.uppercase(),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (tx.status == "Success") Color(0xFF10B981) else Color(0xFFF59E0B),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Processing Dialog
    if (isProcessing) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = NavyPrimary,
                        strokeWidth = 4.dp,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Securing Gateway Connect...",
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = processingMessage,
                        fontSize = 12.sp,
                        color = MutedText,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        progress = { processingProgress },
                        color = GoldAccent,
                        trackColor = LightBg,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                    )
                }
            }
        }
    }

    // Official Receipt Dialog
    selectedTxForReceipt?.let { tx ->
        Dialog(onDismissRequest = { selectedTxForReceipt = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(12.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(2.dp, NavyPrimary.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header with Close
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Receipt, contentDescription = "Receipt", tint = NavyPrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "KAC Official Receipt",
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary,
                                fontSize = 16.sp
                            )
                        }
                        IconButton(onClick = { selectedTxForReceipt = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(vertical = 8.dp))

                    // School Branding
                    Text(
                        text = "KENYA AERONAUTICAL COLLEGE",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = NavyPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Wilson Airport, Hangar K22, Nairobi\nISO 9001:2015 Certified Flight Training Provider",
                        fontSize = 9.sp,
                        color = MutedText,
                        textAlign = TextAlign.Center,
                        lineHeight = 12.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Receipt Info Block
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = LightBg,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Receipt No:", fontSize = 10.sp, color = MutedText)
                                Text("REC-" + (1000 + tx.id.toInt() * 17) + "-2026", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DarkText)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Date of Issue:", fontSize = 10.sp, color = MutedText)
                                Text(tx.date, fontSize = 10.sp, color = DarkText)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Secure Gateway:", fontSize = 10.sp, color = MutedText)
                                Text(tx.paymentMethod, fontSize = 10.sp, color = DarkText)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Ref Code:", fontSize = 10.sp, color = MutedText)
                                Text(tx.referenceCode, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Student Details
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Billed To:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText)
                        Column(horizontalAlignment = Alignment.End) {
                            Text(profile?.fullName ?: "Student Cadet", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkText)
                            Text("Reg ID: " + (profile?.studentId ?: "KAC/2026/0491"), fontSize = 10.sp, color = MutedText)
                            Text(profile?.email ?: "", fontSize = 9.sp, color = MutedText)
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 12.dp))

                    // Ledger Item details
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Item Description", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MutedText)
                            Text(tx.purpose, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkText)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Total Paid", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MutedText)
                            Text("KES " + String.format("%,.2f", tx.amount), fontSize = 13.sp, fontWeight = FontWeight.Black, color = NavyPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Verification Stamp Visual
                    Surface(
                        color = Color(0xFFE6F4EA),
                        border = BorderStroke(1.dp, Color(0xFF137333)),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = Color(0xFF137333), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "VERIFIED SECURE FINANCIAL CLEARANCE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF137333)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                Toast.makeText(context, "Exporting receipt PDF statement...", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = "PDF", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("PDF Invoice", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                Toast.makeText(context, "Sending receipt statement to registered email...", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Email, contentDescription = "Email", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Send Email", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
