package com.lucidia.app.ui.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lucidia.app.ui.theme.CoralCardBackground
import com.lucidia.app.ui.theme.CoralCardBorder
import com.lucidia.app.ui.theme.CoralCardDark
import com.lucidia.app.ui.theme.CoralCardShadow
import com.lucidia.app.ui.theme.MoonBase
import com.lucidia.app.ui.theme.MoonCrater
import com.lucidia.app.ui.theme.NightSkyBottom
import com.lucidia.app.ui.theme.NightSkyTop
import com.lucidia.app.ui.theme.PureWhite
import com.lucidia.app.ui.theme.StarWhite
import com.lucidia.app.ui.theme.TealContainer
import com.lucidia.app.ui.theme.TealDarkEdge
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val prefs by viewModel.preferences.collectAsState()
    var currentTimeString by remember { mutableStateOf("08:01PM") }
    var currentDateString by remember { mutableStateOf("Tue - 02/11") }

    LaunchedEffect(Unit) {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEE - MM/dd", Locale.getDefault())
        while (true) {
            val now = Date()
            currentTimeString = timeFormat.format(now).replace(" ", "")
            currentDateString = dateFormat.format(now)
            delay(1000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(NightSkyTop, NightSkyBottom)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header: Moon and Clock with Twinkling Stars
            TopSkyHeader(
                timeText = currentTimeString,
                dateText = currentDateString
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Main Teal Rounded Container (matching Page 2.png)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .clip(RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp))
                    .background(TealContainer)
                    .border(
                        width = 4.dp,
                        color = TealDarkEdge,
                        shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 1. WBTB Alarm Card
                    AlarmPillCard(
                        title = "WBTB Alarm",
                        timeFormatted = String.format(
                            Locale.US,
                            "%02d:%02d %s",
                            if (prefs.wbtbAlarmHour % 12 == 0) 12 else prefs.wbtbAlarmHour % 12,
                            prefs.wbtbAlarmMinute,
                            if (prefs.wbtbAlarmHour >= 12) "PM" else "AM"
                        ),
                        enabled = prefs.wbtbEnabled,
                        onToggle = { viewModel.toggleWbtbAlarm(it) },
                        badgeSubtitle = if (prefs.autoDismissSeconds > 0) {
                            "Auto-dismiss: ${prefs.autoDismissSeconds}s"
                        } else "Manual Dismiss"
                    )

                    // 2. Wake-up Alarm Card
                    AlarmPillCard(
                        title = "Wakeupalar..",
                        timeFormatted = String.format(
                            Locale.US,
                            "%02d:%02d %s",
                            if (prefs.wakeupAlarmHour % 12 == 0) 12 else prefs.wakeupAlarmHour % 12,
                            prefs.wakeupAlarmMinute,
                            if (prefs.wakeupAlarmHour >= 12) "PM" else "AM"
                        ),
                        enabled = prefs.wakeupEnabled,
                        onToggle = { viewModel.toggleWakeupAlarm(it) },
                        badgeSubtitle = "Gentle crescendo"
                    )

                    // 3. Streak Card (matching Page 2.png with 7 embossed circle indicators)
                    StreakPillCard(
                        streakDays = prefs.streakDays,
                        weekBits = prefs.streakWeekBits
                    )

                    // 4. Reality Check Card
                    RealityCheckPillCard()

                    Spacer(modifier = Modifier.height(80.dp)) // Padding for bottom dock
                }
            }
        }
    }
}

