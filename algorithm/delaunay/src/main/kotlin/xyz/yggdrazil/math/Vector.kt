package xyz.yggdrazil.math

/**
 * Created by amo on 19/09/16.
 */

class Vector(var x: Double, var y: Double, var z: Double) {

    constructor(x: Double, y: Double) : this(x, y, 0.0)

    fun dot(v: Vector): Double {
        return x * v.x + y * v.y + z * v.z
    }

    fun toPoint(): Point {
        return Point(x, y)
    }

    val inverse: Vector
        get() = Vector(1.0f / x, 1.0f / y, 1.0f / z)

    fun normalize(): Double {
        val len = length()
        val inv = 1.0f / len

        x *= inv
        y *= inv
        z *= inv

        return len
    }

    fun angleBetween(v: Vector): Double {
        val v1 = Vector(x, y, z)
        val v2 = Vector(v.x, v.y, v.z)
        v1.normalize()
        v2.normalize()
        return Math.acos(v1.dot(v2))
    }

    fun length(): Double {
        return Math.sqrt(x * x + y * y + z * z).toDouble()
    }

    fun length2(): Double {
        return x * x + y * y + z * z
    }

    fun cross(v: Vector): Vector {
        return Vector(y * v.z - z * v.y,
                z * v.x - x * v.z,
                x * v.y - y * v.x)
    }

    operator fun times(t: Double): Vector {
        return Vector(x * t, y * t, z * t)

    }

    operator fun plus(vector: Vector): Vector {
        return Vector(x + vector.x,
                y + vector.y,
                z + vector.z)
    }

    operator fun minus(vector: Vector): Vector {
        return Vector(x - vector.x,
                y - vector.y,
                z - vector.z)
    }


}
