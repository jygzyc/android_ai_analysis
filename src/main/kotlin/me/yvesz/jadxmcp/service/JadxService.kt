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
        @Volatile
        private var decompilerInstance: JadxDecompiler? = null

        fun getDecompilerInstance(): JadxDecompiler? {
            return decompilerInstance ?: synchronized(this) {
                decompilerInstance ?: run {
                    log.warn("JADX decompiler not initialized")
                    null
                }
            }
        }
    }

    /**
     * Initializes the JADX decompiler with specified configuration options.
     * This function must be called before using any decompilation features.
     * It sets up the decompiler with appropriate settings for analyzing Android APK/JAR files.
     *
     * @param inputPath Path to the input file (APK or JAR) to be decompiled
     * @param outputPath Directory where decompiled files will be saved
     * @return Status message indicating the result of initialization
     */
    @Tool(name = "init_jadx", description = "Initialize the JADX decompiler before use")
    fun initJadxDecompiler(inputPath: String = "", outputPath: String = ""): String {
        log.info("Initializing JADX decompiler with input: '{}', output: '{}'", inputPath, outputPath)
        
        try {
            val inputFile = File(inputPath)
            if (!inputFile.exists()) {
                val errorMsg = "Input file does not exist: $inputPath"
                log.error(errorMsg)
                return errorMsg
            }
            
            val outputDir = File(outputPath)
            if (!outputDir.exists()) {
                log.info("Creating output directory: {}", outputPath)
                outputDir.mkdirs()
            }
            
            val jadxArgs = JadxArgs().apply {
                setInputFile(inputFile)
                outDir = outputDir
                isDeobfuscationOn = false
                isDebugInfo = false
                isSkipResources = true
                isUseDxInput = true
            }
            
            log.debug("JADX configuration: deobfuscation=false, debugInfo=false, skipResources=true, useDxInput=true")
            
            decompiler = JadxDecompiler(jadxArgs)
            decompilerInstance = decompiler
            
            log.info("Loading APK/JAR file...")
            decompiler.load()
            
            val classCount = decompiler.classes.size
            log.info("JADX decompiler initialized successfully. Found {} classes", classCount)
            
            return "JADX decompiler initialized successfully. Found $classCount classes in $inputPath"
        } catch (e: Exception) {
            val errorMsg = "Failed to initialize JADX decompiler: ${e.message}"
            log.error(errorMsg, e)
            return errorMsg
        }
    }

    /**
     * Retrieves the decompiled source code for a specific method.
     * 
     * This function uses the JADX decompiler to extract the source code of a specific method
     * from a decompiled class. It parses the provided method signature to identify the target method,
     * then searches for a matching method in the specified class based on name and parameter types.
     *
     * @param className The fully qualified name of the class containing the method (e.g., "com.example.MyClass")
     * @param methodSignature The signature of the method in JVM format (e.g., "methodName(Ljava/lang/String;I)V")
     * @return The decompiled source code of the method if found, or an error message if the operation fails
     *         Possible error messages include:
     *         - Decompiler not initialized error
     *         - Class not found error
     *         - Method not found error
     *         - No code available error
     *         - General exception error with details
     */
    @Tool(name = "get_method_code", description = "Retrieves the decompiled source code for a specific method.")
    fun getMethodCode(className: String, methodSignature: String): String {
        log.info("Getting method code for: {} in class {}", methodSignature, className)
    
        try {
            // Check if decompiler is initialized
            val decompilerInstance = getDecompilerInstance()
            if (decompilerInstance == null) {
                val errorMsg = "Error: JADX decompiler not initialized. Please call init_jadx first."
                log.error(errorMsg)
                return errorMsg
            }
            
            // Find the target class
            log.debug("Searching for class: {}", className)
            val javaClass: JavaClass = decompilerInstance.classes.find { it.fullName == className }
                ?: run {
                    val errorMsg = "Class not found: $className"
                    log.warn(errorMsg)
                    return errorMsg
                }

            // Parse the method signature
            val signatureParser = MethodSignatureParser(methodSignature)
            val methodName = signatureParser.getMethodName()
            val parameterTypes = signatureParser.getParameterTypes()
            
            log.debug("Looking for method '{}' with {} parameters", methodName, parameterTypes.size)
            
            // Find the target method using improved matching logic
            val method: JavaMethod = javaClass.methods.find { method ->
                // First check method name
                if (method.name != methodName) {
                    return@find false
                }
                
                // Then check parameter count
                val methodArgTypes: List<ArgType> = method.getArguments()
                if (methodArgTypes.size != parameterTypes.size) {
                    return@find false
                }
                
                // Finally check parameter types compatibility
                methodArgTypes.zip(parameterTypes).all { (actual, expected) ->
                    // Compare type names with improved handling of generics and arrays
                    val actualTypeName = actual.toString().substringAfterLast('.')
                    val expectedTypeName = expected.substringAfterLast('.')
                    
                    // Handle array types specially
                    if (actual.isArray && expected.endsWith("[]")) {
                        val actualComponentType = actualTypeName.removeSuffix("[]").trim()
                        val expectedComponentType = expectedTypeName.removeSuffix("[]").trim()
                        actualComponentType.equals(expectedComponentType, ignoreCase = true)
                    } else {
                        actualTypeName.equals(expectedTypeName, ignoreCase = true)
                    }
                }
            } ?: run {
                val errorMsg = "Method not found: $methodSignature in class $className"
                log.warn(errorMsg)
                return errorMsg
            }

            // Get and return the decompiled method code
            log.info("Found method '{}', retrieving code...", methodName)
            val code = method.getCodeStr()
            return code ?: "No code available for method: ${method.name}"
        } catch (e: Exception) {
            val errorMsg = "Error getting method code: ${e.message}"
            log.error(errorMsg, e)
            return errorMsg
        }
    }

    /**
     * Retrieves the decompiled source code for a specified Java class.
     * 
     * This function uses the JADX decompiler to generate human-readable Java source code
     * from a compiled class. It first checks if the decompiler has been properly initialized,
     * then searches for the requested class by its fully qualified name, and finally
     * decompiles the class to return its source code.
     *
     * @param className The fully qualified name of the class to decompile (e.g., "com.example.MyClass")
     * @return The decompiled source code as a string if successful, or an error message if the operation fails
     *         Possible error messages include:
     *         - Decompiler not initialized error
     *         - Class not found error
     *         - General exception error with details
     */
    @Tool(name = "get_class_code", description = "Retrieves the decompiled source code for a specified Java class.")
    fun getClassCode(className: String): String {
        log.info("Getting class code for: {}", className)

        try {
            // Retrieve the singleton decompiler instance
            val decompilerInstance = getDecompilerInstance()
            if (decompilerInstance == null) {
                val errorMsg = "Error: JADX decompiler not initialized. Please call init_jadx first."
                log.error(errorMsg)
                return errorMsg
            }
            
            // Search for the requested class by its fully qualified name
            log.debug("Searching for class: {}", className)
            val javaClass: JavaClass = decompilerInstance.classes.find { it.fullName == className }
                ?: run {
                    val errorMsg = "Class not found: $className"
                    log.warn(errorMsg)
                    return errorMsg
                }

            // Decompile the class to get its source code
            log.info("Found class '{}', decompiling code...", className)
            val code = javaClass.getCode()
            log.debug("Successfully decompiled class code ({} characters)", code.length)
            
            // Return the decompiled class code
            return code
        } catch (e: Exception) {
            // Handle any exceptions that occur during decompilation
            val errorMsg = "Error getting class code: ${e.message}"
            log.error(errorMsg, e)
            return errorMsg
        }
    }
}