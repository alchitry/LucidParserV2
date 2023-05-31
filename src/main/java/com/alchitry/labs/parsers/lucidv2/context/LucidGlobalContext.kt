package com.alchitry.labs.parsers.lucidv2.context

import com.alchitry.labs.parsers.errors.ErrorListener
import com.alchitry.labs.parsers.lucidv2.ErrorCollector
import com.alchitry.labs.parsers.lucidv2.grammar.LucidParser
import com.alchitry.labs.parsers.lucidv2.grammar.LucidParser.SourceContext
import com.alchitry.labs.parsers.lucidv2.parsers.*
import com.alchitry.labs.parsers.lucidv2.signals.SignalOrParent
import com.alchitry.labs.parsers.lucidv2.types.GlobalNamespace
import org.antlr.v4.runtime.tree.ParseTreeListener

class LucidGlobalContext(
    override val project: ProjectContext,
    val errorCollector: ErrorCollector = ErrorCollector()
) : LucidExprContext, ErrorListener by errorCollector {
    override val evalContext: Evaluable? = null
    override fun tick() = error("LucidGlobalContext doesn't support tick()")
    override fun abortTest() = error("LucidGlobalContext doesn't support abortTest()")

    private val expr = ExprParser(this)
    private val bitSelection = BitSelectionParser(this)
    private val struct = StructParser(this)
    private val signal = SignalParser(this)
    private val global = GlobalParser(this)
    private val enum = EnumParser(this)
    private val constant = ConstantParser(this)

    private val listeners = listOf<ParseTreeListener>(
        this.expr,
        this.bitSelection,
        this.struct,
        this.enum,
        this.constant,
        this.signal,
        this.global
    )

    fun walk(t: SourceContext) = ParseTreeMultiWalker.walk(listeners, t, WalkerFilter.GlobalsOnly)

    override fun resolve(exprCtx: LucidParser.ExprContext) = expr.resolve(exprCtx)

    override fun resolve(signalCtx: LucidParser.SignalContext) = signal.resolve(signalCtx)
    override fun resolve(signalWidthContext: LucidParser.SignalWidthContext) = signal.resolve(signalWidthContext)
    override fun resolve(structTypeContext: LucidParser.StructTypeContext) = struct.resolve(structTypeContext)
    override fun resolve(structDecContext: LucidParser.StructDecContext) = struct.resolve(structDecContext)
    override fun resolve(bitSelectionContext: LucidParser.BitSelectionContext) =
        bitSelection.resolve(bitSelectionContext)

    override fun resolve(enumDecContext: LucidParser.EnumDecContext) = enum.resolve(enumDecContext)
    override fun resolve(constDecContext: LucidParser.ConstDecContext) = constant.resolve(constDecContext)

    /**
     * Searches all SignalParsers to resolve a signal name.
     */
    override fun resolveSignal(name: String): SignalOrParent? {
        constant.resolve(name)?.let { return it }
        enum.resolve(name)?.let { return it }

        return project.resolveSignal(name)
    }

    override fun resolveStruct(name: String) = struct.resolveStruct(name)

    override fun resolveGlobal(name: String): GlobalNamespace? = global.resolveGlobal(name)
}