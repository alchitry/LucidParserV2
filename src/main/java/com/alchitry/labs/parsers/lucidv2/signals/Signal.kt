package com.alchitry.labs.parsers.lucidv2.signals

import com.alchitry.labs.parsers.SynchronizedSharedFlow
import com.alchitry.labs.parsers.lucidv2.context.Evaluable
import com.alchitry.labs.parsers.lucidv2.signals.snapshot.Snapshot
import com.alchitry.labs.parsers.lucidv2.signals.snapshot.Snapshotable
import com.alchitry.labs.parsers.lucidv2.values.SignalWidth
import com.alchitry.labs.parsers.lucidv2.values.SimpleValue
import com.alchitry.labs.parsers.lucidv2.values.Value
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex

open class Signal(
    override val name: String, // includes namespace or module name
    override val direction: SignalDirection,
    override val parent: SignalParent?,
    initialValue: Value,
    val signed: Boolean = initialValue is SimpleValue && initialValue.signed
) : SignalOrSubSignal, SignalOrParent, Snapshotable {
    fun select(selection: SignalSelection) = SubSignal(this, selection)

    private val mutableValueFlow = SynchronizedSharedFlow<Value>()
    override val valueFlow: Flow<Value> get() = mutableValueFlow.asFlow()

    private var setEvalContext: Evaluable? = null
    private var nextValue: Value? = null
    private var value: Value = initialValue.withSign(signed)

    val subSignalLock = Mutex() // lock used by sub-signals during read-modify-write cycles

    var hasDriver: Boolean = false
    var isRead: Boolean = false

    override fun takeSnapshot() = Snapshot(name, read())

    override fun read(evalContext: Evaluable?): Value {
        if (evalContext === setEvalContext)
            return nextValue ?: value
        return value
    }

    override val width: SignalWidth = value.width

    override fun quietWrite(v: Value, evalContext: Evaluable?) {
        require(setEvalContext == null || evalContext === setEvalContext) { "Attempted to set signal from two different evalContext!" }
        setEvalContext = evalContext
        quietWrite(v)
    }

    private fun quietWrite(v: Value) {
        require(value.canAssign(v)) { "Signal assigned value does not match its size!" }
        val resizedValue = v.resizeToMatch(value.width)
        nextValue = resizedValue.withSign(signed).asMutable()
    }

    override suspend fun publish() {
        nextValue?.let {
            value = it
            mutableValueFlow.emit(it)
        }
        nextValue = null
        setEvalContext = null
    }

    override suspend fun write(v: Value) {
        quietWrite(v)
        publish()
    }

    fun fullName(): String {
        val parts = mutableListOf<String>()
        var current: SignalOrParent = this
        parts.add(this.name)
        while (current.parent != null) {
            parts.add(current.parent!!.name)
            current = current.parent!!
        }
        return parts.asReversed().joinToString(".")
    }

}