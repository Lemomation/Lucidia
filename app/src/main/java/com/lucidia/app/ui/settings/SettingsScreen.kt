package com.lucidia.app.ui.settings

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lucidia.app.ui.theme.CoralCardBackground
import com.lucidia.app.ui.theme.CoralCardDark
import com.lucidia.app.ui.theme.NightSkyTop
import com.lucidia.app.ui.theme.PureWhite
import com.lucidia.app.ui.theme.TealContainer
import com.lucidia.app.ui.theme.TealDarkEdge

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val prefs by viewModel.preferences.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NightSkyTop)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = CoralCardBackground,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Preferences",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = PureWhite
                )
            }

            // 1. Twilight Sleep Mode (Circadian Red-Shift)
            SettingsGroupCard(
                title = "Twilight Sleep Shield",
                subtitle = "Red-shift screen filter inspired by Twilight to safeguard melatonin during nocturnal WBTB checks"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.BrightnessMedium,
                        contentDescription = null,
                        tint = PureWhite,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Circadian Warm Tint",
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                        Text(
                            text = "Removes 100% blue light",
                            fontSize = 11.sp,
                            color = PureWhite.copy(alpha = 0.75f)
                        )
                    }
                    Switch(
                        checked = prefs.twilightWarmFilterEnabled,
                        onCheckedChange = { viewModel.setTwilightFilter(it, prefs.twilightWarmthIntensity) },
                        colors = customSwitchColors()
                    )
                }

                if (prefs.twilightWarmFilterEnabled) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Warmth Intensity: ${prefs.twilightWarmthIntensity}%",
                        fontSize = 12.sp,
                        color = PureWhite.copy(alpha = 0.85f)
                    )
                    Slider(
                        value = prefs.twilightWarmthIntensity.toFloat(),
                        onValueChange = { viewModel.setTwilightFilter(true, it.toInt()) },
                        valueRange = 20f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = CoralCardBackground,
                            activeTrackColor = CoralCardBackground,
                            inactiveTrackColor = TealDarkEdge
                        )
                    )
                }
            }

            // 2. Lucid Alarm & Auto-Dismissal Configuration
            SettingsGroupCard(
                title = "Lucid Alarms & Auto-Dismiss",
                subtitle = "Dismiss without moving your body to maintain the hypnagogic state for WILD induction"
            ) {
                // Auto dismiss duration
                Text(
                    text = "Auto-Dismiss Duration:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = PureWhite
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(5, 10, 15, 30, 60).forEach { seconds ->
                        val isSelected = prefs.autoDismissSeconds == seconds
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) CoralCardBackground else TealDarkEdge)
                                .clickable { viewModel.setAutoDismissSeconds(seconds) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${seconds}s",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Volume Crescendo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = PureWhite,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Gentle Volume Crescendo",
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                        Text(
                            text = "Soft chime ramps up slowly to prevent heart rate spike",
                            fontSize = 11.sp,
                            color = PureWhite.copy(alpha = 0.75f)
                        )
                    }
                    Switch(
                        checked = prefs.volumeCrescendo,
                        onCheckedChange = { viewModel.setVolumeCrescendo(it) },
                        colors = customSwitchColors()
                    )
                }
            }

            // 3. Reality Check Trainer
            SettingsGroupCard(
                title = "Daytime Reality Checks",
                subtitle = "Subtle hourly haptic taps reminding you to inspect your hands and question reality"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = PureWhite,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Check Frequency",
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                        Text(
                            text = "Every ${prefs.realityCheckIntervalHours} hours during daytime",
                            fontSize = 11.sp,
                            color = PureWhite.copy(alpha = 0.75f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp)) // padding for bottom dock
        }
    }
}

@Composable
private fun SettingsGroupCard(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = TealContainer.copy(alpha = 0.85f)),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, TealDarkEdge)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                color = PureWhite
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = PureWhite.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )
            content()
        }
    }
}

@Composable
private fun customSwitchColors() = SwitchDefaults.colors(
    checkedThumbColor = PureWhite,
    checkedTrackColor = CoralCardDark,
    uncheckedThumbColor = Color(0xFFD1D5DB),
    uncheckedTrackColor = Color(0xFF9CA3AF)
)
