package me.yvesz.jadxmcp

import me.yvesz.jadxmcp.decompiler.JadxDecompilerManager
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.nio.file.Paths

@SpringBootApplication
class JadxMcpApplication 

fun main(args: Array<String>) {
    runApplication<JadxMcpApplication>(*args)
}


