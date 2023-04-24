package com.alchitry.labs.parsers.lucidv2.values

import com.alchitry.labs.parsers.lucidv2.signals.StructType

sealed class SignalWidth {
    /**
     * Returns true if this is a 1D array
     */
    fun isFlatArray(): Boolean {
        return this is SimpleWidth || this is UndefinedSimpleWidth
    }

    /**
     *  Returns true if this is JUST an array (no structs)
     */
    fun isSimpleArray(): Boolean {
        return when (this) {
            is ArrayWidth -> next.isSimpleArray()
            is SimpleWidth, BitWidth -> true
            is UndefinedSimpleWidth -> true
            is StructWidth -> false
        }
    }

    /**
     *  Returns true if this is JUST an array (no structs) and defined
     */
    fun isDefinedSimpleArray(): Boolean {
        return (this is SimpleWidth || this is BitWidth || (this is ArrayWidth && next.isDefinedSimpleArray()))
    }

    /**
     * Returns true if this is an array. It may be an array of anything, including structs.
     */
    fun isArray(): Boolean {
        return this is ArrayWidth || this is UndefinedSimpleWidth || this is SimpleWidth
    }

    /**
     * Returns true if this is a defined array. It may be an array of anything, including structs.
     */
    fun isDefinedArray(): Boolean {
        return this is ArrayWidth || this is SimpleWidth
    }

    /**
     * Returns true if the width is well-defined.
     */
    fun isDefined(): Boolean {
        return when (this) {
            is SimpleWidth, BitWidth -> true
            is ArrayWidth -> next.isDefined()
            is StructWidth -> type.values.all { it.isDefined() }
            is UndefinedSimpleWidth -> false
        }
    }

    fun getDimensions(): List<Int> {
        require(isDefinedSimpleArray()) { "getDimensions can only be called on arrays" }
        val dims = mutableListOf<Int>()
        var array: SignalWidth = this
        while (true) {
            if (array is ArrayWidth) {
                dims.add(array.size)
                array = array.next
            } else if (array is SimpleWidth) {
                dims.add(array.size)
                break
            } else if (array is BitWidth) {
                break
            }
        }
        return dims
    }

    fun getBitCount(): Int {
        return when (this) {
            is BitWidth -> 1
            is ArrayWidth -> size * next.getBitCount()
            is StructWidth -> type.values.sumOf { it.getBitCount() }
            UndefinedSimpleWidth -> error("getBitCount() can't be used when width isn't well defined")
            is SimpleWidth -> size
        }
    }

    /** Returns true if the other width can be scaled to match this width. */
    fun canAssign(other: SignalWidth): Boolean =
        when (this) {
            is ArrayWidth, is StructWidth -> this == other
            BitWidth, is SimpleWidth, UndefinedSimpleWidth -> other is BitWidth || other is SimpleWidth || other is UndefinedSimpleWidth
        }

}

object BitWidth: SignalWidth()

data class SimpleWidth(
    val size: Int
) : SignalWidth()

data class ArrayWidth(
    val size: Int,
    val next: SignalWidth
) : SignalWidth()

data class StructWidth(
    val type: StructType
) : SignalWidth()

object UndefinedSimpleWidth : SignalWidth() {
    override fun equals(other: Any?): Boolean {
        return false
    }
}