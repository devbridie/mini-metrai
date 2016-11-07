package com.devbridie.minimetrai.cv

import com.devbridie.minimetrai.models.*
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.io.File
import java.util.*

/**
 * Converts a screenshot to a `GameState`.
 */
class Analyzer {
    // load OpenCV binaries
    companion object {
        init {
            nu.pattern.OpenCV.loadShared()
        }
    }

    /**
     * Analyses a screenshot by
     * @param file a file which points to a screenshot of *Mini Metro*
     * @return a `GameState` with elements recognized from the screenshot
     */
    fun analyze(file: File): GameState {
        val mat = file.toMat()

        val inventory = analyzeInventory(mat.copy())
        val graph = analyzeGraph(mat.copy())
        val passengersTransported = analyzePassengersTransported(mat.copy())
        val dateTime = analyzeDateTime(mat.copy())

        return GameState(inventory, graph, passengersTransported, dateTime)
    }

    private fun analyzeDateTime(mat: Mat) = Optional.empty<DateTime>() // TODO

    private fun analyzePassengersTransported(mat: Mat) = Optional.empty<Int>() // TODO

    private fun analyzeGraph(mat: Mat): Graph {
        val stations = analyzeStations(mat)
        val placedLines = emptyList<Graph.PlacedLine>() // TODO
        return Graph(stations, placedLines)
    }

    private fun analyzeInventory(mat: Mat) = Inventory(0, 0, 0, 0) // TODO

    private fun analyzeStations(mat: Mat): List<Station> {

        val img = mat.chain { img, hsv ->
            Imgproc.cvtColor(img, hsv, Imgproc.COLOR_BGR2HSV)
        }.chain { img ->
            // removes certain colors
            img.map { row, col, data ->
                if (data[0] > 100.0 || data[2] > 150.0) {
                    doubleArrayOf(255.0, 0.0, 255.0)
                } else data
            }
        }.chain { hsv, gray ->
            // unfortunately there is no hsv2gray, so we need to convert twice
            Imgproc.cvtColor(hsv, gray, Imgproc.COLOR_HSV2BGR)
        }.chain { img, gray ->
            Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY)
        }.chain { gray, t ->
            Imgproc.threshold(gray, t, 100.0, 255.0, Imgproc.THRESH_BINARY_INV)
        }

        // floodfill station shapes
        val flood = img.copy()
        val mask = Mat.zeros(flood.rows() + 2, flood.cols() + 2, CvType.CV_8U)
        Imgproc.floodFill(flood, mask, Point(0.0, 0.0), Scalar(255.0))
        val not = flood.chain { flood, not -> Core.bitwise_not(flood, not) }
        val filled = Mat()
        Core.bitwise_or(img, not, filled)

        // find contours
        val contours = ArrayList<MatOfPoint>()
        val hierarchy = Mat()
        Imgproc.findContours(filled, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE)

        data class Feature(val stationType: StationType, val box: Rect)
        fun Rect.toPosition(): Position = Position(this.x / mat.cols().toDouble(), this.y / mat.rows().toDouble())
        val (stationFeatures, passengerFeatures) = contours.mapIndexed { i, contour -> // find bounding box and station type
            val approxCurve = MatOfPoint2f()
            val matOfPoint2f = MatOfPoint2f(*contour.toArray())
            Imgproc.approxPolyDP(matOfPoint2f, approxCurve, 0.01 * Imgproc.arcLength(matOfPoint2f, true), true)

            val stationType = when (approxCurve.rows()) { // TODO add the rest of the station types
                3 -> StationType.TRIANGLE
                4 -> StationType.SQUARE
                else -> StationType.CIRCLE
            }
            val box = Imgproc.boundingRect(contours[i])
            Feature(stationType, box)
        }.partition { feature -> //split into stations/passengers
            val ration = feature.box.height / 1080.0
            ration > 0.03
        }

        // group passengers to closest stations
        // TODO will break with close stations and long lists
        val matchedPassengers = passengerFeatures.groupBy { passengerFeature ->
            stationFeatures.minBy { station ->
                station.box.toPosition().dist(passengerFeature.box.toPosition())
            }
        }
        val stations = stationFeatures.map { feature ->
            val passengers: List<Passenger> = matchedPassengers.getOrElse(feature, { emptyList<Feature>() }).map { Passenger(it.stationType) }
            Station(feature.box.toPosition(), feature.stationType, 0.0, false, passengers)
        }

        return stations
    }
}