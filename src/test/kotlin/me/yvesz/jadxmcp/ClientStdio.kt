package me.yvesz.jadxmcp

import io.modelcontextprotocol.client.McpClient
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport
import io.modelcontextprotocol.spec.McpSchema
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

class ClientSseTest

fun main(args: Array<String>) {
    val transport = HttpClientSseClientTransport("http://localhost:8080")
    val client = McpClient.sync(transport).build()

    try {
        client.initialize()
        client.ping()

        // List available tools
        val toolsList = client.listTools()
        println("Available Tools = $toolsList")

        client.closeGracefully()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}