package me.yvesz.jadxmcp.config

import me.yvesz.jadxmcp.service.JadxService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.ai.tool.method.MethodToolCallbackProvider

@Configuration
class McpServerConfig {

    @Bean
    fun jadxTools(jadxService: JadxService): ToolCallbackProvider {
        return MethodToolCallbackProvider.builder().toolObjects(jadxService).build()
    }
}