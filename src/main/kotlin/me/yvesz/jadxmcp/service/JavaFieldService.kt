package me.yvesz.jadxmcp.service

import jadx.api.JavaField
import me.yvesz.jadxmcp.decompiler.JadxDecompilerManager
import org.slf4j.LoggerFactory
import org.springframework.ai.tool.annotation.Tool
import org.springframework.stereotype.Service

@Service
class JavaFieldService {
    
    companion object {
        private val log = LoggerFactory.getLogger(JavaFieldService::class.java)
    }

    // @Tool(name = "get_field_code", description = "Get the string of the field code decompiled by jadx")
    // fun getFieldCode(fieldSignature: String): String {
    //     log.info("Getting field code for: {}", fieldSignature)
        
    //     try {
    //         // Parse field signature to extract class name and field name
    //         val parts = fieldSignature.split("#")
    //         if (parts.size != 2) {
    //             return "Invalid field signature format. Expected: 'className#fieldName'"
    //         }
            
    //         val className = parts[0]
    //         val fieldName = parts[1]
            
    //         // Get the class from decompiler
    //         val javaClass = JadxMcpApplication.jadxDecompiler.classes?.find { it.fullName == className }
    //             ?: return "Class not found: $className"
            
    //         // Find the field in the class
    //         val field: JavaField = javaClass.fields.find { it.name == fieldName }
    //             ?: return "Field not found: $fieldName in class $className"
            
    //         // Return the decompiled field code
    //         return field.code ?: "No code available for field: $fieldName"
    //     } catch (e: Exception) {
    //         log.error("Error getting field code", e)
    //         return "Error: ${e.message}"
    //     }
    // }
}