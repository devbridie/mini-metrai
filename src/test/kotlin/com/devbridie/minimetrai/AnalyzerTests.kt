package com.devbridie.minimetrai

import com.devbridie.minimetrai.cv.Analyzer
import com.devbridie.minimetrai.models.GameState
import com.devbridie.minimetrai.models.Inventory
import com.devbridie.minimetrai.models.Passenger
import com.devbridie.minimetrai.models.StationType
import io.kotlintest.specs.FreeSpec
import java.io.File

class AnalyzerTests : FreeSpec() {
    val analyzer: Analyzer = Analyzer()

    init {
        "should analyze simple screenshot correctly" - {
            val state = screenshot("start")

            "should analyze inventory correctly" {
                state.inventory shouldBe Inventory(
                        carriages = 0,
                        trains = 3,
                        crossings = 4,
                        specialTrains = 0
                )
            }

            "should analyze stations correctly" - {
                "should identify all stations" {
                    state.graph.stations.size shouldBe 3
                    state.graph.stations.map { stations -> stations.stationType }
                            .containsAll(listOf(StationType.CIRCLE, StationType.SQUARE, StationType.TRIANGLE)) shouldBe true
                }
                "should identify passenger waiting at Circle" {
                    val circle = state.graph.stations.find { station -> station.stationType == StationType.CIRCLE }
                    (circle == null) shouldBe false
                    circle!!.passengers shouldBe listOf(Passenger(StationType.SQUARE))
                }
            }
        }
    }

    fun screenshot(screenshotName: String): GameState {
        return analyzer.analyze(File("src/test/resources/screenshots/osaka/$screenshotName.png"))
    }
}