package me.yvesz.jadxmcp.decompiler

import me.yvesz.jadxmcp.config.JadxProperties
import jadx.api.JadxArgs
import jadx.api.JadxDecompiler
import org.springframework.stereotype.Component
import jakarta.annotation.PostConstruct
import java.nio.file.Files
import java.nio.file.Path
import java.io.File

@Component
class JadxDecompilerManager(private val jadxProperties: JadxProperties) {
    private lateinit var jadxDecompiler: JadxDecompiler
    
    @PostConstruct
    fun init() {
        
        val jadxArgs = JadxArgs().apply {
            setInputFile(File(jadxProperties.inputPath))
            outDir = File(jadxProperties.outputDir).apply {
                parentFile?.takeUnless { parentFile -> parentFile.exists() }?.mkdirs()
            }
        }
        
        jadxDecompiler = JadxDecompiler(jadxArgs)
        jadxDecompiler.load()
    }
    
    fun getJadxDecompiler(): JadxDecompiler {
        return jadxDecompiler
    }
    
    companion object {
        private var instance: JadxDecompilerManager? = null
        
        fun getInstance(): JadxDecompilerManager {
            if (instance == null) {
                throw IllegalStateException("JadxDecompilerManager has not been initialized yet")
            }
            return instance!!
        }
        
        fun setInstance(manager: JadxDecompilerManager) {
            instance = manager
        }
    }
}

