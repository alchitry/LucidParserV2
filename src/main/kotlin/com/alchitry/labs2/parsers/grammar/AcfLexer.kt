// Generated from java-escape by ANTLR 4.13.0
package com.alchitry.labs2.parsers.grammar
import com.alchitry.kotlinmultiplatform.asCharArray
import org.antlr.v4.kotlinruntime.CharStream
import org.antlr.v4.kotlinruntime.Lexer
import org.antlr.v4.kotlinruntime.Vocabulary
import org.antlr.v4.kotlinruntime.VocabularyImpl
import org.antlr.v4.kotlinruntime.atn.ATN
import org.antlr.v4.kotlinruntime.atn.ATNDeserializer
import org.antlr.v4.kotlinruntime.atn.LexerATNSimulator
import org.antlr.v4.kotlinruntime.atn.PredictionContextCache
import org.antlr.v4.kotlinruntime.dfa.DFA

class AcfLexer(val _input: CharStream) : Lexer(_input) {
	// TODO Verify the runtime version is correct

    override val ruleNames: Array<String> = Rules.values().map { it.name }.toTypedArray()

    override val grammarFileName: String = "Acf.g4"

    override val atn: ATN = AcfLexer.ATN

    override val vocabulary: Vocabulary = AcfLexer.VOCABULARY

