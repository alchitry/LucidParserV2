package com.alchitry.labs.parsers.lucidv2.types

import com.alchitry.labs.parsers.lucidv2.context.LucidModuleContext
import com.alchitry.labs.parsers.lucidv2.context.ProjectContext
import com.alchitry.labs.parsers.lucidv2.signals.Signal
import com.alchitry.labs.parsers.lucidv2.signals.SignalDirection
import com.alchitry.labs.parsers.lucidv2.signals.SignalParent
import com.alchitry.labs.parsers.lucidv2.values.Bit
import com.alchitry.labs.parsers.lucidv2.values.Value

class ModuleInstance(
    override val name: String,
    private val project: ProjectContext,
    override val parent: ModuleInstance?,
    val module: Module,
    parameters: Map<String, Value>
) : SignalParent {
    val context = LucidModuleContext(project, this)

    fun checkParameters() = context.checkParameters()
    fun initialWalk() = context.initialWalk(module.context)

    private val inouts = module.ports.mapNotNull { (_, port) ->
        if (port.direction != SignalDirection.Both)
            null
        else
            Inout(port.name, project, this, port.width, port.signed)
    }.associateBy { it.name }

    private val ports: Map<String, Signal> = module.ports.mapValues { (_, port) ->
        if (port.direction == SignalDirection.Both)
            inouts[port.name]?.internal ?: error("Missing inout for port ${port.name}! This should be impossible!")
        else
            Signal(port.name, port.direction, null, port.width.filledWith(Bit.Bx, false, port.signed), port.signed)
    }

    private val externalPorts: Map<String, Signal> = module.ports.mapValues { (_, port) ->
        if (port.direction == SignalDirection.Both)
            inouts[port.name]?.external ?: error("Missing inout for port ${port.name}! This should be impossible!")
        else
            Signal(
                port.name,
                port.direction.flip(),
                this,
                port.width.filledWith(Bit.Bx, false, port.signed),
                port.signed
            )
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

    init {
        ports.keys.forEach { portName ->
            val internal = ports[portName] ?: error("Missing port $portName!")
            val external = externalPorts[portName] ?: error("Missing port $portName!")

            when (internal.direction) {
                SignalDirection.Read -> external.connect(internal, project)
                SignalDirection.Write -> internal.connect(external, project)
                else -> {}
            }
        }
    }

    fun getInternalSignal(name: String) = ports[name] ?: parameters[name]
    override fun getSignal(name: String) = externalPorts[name]
}
