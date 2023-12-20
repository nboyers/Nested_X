package com.nobosoftware.nestedx.android.models

data class BigGrid(
    val smallGrids: Array<SmallGrid> = Array(9) { index -> SmallGrid(index = index) }
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BigGrid

        return smallGrids.contentEquals(other.smallGrids)
    }

    override fun hashCode(): Int {
        return smallGrids.contentHashCode()
    }
}

