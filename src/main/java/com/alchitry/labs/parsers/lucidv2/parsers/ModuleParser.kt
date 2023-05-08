package com.alchitry.labs.parsers.lucidv2.parsers

import com.alchitry.labs.parsers.lucidv2.context.LucidExprContext
import com.alchitry.labs.parsers.lucidv2.context.SignalResolver
import com.alchitry.labs.parsers.lucidv2.grammar.LucidBaseListener
import com.alchitry.labs.parsers.lucidv2.grammar.LucidParser
import com.alchitry.labs.parsers.lucidv2.signals.*
import com.alchitry.labs.parsers.lucidv2.values.Bit
import com.alchitry.labs.parsers.lucidv2.values.UndefinedValue

data class ModuleParser(
    private val context: LucidExprContext,
    var module: Module? = null,
    private val localParams: MutableMap<String, Signal> = mutableMapOf()
) : LucidBaseListener(), SignalResolver {
    fun withContext(context: LucidExprContext) = copy(context = context)

    override fun resolve(name: String) = localParams[name]

    /**
     * Build a local reference to the default value
     */
    override fun enterParamConstraint(ctx: LucidParser.ParamConstraintContext) {
        val parent = ctx.parent
        if (parent is LucidParser.ParamDecContext) {
            val name = parent.name().text
            val defaultValue = parent.paramDefault()?.expr()?.let { context.resolve(it) } ?: UndefinedValue(true)
            localParams[name] = Signal(name, SignalDirection.Read, null, defaultValue)
        }
    }

    override fun exitParamConstraint(ctx: LucidParser.ParamConstraintContext) {
        val value = context.resolve(ctx.expr())
        if (value?.isTrue()?.bit != Bit.B1) {
            context.reportError(ctx, "Parameter constraint \"${ctx.text}\" failed!")
        }
    }

    override fun exitModule(ctx: LucidParser.ModuleContext) {
        if (module != null) {
            context.reportError(ctx, "Only one module per file is allowed.")
            return
        }

        val name = ctx.name().text

        if (ctx.name().TYPE_ID() == null)
            context.reportError(ctx.name(), "Module names must start with a lowercase letter!")

        val params = mutableMapOf<String, Parameter>()
        val ports = mutableMapOf<String, Port>()

        ctx.paramList()?.paramDec()?.forEach { paramCtx ->
            val paramName = paramCtx.name().text
            if (paramCtx.name().CONST_ID() == null) {
                context.reportError(
                    paramCtx.name(),
                    "Parameter names must start with an uppercase letter and can only contain uppercase letters, numbers, and underscores."
                )
            }

            val defaultValue = paramCtx.paramDefault()?.expr()?.let { context.resolve(it) }
            val constraintContext = paramCtx.paramConstraint()?.expr()

            if (params.putIfAbsent(paramName, Parameter(paramName, defaultValue, constraintContext)) != null) {
                context.reportError(
                    paramCtx.name(),
                    "The parameter name $paramName has already been used."
                )
            }
        }

        ctx.portList().portDec().forEach { portCtx ->
            val signed = portCtx.SIGNED() != null

            val portName = portCtx.name().text
            if (portCtx.name().TYPE_ID() == null) {
                context.reportError(portCtx.name(), "Port names must start with a lowercase letter!")
            }

            val direction = when (portCtx.portDirection().text) {
                "input" -> SignalDirection.Read
                "output" -> SignalDirection.Write
                "inout" -> SignalDirection.Both
                else -> {
                    context.reportError(
                        portCtx.portDirection(),
                        "Unknown signal type ${portCtx.portDirection().text}!"
                    )
                    return
                }
            }

            val width = context.resolve(portCtx.signalWidth())
            if (width == null) {
                context.reportError(portCtx.signalWidth(), "Failed to resolve signal width!")
                return@forEach
            }

            ports[portName] = Port(portName, direction, width, signed)
        }

        module = Module(name, params, ports).also {
            if (!context.project.addModule(it)) {
                context.reportError(ctx.name(), "A module with name $name already exists!")
            }
        }

    }
}