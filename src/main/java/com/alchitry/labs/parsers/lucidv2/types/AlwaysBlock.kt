package com.alchitry.labs.parsers.lucidv2.types

import com.alchitry.labs.parsers.lucidv2.context.Evaluable
import com.alchitry.labs.parsers.lucidv2.context.LucidBlockContext
import com.alchitry.labs.parsers.lucidv2.grammar.LucidParser.AlwaysBlockContext
import com.alchitry.labs.parsers.lucidv2.grammar.LucidParser.RepeatStatContext
import com.alchitry.labs.parsers.lucidv2.signals.Signal
import com.alchitry.labs.parsers.onAnyChange
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch

class AlwaysBlock(
    context: LucidBlockContext,
    dependencies: Set<Signal>,
    val drivenSignals: Set<Signal>,
    val repeatSignals: Map<RepeatStatContext, Signal>,
    private val alwaysBlockContext: AlwaysBlockContext
): Evaluable {
    val context = context.withEvalContext(this)

    init {
        dependencies.forEach { it.isRead = true }
        drivenSignals.forEach {
            require(!it.hasDriver) { "Signal \"${it.name}\" is already driven!" }
            it.hasDriver = true
        }
        this.context.project.scope.launch(start = CoroutineStart.UNDISPATCHED) {
            onAnyChange(dependencies.map { it.valueFlow }) {
                this@AlwaysBlock.context.project.queueEvaluation(this@AlwaysBlock)
            }
        }
    }

    override suspend fun evaluate() {
        context.walk(alwaysBlockContext)

        if (context.errorCollector.errors.isNotEmpty()) {
            context.errorCollector.errors.forEach { println(it) }
            error("Failed to evaluate always block!")
        }

        context.blockEvaluator.processWriteQueue()
    }
}