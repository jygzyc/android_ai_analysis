package me.yvesz.jadxmcp.service

import jadx.api.JavaClass
import me.yvesz.jadxmcp.JadxMcpApplication
import me.yvesz.jadxmcp.decompiler.JadxDecompilerManager
import org.slf4j.LoggerFactory
import org.springframework.ai.tool.annotation.Tool
import org.springframework.stereotype.Service

@Service
class JavaClassService {
    
    companion object {
        private val log = LoggerFactory.getLogger(JavaClassService::class.java)
    }

    @Tool(name = "get_class_code", description = "Get the string of the class code decompiled by jadx")
    fun getClassCode(className: String): String {
        log.info("Getting class code for: {}", className)
        
        try {
            val decompiler = JadxDecompilerManager.getInstance().getJadxDecompiler()
            val javaClass = decompiler.classes.find { it.fullName == className }
                ?: return "Class not found: $className"
            
            // Return the decompiled class code
            return javaClass.code
        } catch (e: Exception) {
            log.error("Error getting class code", e)
            return "Error: ${e.message}"
        }
    }
}
