package com.nobosoftware.nestedx.android.models

data class BigGrid(
    val smallGrids: Array<SmallGrid> = Array(9) { SmallGrid() }
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BigGrid

        if (!smallGrids.contentEquals(other.smallGrids)) return false

        return true
    }

    override fun hashCode(): Int {
        return smallGrids.contentHashCode()
    }
}

