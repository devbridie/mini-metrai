package com.devbridie.minimetrai.models

data class Position(val x: Double, val y: Double)
enum class StationType {
    CIRCLE,
    TRIANGLE,
    SQUARE,
    STAR,
    PENTAGON,
    DROP,
    FOOTBALL,
}

data class Passenger(val destination: StationType)
data class Station(val position: Position, val stationType: StationType, val gameOverTimer: Double, val interchange: Boolean, val passengers: List<Passenger>)
class Line()


enum class TrainType {
    REGULAR,
    SHINKANSEN
}

data class Inventory(val trains: Int, val specialTrains: Int, val carriages: Int, val crossings: Int)
class Crossing()

data class Graph(val stations: List<Station>, val placedLines: List<PlacedLine>) {
    data class PlacedLine(val passingStations: List<Station>) {
        sealed class Carrier(val capacity: Int, val passengers: List<Passenger>) {
            class Train(val type: TrainType, capacity: Int, passengers: List<Passenger>, val carriages: List<Carriage>) : Carrier(capacity, passengers)
            class Carriage(capacity: Int, passengers: List<Passenger>) : Carrier(capacity, passengers)
        }
    }
}


data class GameState(
        val inventory: Inventory,
        val graph: Graph,
        val passengersTransported: Int
)

data class DateTime(val day: Int, val time: Double)