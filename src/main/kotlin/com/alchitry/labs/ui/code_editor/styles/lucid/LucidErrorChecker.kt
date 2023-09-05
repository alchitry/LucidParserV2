package com.alchitry.labs.ui.code_editor.styles.lucid

import com.alchitry.labs.parsers.EvalQueue
import com.alchitry.labs.parsers.errors.ErrorCollector
import com.alchitry.labs.parsers.grammar.LucidLexer
import com.alchitry.labs.parsers.grammar.LucidParser
import com.alchitry.labs.parsers.lucidv2.context.LucidModuleTypeContext
import com.alchitry.labs.parsers.lucidv2.context.LucidTestBenchContext
import com.alchitry.labs.parsers.lucidv2.types.ModuleInstance
import com.alchitry.labs.project.Board
import com.alchitry.labs.project.Project
import com.alchitry.labs.project.files.FileProvider
import com.alchitry.labs.project.files.SourceFile
import com.alchitry.labs.ui.code_editor.StyleToken
import com.alchitry.labs.ui.code_editor.styles.CodeErrorChecker
import com.alchitry.labs.ui.code_editor.toStyleToken
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import java.io.File

class LucidErrorChecker : CodeErrorChecker {
    override suspend fun checkText(text: String): List<StyleToken> {
        val project = Project("Testing", File("."), Board.AlchitryAu, emptySet(), emptySet(), emptySet())
        val errorCollector = ErrorCollector(SourceFile(FileProvider.DiskFile("alchitry_top.luc"), true))

        val parser = LucidParser(
            CommonTokenStream(
                LucidLexer(
                    CharStreams.fromString(text)
                ).also { it.removeErrorListeners() })
        ).apply {
            (tokenStream?.tokenSource as? LucidLexer)?.addErrorListener(errorCollector)
                ?: error("TokenSource was not a LucidLexer!")
            removeErrorListeners()
            addErrorListener(errorCollector)
        }

        val tree = parser.source()

        if (errorCollector.hasErrors) {
            return errorCollector.getAllNotations().map { it.toStyleToken() }
        }

        EvalQueue().use { evalQueue ->
            val moduleTypeContext = LucidModuleTypeContext(project, evalQueue, errorCollector)
            val module = moduleTypeContext.extract(tree)

            if (errorCollector.hasErrors) {
                return errorCollector.getAllNotations().map { it.toStyleToken() }
            }

            if (module != null) {
                val moduleInstance =
                    ModuleInstance("top", project, evalQueue, null, module, mapOf(), mapOf(), errorCollector)

                moduleInstance.initialWalk()

                if (errorCollector.hasErrors) {
                    return errorCollector.getAllNotations().map { it.toStyleToken() }
                }
            }

            val testBenchContext = LucidTestBenchContext(project, evalQueue, errorCollector)
            testBenchContext.walk(tree)

            if (errorCollector.hasErrors) {
                return errorCollector.getAllNotations().map { it.toStyleToken() }
            }

            return project.getTestBenches().flatMap { testBench ->
                testBench.initialWalk()
                testBench.context.errorCollector.getAllNotations().map { it.toStyleToken() }
            }
        }
    }
}