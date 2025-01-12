package build

import com.alchitry.labs2.Settings
import com.alchitry.labs2.hardware.Board
import com.alchitry.labs2.project.Project
import com.alchitry.labs2.project.ProjectTemplate
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class FullBuildTests {
    @field:TempDir
    lateinit var tempFolder: File

    val templates: List<ProjectTemplate>

    init {
        System.setProperty("app.dir", "../")
        System.setProperty("alchitry.bin.dir", "tools/linux/x64/bin")
        templates = ProjectTemplate.getTemplates()
    }

    suspend fun buildAllProjectsForBoard(board: Board) {
        val validTemplates = templates.filter { it.boards.contains(board) }
        validTemplates.forEach { template ->
            template.instantiate("${template.name}_${board.name}", tempFolder, board)
            println("Building ${template.name} for $board.")
            Project.withBuildLock {
                assert(Project.current!!.build()) { "Failed to build ${template.name} for $board!" }
            }
            assert(Project.current!!.binFile.exists()) { "Bin file does not exist for ${template.name} with $board!" }
        }
    }

    @Test
    fun buildAllAuProjects() = runBlocking {
        buildAllProjectsForBoard(Board.AlchitryAu)
    }

    @Test
    fun buildAllCuProjectsWithIceCube() = runBlocking {
        val default = Settings.useIceCube
        try {
            Settings.useIceCube = true
            buildAllProjectsForBoard(Board.AlchitryCu)
        } finally {
            Settings.useIceCube = default
        }
    }

    @Test
    fun buildAllCuProjectsWithYosys() = runBlocking {
        val default = Settings.useIceCube
        try {
            Settings.useIceCube = false
            buildAllProjectsForBoard(Board.AlchitryCu)
        } finally {
            Settings.useIceCube = default
        }
    }
}