package me.yvesz.jadxmcp.services

import jadx.api.JavaMethod
import me.yvesz.jadxmcp.JadxDecompiler
import org.slf4j.LoggerFactory
import org.springframework.ai.tool.annotation.Tool
import org.springframework.stereotype.Service

@Service
class JavaMethodService {

    companion object {
        private val log = LoggerFactory.getLogger(JavaMethodService::class.java)
        private val decompiler = JadxDecompiler.getInstance()
    }
    
    // @Tool(name = "get_method_code", description = "Get the string of the method code decompiled by jadx")
    // fun getMethodCode(methodSignature: String): String {
    //     log.info("Getting method code for: {}", methodSignature)
        
    //     try {
    //         // Parse method signature to extract class name and method details
    //         val parts = methodSignature.split("#")
    //         if (parts.size != 2) {
    //             return "Invalid method signature format. Expected: 'className#methodName'"
    //         }
            
    //         val className = parts[0]
    //         val methodName = parts[1].substringBefore("(")
            
    //         // Get the class from decompiler
    //         val javaClass = decompiler?.classes?.find { it.fullName == className }
    //             ?: return "Class not found: $className"
            
    //         // Find the method in the class
    //         val method: JavaMethod? = javaClass.methods.find { it.name == methodName }
    //             ?: return "Method not found: $methodName in class $className"
            
    //         // Return the decompiled method code
    //         return method.getCodeStr() ?: "No code available for method: $methodName"
    //     } catch (e: Exception) {
    //         log.error("Error getting method code", e)
    //         return "Error: ${e.message}"
    //     }
    // }
}