@Composable
private fun TopSkyHeader(
    timeText: String,
    dateText: String
) {
    val infiniteTransition = rememberInfiniteTransition(label = "star_twinkle")
    val starAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        // Star canvas in background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val starPositions = listOf(
                Offset(30f, 40f),
                Offset(size.width - 40f, 30f),
                Offset(size.width * 0.45f, 20f),
                Offset(size.width - 60f, 130f),
                Offset(40f, 150f)
            )
            for (pos in starPositions) {
                drawStar(pos, 10f, starAlpha)
            }
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Moon with craters
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(MoonBase)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val craterColor = MoonCrater
                    drawCircle(craterColor, radius = 9f, center = Offset(size.width * 0.32f, size.height * 0.28f))
                    drawCircle(craterColor, radius = 6f, center = Offset(size.width * 0.65f, size.height * 0.25f))
                    drawCircle(craterColor, radius = 11f, center = Offset(size.width * 0.48f, size.height * 0.50f))
                    drawCircle(craterColor, radius = 8f, center = Offset(size.width * 0.78f, size.height * 0.48f))
                    drawCircle(craterColor, radius = 7f, center = Offset(size.width * 0.35f, size.height * 0.75f))
                    drawCircle(craterColor, radius = 9f, center = Offset(size.width * 0.70f, size.height * 0.78f))
                    drawCircle(craterColor, radius = 6f, center = Offset(size.width * 0.48f, size.height * 0.88f))
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Time and Date
            Column {
                Text(
                    text = timeText,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    color = PureWhite,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = dateText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite.copy(alpha = 0.85f)
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStar(
    center: Offset,
    size: Float,
    alpha: Float
) {
    val path = Path().apply {
        moveTo(center.x, center.y - size)
        lineTo(center.x + size * 0.3f, center.y - size * 0.3f)
        lineTo(center.x + size, center.y)
        lineTo(center.x + size * 0.3f, center.y + size * 0.3f)
        lineTo(center.x, center.y + size)
        lineTo(center.x - size * 0.3f, center.y + size * 0.3f)
        lineTo(center.x - size, center.y)
        lineTo(center.x - size * 0.3f, center.y - size * 0.3f)
        close()
    }
    drawPath(path, color = StarWhite.copy(alpha = alpha))
}

@Composable
private fun AlarmPillCard(
    title: String,
    timeFormatted: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    badgeSubtitle: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .shadow(6.dp, RoundedCornerShape(36.dp)),
        shape = RoundedCornerShape(36.dp),
        colors = CardDefaults.cardColors(
            containerColor = CoralCardBackground
        ),
        border = androidx.compose.foundation.BorderStroke(2.dp, CoralCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Clock icon inside embossed circle
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Alarm,
                    contentDescription = null,
                    tint = CoralCardDark,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Alarm Title & Badge
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = PureWhite
                )
                Text(
                    text = badgeSubtitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = PureWhite.copy(alpha = 0.85f)
                )
            }

            // Time text
            Text(
                text = timeFormatted,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                color = PureWhite,
                modifier = Modifier.padding(horizontal = 6.dp)
            )

            // Switch toggle
            Switch(
                checked = enabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = PureWhite,
                    checkedTrackColor = CoralCardDark,
                    uncheckedThumbColor = Color(0xFFD1D5DB),
                    uncheckedTrackColor = Color(0xFF9CA3AF)
                )
            )
        }
    }
}

@Composable
private fun StreakPillCard(
    streakDays: Int,
    weekBits: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .shadow(6.dp, RoundedCornerShape(32.dp)),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = CoralCardBackground
        ),
        border = androidx.compose.foundation.BorderStroke(2.dp, CoralCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "STREAK",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = PureWhite
            )

            Spacer(modifier = Modifier.weight(1f))

            // 7 embossed circles for the week (matching Page 2.png)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")
                for (i in 0 until 7) {
                    val isDone = (weekBits and (1 shl i)) != 0
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                if (isDone) CoralCardDark else CoralCardShadow.copy(alpha = 0.45f)
                            )
                            .border(
                                1.5.dp,
                                if (isDone) PureWhite else CoralCardBorder.copy(alpha = 0.4f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isDone) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = PureWhite,
                                modifier = Modifier.size(14.dp)
                            )
                        } else {
                            Text(
                                text = dayLabels[i],
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RealityCheckPillCard() {
    var checked by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { checked = !checked }
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = TealDarkEdge
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Visibility,
                contentDescription = null,
                tint = PureWhite,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Reality Check Trigger",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = PureWhite
                )
                Text(
                    text = if (checked) "Awareness confirmed: You are awake!" else "Tap to question your state: Am I dreaming?",
                    fontSize = 12.sp,
                    color = PureWhite.copy(alpha = 0.8f)
                )
            }
        }
    }
}
