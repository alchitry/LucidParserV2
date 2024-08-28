import com.alchitry.labs2.parsers.hdl.ExprEvalMode
import com.alchitry.labs2.parsers.hdl.lucid.parsers.toSourceFile
import com.alchitry.labs2.parsers.hdl.types.ModuleInstance
import com.alchitry.labs2.parsers.hdl.types.ModuleInstanceArray
import com.alchitry.labs2.parsers.hdl.types.Signal
import com.alchitry.labs2.parsers.hdl.types.SignalDirection
import com.alchitry.labs2.parsers.hdl.values.*
import helpers.LucidTester
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ModuleInstanceTests {
    @Test
    fun basicTwoModules() = runBlocking {
        val tester = LucidTester(
            """
                module alchitryTop (
                    input clk
                ) {
                
                    testModule myMod (.clk(clk))
                    
                    always {
                        if (myMod.a) { } // to remove unused signal warning
                    }
                }
            """.trimIndent().toSourceFile(),
            """
                module testModule (
                    input clk,
                    output a
                ) {
                    always  {
                        a = clk
                    }
                }
            """.trimIndent().toSourceFile("testModule.luc")
        )

        val top = tester.fullParse()
        tester.assertNoErrors()
        val myModInst = top.context.types.resolve(top.moduleContext, "myMod") as ModuleInstance
        assertEquals(SignalDirection.Read, myModInst.getSignal("a")?.direction)
    }

    @Test
    fun basicModuleArray() = runBlocking {
        val tester = LucidTester(
            """
                module alchitryTop (
                    input clk
                ) {
                    .clk(clk) {
                        testModule myMod[8]
                    }
                    
                    always {
                        if (myMod.a[0]) { } // to remove unused signal warning
                    }
                }
            """.trimIndent().toSourceFile(),
            """
                module testModule (
                    input clk,
                    output a[5]
                ) {
                    always  {
                        a = clk
                    }
                }
            """.trimIndent().toSourceFile("testModule.luc")
        )

        val top = tester.fullParse()
        tester.assertNoIssues()
        val myModInst = top.context.types.resolve(top.moduleContext, "myMod") as ModuleInstanceArray
        assertEquals(DefinedArrayWidth(8, BitListWidth(5)), myModInst.getSignal("a")?.width)
        assertEquals(SignalDirection.Read, myModInst.getSignal("a")?.direction)
    }

    @Test
    fun counterTest() = runBlocking {
        val tester = LucidTester(
            """
                module alchitryTop (
                    input clk,
                    output count[8]
                ) {
                    dff counter[8] (.clk(clk))
                    always {
                        counter.d = counter.q + 1
                        count = counter.q
                    }
                }
            """.trimIndent().toSourceFile()
        )

        val top = tester.fullParse()
        tester.assertNoIssues()

        val clk = top.getSignal("clk") as Signal
        val count = top.getSignal("count") as Signal

        val b0 = BitValue(Bit.B0, false)
        val b1 = BitValue(Bit.B1, false)

        runBlocking {
            top.context.initialize()

            (0..255).forEach {
                assertEquals(BitListValue(it, 8, false), count.read())

                clk.write(b0)
                tester.project.processQueue()
                clk.write(b1)
                tester.project.processQueue()
            }
            assertEquals(BitListValue(0, 8, false), count.read())
        }
    }

    @Test
    fun complexParamTest() = runBlocking {
        val tester = LucidTester(
            """
                module alchitryTop (
                    input clk,
                    output count[8]
                ) {
                    decoder myMod (#WIDTH(3))
                    
                    always {
                        myMod.in = 3
                        count = myMod.out
                    }
                }
            """.trimIndent().toSourceFile(),
            """
                module decoder #(
                    WIDTH ~ 4 : WIDTH > 0
                )(
                    input in[WIDTH],
                    output out[c{1, WIDTHx{1b0}}]
                ) {
                    always {
                        out = 0
                        out[in] = 1
                    }
                }
            """.trimIndent().toSourceFile("testModule.luc")
        )

        val top = tester.fullParse()
        tester.assertNoErrors()

        val clk = top.getSignal("clk") as Signal
        val count = top.getSignal("count") as Signal

        val b0 = BitValue(Bit.B0, false)
        val b1 = BitValue(Bit.B1, false)


        top.context.initialize()

        clk.write(b0)
        tester.project.processQueue()
        clk.write(b1)
        tester.project.processQueue()

        assertEquals(BitListValue(8, 8, false), count.read())
    }

    @Test
    fun counterParamTest() = runBlocking {
        val tester = LucidTester(
            """
                module alchitryTop (
                    input clk,
                    output count[8]
                ) {
                    .clk(clk) {
                        testModule myMod (#INC(2), #CTR_SIZE(8))
                    }
                   
                    always {
                        count = myMod.count
                    }
                }
            """.trimIndent().toSourceFile(),
            """
                module testModule #(
                    INC ~ 1,
                    CTR_SIZE ~ 4
                )(
                    input clk,
                    output count[CTR_SIZE]
                ) {
                    dff counter[8] (.clk(clk))
                    
                    always {
                        counter.d = counter.q + INC
                        count = counter.q
                    }
                }
            """.trimIndent().toSourceFile("testModule.luc")
        )

        val top = tester.fullParse()
        tester.assertNoIssues()

        val clk = top.getSignal("clk") as Signal
        val count = top.getSignal("count") as Signal

        val b0 = BitValue(Bit.B0, false)
        val b1 = BitValue(Bit.B1, false)


        top.context.initialize()

        (0..127).forEach {
            assertEquals(BitListValue(it * 2, 8, false), count.read())

            clk.write(b0)
            tester.project.processQueue()
            clk.write(b1)
            tester.project.processQueue()
        }
        assertEquals(BitListValue(0, 8, false), count.read())
    }

    @Test
    fun inoutPassthroughModules() = runBlocking {
        val tester = LucidTester(
            """
                module alchitryTop (
                    input clk,
                    inout btn[5]
                ) {
              
                    testModule myMod (.clk(clk), .btn(btn))
                   
                }
            """.trimIndent().toSourceFile(),
            """
                module testModule (
                    input clk,
                    inout btn
                ) {
                    always  {
                        btn = bz
                    }
                }
            """.trimIndent().toSourceFile("testModule.luc")
        )

        val top = tester.fullParse()
        tester.assertNoErrors()
        val myModInst = top.context.types.resolve(top.moduleContext, "myMod") as ModuleInstance
        top.ports["btn"]!!.external.write(Bit.B1.toBitValue())
        tester.project.processQueue()
        assertEquals(Bit.B1.toBitValue().asBitListValue(), myModInst.ports["btn"]?.internal?.read())
    }

    @Test
    fun badResolvableWidthModules() = runBlocking {
        val tester = LucidTester(
            """
                module alchitryTop (
                    input clk,
                    inout btn[5][3]
                ) {
              
                    testModule myMod (#DIM_1(1), #DIM_2(5), .clk(clk), .in(btn))
                   
                }
            """.trimIndent().toSourceFile(),
            """
                module testModule #(
                    DIM_1 = 1 : DIM_1 > 0,
                    DIM_2 = 1 : DIM_2 > 0
                )(
                    input clk,  // clock
                    inout in[DIM_2][DIM_1],
                    output out[DIM_2][DIM_1]
                ) {
                    always  {
                        out = in
                    }
                }
            """.trimIndent().toSourceFile("testModule.luc")
        )

        tester.fullParse()
        assert(tester.notationManager.hasErrors)
    }

    @Test
    fun missingParamValueInModuleArray() = runBlocking {
        val tester = LucidTester(
            """
                module multiDecimalCounter #(
                    DIGITS : DIGITS >= 2  // number of digits
                ) (
                    input clk,                // clock
                    input rst,                // reset
                    input inc,                // increment counter
                    output digits[DIGITS][4]  // digit values
                ) {
                    .clk(clk), .rst(rst) {
                        decimalCounter dctr [DIGITS] // digit counters
                    }
                    
                    always {
                        dctr.inc[0] = inc   // increment the first digit
                        digits = dctr.value // output the values
                        
                        // if the previous digit overflows, increment the next
                        dctr.inc[1+:DIGITS-1] = dctr.ovf[0+:DIGITS-1]
                    }
                }
            """.trimIndent().toSourceFile(),
            """
                module decimalCounter (
                    input clk,      // clock
                    input rst,      // reset
                    input inc,      // increment the counter
                    output ovf,     // counter overflowed
                    output value[4] // current value
                ) {
                    .clk(clk), .rst(rst) {
                        dff val[4]     // value storage
                    }
                    
                    always {
                        value = val.q             // output the value
                        
                        ovf = val.q == 9 && inc   // if max value and incrementing, overflow!
                        
                        if (inc) {                // should add 1
                            if (val.q == 9)       // if max value
                                val.d = 0         // reset to 0
                            else                  // otherwise
                                val.d = val.q + 1 // add one
                        }
                    }
                }
            """.trimIndent().toSourceFile("decimalCounter.luc")
        )

        tester.fullParse(ExprEvalMode.Testing)
        tester.assertNoErrors()
    }
}