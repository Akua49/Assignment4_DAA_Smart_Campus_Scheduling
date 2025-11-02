package graph

import com.fasterxml.jackson.annotation.JsonProperty

data class Graph(
    @JsonProperty("directed") val directed: Boolean,
    @JsonProperty("n") val n: Int,
    @JsonProperty("edges") val edges: List<Edge>,
    @JsonProperty("source") val source: Int? = null,
    @JsonProperty("weight_model") val weightModel: String = "edge"
) {
    fun getAdjacencyList(): Map<Int, List<Edge>> {
        return edges.groupBy { it.u }
    }
}

data class Edge(
    @JsonProperty("u") val u: Int,
    @JsonProperty("v") val v: Int,
    @JsonProperty("w") val w: Int
) {
    override fun toString(): String = "($u -> $v, w=$w)"
}

data class SCCResult(
    val components: List<List<Int>>,
    val condensationGraph: Graph,
    val componentAssignment: IntArray
)

data class ShortestPathResult(
    val distances: IntArray,
    val predecessors: IntArray,
    val source: Int
) {
    fun reconstructPath(target: Int): List<Int> {
        val path = mutableListOf<Int>()
        var current = target

        if (predecessors[target] == -1 && target != source) {
            return emptyList()
        }

        while (current != -1) {
            path.add(current)
            current = predecessors[current]
        }
        return path.reversed()
    }
}