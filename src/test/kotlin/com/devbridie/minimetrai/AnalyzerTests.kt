package com.devbridie.minimetrai

import com.devbridie.minimetrai.cv.Analyzer
import com.devbridie.minimetrai.models.GameState
import com.devbridie.minimetrai.models.Inventory
import com.devbridie.minimetrai.models.Passenger
import com.devbridie.minimetrai.models.StationType
import io.kotlintest.specs.StringSpec
import java.io.File

class AnalyzerTests : StringSpec() {
    val analyzer: Analyzer = Analyzer()

    init {
        "should analyze simple screenshot correctly" {
            val state = screenshot("start")

            "should analyze inventory correctly" {
                state.inventory shouldBe Inventory(
                        carriages = 0,
                        trains = 3,
                        crossings = 4,
                        specialTrains = 0
                )
            }

            "should analyze stations correctly" {
                state.graph.stations.size shouldBe 3
                state.graph.stations.map { stations -> stations.stationType }
                        .containsAll(listOf(StationType.CIRCLE, StationType.SQUARE, StationType.TRIANGLE)) shouldBe true

                "should identify passenger waiting at Circle" {
                    state.graph.stations.find { station -> station.stationType == StationType.CIRCLE }!!
                            .passengers shouldBe listOf(Passenger(StationType.SQUARE))
                }
            }
        }
    }

    fun screenshot(screenshotName: String): GameState {
        return analyzer.analyze(File("src/test/resources/screenshots/osaka/$screenshotName.png"))
    }
}