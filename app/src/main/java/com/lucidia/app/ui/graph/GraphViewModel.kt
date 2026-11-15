package com.lucidia.app.ui.graph

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lucidia.app.LucidiaApplication
import com.lucidia.app.data.DreamEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

data class GraphNode(
    val id: String,
    val label: String,
    val type: NodeType,
    var x: Float = 0f,
    var y: Float = 0f,
    var vx: Float = 0f,
    var vy: Float = 0f,
    val radius: Float = 22f,
    val degree: Int = 1
)

enum class NodeType {
    DREAM,
    DREAM_SIGN,
    CHARACTER,
    LOCATION
}

data class GraphEdge(
    val sourceId: String,
    val targetId: String
)

data class GraphData(
    val nodes: List<GraphNode> = emptyList(),
    val edges: List<GraphEdge> = emptyList()
)

class GraphViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as LucidiaApplication
    private val dreamDao = app.database.dreamDao()

    private val _graphData = MutableStateFlow(GraphData())
    val graphData: StateFlow<GraphData> = _graphData.asStateFlow()

    private val _selectedNode = MutableStateFlow<GraphNode?>(null)
    val selectedNode: StateFlow<GraphNode?> = _selectedNode.asStateFlow()

    init {
        viewModelScope.launch {
            dreamDao.getAllDreams().collect { dreams ->
                buildGraphFromDreams(dreams)
            }
        }
    }

    fun selectNode(node: GraphNode?) {
        _selectedNode.value = node
    }

    private fun buildGraphFromDreams(dreams: List<DreamEntity>) {
        if (dreams.isEmpty()) {
            // Seed a sample dream graph if empty so user can experience Obsidian graph right away
            generateSampleDreamGraph()
            return
        }

        val nodesMap = mutableMapOf<String, GraphNode>()
        val edges = mutableListOf<GraphEdge>()
        val degreeCount = mutableMapOf<String, Int>()

        // 1. Create nodes for each dream
        dreams.forEachIndexed { index, dream ->
            val dreamNodeId = "dream_${dream.id}"
            val angle = (index.toFloat() / dreams.size.coerceAtLeast(1)) * 2 * Math.PI.toFloat()
            val radius = 280f

            nodesMap[dreamNodeId] = GraphNode(
                id = dreamNodeId,
                label = dream.title,
                type = NodeType.DREAM,
                x = 500f + cos(angle) * radius,
                y = 500f + sin(angle) * radius,
                radius = if (dream.lucidityLevel >= 2) 28f else 20f
            )

            // Connect dream to tags (Dream signs)
            dream.tags.forEach { tag ->
                val tagNodeId = "tag_$tag"
                degreeCount[tagNodeId] = (degreeCount[tagNodeId] ?: 0) + 1
                edges.add(GraphEdge(sourceId = dreamNodeId, targetId = tagNodeId))

                if (!nodesMap.containsKey(tagNodeId)) {
                    val tagAngle = Math.random().toFloat() * 2 * Math.PI.toFloat()
                    val tagDist = 120f + Math.random().toFloat() * 220f
                    nodesMap[tagNodeId] = GraphNode(
                        id = tagNodeId,
                        label = tag,
                        type = NodeType.DREAM_SIGN,
                        x = 500f + cos(tagAngle) * tagDist,
                        y = 500f + sin(tagAngle) * tagDist,
                        radius = 24f
                    )
                }
            }
        }

        // Adjust node sizes based on degree (recurring dream signs appear larger)
        val finalNodes = nodesMap.values.map { node ->
            val deg = degreeCount[node.id] ?: 1
            node.copy(
                degree = deg,
                radius = if (node.type == NodeType.DREAM_SIGN) 18f + (deg * 5f).coerceAtMost(36f) else node.radius
            )
        }

        _graphData.value = GraphData(nodes = finalNodes, edges = edges)
    }

    private fun generateSampleDreamGraph() {
        val sampleNodes = listOf(
            GraphNode("d1", "Flying Over Crystal Sea", NodeType.DREAM, 400f, 320f, radius = 26f),
            GraphNode("d2", "Mirror Maze Escape", NodeType.DREAM, 650f, 300f, radius = 28f),
            GraphNode("d3", "Meeting The Dream Guide", NodeType.DREAM, 520f, 580f, radius = 24f),
            GraphNode("d4", "Clock with Melting Numbers", NodeType.DREAM, 320f, 520f, radius = 22f),

            // Dream Signs (Hubs)
            GraphNode("t1", "#flying", NodeType.DREAM_SIGN, 460f, 420f, radius = 32f, degree = 3),
            GraphNode("t2", "#mirror", NodeType.DREAM_SIGN, 600f, 440f, radius = 26f, degree = 2),
            GraphNode("t3", "#guide", NodeType.DREAM_SIGN, 530f, 490f, radius = 24f, degree = 2),
            GraphNode("t4", "#water", NodeType.DREAM_SIGN, 370f, 260f, radius = 20f, degree = 1),
            GraphNode("t5", "#clock", NodeType.DREAM_SIGN, 360f, 460f, radius = 22f, degree = 1)
        )

        val sampleEdges = listOf(
            GraphEdge("d1", "t1"),
            GraphEdge("d1", "t4"),
            GraphEdge("d2", "t1"),
            GraphEdge("d2", "t2"),
            GraphEdge("d3", "t3"),
            GraphEdge("d3", "t1"),
            GraphEdge("d4", "t5"),
            GraphEdge("d4", "t2")
        )

        _graphData.value = GraphData(nodes = sampleNodes, edges = sampleEdges)
    }
}
