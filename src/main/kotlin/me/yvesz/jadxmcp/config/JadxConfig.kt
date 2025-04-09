package me.yvesz.jadxmcp.config

import me.yvesz.jadxmcp.decompiler.JadxDecompilerManager
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener

@Configuration
class JadxConfig {
    
    @EventListener
    fun handleContextRefresh(event: ContextRefreshedEvent) {
        val jadxDecompilerManager = event.applicationContext.getBean(JadxDecompilerManager::class.java)
        JadxDecompilerManager.setInstance(jadxDecompilerManager)
    }
}