package com.alchitry.labs2.parsers.lucidv2.parsers

import com.alchitry.labs2.parsers.grammar.LucidParser.NameContext
import com.alchitry.labs2.parsers.lucidv2.types.DynamicExpr

data class Connection(val portCtx: NameContext, val value: DynamicExpr) {
    val port: String = portCtx.text
}

data class AssignmentBlock(val signals: List<Connection>, val params: List<Connection>)