	companion object {
        val decisionToDFA: Array<DFA>
		val sharedContextCache = PredictionContextCache()

        private val LITERAL_NAMES: List<String?> = listOf(
            null, "'pin'", "','",
            "'{'", "'}'", "'('",
            "')'", "'.'", "'['",
            "']'", "'pullup'", "'pulldown'",
            "';'"
        )
        private val SYMBOLIC_NAMES: List<String?> = listOf(
            null, null, null, null,
            null, null, null, null,
            null, null, "PULLUP",
            "PULLDOWN", "SEMICOLON",
            "NL", "FREQ_UNIT",
            "BASIC_NAME", "REAL",
            "INT", "BLOCK_COMMENT",
            "COMMENT", "WS"
        )

		val VOCABULARY = VocabularyImpl(LITERAL_NAMES.toTypedArray(), SYMBOLIC_NAMES.toTypedArray())

        val tokenNames: Array<String?> = Array<String?>(SYMBOLIC_NAMES.size) {
            var el = VOCABULARY.getLiteralName(it)
            if (el == null) {
                el = VOCABULARY.getSymbolicName(it)
            }

            if (el == null) {
                el = "<INVALID>"
            }
            el
        }

        private const val serializedATN: String =
            "\u0004\u0000\u0014\u010a\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002\u0008\u0007\u0008\u0002\u0009\u0007\u0009\u0002\u000a\u0007\u000a\u0002\u000b\u0007\u000b\u0002\u000c\u0007\u000c\u0002\u000d\u0007\u000d\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e\u0002\u001f\u0007\u001f\u0002\u0020\u0007\u0020\u0002\u0021\u0007\u0021\u0002\u0022\u0007\u0022\u0002\u0023\u0007\u0023\u0002\u0024\u0007\u0024\u0002\u0025\u0007\u0025\u0002\u0026\u0007\u0026\u0002\u0027\u0007\u0027\u0002\u0028\u0007\u0028\u0002\u0029\u0007\u0029\u0002\u002a\u0007\u002a\u0002\u002b\u0007\u002b\u0002\u002c\u0007\u002c\u0002\u002d\u0007\u002d\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0008\u0001\u0008\u0001\u0009\u0001\u0009\u0001\u0009\u0001\u0009\u0001\u0009\u0001\u0009\u0001\u0009\u0001\u000a\u0001\u000a\u0001\u000a\u0001\u000a\u0001\u000a\u0001\u000a\u0001\u000a\u0001\u000a\u0001\u000a\u0001\u000b\u0001\u000b\u0001\u000c\u0001\u000c\u0001\u000c\u0003\u000c\u0087\u0008\u000c\u0001\u000d\u0001\u000d\u0001\u000d\u0003\u000d\u008c\u0008\u000d\u0001\u000d\u0001\u000d\u0001\u000d\u0001\u000e\u0001\u000e\u0005\u000e\u0093\u0008\u000e\u000a\u000e\u000c\u000e\u0096\u0009\u000e\u0001\u000f\u0005\u000f\u0099\u0008\u000f\u000a\u000f\u000c\u000f\u009c\u0009\u000f\u0001\u000f\u0001\u000f\u0004\u000f\u00a0\u0008\u000f\u000b\u000f\u000c\u000f\u00a1\u0001\u000f\u0004\u000f\u00a5\u0008\u000f\u000b\u000f\u000c\u000f\u00a6\u0001\u000f\u0001\u000f\u0005\u000f\u00ab\u0008\u000f\u000a\u000f\u000c\u000f\u00ae\u0009\u000f\u0003\u000f\u00b0\u0008\u000f\u0001\u0010\u0004\u0010\u00b3\u0008\u0010\u000b\u0010\u000c\u0010\u00b4\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0005\u0011\u00bb\u0008\u0011\u000a\u0011\u000c\u0011\u00be\u0009\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0005\u0012\u00c9\u0008\u0012\u000a\u0012\u000c\u0012\u00cc\u0009\u0012\u0001\u0012\u0001\u0012\u0001\u0013\u0004\u0013\u00d1\u0008\u0013\u000b\u0013\u000c\u0013\u00d2\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0016\u0001\u0016\u0001\u0017\u0001\u0017\u0001\u0018\u0001\u0018\u0001\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0001\u001b\u0001\u001b\u0001\u001c\u0001\u001c\u0001\u001d\u0001\u001d\u0001\u001e\u0001\u001e\u0001\u001f\u0001\u001f\u0001\u0020\u0001\u0020\u0001\u0021\u0001\u0021\u0001\u0022\u0001\u0022\u0001\u0023\u0001\u0023\u0001\u0024\u0001\u0024\u0001\u0025\u0001\u0025\u0001\u0026\u0001\u0026\u0001\u0027\u0001\u0027\u0001\u0028\u0001\u0028\u0001\u0029\u0001\u0029\u0001\u002a\u0001\u002a\u0001\u002b\u0001\u002b\u0001\u002c\u0001\u002c\u0001\u002d\u0001\u002d\u0001\u00bc\u0000\u002e\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004\u0009\u0005\u000b\u0006\u000d\u0007\u000f\u0008\u0011\u0009\u0013\u000a\u0015\u000b\u0017\u000c\u0019\u000d\u001b\u000e\u001d\u000f\u001f\u0010\u0021\u0011\u0023\u0012\u0025\u0013\u0027\u0014\u0029\u0000\u002b\u0000\u002d\u0000\u002f\u0000\u0031\u0000\u0033\u0000\u0035\u0000\u0037\u0000\u0039\u0000\u003b\u0000\u003d\u0000\u003f\u0000\u0041\u0000\u0043\u0000\u0045\u0000\u0047\u0000\u0049\u0000\u004b\u0000\u004d\u0000\u004f\u0000\u0051\u0000\u0053\u0000\u0055\u0000\u0057\u0000\u0059\u0000\u005b\u0000\u0001\u0000\u001f\u0002\u0000\u000a\u000a\u000d\u000d\u0002\u0000\u0041\u005a\u0061\u007a\u0004\u0000\u0030\u0039\u0041\u005a\u005f\u005f\u0061\u007a\u0001\u0000\u0030\u0039\u0002\u0000\u0009\u0009\u0020\u0020\u0002\u0000\u0041\u0041\u0061\u0061\u0002\u0000\u0042\u0042\u0062\u0062\u0002\u0000\u0043\u0043\u0063\u0063\u0002\u0000\u0044\u0044\u0064\u0064\u0002\u0000\u0045\u0045\u0065\u0065\u0002\u0000\u0046\u0046\u0066\u0066\u0002\u0000\u0047\u0047\u0067\u0067\u0002\u0000\u0048\u0048\u0068\u0068\u0002\u0000\u0049\u0049\u0069\u0069\u0002\u0000\u004a\u004a\u006a\u006a\u0002\u0000\u004b\u004b\u006b\u006b\u0002\u0000\u004c\u004c\u006c\u006c\u0002\u0000\u004d\u004d\u006d\u006d\u0002\u0000\u004e\u004e\u006e\u006e\u0002\u0000\u004f\u004f\u006f\u006f\u0002\u0000\u0050\u0050\u0070\u0070\u0002\u0000\u0051\u0051\u0071\u0071\u0002\u0000\u0052\u0052\u0072\u0072\u0002\u0000\u0053\u0053\u0073\u0073\u0002\u0000\u0054\u0054\u0074\u0074\u0002\u0000\u0055\u0055\u0075\u0075\u0002\u0000\u0056\u0056\u0076\u0076\u0002\u0000\u0057\u0057\u0077\u0077\u0002\u0000\u0058\u0058\u0078\u0078\u0002\u0000\u0059\u0059\u0079\u0079\u0002\u0000\u005a\u005a\u007a\u007a\u00fd\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\u0009\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\u000d\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019\u0001\u0000\u0000\u0000\u0000\u001b\u0001\u0000\u0000\u0000\u0000\u001d\u0001\u0000\u0000\u0000\u0000\u001f\u0001\u0000\u0000\u0000\u0000\u0021\u0001\u0000\u0000\u0000\u0000\u0023\u0001\u0000\u0000\u0000\u0000\u0025\u0001\u0000\u0000\u0000\u0000\u0027\u0001\u0000\u0000\u0000\u0001\u005d\u0001\u0000\u0000\u0000\u0003\u0061\u0001\u0000\u0000\u0000\u0005\u0063\u0001\u0000\u0000\u0000\u0007\u0065\u0001\u0000\u0000\u0000\u0009\u0067\u0001\u0000\u0000\u0000\u000b\u0069\u0001\u0000\u0000\u0000\u000d\u006b\u0001\u0000\u0000\u0000\u000f\u006d\u0001\u0000\u0000\u0000\u0011\u006f\u0001\u0000\u0000\u0000\u0013\u0071\u0001\u0000\u0000\u0000\u0015\u0078\u0001\u0000\u0000\u0000\u0017\u0081\u0001\u0000\u0000\u0000\u0019\u0086\u0001\u0000\u0000\u0000\u001b\u008b\u0001\u0000\u0000\u0000\u001d\u0090\u0001\u0000\u0000\u0000\u001f\u00af\u0001\u0000\u0000\u0000\u0021\u00b2\u0001\u0000\u0000\u0000\u0023\u00b6\u0001\u0000\u0000\u0000\u0025\u00c4\u0001\u0000\u0000\u0000\u0027\u00d0\u0001\u0000\u0000\u0000\u0029\u00d6\u0001\u0000\u0000\u0000\u002b\u00d8\u0001\u0000\u0000\u0000\u002d\u00da\u0001\u0000\u0000\u0000\u002f\u00dc\u0001\u0000\u0000\u0000\u0031\u00de\u0001\u0000\u0000\u0000\u0033\u00e0\u0001\u0000\u0000\u0000\u0035\u00e2\u0001\u0000\u0000\u0000\u0037\u00e4\u0001\u0000\u0000\u0000\u0039\u00e6\u0001\u0000\u0000\u0000\u003b\u00e8\u0001\u0000\u0000\u0000\u003d\u00ea\u0001\u0000\u0000\u0000\u003f\u00ec\u0001\u0000\u0000\u0000\u0041\u00ee\u0001\u0000\u0000\u0000\u0043\u00f0\u0001\u0000\u0000\u0000\u0045\u00f2\u0001\u0000\u0000\u0000\u0047\u00f4\u0001\u0000\u0000\u0000\u0049\u00f6\u0001\u0000\u0000\u0000\u004b\u00f8\u0001\u0000\u0000\u0000\u004d\u00fa\u0001\u0000\u0000\u0000\u004f\u00fc\u0001\u0000\u0000\u0000\u0051\u00fe\u0001\u0000\u0000\u0000\u0053\u0100\u0001\u0000\u0000\u0000\u0055\u0102\u0001\u0000\u0000\u0000\u0057\u0104\u0001\u0000\u0000\u0000\u0059\u0106\u0001\u0000\u0000\u0000\u005b\u0108\u0001\u0000\u0000\u0000\u005d\u005e\u0005\u0070\u0000\u0000\u005e\u005f\u0005\u0069\u0000\u0000\u005f\u0060\u0005\u006e\u0000\u0000\u0060\u0002\u0001\u0000\u0000\u0000\u0061\u0062\u0005\u002c\u0000\u0000\u0062\u0004\u0001\u0000\u0000\u0000\u0063\u0064\u0005\u007b\u0000\u0000\u0064\u0006\u0001\u0000\u0000\u0000\u0065\u0066\u0005\u007d\u0000\u0000\u0066\u0008\u0001\u0000\u0000\u0000\u0067\u0068\u0005\u0028\u0000\u0000\u0068\u000a\u0001\u0000\u0000\u0000\u0069\u006a\u0005\u0029\u0000\u0000\u006a\u000c\u0001\u0000\u0000\u0000\u006b\u006c\u0005\u002e\u0000\u0000\u006c\u000e\u0001\u0000\u0000\u0000\u006d\u006e\u0005\u005b\u0000\u0000\u006e\u0010\u0001\u0000\u0000\u0000\u006f\u0070\u0005\u005d\u0000\u0000\u0070\u0012\u0001\u0000\u0000\u0000\u0071\u0072\u0005\u0070\u0000\u0000\u0072\u0073\u0005\u0075\u0000\u0000\u0073\u0074\u0005\u006c\u0000\u0000\u0074\u0075\u0005\u006c\u0000\u0000\u0075\u0076\u0005\u0075\u0000\u0000\u0076\u0077\u0005\u0070\u0000\u0000\u0077\u0014\u0001\u0000\u0000\u0000\u0078\u0079\u0005\u0070\u0000\u0000\u0079\u007a\u0005\u0075\u0000\u0000\u007a\u007b\u0005\u006c\u0000\u0000\u007b\u007c\u0005\u006c\u0000\u0000\u007c\u007d\u0005\u0064\u0000\u0000\u007d\u007e\u0005\u006f\u0000\u0000\u007e\u007f\u0005\u0077\u0000\u0000\u007f\u0080\u0005\u006e\u0000\u0000\u0080\u0016\u0001\u0000\u0000\u0000\u0081\u0082\u0005\u003b\u0000\u0000\u0082\u0018\u0001\u0000\u0000\u0000\u0083\u0084\u0005\u000d\u0000\u0000\u0084\u0087\u0005\u000a\u0000\u0000\u0085\u0087\u0007\u0000\u0000\u0000\u0086\u0083\u0001\u0000\u0000\u0000\u0086\u0085\u0001\u0000\u0000\u0000\u0087\u001a\u0001\u0000\u0000\u0000\u0088\u008c\u0003\u0041\u0020\u0000\u0089\u008c\u0003\u003d\u001e\u0000\u008a\u008c\u0003\u0035\u001a\u0000\u008b\u0088\u0001\u0000\u0000\u0000\u008b\u0089\u0001\u0000\u0000\u0000\u008b\u008a\u0001\u0000\u0000\u0000\u008b\u008c\u0001\u0000\u0000\u0000\u008c\u008d\u0001\u0000\u0000\u0000\u008d\u008e\u0003\u0037\u001b\u0000\u008e\u008f\u0003\u005b\u002d\u0000\u008f\u001c\u0001\u0000\u0000\u0000\u0090\u0094\u0007\u0001\u0000\u0000\u0091\u0093\u0007\u0002\u0000\u0000\u0092\u0091\u0001\u0000\u0000\u0000\u0093\u0096\u0001\u0000\u0000\u0000\u0094\u0092\u0001\u0000\u0000\u0000\u0094\u0095\u0001\u0000\u0000\u0000\u0095\u001e\u0001\u0000\u0000\u0000\u0096\u0094\u0001\u0000\u0000\u0000\u0097\u0099\u0007\u0003\u0000\u0000\u0098\u0097\u0001\u0000\u0000\u0000\u0099\u009c\u0001\u0000\u0000\u0000\u009a\u0098\u0001\u0000\u0000\u0000\u009a\u009b\u0001\u0000\u0000\u0000\u009b\u009d\u0001\u0000\u0000\u0000\u009c\u009a\u0001\u0000\u0000\u0000\u009d\u009f\u0005\u002e\u0000\u0000\u009e\u00a0\u0007\u0003\u0000\u0000\u009f\u009e\u0001\u0000\u0000\u0000\u00a0\u00a1\u0001\u0000\u0000\u0000\u00a1\u009f\u0001\u0000\u0000\u0000\u00a1\u00a2\u0001\u0000\u0000\u0000\u00a2\u00b0\u0001\u0000\u0000\u0000\u00a3\u00a5\u0007\u0003\u0000\u0000\u00a4\u00a3\u0001\u0000\u0000\u0000\u00a5\u00a6\u0001\u0000\u0000\u0000\u00a6\u00a4\u0001\u0000\u0000\u0000\u00a6\u00a7\u0001\u0000\u0000\u0000\u00a7\u00a8\u0001\u0000\u0000\u0000\u00a8\u00ac\u0005\u002e\u0000\u0000\u00a9\u00ab\u0007\u0003\u0000\u0000\u00aa\u00a9\u0001\u0000\u0000\u0000\u00ab\u00ae\u0001\u0000\u0000\u0000\u00ac\u00aa\u0001\u0000\u0000\u0000\u00ac\u00ad\u0001\u0000\u0000\u0000\u00ad\u00b0\u0001\u0000\u0000\u0000\u00ae\u00ac\u0001\u0000\u0000\u0000\u00af\u009a\u0001\u0000\u0000\u0000\u00af\u00a4\u0001\u0000\u0000\u0000\u00b0\u0020\u0001\u0000\u0000\u0000\u00b1\u00b3\u0007\u0003\u0000\u0000\u00b2\u00b1\u0001\u0000\u0000\u0000\u00b3\u00b4\u0001\u0000\u0000\u0000\u00b4\u00b2\u0001\u0000\u0000\u0000\u00b4\u00b5\u0001\u0000\u0000\u0000\u00b5\u0022\u0001\u0000\u0000\u0000\u00b6\u00b7\u0005\u002f\u0000\u0000\u00b7\u00b8\u0005\u002a\u0000\u0000\u00b8\u00bc\u0001\u0000\u0000\u0000\u00b9\u00bb\u0009\u0000\u0000\u0000\u00ba\u00b9\u0001\u0000\u0000\u0000\u00bb\u00be\u0001\u0000\u0000\u0000\u00bc\u00bd\u0001\u0000\u0000\u0000\u00bc\u00ba\u0001\u0000\u0000\u0000\u00bd\u00bf\u0001\u0000\u0000\u0000\u00be\u00bc\u0001\u0000\u0000\u0000\u00bf\u00c0\u0005\u002a\u0000\u0000\u00c0\u00c1\u0005\u002f\u0000\u0000\u00c1\u00c2\u0001\u0000\u0000\u0000\u00c2\u00c3\u0006\u0011\u0000\u0000\u00c3\u0024\u0001\u0000\u0000\u0000\u00c4\u00c5\u0005\u002f\u0000\u0000\u00c5\u00c6\u0005\u002f\u0000\u0000\u00c6\u00ca\u0001\u0000\u0000\u0000\u00c7\u00c9\u0008\u0000\u0000\u0000\u00c8\u00c7\u0001\u0000\u0000\u0000\u00c9\u00cc\u0001\u0000\u0000\u0000\u00ca\u00c8\u0001\u0000\u0000\u0000\u00ca\u00cb\u0001\u0000\u0000\u0000\u00cb\u00cd\u0001\u0000\u0000\u0000\u00cc\u00ca\u0001\u0000\u0000\u0000\u00cd\u00ce\u0006\u0012\u0000\u0000\u00ce\u0026\u0001\u0000\u0000\u0000\u00cf\u00d1\u0007\u0004\u0000\u0000\u00d0\u00cf\u0001\u0000\u0000\u0000\u00d1\u00d2\u0001\u0000\u0000\u0000\u00d2\u00d0\u0001\u0000\u0000\u0000\u00d2\u00d3\u0001\u0000\u0000\u0000\u00d3\u00d4\u0001\u0000\u0000\u0000\u00d4\u00d5\u0006\u0013\u0001\u0000\u00d5\u0028\u0001\u0000\u0000\u0000\u00d6\u00d7\u0007\u0005\u0000\u0000\u00d7\u002a\u0001\u0000\u0000\u0000\u00d8\u00d9\u0007\u0006\u0000\u0000\u00d9\u002c\u0001\u0000\u0000\u0000\u00da\u00db\u0007\u0007\u0000\u0000\u00db\u002e\u0001\u0000\u0000\u0000\u00dc\u00dd\u0007\u0008\u0000\u0000\u00dd\u0030\u0001\u0000\u0000\u0000\u00de\u00df\u0007\u0009\u0000\u0000\u00df\u0032\u0001\u0000\u0000\u0000\u00e0\u00e1\u0007\u000a\u0000\u0000\u00e1\u0034\u0001\u0000\u0000\u0000\u00e2\u00e3\u0007\u000b\u0000\u0000\u00e3\u0036\u0001\u0000\u0000\u0000\u00e4\u00e5\u0007\u000c\u0000\u0000\u00e5\u0038\u0001\u0000\u0000\u0000\u00e6\u00e7\u0007\u000d\u0000\u0000\u00e7\u003a\u0001\u0000\u0000\u0000\u00e8\u00e9\u0007\u000e\u0000\u0000\u00e9\u003c\u0001\u0000\u0000\u0000\u00ea\u00eb\u0007\u000f\u0000\u0000\u00eb\u003e\u0001\u0000\u0000\u0000\u00ec\u00ed\u0007\u0010\u0000\u0000\u00ed\u0040\u0001\u0000\u0000\u0000\u00ee\u00ef\u0007\u0011\u0000\u0000\u00ef\u0042\u0001\u0000\u0000\u0000\u00f0\u00f1\u0007\u0012\u0000\u0000\u00f1\u0044\u0001\u0000\u0000\u0000\u00f2\u00f3\u0007\u0013\u0000\u0000\u00f3\u0046\u0001\u0000\u0000\u0000\u00f4\u00f5\u0007\u0014\u0000\u0000\u00f5\u0048\u0001\u0000\u0000\u0000\u00f6\u00f7\u0007\u0015\u0000\u0000\u00f7\u004a\u0001\u0000\u0000\u0000\u00f8\u00f9\u0007\u0016\u0000\u0000\u00f9\u004c\u0001\u0000\u0000\u0000\u00fa\u00fb\u0007\u0017\u0000\u0000\u00fb\u004e\u0001\u0000\u0000\u0000\u00fc\u00fd\u0007\u0018\u0000\u0000\u00fd\u0050\u0001\u0000\u0000\u0000\u00fe\u00ff\u0007\u0019\u0000\u0000\u00ff\u0052\u0001\u0000\u0000\u0000\u0100\u0101\u0007\u001a\u0000\u0000\u0101\u0054\u0001\u0000\u0000\u0000\u0102\u0103\u0007\u001b\u0000\u0000\u0103\u0056\u0001\u0000\u0000\u0000\u0104\u0105\u0007\u001c\u0000\u0000\u0105\u0058\u0001\u0000\u0000\u0000\u0106\u0107\u0007\u001d\u0000\u0000\u0107\u005a\u0001\u0000\u0000\u0000\u0108\u0109\u0007\u001e\u0000\u0000\u0109\u005c\u0001\u0000\u0000\u0000\u000d\u0000\u0086\u008b\u0094\u009a\u00a1\u00a6\u00ac\u00af\u00b4\u00bc\u00ca\u00d2\u0002\u0000\u0001\u0000\u0006\u0000\u0000"

		val ATN = ATNDeserializer().deserialize(serializedATN.asCharArray())
		init {
			decisionToDFA = Array<DFA>(ATN.numberOfDecisions, {
				DFA(ATN.getDecisionState(it)!!, it)
			})


		}
	}

