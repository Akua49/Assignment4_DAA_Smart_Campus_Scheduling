package graph.scc

import graph.*

class StronglyConnectedComponents {

    fun findSCC(graph: Graph, metrics: Metrics = Metrics()): SCCResult {
        metrics.startTimer()

        val visited = BooleanArray(graph.n)
        val order = mutableListOf<Int>()
        val adjacencyList = graph.getAdjacencyList()

        for (i in 0 until graph.n) {
            if (!visited[i]) {
                dfsFirst(i, adjacencyList, visited, order, metrics)
            }
        }

        val reversedEdges = graph.edges.map { Edge(it.v, it.u, it.w) }
        val reversedGraph = Graph(true, graph.n, reversedEdges)
        val reversedAdjacencyList = reversedGraph.getAdjacencyList()

        val compVisited = BooleanArray(graph.n)
        val compAssignment = IntArray(graph.n) { -1 }
        var compId = 0
        val components = mutableListOf<List<Int>>()

        for (i in order.indices.reversed()) {
            val node = order[i]
            if (!compVisited[node]) {
                val component = mutableListOf<Int>()
                dfsSecond(node, reversedAdjacencyList, compVisited, component, compId, compAssignment, metrics)
                components.add(component)
                compId++
            }
        }

        val condensationEdges = mutableSetOf<Edge>()
        for (edge in graph.edges) {
            val uComp = compAssignment[edge.u]
            val vComp = compAssignment[edge.v]
            if (uComp != vComp) {
                condensationEdges.add(Edge(uComp, vComp, edge.w))
            }
        }

        val condensationGraph = Graph(
            directed = true,
            n = components.size,
            edges = condensationEdges.toList()
        )

        metrics.stopTimer()
        return SCCResult(components, condensationGraph, compAssignment)
    }

    private fun dfsFirst(
        u: Int,
        adjList: Map<Int, List<Edge>>,
        visited: BooleanArray,
        order: MutableList<Int>,
        metrics: Metrics
    ) {
        visited[u] = true
        metrics.incrementCounter("DFS1_visits")

        adjList[u]?.forEach { edge ->
            metrics.incrementCounter("DFS1_edges_checked")
            if (!visited[edge.v]) {
                dfsFirst(edge.v, adjList, visited, order, metrics)
            }
        }

        order.add(u)
    }

    private fun dfsSecond(
        u: Int,
        adjList: Map<Int, List<Edge>>,
        visited: BooleanArray,
        component: MutableList<Int>,
        compId: Int,
        compAssignment: IntArray,
        metrics: Metrics
    ) {
        visited[u] = true
        compAssignment[u] = compId
        component.add(u)
        metrics.incrementCounter("DFS2_visits")

        adjList[u]?.forEach { edge ->
            metrics.incrementCounter("DFS2_edges_checked")
            if (!visited[edge.v]) {
                dfsSecond(edge.v, adjList, visited, component, compId, compAssignment, metrics)
            }
        }
    }
}