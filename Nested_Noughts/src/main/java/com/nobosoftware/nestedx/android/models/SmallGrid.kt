package com.nobosoftware.nestedx.android.models

data class SmallGrid(
    val cells: Array<Player> = Array(9) { Player.None }
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SmallGrid

        if (!cells.contentEquals(other.cells)) return false

        return true
    }

    override fun hashCode(): Int {
        return cells.contentHashCode()
    }
}