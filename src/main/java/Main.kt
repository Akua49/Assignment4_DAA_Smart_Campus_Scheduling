import com.fasterxml.jackson.module.kotlin.*
import graph.*
import graph.scc.StronglyConnectedComponents
import graph.topo.TopologicalSort
import graph.dagsp.DAGShortestPath
import java.io.File

fun main() {
    println("Smart Campus Scheduling Analysis\n")

    val graph = readGraphFromFile("data/tasks.json")
    println("Loaded graph with ${graph.n} nodes and ${graph.edges.size} edges")
    println("Source node: ${graph.source ?: "Not specified"}")
    println("Weight model: ${graph.weightModel}\n")

    val sccAlgorithm = StronglyConnectedComponents()
    val topoSort = TopologicalSort()
    val dagSP = DAGShortestPath()

    println("1. FINDING STRONGLY CONNECTED COMPONENTS")
    val sccMetrics = Metrics()
    val sccResult = sccAlgorithm.findSCC(graph, sccMetrics)

    println("Found ${sccResult.components.size} SCCs:")
    sccResult.components.forEachIndexed { index, component ->
        println("  Component $index: $component (size: ${component.size})")
    }
    sccMetrics.printMetrics("SCC Analysis")

    println("2. TOPOLOGICAL SORTING")
    val topoMetrics = Metrics()
    val componentOrder = topoSort.sortComponents(sccResult.components, sccResult.condensationGraph, topoMetrics)

    println("Topological order of components:")
    componentOrder.forEachIndexed { index, component ->
        println("  Step $index: $component")
    }
    topoMetrics.printMetrics("Topological Sort")

    println("3. PATH ANALYSIS IN DAG")
    val sourceNode = graph.source ?: 0
    println("Using source node: $sourceNode")

    val spMetrics = Metrics()
    val shortestResult = dagSP.shortestPath(graph, sourceNode, topoMetrics = spMetrics)

    println("\nShortest paths from node $sourceNode:")
    for (i in 0 until graph.n) {
        if (shortestResult.distances[i] != Int.MAX_VALUE) {
            val path = shortestResult.reconstructPath(i)
            println("  To $i: distance=${shortestResult.distances[i]}, path=$path")
        } else {
            println("  To $i: unreachable")
        }
    }

    val lpMetrics = Metrics()
    val longestResult = dagSP.longestPath(graph, sourceNode, topoMetrics = lpMetrics)

    println("\nLongest paths from node $sourceNode:")
    for (i in 0 until graph.n) {
        if (longestResult.distances[i] != Int.MAX_VALUE && longestResult.distances[i] != Int.MIN_VALUE) {
            val path = longestResult.reconstructPath(i)
            println("  To $i: distance=${longestResult.distances[i]}, path=$path")
        }
    }

    val criticalMetrics = Metrics()
    val (criticalPath, criticalLength) = dagSP.findCriticalPath(graph, criticalMetrics)

    println("\nCritical Path (Longest path in entire DAG):")
    println("  Path: $criticalPath")
    println("  Length: $criticalLength")

    println("\nPERFORMANCE SUMMARY")
    sccMetrics.printMetrics("SCC")
    topoMetrics.printMetrics("Topological Sort")
    spMetrics.printMetrics("Shortest Path")
    lpMetrics.printMetrics("Longest Path")
    criticalMetrics.printMetrics("Critical Path")
}

fun readGraphFromFile(filename: String): Graph {
    val mapper = jacksonObjectMapper()
    return mapper.readValue(File(filename), Graph::class.java)
}