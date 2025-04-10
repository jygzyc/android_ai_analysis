package me.yvesz.jadxmcp.config

import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.ai.tool.method.MethodToolCallbackProvider
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service

@Configuration
class McpServerConfig {

    @Bean
    fun autoRegisterTools(applicationContext: ApplicationContext): ToolCallbackProvider {
        val beanNames: Array<String> = applicationContext.getBeanNamesForAnnotation(Service::class.java)
        val serviceBeans: MutableList<Any> = ArrayList()
        for (beanName in beanNames) {
            if (beanName.endsWith("Service")) {
                serviceBeans.add(applicationContext.getBean(beanName))
            }
        }
        return MethodToolCallbackProvider.builder()
            .toolObjects(*serviceBeans.toTypedArray())
            .build()
    }
}