package me.yvesz.jadxmcp

import me.yvesz.jadxmcp.JadxDecompileManager
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@SpringBootApplication
class JadxMcpApplication {
    
    private val logger = LoggerFactory.getLogger(JadxMcpApplication::class.java)

    @Value("${jadxmcp.input}")
    private lateinit var inputPath: String

    @Value("${jadxmcp.output}")
    private lateinit var outputPath: String

    lateinit var jadxDecompiler: JadxDecompiler

    @Bean
    fun initializeJadxDecompiler() {
        logger.info("Initializing JadxDecompiler with input: $inputPath, output: $outputPath")
        val inputFilePath = Paths.get(inputPath)
        val outputDirPath = Paths.get(outputPath)
        
        jadxDecompiler = JadxDecompiler.getInstance(inputFilePath, outputDirPath)
            ?: throw IllegalStateException("Failed to initialize JadxDecompiler")
        
        logger.info("JadxDecompiler initialized successfully")
    }

}

fun main(args: Array<String>) {
    runApplication<JadxMcpApplication>(*args)
}


