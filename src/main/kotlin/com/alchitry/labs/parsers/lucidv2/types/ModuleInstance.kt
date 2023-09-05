package com.alchitry.labs.parsers.lucidv2.types

import com.alchitry.labs.parsers.EvalQueue
import com.alchitry.labs.parsers.errors.ErrorCollector
import com.alchitry.labs.parsers.lucidv2.context.LucidBlockContext
import com.alchitry.labs.parsers.lucidv2.signals.Signal
import com.alchitry.labs.parsers.lucidv2.signals.SignalDirection
import com.alchitry.labs.parsers.lucidv2.signals.SignalOrSubSignal
import com.alchitry.labs.parsers.lucidv2.signals.snapshot.SnapshotOrParent
import com.alchitry.labs.parsers.lucidv2.signals.snapshot.SnapshotParent
import com.alchitry.labs.parsers.lucidv2.values.Value
import com.alchitry.labs.project.Project

class ModuleInstance(
    override val name: String,
    project: Project,
    private val evalQueue: EvalQueue,
    override val parent: ModuleInstance?,
    val module: Module,
    parameters: Map<String, Value>,
    connections: Map<String, SignalOrSubSignal>,
    errorCollector: ErrorCollector
) : ModuleInstanceOrArray, ListOrModuleInstance, TestOrModuleInstance {
    override val context = LucidBlockContext(project, evalQueue, this, errorCollector = errorCollector)

    override fun takeSnapshot(): SnapshotParent {
        val snapshots = mutableListOf<SnapshotOrParent>()
        snapshots.addAll(context.types.dffs.values.map { it.takeSnapshot() })
        snapshots.addAll(context.types.sigs.values.map { it.takeSnapshot() })
        snapshots.addAll(context.types.moduleInstances.values.map { it.takeSnapshot() })
        snapshots.addAll(internal.values.map { it.takeSnapshot() })
        return SnapshotParent(name, snapshots)
    }

    suspend fun checkParameters(): Boolean = context.checkParameters()
    suspend fun initialWalk() = context.initialWalk(module.context)

    val ports = module.ports.mapValues { (_, port) ->
        port.instantiate(this, evalQueue)
    }

    override val internal: Map<String, Signal> = ports.mapValues { it.value.internal }
    override val external: Map<String, Signal> = ports
        .filter { !connections.contains(it.key) }
        .mapValues { it.value.external }

    init {
        connections.forEach { (name, sig) ->
            val port = ports[name]?.external ?: error("No matching port for given connection \"$name\"!")
            if (port.direction.canWrite)
                sig.connectTo(port, evalQueue)
            if (port.direction.canRead)
                port.connectTo(sig, evalQueue)
        }
    }

    // Use the provided parameters or the default value from the module is it is missing
    val parameters = module.parameters.mapValues { (name, param) ->
        Signal(
            name,
            SignalDirection.Read,
            this,
            parameters[name] ?: param.default ?: error("Missing module parameter!")
        )
    }

    fun getInternalSignal(name: String) = internal[name] ?: parameters[name]
    override fun getSignal(name: String) = external[name]
}
