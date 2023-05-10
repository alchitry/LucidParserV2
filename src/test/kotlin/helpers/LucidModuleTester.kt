package helpers

import com.alchitry.labs.com.alchitry.labs.parsers.lucidv2.ErrorCollector
import com.alchitry.labs.parsers.lucidv2.context.LucidGlobalContext
import com.alchitry.labs.parsers.lucidv2.context.LucidModuleContext
import com.alchitry.labs.parsers.lucidv2.context.LucidModuleTypeContext
import com.alchitry.labs.parsers.lucidv2.context.ProjectContext
import com.alchitry.labs.parsers.lucidv2.grammar.LucidLexer
import com.alchitry.labs.parsers.lucidv2.grammar.LucidParser
import com.alchitry.labs.parsers.lucidv2.grammar.LucidParser.SourceContext
import com.alchitry.labs.parsers.lucidv2.parsers.ParseStage
import com.alchitry.labs.parsers.lucidv2.signals.Module
import com.alchitry.labs.parsers.lucidv2.signals.ModuleInstance
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import kotlin.test.assertNotNull

class LucidModuleTester(val text: String) {
    val project = ProjectContext()

    fun parseText(errorCollector: ErrorCollector): SourceContext {
        val parser = LucidParser(
            CommonTokenStream(
                LucidLexer(
                    CharStreams.fromString(text)
                ).also { it.removeErrorListeners() })
        ).apply {
            (tokenStream.tokenSource as LucidLexer).addErrorListener(errorCollector)
            removeErrorListeners()
            addErrorListener(errorCollector)
        }

        return parser.source()
    }

    fun globalParse(
        errorCollector: ErrorCollector = ErrorCollector(),
        tree: SourceContext = parseText(errorCollector)
    ) {
        assert(errorCollector.hasNoIssues)

        val globalContext = LucidGlobalContext(project, errorCollector)
        globalContext.walk(tree)

        assert(errorCollector.hasNoIssues)
    }

    fun moduleTypeParse(
        errorCollector: ErrorCollector = ErrorCollector(),
        tree: SourceContext = parseText(errorCollector)
    ): Module {
        assert(errorCollector.hasNoIssues)

        val moduleTypeContext = LucidModuleTypeContext(project, errorCollector)
        val module = moduleTypeContext.extract(tree)

        assert(errorCollector.hasNoIssues)
        assertNotNull(module)
        return module
    }

    /**
     * Performs a full parse on the file.
     * @param errorCollector if null, the function will automatically check for errors. If provided, you should check
     * for errors after calling this function.
     */
    fun fullParse(errorCollector: ErrorCollector? = ErrorCollector()): LucidModuleContext {
        val errors = errorCollector ?: ErrorCollector()
        val tree = parseText(errors)

        globalParse(errors, tree)
        val module = moduleTypeParse(errors, tree)

        val moduleInstance = ModuleInstance(project, "top", module, mapOf())

        val moduleContext =
            LucidModuleContext(project, ParseStage.ModuleInternals, moduleInstance, null, errors)

        val stages = listOf(
            ParseStage.ModuleInternals,
            ParseStage.Drivers
        )

        stages.forEach {
            moduleContext.stage = it
            moduleContext.walk(tree)

            if (errorCollector == null)
                assert(errors.hasNoIssues)
        }

        return moduleContext
    }
}