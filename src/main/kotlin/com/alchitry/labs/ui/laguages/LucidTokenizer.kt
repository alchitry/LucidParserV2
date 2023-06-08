package com.alchitry.labs.ui.laguages

import com.alchitry.labs.parsers.grammar.LucidLexer
import com.alchitry.labs.ui.code_editor.EditorTokenizer
import com.alchitry.labs.ui.code_editor.StyleToken
import com.alchitry.labs.ui.code_editor.toStyleToken
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CommonTokenStream

class LucidTokenizer : EditorTokenizer {
    override fun getTokens(stream: CharStream): List<StyleToken> {
        val lexer = LucidLexer(stream)
        lexer.removeErrorListeners()
        val tokenStream = CommonTokenStream(lexer)
        tokenStream.fill()

        return tokenStream.tokens.map { token ->
            val style = when (token.type) {
                LucidLexer.SIGNED -> LucidStyle.signed
                LucidLexer.COMMENT, LucidLexer.BLOCK_COMMENT -> LucidStyle.comment
                LucidLexer.HEX, LucidLexer.BIN, LucidLexer.DEC, LucidLexer.INT -> LucidStyle.value
                LucidLexer.REAL -> LucidStyle.realValue
                LucidLexer.STRING -> LucidStyle.string
                LucidLexer.CONST_ID -> LucidStyle.constant
                LucidLexer.SPACE_ID -> LucidStyle.namespace
                LucidLexer.FUNCTION_ID -> LucidStyle.function
                else -> when (token.text) {
                    "input", "output", "inout", "dff", "sig", "const", "struct", "enum" -> LucidStyle.variable
                    "always", "if", "else", "repeat", "case", "test", "fun" -> LucidStyle.keyword
                    "module", "global", "testBench" -> LucidStyle.module
                    "default" -> LucidStyle.defaultKeyword
                    else ->
                        if (operatorRegex.matches(token.text)) LucidStyle.operator else null
                }
            }
            token.toStyleToken(style)
        }
    }

    companion object {
        private val operatorRegex = Regex("([*!~+#\\-/:@|&{}?^=><\\]\\[,();]+)|(c\\{|x\\{)")
    }
}