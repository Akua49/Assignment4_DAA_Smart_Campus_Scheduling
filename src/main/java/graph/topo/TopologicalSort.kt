package graph.topo

import graph.Graph
import graph.Metrics

class TopologicalSort {

    fun sort(graph: Graph, metrics: Metrics = Metrics()): List<Int> {
        metrics.startTimer()

        val inDegree = IntArray(graph.n)
        val adjacencyList = graph.getAdjacencyList()

        graph.edges.forEach { edge ->
            inDegree[edge.v]++
            metrics.incrementCounter("in_degree_calc")
        }

        val queue = ArrayDeque<Int>()
        inDegree.forEachIndexed { node, degree ->
            if (degree == 0) {
                queue.add(node)
                metrics.incrementCounter("queue_init")
            }
        }

        val topoOrder = mutableListOf<Int>()
        var processedCount = 0

        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            topoOrder.add(node)
            processedCount++
            metrics.incrementCounter("node_popped")

            adjacencyList[node]?.forEach { edge ->
                metrics.incrementCounter("edge_processed")
                inDegree[edge.v]--
                if (inDegree[edge.v] == 0) {
                    queue.add(edge.v)
                    metrics.incrementCounter("node_pushed")
                }
            }
        }

        if (processedCount != graph.n) {
            throw IllegalArgumentException("Graph contains a cycle! Topological sort not possible. Processed: $processedCount/${graph.n}")
        }

        metrics.stopTimer()
        return topoOrder
    }

    fun sortComponents(components: List<List<Int>>, condensationGraph: Graph, metrics: Metrics = Metrics()): List<List<Int>> {
        val componentOrder = sort(condensationGraph, metrics)
        return componentOrder.map { compIndex -> components[compIndex] }
    }
}