# Smart Campus Scheduling - Assignment 4

## Project Description
This project implements graph algorithms for analyzing task dependencies in smart city/campus environments. It handles both cyclic and acyclic dependencies between tasks like street cleaning, repairs, and maintenance.

## Algorithms Implemented
- **Strongly Connected Components** (Kosaraju's algorithm)
- **Topological Sorting** (Kahn's algorithm)
- **Shortest/Longest Paths** in Directed Acyclic Graphs (DAGs)

## How to Build:
./gradlew build
## How to Run:
./gradlew run
## How to Test:
./gradlew test

## Project Structure:
- **src/main/java/graph/** - algorithm implementations
- **src/main/java/Main.kt** - main application
- **data/tasks.json** - input data

## Output
- List of strongly connected components
- Topological order of tasks
- Shortest paths from source node
- Longest paths from source node
- Critical path analysis
- Performance metrics for each algorithm

## Technologies
- Kotlin
- Gradle
- Jackson for JSON parsing
- JUnit for testing

## Weight Model
This implementation uses **edge weights** for path calculations.


## Input Data Format
```json
{
  "directed": true,
  "n": 8
  "edges": [
    {"u": 0, "v": 1, "w": 3},
    {"u": 1, "v": 2, "w": 2}
  ],
  "source": 4,
  "weight_model": "edge"
}
