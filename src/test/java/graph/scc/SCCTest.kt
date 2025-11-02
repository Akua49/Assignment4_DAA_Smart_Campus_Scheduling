package graph.scc

import graph.Graph
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SCCTest {

    @Test
    fun `test SCC on simple cyclic graph`() {
        val graph = Graph(
            directed = true,
            n = 4,
            edges = listOf(
                Graph.Edge(0, 1, 1),
                Graph.Edge(1, 2, 1),
                Graph.Edge(2, 0, 1),
                Graph.Edge(2, 3, 1)
            )
        )

        val scc = StronglyConnectedComponents()
        val result = scc.findSCC(graph)

        assertEquals(2, result.components.size)
        assertTrue(result.components.any { it.size == 3 && it.containsAll(listOf(0, 1, 2)) })
        assertTrue(result.components.any { it.size == 1 && it.contains(3) })
    }
}