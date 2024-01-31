package com.alchitry.labs2.simulation

import androidx.compose.runtime.*
import com.alchitry.labs2.Log
import com.alchitry.labs2.hardware.pinout.AuPin
import com.alchitry.labs2.parsers.ProjectContext
import com.alchitry.labs2.parsers.lucidv2.types.SignalOrSubSignal
import com.alchitry.labs2.parsers.lucidv2.values.Bit
import com.alchitry.labs2.parsers.lucidv2.values.BitValue
import com.alchitry.labs2.project.Board
import com.alchitry.labs2.ui.simulation.AlchitryBoard
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Suppress("DataClassPrivateConstructor")
data class AlchitrySimulator private constructor(
    val rstN: SignalOrSubSignal?,
    val leds: List<SignalOrSubSignal?>
) {
    private val resetValueFlow = MutableStateFlow(false)
    private var ledAccumulators = List(8) { SampleAccumulator(0, 0) }
    private val accumulatorLock = Mutex()

    fun reportMissingSignals() {
        if (rstN == null)
            Log.warn("Design is missing a connection to the reset button.")
        leds.forEachIndexed { index, signal ->
            if (signal == null)
                Log.warn("Design is missing a connection to led[$index].")
        }
    }

    suspend fun initialize(clk: SignalOrSubSignal) {
        accumulatorLock.withLock {
            ledAccumulators.forEach { it.reset() }
        }

        rstN?.write(ProjectSimulator.high)

        clk.addDependant {
            onClkChange(clk.read().isTrue().bit == Bit.B1)
        }
    }

    private suspend fun onClkChange(rising: Boolean) {
        rstN?.write(if (resetValueFlow.value) ProjectSimulator.low else ProjectSimulator.high)
        if (rising)
            accumulatorLock.withLock {
                leds.forEachIndexed { index, led ->
                    val bit = (led?.read() as? BitValue)?.bit ?: return@forEachIndexed
                    val accumulator = ledAccumulators[index]
                    accumulator.runningSum += when (bit) {
                        Bit.B0 -> 0
                        Bit.B1 -> 1
                        Bit.Bx -> {
                            accumulator.invalid = true
                            0
                        }

                        Bit.Bz -> 0
                    }
                    accumulator.count += 1
                }
            }
    }

    private suspend fun getLedValues(): List<Float?> {
        return accumulatorLock.withLock {
            ledAccumulators.map { accumulator ->
                when {
                    accumulator.invalid -> Float.NaN
                    accumulator.count == 0L -> null
                    else -> accumulator.runningSum.toFloat() / accumulator.count.toFloat()
                }.also { accumulator.reset() }
            }
        }
    }

    @Composable
    fun contents() {
        var leds by remember { mutableStateOf(List(8) { 0f }) }

        LaunchedEffect(Unit) {
            while (isActive) {
                delay(1000 / 30)
                leds = getLedValues().mapIndexed { i, v -> v ?: leds[i] }
            }
        }

        AlchitryBoard(Board.AlchitryAu, leds, 1.0f) {
            resetValueFlow.tryEmit(it)
        }
    }

    companion object {
        fun connect(projectContext: ProjectContext): AlchitrySimulator {
            val constraints = projectContext.getConstraints()
            val reset = constraints.firstOrNull { it.pin.name == AuPin.RESET.name }?.port
            val ledNames = listOf(
                AuPin.LED0.name,
                AuPin.LED1.name,
                AuPin.LED2.name,
                AuPin.LED3.name,
                AuPin.LED4.name,
                AuPin.LED5.name,
                AuPin.LED6.name,
                AuPin.LED7.name,
            )
            val led = ledNames.map { ledName ->
                constraints.firstOrNull { it.pin.name == ledName }?.port
            }
            return AlchitrySimulator(reset, led)
        }
    }
}