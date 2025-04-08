package me.yvesz.jadxmcp

import me.yvesz.jadxmcp.config.JadxDecompileManager
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.nio.file.Paths

@SpringBootApplication
class JadxMcpApplication {
    
    companion object {
        private val logger = LoggerFactory.getLogger(JadxMcpApplication::class.java)
        lateinit var jadxDecompiler: JadxDecompileManager
    }

    @Value("\${jadxmcp.input}")
    private lateinit var inputPath: String

    @Value("\${jadxmcp.output}")
    private lateinit var outputPath: String

    @Bean
    fun initializeJadxDecompiler() {
        logger.info("Initializing JadxDecompiler with input: $inputPath, output: $outputPath")
        val inputFilePath = Paths.get(inputPath)
        val outputDirPath = Paths.get(outputPath)
        
        jadxDecompiler = JadxDecompileManager
        JadxDecompileManager.getInstance(inputFilePath, outputDirPath)
            ?: throw IllegalStateException("Failed to initialize JadxDecompiler")
        
        logger.info("JadxDecompiler initialized successfully")
    }


}

fun main(args: Array<String>) {
    runApplication<JadxMcpApplication>(*args)
}


