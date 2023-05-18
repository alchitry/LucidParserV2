package com.alchitry.labs.parsers.lucidv2.parsers

import com.alchitry.labs.parsers.lucidv2.context.LucidExprContext
import com.alchitry.labs.parsers.lucidv2.context.SignalResolver
import com.alchitry.labs.parsers.lucidv2.grammar.LucidBaseListener
import com.alchitry.labs.parsers.lucidv2.grammar.LucidParser
import com.alchitry.labs.parsers.lucidv2.grammar.LucidParser.EnumDecContext
import com.alchitry.labs.parsers.lucidv2.types.EnumType

data class EnumParser(
    private val context: LucidExprContext,
    private val enumTypes: MutableMap<EnumDecContext, EnumType> = mutableMapOf(),
    private val localEnumType: MutableMap<String, EnumType> = mutableMapOf()
) : LucidBaseListener(), SignalResolver {
    override fun resolve(name: String) = localEnumType[name]
    fun resolve(enumDecContext: EnumDecContext) = enumTypes[enumDecContext]

    fun getLocalEnums(): List<EnumType> = localEnumType.values.toList()

    override fun enterGlobal(ctx: LucidParser.GlobalContext) {
        localEnumType.clear()
    }

    override fun enterModule(ctx: LucidParser.ModuleContext) {
        localEnumType.clear()
    }

    override fun exitEnumDec(ctx: EnumDecContext) {
        val names = ctx.name()
        val nameCtx = names.firstOrNull() ?: return

        if (names.size < 2) {
            context.reportError(ctx, "Enums must have at least one member!")
            return
        }

        if (nameCtx.TYPE_ID() == null) {
            context.reportError(nameCtx, "Enum names must start with a lowercase letter.")
            return
        }

        val membersCtx = ctx.name().subList(1, ctx.name().size)

        membersCtx.forEach { mCtx ->
            if (mCtx.CONST_ID() == null) {
                context.reportError(mCtx, "Enum member names must be all uppercase letters an underscores.")
                return
            }
        }

        enumTypes[ctx] = EnumType(nameCtx.text, membersCtx.map { it.text }, null)
            .also { localEnumType[nameCtx.text] = it }
    }
}