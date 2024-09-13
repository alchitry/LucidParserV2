package basic

import com.alchitry.labs2.parsers.hdl.lucid.parsers.toSourceFile
import com.alchitry.labs2.parsers.hdl.types.Signal
import com.alchitry.labs2.parsers.hdl.values.BitListValue
import helpers.ProjectTester
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SignalDriverTests {
    @Test
    fun basicDriverTest(): Unit = runBlocking {
        val tester = ProjectTester(
            """
                module myModule (
                    input a
                ) {
                    sig mySig[8]
                   
                    always {
                        mySig = 8b0;
                        
                        if (mySig) {} // to remove unused signal warning
                        if (a) {}
                    }
                }
            """.trimIndent().toSourceFile()
        )
        tester.fullParse()
        assert(tester.notationManager.hasNoErrors)
    }

    @Test
    fun signalNotWrittenTest(): Unit = runBlocking {
        val tester = ProjectTester(
            """
                module myModule (
                    input a
                ) {
                    sig mySig[8]
                   
                    always {
                        if (mySig) {} // to remove unused signal warning
                        if (a) {}
                    }
                }
            """.trimIndent().toSourceFile()
        )
        tester.fullParse()
        assert(tester.notationManager.hasErrors) { tester.notationManager.getReport() }
    }

    @Test
    fun checkSigInRepeat() = runBlocking {
        val tester = ProjectTester(
            """
                module errorTest (
                    input in[32],
                    output out[32]
                ) {
                    sig currentIdx[6]
                    always {
                        currentIdx = 0
                        //out = 0
                        repeat(i, 16) {
                            out[currentIdx] = 0
                            out[currentIdx+1] = 1
                            currentIdx = currentIdx + 2
                        }
                    }
                }
            """.trimIndent().toSourceFile()
        )
        tester.fullParse()
        assert(tester.notationManager.hasNoErrors)
    }

    @Test
    fun doubleDriverTest() = runBlocking {
        val tester = ProjectTester(
            """
                module myModule (
                    input a
                ) {
                    sig mySig[8]
                    
                    always {
                        mySig = 8b1;
                    }
                   
                    always {
                        mySig = 8b0;
                        
                        if (mySig) {} // to remove unused signal warning
                        if (a) {}
                    }
                }
            """.trimIndent().toSourceFile()
        )
        tester.fullParse()
        assert(tester.notationManager.hasErrors)
        assert(tester.notationManager.hasNoWarnings)
    }

    @Test
    fun basicPartialDrivenTest() = runBlocking {
        val tester = ProjectTester(
            """
                module myModule (
                    input a
                ) {
                    sig mySig[8]
                   
                    always {
                        mySig[4:0] = 0;
                        
                        if (mySig) { } // to remove unused signal warning
                        if (a) {}
                    }
                }
            """.trimIndent().toSourceFile()
        )
        tester.fullParse()
        assert(tester.notationManager.hasErrors) // should complain about test being partially driven
        assert(tester.notationManager.hasNoWarnings)
    }

    @Test
    fun multiPartialDrivenTest(): Unit = runBlocking {
        val tester = ProjectTester(
            """
                module myModule (
                    input a
                ) {
                    sig mySig[8]
                   
                    always {
                        mySig[4:0] = 0;
                        mySig[7:5] = 1;
                        
                        if (mySig) { } // to remove unused signal warning
                        if (a) {}
                    }
                }
            """.trimIndent().toSourceFile()
        )
        tester.fullParse()
        tester.assertNoIssues()
    }

    @Test
    fun basicIfPartialDrivenTest() = runBlocking {
        val tester = ProjectTester(
            """
                module myModule (
                    input a
                ) {
                    sig dummy
                    sig mySig[8]
                   
                    always {
                        dummy = 1
                        if (dummy) {
                            mySig = 0
                        }
                        if (a) {}
                    }
                }
            """.trimIndent().toSourceFile()
        )
        tester.fullParse()
        assert(tester.notationManager.hasErrors) // should complain about test being partially driven
    }

    @Test
    fun basicIfCompleteDrivenTest(): Unit = runBlocking {
        val tester = ProjectTester(
            """
                module myModule (
                    input a
                ) {
                    sig mySig[8]
                   
                    always {
                        if (1) 
                            mySig = a
                        else
                            mySig = 1
                            
                        if (mySig) {} // used to remove unread warnings
                        if (a) {}
                    }
                }
            """.trimIndent().toSourceFile()
        )
        tester.fullParse()
        tester.assertNoIssues()
    }

    @Test
    fun sigDirectAssignTest() = runBlocking {
        val tester = ProjectTester(
            """
                module myModule (
                    input a,
                    output b[8]
                ) {
                    sig mySig[8] = 12
                   
                    always {
                        b = mySig
                        if (a) {}
                    }
                }
            """.trimIndent().toSourceFile()
        )
        val top = tester.fullParse()
        tester.assertNoIssues()

        runBlocking {
            top.context.initialize()
        }

        assertEquals(BitListValue(12, 8, false), (top.getSignal("b") as Signal).read())
    }

    @Test
    fun sigDirectAssignOverdriveTest() = runBlocking {
        val tester = ProjectTester(
            """
                module myModule (
                    input a,
                    output b[8]
                ) {
                    sig mySig[8] = 12
                   
                    always {
                        mySig = a
                        b = mySig
                    }
                }
            """.trimIndent().toSourceFile()
        )
        tester.fullParse()
        assert(tester.notationManager.hasErrors)
        assert(tester.notationManager.hasNoWarnings)
    }

    @Test
    fun sigDirectAssignTruncationTest() = runBlocking {
        val tester = ProjectTester(
            """
                module myModule (
                    input a,
                    output b[8]
                ) {
                    sig mySig[8] = 9b0 // 9b0 is wider than 8 bits
                   
                    always {
                        if (a) {}
                        b = mySig
                    }
                }
            """.trimIndent().toSourceFile()
        )
        tester.fullParse()
        assert(tester.notationManager.hasNoErrors)
        assert(tester.notationManager.hasWarnings)
    }

    @Test
    fun localSignalTruncationTest() = runBlocking {
        val tester = ProjectTester(
            """
                module myModule (
                    input a,
                    output b[8]
                ) {
                    always {
                        sig mySig[8] = 9b0 // 9b0 is wider than 8 bits
                        if (a) {}
                        b = mySig
                    }
                }
            """.trimIndent().toSourceFile()
        )
        tester.fullParse()
        assert(tester.notationManager.hasNoErrors)
        assert(tester.notationManager.hasWarnings)
    }

    @Test
    fun localSignalNestedNameTest() = runBlocking {
        val tester = ProjectTester(
            """
                module myModule (
                    input a,
                    output b[8]
                ) {
                    always {
                        sig mySig[8] = 8haa
                        if (a) {
                            sig mySig2[8] = 8hbb
                            b = mySig2
                        }
                        b = mySig
                    }
                }
            """.trimIndent().toSourceFile()
        )
        tester.fullParse()
        assert(tester.notationManager.hasNoIssues) { tester.notationManager.getReport() }
    }

    @Test
    fun localSignalReadBeforeWrittenTest() = runBlocking {
        val tester = ProjectTester(
            """
                module myModule (
                    input a,
                    output b[8]
                ) {
                    always {
                        if (a) {}
                        sig mySig[8]
                        b = mySig
                    }
                }
            """.trimIndent().toSourceFile()
        )
        tester.fullParse()
        assert(tester.notationManager.hasErrors) { tester.notationManager.getReport() }
    }

    @Test
    fun localSignalLocalWriteTest() = runBlocking {
        val tester = ProjectTester(
            """
                module myModule (
                    input a,
                    output b[8]
                ) {
                    always {
                        if (a) {}
                        sig mySig[8]
                        mySig = 8haa
                        b = mySig
                    }
                }
            """.trimIndent().toSourceFile()
        )
        tester.fullParse()
        assert(tester.notationManager.hasNoIssues) { tester.notationManager.getReport() }
    }

    @Test
    fun localSignalDriverTest() = runBlocking {
        val tester = ProjectTester(
            """
                module binToDec #(
                    DIGITS = 4 : DIGITS > 0 && DIGITS < 20,           // limited by 64 bit constants in the tools
                    LEADING_ZEROS = 0 : LEADING_ZEROS == 0 || LEADING_ZEROS == 1
                )(
                    input value[${'$'}clog2(${'$'}pow(10, DIGITS))],            // minimum number of bits for DIGITS
                    output digits[DIGITS][4]                          // decimal output
                ) {
                    always {
                        repeat(i, DIGITS)                                   // for all digits
                            digits[i] = d11                                 // default to invalid number

                        sig remainder[${'$'}width(value)] = value                // initialize remainder
                        sig blank = !LEADING_ZEROS                          // set blank zero flag

                        if (value < ${'$'}pow(10, DIGITS)) {                     // if can be displayed
                            repeat(j, DIGITS, DIGITS-1, -1) {               // for each digit
                                sig scale[${'$'}width(value)] = ${'$'}pow(10, j)      // get the scale for the digit

                                if (remainder < scale) {                    // if this digit is 0
                                    if (j != 0 && blank)                    // use 10 for blank
                                        digits[j] = 10
                                    else                                    // or 0 for zero
                                        digits[j] = 0
                                } else {                                    // digit is 1-9
                                    blank = 0                               // don't blank future zeros
                                    sig sub_value[${'$'}width(value)] = 0        // default to no subtraction
                                    repeat(i, 8, 9, -1) {                   // for each possible value (starting from 9)
                                        if (remainder < (i+1) * scale) {    // if remainder is less than value
                                            digits[j] = i                   // set digit to this value
                                            sub_value = i * scale           // set subtraction value
                                        }
                                    }
                                    remainder = remainder - sub_value       // subtract off last digit
                                }
                            }
                        }
                    }
                }
            """.trimIndent().toSourceFile()
        )
        tester.fullParse()
        assert(tester.notationManager.hasNoIssues) { tester.notationManager.getReport() }
    }
}