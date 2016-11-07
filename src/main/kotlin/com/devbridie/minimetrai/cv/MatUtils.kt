package com.devbridie.minimetrai.cv

import org.opencv.core.Mat
import org.opencv.highgui.Highgui
import java.io.File

/**
 * Allows the chaining of operators which work on `Mat`s.
 *
 * @param f a lambda which encloses `this` and a new `Mat`, should **not** mutate `this`
 * @return the second parameter of `f` for continued chaining
 */
fun Mat.chain(f: (Mat, Mat) -> Unit): Mat {
    return Mat().apply { f.invoke(this@chain, this) }
}

/**
 * Allows the chaining of operators which work on `Mat`s.
 *
 * @param f a mutation function which is applied to `this`
 */
fun Mat.chain(f: (Mat) -> Mat): Mat {
    return f.invoke(this)
}

/**
 * Creates an `Iterator` for conveniently looping over `Mat`s.
 */
operator fun Mat.iterator(): Iterator<DoubleArray> {
    return object : Iterator<DoubleArray> {
        var i = 0
        var j = 0
        override fun hasNext(): Boolean {
            return i < cols() && j < rows()
        }

        override fun next(): DoubleArray {
            val result = this@iterator[j, i]
            i++;
            if (i > cols()) {
                i = 0; j++
            }
            return result
        }

    }
}

/**
 * Creates an `Iterable` for conveniently looping over `Mat`s.
 */
fun Mat.iterable(): Iterable<DoubleArray> {
    return Iterable { this.iterator() }
}

/**
 * Creates a new copy of a `Mat`.
 */
fun Mat.copy(): Mat {
    return this.chain { old, copy -> old.copyTo(copy) }
}

/**
 * Creates a `Mat` from a file.
 */
fun File.toMat(): Mat {
    return Highgui.imread(this.absolutePath)
}

/**
 * Map over row, col, data.
 */
fun Mat.map(f: (Int, Int, DoubleArray) -> DoubleArray): Mat {
    return this.copy().apply {
        for (i in 0..cols() - 1) {
            for (j in 0..rows() - 1) {
                this[j, i] = f.invoke(j, i, this[j, i])
            }
        }
    }
}

/**
 * Remaps set to put.
 */
operator fun Mat.set(row: Int, col: Int, data: DoubleArray) = this.put(row, col, *data)