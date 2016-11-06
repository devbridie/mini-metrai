package com.devbridie.minimetrai

import com.devbridie.minimetrai.cv.Analyzer
import com.devbridie.minimetrai.models.DateTime
import com.devbridie.minimetrai.models.GameState
import com.devbridie.minimetrai.models.Passenger
import com.devbridie.minimetrai.models.StationType
import io.kotlintest.specs.FreeSpec
import java.io.File
import java.util.*

class AnalyzerTests : FreeSpec() {
    val analyzer: Analyzer = Analyzer()

    init {
        "should analyze simple screenshot correctly" - {
            val state = screenshot("start")

            "should analyze inventory correctly" - {
                "should identify 0 carriages" {
                    state.inventory.carriages shouldBe 0
                }
                "should identify 3 trains" {
                    state.inventory.trains shouldBe 3
                }
                "should identify 4 crossings" {
                    state.inventory.crossings shouldBe 4
                }
                "should identify 0 special trains" {
                    state.inventory.specialTrains shouldBe 0
                }
            }

            "should analyze graph correctly" - {
                "should identify three stations" {
                    state.graph.stations.size shouldBe 3
                }
                "should be able to identify one circle station" {
                    state.graph.stations.count { station -> station.stationType == StationType.CIRCLE } shouldBe 1
                }
                "should be able to identify one square station" {
                    state.graph.stations.count { station -> station.stationType == StationType.SQUARE } shouldBe 1
                }
                "should be able to identify one triangle station" {
                    state.graph.stations.count { station -> station.stationType == StationType.TRIANGLE } shouldBe 1
                }
                "should identify square passenger waiting at circle" {
                    val circle = state.graph.stations.find { station -> station.stationType == StationType.CIRCLE }
                    (circle == null) shouldBe false
                    circle!!.passengers shouldBe listOf(Passenger(StationType.SQUARE))
                }
                "should analyze lines correctly" - {
                    "should find no placed lines" {
                        state.graph.placedLines should haveSize(0)
                    }
                }
            }
            "should not analyze datetime" {
                state.dateTime shouldBe Optional.empty<DateTime>()
            }
            "should not analyze passengers transported" {
                state.passengersTransported shouldBe Optional.empty<DateTime>()
            }
        }
    }

    fun screenshot(screenshotName: String): GameState {
        return analyzer.analyze(File("src/test/resources/screenshots/osaka/$screenshotName.png"))
    }
}