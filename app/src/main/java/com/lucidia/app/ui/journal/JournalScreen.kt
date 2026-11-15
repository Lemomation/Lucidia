package com.lucidia.app.ui.journal

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lucidia.app.data.DreamEntity
import com.lucidia.app.ui.theme.CoralCardBackground
import com.lucidia.app.ui.theme.CoralCardBorder
import com.lucidia.app.ui.theme.CoralCardDark
import com.lucidia.app.ui.theme.NightSkyTop
import com.lucidia.app.ui.theme.PureWhite
import com.lucidia.app.ui.theme.TealContainer
import com.lucidia.app.ui.theme.TealDarkEdge
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun JournalScreen(viewModel: JournalViewModel) {
    val dreams by viewModel.dreams.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NightSkyTop)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dream Journal",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = PureWhite
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${dreams.size} entries",
                    fontSize = 13.sp,
                    color = PureWhite.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search dreams, signs (#flying)...", color = PureWhite.copy(alpha = 0.6f)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PureWhite) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = PureWhite,
                            modifier = Modifier.clickable { viewModel.onSearchQueryChanged("") }
                        )
                    }
                },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CoralCardBackground,
                    unfocusedBorderColor = TealDarkEdge,
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = PureWhite,
                    focusedContainerColor = TealContainer.copy(alpha = 0.3f),
                    unfocusedContainerColor = TealContainer.copy(alpha = 0.2f)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Dream entries list
            if (dreams.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = CoralCardBackground,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No dreams logged yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = PureWhite
                        )
                        Text(
                            text = "Tap + to capture your dreams as soon as you awaken",
                            fontSize = 13.sp,
                            color = PureWhite.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(dreams, key = { it.id }) { dream ->
                        DreamCard(dream = dream)
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp)) // padding for bottom dock
                    }
                }
            }
        }

        // Floating Action Button to Add New Dream
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 90.dp, end = 20.dp),
            containerColor = CoralCardBackground,
            contentColor = PureWhite,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Dream", modifier = Modifier.size(28.dp))
        }

        // Add Dream Dialog
        if (showAddDialog) {
            AddDreamDialog(
                onDismiss = { showAddDialog = false },
                onSave = { title, content, lucidity, clarity, tags ->
                    viewModel.addDream(
                        title = title,
                        content = content,
                        lucidityLevel = lucidity,
                        clarityScore = clarity,
                        tags = tags,
                        characters = emptyList(),
                        locations = emptyList()
                    )
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun DreamCard(dream: DreamEntity) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()) }
    val formattedDate = remember(dream.timestamp) { dateFormat.format(Date(dream.timestamp)) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = TealContainer.copy(alpha = 0.85f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, TealDarkEdge)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dream.title,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = PureWhite,
                    modifier = Modifier.weight(1f)
                )

                // Lucidity Level Badge
                val (badgeText, badgeBg) = when (dream.lucidityLevel) {
                    2, 3 -> "LUCID" to CoralCardDark
                    1 -> "SEMI" to Color(0xFFF59E0B)
                    else -> "NORMAL" to TealDarkEdge
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(badgeBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                }
            }

            Text(
                text = formattedDate,
                fontSize = 11.sp,
                color = PureWhite.copy(alpha = 0.75f),
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = dream.content,
                fontSize = 14.sp,
                color = PureWhite,
                lineHeight = 20.sp
            )

            if (dream.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    dream.tags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(TealDarkEdge)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(text = tag, fontSize = 11.sp, color = PureWhite)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddDreamDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, Int, Int, List<String>) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var tagsText by remember { mutableStateOf("") }
    var lucidityLevel by remember { mutableIntStateOf(0) } // 0 = Normal, 1 = Semi, 2 = Lucid

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NightSkyTop,
        title = {
            Text(
                text = "Record Dream",
                color = PureWhite,
                fontWeight = FontWeight.Black
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Dream Title", color = PureWhite.copy(0.7f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite,
                        focusedBorderColor = CoralCardBackground
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("What happened in the dream?", color = PureWhite.copy(0.7f)) },
                    modifier = Modifier.height(130.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite,
                        focusedBorderColor = CoralCardBackground
                    )
                )

                // Lucidity selector
                Text(
                    text = "Lucidity Level:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Normal" to 0, "Semi-Lucid" to 1, "Lucid" to 2).forEach { (label, lvl) ->
                        val isSelected = lucidityLevel == lvl
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) CoralCardBackground else TealDarkEdge)
                                .clickable { lucidityLevel = lvl }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = tagsText,
                    onValueChange = { tagsText = it },
                    label = { Text("Dream Signs / Tags (e.g. #flying, #mirror)", color = PureWhite.copy(0.7f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite,
                        focusedBorderColor = CoralCardBackground
                    ),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedTags = tagsText.split(",", " ")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                        .map { if (it.startsWith("#")) it else "#$it" }
                    onSave(title, content, lucidityLevel, 3, parsedTags)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CoralCardBackground)
            ) {
                Text("Save to Journal", fontWeight = FontWeight.Bold, color = PureWhite)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = PureWhite.copy(alpha = 0.7f))
            }
        }
    )
}
