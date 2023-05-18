import com.alchitry.labs.parsers.lucidv2.context.QueueExhaustionException
import com.alchitry.labs.parsers.lucidv2.signals.Signal
import com.alchitry.labs.parsers.lucidv2.types.Dff
import com.alchitry.labs.parsers.lucidv2.types.EnumType
import com.alchitry.labs.parsers.lucidv2.values.Bit
import com.alchitry.labs.parsers.lucidv2.values.BitListValue
import com.alchitry.labs.parsers.lucidv2.values.BitValue
import helpers.LucidTester
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ModuleMultiPassTests {
    @Test
    fun testGlobalUse() {
        val tester = LucidTester(
            """
                global MyGlobal {
                    const ONE = 1
                }
                
                module myModule (
                    input a
                ) {
                
                    dff test(.clk(MyGlobal.ONE))
                    
                    always {
                        test.d = 0
                        if (test.q) {}
                        if (a) {}
                    }
                }
            """.trimIndent()
        )
        val context = tester.fullParse().context

        val dff = context.resolveSignal("test") as Dff

        assertEquals(BitValue(Bit.B1, true, false), dff.clk.value)
    }

    @Test
    fun basicRepeatTest() {
        val tester = LucidTester(
            """
                module myModule (
                    input a
                ) {
                    sig endValue[16]
                    const REP_CT = 5
                
                    always {
                        if (a) {}
                        endValue = 0
                        repeat(i, REP_CT) {
                            endValue = endValue + i
                        }
                    }
                }
            """.trimIndent()
        )
        val context = tester.fullParse().context
        val testSig = context.resolveSignal("endValue") as Signal

        runBlocking {
            context.initialize()
            tester.project.processQueue()
        }

        assertEquals(BitListValue(1 + 2 + 3 + 4, 16, signed = false, constant = false), testSig.read(null))
    }

    @Test
    fun doubleDriverTest() {
        val tester = LucidTester(
            """
                module myModule (
                    input a
                ) {
                    sig testA
                    sig testB
                
                    always {
                        testB = testA
                    }
                    
                    always {
                        if (testB) { } // to remove unused signal warning
                        if (a) {}
                        
                        testA = 1
                    }
                }
            """.trimIndent()
        )
        val context = tester.fullParse().context
        val testSig = context.resolveSignal("testB") as Signal

        runBlocking {
            context.initialize()
            tester.project.processQueue()
        }

        assertEquals(BitValue(Bit.B1, signed = false, constant = false), testSig.read(null))
    }

    @Test
    fun endlessLoopTest() {
        val tester = LucidTester(
            """
                module myModule (
                    input a
                ) {
                    sig testA
                    sig testB
                
                    always {
                        if (a)
                            testB = ~testA
                        else
                            testB = 1
                    }
                    
                    always {
                        testA = testB
                    }
                }
            """.trimIndent()
        )
        val top = tester.fullParse()

        val context = top.context

        runBlocking {
            top.getSignal("a")!!.write(BitValue(Bit.B0, false, false))
            context.initialize()
            tester.project.processQueue()
            top.getSignal("a")!!.write(BitValue(Bit.B1, false, false))
            try {
                tester.project.processQueue()
                error("ProcessQueue should've thrown an error!")
            } catch (_: QueueExhaustionException) {

            }
        }
    }

    @Test
    fun enumTest() {
        val tester = LucidTester(
            """
                module myModule (
                    input a,
                    output test[2]
                ) {
                    enum myFSM {
                        IDLE,
                        INIT,
                        RUN,
                        STOP
                    }
                
                    always {
                        if (a) {}
                        test = myFSM.RUN
                    }
                }
            """.trimIndent()
        )
        val top = tester.fullParse()
        val context = top.context
        val testSig = top.ports["test"]

        runBlocking {
            context.initialize()
            tester.project.processQueue()
        }

        val enum = EnumType("myFSM", listOf("IDLE", "INIT", "RUN", "STOP"), null)

        assertEquals(BitListValue(2, 2, signed = false, constant = false), testSig?.read())
        assertEquals(enum, context.enum.resolve("myFSM"))
    }

    @Test
    fun globalEnumTest() {
        val tester = LucidTester(
            """
                module myModule (
                    output test[2]
                ) {
                    always {
                        test = Enums.myFSM.RUN
                    }
                }
            """.trimIndent(),
            """
                global Enums {
                    enum myFSM {
                        IDLE,
                        INIT,
                        RUN,
                        STOP
                    }
                }
            """.trimIndent()
        )
        val top = tester.fullParse()
        val context = top.context
        val testSig = top.ports["test"]

        runBlocking {
            context.initialize()
            tester.project.processQueue()
        }

        val enum = EnumType("myFSM", listOf("IDLE", "INIT", "RUN", "STOP"), tester.project.resolveGlobal("Enums"))

        assertEquals(BitListValue(2, 2, signed = false, constant = false), testSig?.read())
        assertEquals(enum, tester.project.resolveGlobal("Enums")?.enums?.values?.first())
    }
}