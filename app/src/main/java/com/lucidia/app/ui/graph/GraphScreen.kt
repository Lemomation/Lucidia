package com.lucidia.app.ui.graph

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lucidia.app.ui.theme.CoralCardBackground
import com.lucidia.app.ui.theme.CoralCardDark
import com.lucidia.app.ui.theme.GraphBackground
import com.lucidia.app.ui.theme.GraphEdgeColor
import com.lucidia.app.ui.theme.NodeCharacterColor
import com.lucidia.app.ui.theme.NodeDreamColor
import com.lucidia.app.ui.theme.NodeLocationColor
import com.lucidia.app.ui.theme.NodeSignColor
import com.lucidia.app.ui.theme.PureWhite
import com.lucidia.app.ui.theme.TealContainer
import com.lucidia.app.ui.theme.TealDarkEdge

@Composable
fun GraphScreen(viewModel: GraphViewModel) {
    val graphData by viewModel.graphData.collectAsState()
    val selectedNode by viewModel.selectedNode.collectAsState()

    var scale by remember { mutableFloatStateOf(1.0f) }
    var panOffset by remember { mutableStateOf(Offset(0f, 0f)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GraphBackground)
    ) {
        // Main Interactive Canvas for the Obsidian Graph
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.4f, 3.0f)
                        panOffset += pan
                    }
                }
                .pointerInput(graphData) {
                    detectTapGestures { tapOffset ->
                        // Calculate hit detection on nodes
                        val transformedTap = (tapOffset - panOffset) / scale
                        val hitNode = graphData.nodes.find { node ->
                            val dx = node.x - transformedTap.x
                            val dy = node.y - transformedTap.y
                            (dx * dx + dy * dy) <= (node.radius + 15f) * (node.radius + 15f)
                        }
                        viewModel.selectNode(hitNode)
                    }
                }
        ) {
            // Draw Edges / Links between dreams and dream signs
            val nodeMap = graphData.nodes.associateBy { it.id }
            for (edge in graphData.edges) {
                val source = nodeMap[edge.sourceId]
                val target = nodeMap[edge.targetId]
                if (source != null && target != null) {
                    val p1 = Offset(source.x * scale + panOffset.x, source.y * scale + panOffset.y)
                    val p2 = Offset(target.x * scale + panOffset.x, target.y * scale + panOffset.y)

                    val isConnectedToSelected = selectedNode != null &&
                            (selectedNode?.id == source.id || selectedNode?.id == target.id)

                    val strokeColor = if (isConnectedToSelected) CoralCardBackground else GraphEdgeColor
                    val strokeWidth = if (isConnectedToSelected) 3.5f * scale else 1.5f * scale

                    drawLine(
                        color = strokeColor,
                        start = p1,
                        end = p2,
                        strokeWidth = strokeWidth
                    )
                }
            }

            // Draw Nodes
            for (node in graphData.nodes) {
                val center = Offset(node.x * scale + panOffset.x, node.y * scale + panOffset.y)
                val nodeColor = when (node.type) {
                    NodeType.DREAM -> NodeDreamColor
                    NodeType.DREAM_SIGN -> NodeSignColor
                    NodeType.CHARACTER -> NodeCharacterColor
                    NodeType.LOCATION -> NodeLocationColor
                }

                val isSelected = selectedNode?.id == node.id

                // Glow ring if selected
                if (isSelected) {
                    drawCircle(
                        color = PureWhite,
                        radius = (node.radius + 6f) * scale,
                        center = center,
                        style = Stroke(width = 3f * scale)
                    )
                }

                // Inner circle node
                drawCircle(
                    color = nodeColor,
                    radius = node.radius * scale,
                    center = center
                )
            }
        }

        // Top Header
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Hub,
                    contentDescription = null,
                    tint = CoralCardBackground,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Obsidian Dream Graph",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = PureWhite
                )
            }
            Text(
                text = "Pinch to zoom • Drag to explore • Tap to isolate hubs",
                fontSize = 11.sp,
                color = PureWhite.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 38.dp, top = 2.dp)
            )
        }

        // Legend Overlay
        Card(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(18.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = TealDarkEdge.copy(alpha = 0.9f))
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LegendItem(color = NodeDreamColor, label = "Dream")
                LegendItem(color = NodeSignColor, label = "Dream Sign")
            }
        }

        // Selected Node Details Card at bottom
        if (selectedNode != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 20.dp, vertical = 95.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = TealContainer),
                border = androidx.compose.foundation.BorderStroke(2.dp, CoralCardBackground)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(
                                if (selectedNode?.type == NodeType.DREAM) NodeDreamColor else NodeSignColor
                            )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selectedNode?.label.orEmpty(),
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            color = PureWhite
                        )
                        Text(
                            text = if (selectedNode?.type == NodeType.DREAM_SIGN) {
                                "Recurring Dream Sign (Connection Degree: ${selectedNode?.degree})"
                            } else {
                                "Recorded Dream Entry"
                            },
                            fontSize = 12.sp,
                            color = PureWhite.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, fontSize = 11.sp, color = PureWhite, fontWeight = FontWeight.Bold)
    }
}
