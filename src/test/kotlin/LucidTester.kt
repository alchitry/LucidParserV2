import com.alchitry.labs.parsers.lucidv2.context.LucidModuleContext
import com.alchitry.labs.parsers.lucidv2.context.ProjectContext
import com.alchitry.labs.parsers.lucidv2.grammar.LucidLexer
import com.alchitry.labs.parsers.lucidv2.grammar.LucidParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

class LucidTester(text: String) :
    LucidParser(
        CommonTokenStream(
            LucidLexer(
                CharStreams.fromString(text)
            ).also { it.removeErrorListeners() })
    ) {

    val context = LucidModuleContext(ProjectContext(), null)

    init {
        (tokenStream.tokenSource as LucidLexer).addErrorListener(context.errorCollector)
        removeErrorListeners()
        addErrorListener(context.errorCollector)

        context.addToParser(this)
    }

    val hasNoIssues: Boolean get() = context.errorCollector.hasNoIssues
    val hasNoErrors: Boolean get() = context.errorCollector.hasNoErrors
    val hasNoWarnings: Boolean get() = context.errorCollector.hasNoWarnings
    val hasNoSyntaxIssues: Boolean get() = context.errorCollector.hasNoSyntaxIssues
    val hasErrors: Boolean get() = context.errorCollector.hasErrors
    val hasWarnings: Boolean get() = context.errorCollector.hasWarnings
    val hasSyntaxIssues: Boolean get() = context.errorCollector.hasSyntaxIssues
}