package me.yvesz.jadxmcp

import me.yvesz.jadxmcp.beans.JadxDecompiler
import me.yvesz.jadxmcp.config.JadxDecompilerManager
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
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

    @Value("${jadxmcp.input:}")
    private lateinit var inputPath: String

    @Value("${jadxmcp.output:}")
    private lateinit var outputPath: String

    lateinit var jadxDecompiler: JadxDecompiler

    @Bean
    fun commandLineRunner(environment: Environment): CommandLineRunner {
        return CommandLineRunner { args ->
            logger.info("Starting JADX decompiler tool...")
            
            // Prioritize command line arguments, then use values from configuration file
            val input = if (environment.containsProperty("input")) {
                environment.getProperty("input")
            } else {
                inputPath
            }
            
            val output = if (environment.containsProperty("output")) {
                environment.getProperty("output")
            } else {
                outputPath
            }
            
            if (input.isNullOrBlank()) {
                logger.error("Input path cannot be empty, please use --input parameter or set jadxmcp.input in the configuration file")
                return@CommandLineRunner
            }
            
            if (output.isNullOrBlank()) {
                logger.error("Output path cannot be empty, please use --output parameter or set jadxmcp.output in the configuration file")
                return@CommandLineRunner
            }
            
            try {
                val inputFilePath = Paths.get(input)
                val outputDirPath = Paths.get(output)
                
                if (!Files.exists(inputFilePath)) {
                    logger.error("Input file does not exist: $input")
                    return@CommandLineRunner
                }
                
                // Ensure output directory exists
                Files.createDirectories(outputDirPath)
                
                logger.info("Initializing JADX decompiler, input file: $input, output directory: $output")
                jadxDecompiler = JadxDecompilerManager.getInstance(inputFilePath, outputDirPath) ?: run {
                    logger.error("Failed to initialize JADX decompiler")
                    return@CommandLineRunner
                }
                
                logger.info("JADX decompiler initialized successfully")
            } catch (e: Exception) {
                logger.error("Error occurred while initializing JADX decompiler: ${e.message}", e)
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<JadxMcpApplication>(*args)
}


