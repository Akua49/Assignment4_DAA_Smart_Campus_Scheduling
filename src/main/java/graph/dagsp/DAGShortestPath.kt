package graph.dagsp

import graph.*
import graph.topo.TopologicalSort

class DAGShortestPath {

    fun shortestPath(
        graph: Graph,
        source: Int,
        topoOrder: List<Int>? = null,
        metrics: Metrics = Metrics()
    ): ShortestPathResult {
        metrics.startTimer()

        val order = topoOrder ?: TopologicalSort().sort(graph, metrics)
        val dist = IntArray(graph.n) { Int.MAX_VALUE }
        val pred = IntArray(graph.n) { -1 }
        dist[source] = 0

        val adjacencyList = graph.getAdjacencyList()

        for (u in order) {
            metrics.incrementCounter("topo_node_processed")

            if (dist[u] == Int.MAX_VALUE) continue

            adjacencyList[u]?.forEach { edge ->
                metrics.incrementCounter("edge_relaxed")
                val v = edge.v
                val newDist = dist[u] + edge.w

                if (newDist < dist[v]) {
                    dist[v] = newDist
                    pred[v] = u
                    metrics.incrementCounter("distance_updated")
                }
            }
        }

        metrics.stopTimer()
        return ShortestPathResult(dist, pred, source)
    }

    fun longestPath(
        graph: Graph,
        source: Int,
        topoOrder: List<Int>? = null,
        metrics: Metrics = Metrics()
    ): ShortestPathResult {
        metrics.startTimer()

        val invertedEdges = graph.edges.map { Edge(it.u, it.v, -it.w) }
        val invertedGraph = graph.copy(edges = invertedEdges)

        val result = shortestPath(invertedGraph, source, topoOrder, metrics)

        val longDist = IntArray(result.distances.size)
        for (i in result.distances.indices) {
            longDist[i] = when {
                result.distances[i] == Int.MAX_VALUE -> Int.MAX_VALUE
                result.distances[i] == Int.MIN_VALUE -> Int.MIN_VALUE
                else -> -result.distances[i]
            }
        }

        metrics.stopTimer()
        return ShortestPathResult(longDist, result.predecessors, source)
    }

    fun findCriticalPath(graph: Graph, metrics: Metrics = Metrics()): Pair<List<Int>, Int> {
        var maxLength = Int.MIN_VALUE
        var criticalPath = emptyList<Int>()

        val topoOrder = TopologicalSort().sort(graph, metrics)

        for (source in topoOrder) {
            val result = longestPath(graph, source, topoOrder, metrics)
            for (target in topoOrder) {
                if (result.distances[target] != Int.MAX_VALUE && result.distances[target] > maxLength) {
                    maxLength = result.distances[target]
                    criticalPath = result.reconstructPath(target)
                }
            }
        }

        return Pair(criticalPath, maxLength)
    }
}