package tabletop.common.geometry

import java.io.Serializable


data class Point(val x: Int, val y: Int) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
