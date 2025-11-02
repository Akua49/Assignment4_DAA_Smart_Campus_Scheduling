package graph

class Metrics {
    private var startTime: Long = 0
    val operationCount: MutableMap<String, Int> = mutableMapOf()
    private var _elapsedTime: Long = 0

    fun startTimer() {
        startTime = System.nanoTime()
    }

    fun stopTimer(): Long {
        _elapsedTime = System.nanoTime() - startTime
        return _elapsedTime
    }

    fun getElapsedTime(): Long = _elapsedTime

    fun incrementCounter(name: String, value: Int = 1) {
        operationCount[name] = operationCount.getOrDefault(name, 0) + value
    }

    fun reset() {
        startTime = 0
        _elapsedTime = 0
        operationCount.clear()
    }

    fun printMetrics(algorithmName: String) {
        println("=== $algorithmName Metrics ===")
        println("Time: ${getElapsedTime() / 1_000_000.0} ms")
        println("Operations:")
        operationCount.forEach { (name, count) ->
            println("  $name: $count")
        }
        println()
    }

    fun getSummary(): Map<String, Any> {
        return mapOf(
            "time_ms" to getElapsedTime() / 1_000_000.0,
            "operations" to operationCount
        )
    }
}