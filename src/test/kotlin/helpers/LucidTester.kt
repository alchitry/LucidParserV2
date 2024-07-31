package helpers

import com.alchitry.labs2.parsers.ProjectContext
import com.alchitry.labs2.parsers.grammar.LucidLexer
import com.alchitry.labs2.parsers.grammar.LucidParser
import com.alchitry.labs2.parsers.grammar.LucidParser.SourceContext
import com.alchitry.labs2.parsers.hdl.lucid.context.LucidGlobalContext
import com.alchitry.labs2.parsers.hdl.lucid.context.LucidModuleTypeContext
import com.alchitry.labs2.parsers.hdl.lucid.context.LucidTestBenchContext
import com.alchitry.labs2.parsers.hdl.lucid.signals.snapshot.SimParent
import com.alchitry.labs2.parsers.hdl.types.Module
import com.alchitry.labs2.parsers.hdl.types.ModuleInstance
import com.alchitry.labs2.parsers.hdl.types.ModuleInstanceArray
import com.alchitry.labs2.parsers.notations.NotationCollector
import com.alchitry.labs2.parsers.notations.NotationManager
import com.alchitry.labs2.project.files.SourceFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream

class LucidTester(vararg val files: SourceFile) {
    val notationManager = NotationManager()
    val project = ProjectContext(notationManager)

    fun parseText(): List<Pair<SourceFile, SourceContext>> {
        return files.map { file ->
            val notationCollector = notationManager.getCollector(file)
            val parser = LucidParser(
                CommonTokenStream(
                    LucidLexer(
                        CharStreams.fromString(runBlocking { file.readText() })
                    ).also { it.removeErrorListeners() })
            ).apply {
                (tokenStream?.tokenSource as LucidLexer).addErrorListener(notationCollector)
                removeErrorListeners()
                addErrorListener(notationCollector)
            }

            file to parser.source()
        }
    }

    suspend fun globalParse(
        trees: List<Pair<SourceFile, SourceContext>> = parseText()
    ) {
        assert(notationManager.hasNoErrors) { notationManager.getReport() }

        trees.forEach {
            val globalContext = LucidGlobalContext(project, it.first)
            globalContext.walk(it.second)

            assert(notationManager.hasNoErrors) { notationManager.getReport() }
        }
    }

    private suspend fun testBenchParse(
        trees: List<Pair<SourceFile, SourceContext>> = parseText()
    ) {
        assert(notationManager.hasNoErrors) { notationManager.getReport() }

        trees.forEach {
            val testBenchContext = LucidTestBenchContext(project, it.first)
            testBenchContext.walk(it.second)

            assert(notationManager.hasNoErrors) { notationManager.getReport() }
        }
    }

    suspend fun moduleTypeParse(
        trees: List<Pair<SourceFile, SourceContext>> = parseText()
    ): List<Module> {
        assert(notationManager.hasNoErrors) { notationManager.getReport() }

        return trees.flatMap {
            val moduleTypeContext = LucidModuleTypeContext(project, it.first)
            val modules = moduleTypeContext.extract(it.second)

            assert(notationManager.hasNoErrors) { notationManager.getReport() }
            modules
        }
    }

    /**
     * Performs a full parse on the file.
     * It does not check for errors after the parse.
     */
    suspend fun fullParse(testing: Boolean = false): ModuleInstance {
        val trees = parseText()

        globalParse(trees)
        val modules = moduleTypeParse(trees)

        val moduleInstance = ModuleInstance("top", project, null, modules.first(), mapOf(), mapOf(), testing = testing)

        moduleInstance.initialWalk()

        return moduleInstance
    }

    suspend fun runFirstTestBench(): SimParent? {
        val tree = parseText()

        globalParse(tree)
        moduleTypeParse(tree)
        testBenchParse(tree)

        val testBenches = project.getTestBenches()

        val testBench = testBenches.first()
        testBench.initialWalk()
        val tests = testBench.getTestBlocks()

        val test = tests.first()
        return testBench.runTest(test.name).also {
            test.context.notationCollector.assertNoIssues()
        }
    }

    suspend fun runTestBenches() {
        val tree = parseText()

        globalParse(tree)
        moduleTypeParse(tree)
        testBenchParse(tree)

        val testBenches = project.getTestBenches()

        testBenches.forEach { testBench ->
            testBench.initialWalk()
        }

        assert(notationManager.hasNoErrors) { notationManager.getReport() }

        testBenches.forEach { testBench ->
            val tests = testBench.getTestBlocks()
            tests.forEach {
                testBench.runTest(it.name)
                it.context.notationCollector.assertNoIssues()
            }
        }
    }

    private suspend fun runTest(testBench: String, test: String) {
        val tree = parseText()

        globalParse(tree)
        moduleTypeParse(tree)
        testBenchParse(tree)

        val bench = project.getTestBenches().first { it.name == testBench }

        bench.initialWalk()

        assert(notationManager.hasNoErrors) { notationManager.getReport() }

        bench.runTest(test)
        bench.context.notationCollector.assertNoIssues()
    }

    suspend fun parallelRunTestBenches(notationCollector: NotationCollector? = null) {
        val tree = parseText()

        globalParse(tree)
        moduleTypeParse(tree)
        testBenchParse(tree)

        val testBenches = project.getTestBenches()

        testBenches.forEach { testBench ->
            testBench.initialWalk()
        }

        assert(notationManager.hasNoErrors) { notationManager.getReport() }

        coroutineScope {
            testBenches.forEach { testBench ->
                val tests = testBench.getTestBlocks()
                tests.forEach {
                    val tester = LucidTester(*files)
                    launch(Dispatchers.Default) {
                        tester.runTest(testBench.name, it.name)
                    }
                }
            }
        }
    }

    suspend fun getVerilog(): Map<String, String> {
        val topInstance = fullParse()
        assertNoIssues()
        val instances = mutableMapOf<String, ModuleInstance>()
        fun add(instance: ModuleInstance) {
            instances[instance.parameterizedModuleName] = instance
            instance.context.types.moduleInstances.values.forEach { moduleInstanceOrArray ->
                when (moduleInstanceOrArray) {
                    is ModuleInstance -> add(moduleInstanceOrArray)
                    is ModuleInstanceArray -> moduleInstanceOrArray.modules.forEach { add(it) }
                }
            }
        }
        add(topInstance)
        return instances.mapValues {
            assert(project.notationManager.hasNoErrors) { project.notationManager.getReport() }
            it.value.context.convertToVerilog() ?: error("Missing verilog for ${it.key}")
        }.also { assert(project.notationManager.hasNoErrors) { project.notationManager.getReport() } }
    }

    fun assertNoErrors() = notationManager.assertNoErrors()
    fun assertNoIssues() = notationManager.assertNoIssues()
}