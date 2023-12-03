package com.nobosoftware.nestedx.android.models

data class SmallGrid(
    val cells: Array<Player> = Array(9) { Player.None },
    var winner: Player? = null,
    var isTie: Boolean = false,
    var isWon: Boolean = false

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SmallGrid

        if (!cells.contentEquals(other.cells)) return false
        if (winner != other.winner) return false
        if (isTie != other.isTie) return false
        return isWon == other.isWon
    }

    override fun hashCode(): Int {
        var result = cells.contentHashCode()
        result = 31 * result + (winner?.hashCode() ?: 0)
        result = 31 * result + isTie.hashCode()
        result = 31 * result + isWon.hashCode()
        return result
    }
}