    enum class Tokens(val id: Int) {
        T__0(1),
        T__1(2),
        T__2(3),
        T__3(4),
        T__4(5),
        T__5(6),
        T__6(7),
        T__7(8),
        T__8(9),
        PULLUP(10),
        PULLDOWN(11),
        SEMICOLON(12),
        NL(13),
        FREQ_UNIT(14),
        BASIC_NAME(15),
        REAL(16),
        INT(17),
        BLOCK_COMMENT(18),
        COMMENT(19),
        WS(20)
    }

	enum class Channels(val id: Int) {
		DEFAULT_TOKEN_CHANNEL(0),
		HIDDEN(1),
	}

	override val channelNames = Channels.values().map(Channels::name).toTypedArray()

	enum class Modes(val id: Int) {
		DEFAULT_MODE(0),
	}

	enum class Rules {
        T__0,
        T__1,
        T__2,
        T__3,
        T__4,
        T__5,
        T__6,
        T__7,
        T__8,
        PULLUP,
        PULLDOWN,
        SEMICOLON,
        NL,
        FREQ_UNIT,
        BASIC_NAME,
        REAL,
        INT,
        BLOCK_COMMENT,
        COMMENT,
        WS,
        A,
        B,
        C,
        D,
        E,
        F,
        G,
        H,
        I,
        J,
        K,
        L,
        M,
        N,
        O,
        P,
        Q,
        R,
        S,
        T,
        U,
        V,
        W,
        X,
        Y,
        Z
	}


	init {
		this.interpreter = LexerATNSimulator(this, ATN, decisionToDFA as Array<DFA?>, sharedContextCache)
	}

}