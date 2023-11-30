package com.nobosoftware.nestedx.android.models

data class SmallGrid(
    val cells: Array<Player> = Array(9) { Player.None },
    var winner: Player? = null
) {
    val isDraw: Boolean
        get() = cells.none { it == Player.None } && winner == null
    val isWon: Boolean
        get() = winner != null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SmallGrid

        if (!cells.contentEquals(other.cells)) return false
        if (winner != other.winner) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cells.contentHashCode()
        result = 31 * result + (winner?.hashCode() ?: 0)
        return result
    }
}