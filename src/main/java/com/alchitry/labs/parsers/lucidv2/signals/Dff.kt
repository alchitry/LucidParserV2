package com.alchitry.labs.parsers.lucidv2.signals

import com.alchitry.labs.parsers.lucidv2.values.DynamicExpr
import com.alchitry.labs.parsers.lucidv2.values.Value

data class Dff(
    override val name: String,
    val init: Value,
    val clk: DynamicExpr,
    val rst: DynamicExpr?
) : SignalParent {
    val d = Signal("d", SignalDirection.Write, this, init.asMutable())
    val q = Signal("q", SignalDirection.Read, this, init.asMutable())

    override fun getSignal(name: String): Signal? =
        when (name) {
            "d" -> d
            "q" -> q
            else -> null
        }
}
