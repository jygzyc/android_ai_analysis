package me.yvesz.jadxmcp

import me.yvesz.jadxmcp.service.JadxService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.ai.tool.ToolCallback
import org.springframework.ai.tool.ToolCallbacks
import org.springframework.context.annotation.Bean

@SpringBootApplication
class JadxMcpApplication 

fun main(args: Array<String>) {
    runApplication<JadxMcpApplication>(*args)
}


