package me.yvesz.jadxmcp.service

import jadx.api.JavaClass
import jadx.api.JavaField
import jadx.api.JavaMethod
import jadx.api.JavaNode
import jadx.api.JadxDecompiler
import jadx.api.JadxArgs
import jadx.core.dex.instructions.args.ArgType
import org.slf4j.LoggerFactory
import org.springframework.ai.tool.annotation.Tool
import org.springframework.stereotype.Service
import java.io.File

import me.yvesz.jadxmcp.utils.MethodSignatureParser


@Service
class JadxService {

    private lateinit var decompiler: JadxDecompiler

    companion object {
        private val log = LoggerFactory.getLogger(JadxService::class.java)
        private var decompilerInstance: JadxDecompiler? = null

        fun getDecompilerInstance(): JadxDecompiler? {
            if (decompilerInstance == null) {
                return null
            }
            return decompilerInstance!!
        }
    }

    @Tool(name = "init_jadx", description = "Initialize the jadx decompiler before use")
    fun initJadxDecompiler(inputPath: String = "", outputPath: String = ""){
        val jadxArgs = JadxArgs().apply{
            setInputFile(File(inputPath))
            outDir = File(outputPath).apply {
                parentFile?.takeUnless { parentFile -> parentFile.exists() }?.mkdirs()
            }
            setDeobfuscationOn(false)
            setDebugInfo(false)
            setSkipResources(true)
            setUseDxInput(true)
        }
        decompiler = JadxDecompiler(jadxArgs)
        decompiler.load()
    }

    // /**
    //  * Get the string of the method code decompiled by jadx
    //  */
    // @Tool(name = "get_method_code", description = "Get the string of the method code decompiled by jadx")
    // fun getMethodCode(className:String, methodSignature: String): String {
    //     log.info("Getting method code for: {}", methodSignature)
    
    //     try {
    //         val javaClass: JavaClass = getDecompilerInstance()!!.getClasses().find { it.fullName == className }
    //             ?: return "Class not found: $className"

            
    //         // 解析方法签名
    //         val signatureParser = MethodSignatureParser(methodSignature)
    //         val methodName = signatureParser.getMethodName()
    //         val parameterTypes = signatureParser.getParameterTypes()
            
    //         // 根据方法名和参数类型匹配方法
    //         val method: JavaMethod = javaClass.methods.find { method ->
    //             if (method.name != methodName) {
    //                 return@find false
    //             }
                
    //             // 比较参数类型数量
    //             val methodArgTypes: List<ArgType> = method.getArguments()
    //             if (methodArgTypes.size != parameterTypes.size) {
    //                 return@find false
    //             }
                
    //             methodArgTypes.zip(parameterTypes).all { (actual, expected) ->
    //                 // 简化比较：移除包名后比较
    //                 val simplifiedActual = actual.substringAfterLast('.')
    //                 val simplifiedExpected = expected.substringAfterLast('.')
    //                 simplifiedActual == simplifiedExpected
    //             }
    //         } ?: return "Method not found: $methodSignature"

    //         // Return the decompiled method code
    //         return method.getCodeStr() ?: "No code available for method: ${method.name}"
    //     } catch (e: Exception) {
    //         log.error("Error getting method code", e)
    //         return "Error: ${e.message}"
    //     }
    // }

    /**
     * Get the string of the class code decompiled by jadx
     */
    @Tool(name = "get_class_code", description = "Get the string of the class code decompiled by jadx")
    fun getClassCode(className: String): String {
        log.info("Getting class code for: {}", className)
        
        try {
            val javaClass: JavaClass = getDecompilerInstance()!!.getClasses().find { it.fullName == className }
                ?: return "Class not found: $className"
            
            // Return the decompiled class code
            return javaClass.getCode()
        } catch (e: Exception) {
            log.error("Error getting class code", e)
            return "Error: ${e.message}"
        }
    }
}