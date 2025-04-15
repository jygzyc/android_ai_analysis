package me.yvesz.jadxServer.service

import com.google.gson.Gson
import jadx.api.JavaClass
import jadx.api.JavaField
import jadx.api.JavaMethod
import jadx.api.JavaNode
import jadx.api.JadxDecompiler
import jadx.api.JadxArgs
import jadx.core.dex.instructions.args.ArgType
import me.yvesz.jadxServer.model.ServiceResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.util.concurrent.ConcurrentHashMap

@Service
class JadxService {

    private lateinit var decompiler: JadxDecompiler
    private lateinit var gson: Gson

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

        fun isDecompilerInit(decompilerInstance: JadxDecompiler): ServiceResponse? {
            if (decompilerInstance == null) {
                val errorMsg = "Error: JADX decompiler not initialized. Please call initJadxDecompiler first."
                log.error(errorMsg)
                return ServiceResponse(success = false, error = errorMsg)
            } else {}
        }
    }
    
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

    fun getMethodCode(className: String, methodName: String): ServiceResponse {
        log.info("Getting method code for: {} in class {}", methodName, className)
    
        try {
            // Check if decompiler is initialized
            val decompilerInstance = getDecompilerInstance()
            isDecompilerInit(decompilerInstance)
            
            // Find the target class
            log.debug("Searching for class: {}", className)
            val javaClass: JavaClass = decompilerInstance.classes.find { it.fullName == className }
                ?: run {
                    val errorMsg = "Class not found: $className"
                    log.warn(errorMsg)
                    return ServiceResponse(success = false, error = errorMsg)
                }

            // Find the target method using improved matching logic
            val method: JavaMethod = javaClass.methods.find { method ->
                // First check method name
                if (method.name != methodName) {
                    return@find false
                }
            } ?: run {
                val errorMsg = "Method not found: $methodName in class $className"
                log.warn(errorMsg)
                return ServiceResponse(success = false, error = errorMsg)
            }

            // Get and return the decompiled method code
            log.info("Found method '{}', retrieving code...", methodName)
            val code = method.getCodeStr()
            return ServiceResponse(success = true, data = code)
        } catch (e: Exception) {
            val errorMsg = "Error getting method code: ${e.message}"
            log.error(errorMsg, e)
            return ServiceResponse(success = false, error = errorMsg)
        }
    }

    fun getClassCode(className: String): ServiceResponse<String> {
        log.info("Getting class code for: {}", className)

        try {
            // Retrieve the singleton decompiler instance
            val decompilerInstance = getDecompilerInstance()
            isDecompilerInit(decompilerInstance)
            
            // Search for the requested class by its fully qualified name
            log.debug("Searching for class: {}", className)
            val javaClass: JavaClass = decompilerInstance.classes.find { it.fullName == className }
                ?: run {
                    val errorMsg = "Class not found: $className"
                    log.warn(errorMsg)
                    return errorMsg
                }

            // Decompile the class to get its source code
            log.info("Found class '{}'", className)
            val code = javaClass.getCode()
            log.debug("Successfully decompiled class code ({} characters)", code.length)
            
            // Return the decompiled class code
            return ServiceResponse(success = true, data = code)
        } catch (e: Exception) {
            // Handle any exceptions that occur during decompilation
            val errorMsg = "Error getting class code: ${e.message}"
            log.error(errorMsg, e)
            return ServiceResponse(success = false, error = errorMsg)
        }
    }
    
    fun getAllClasses(): ServiceResponse<List<String>> {
        log.info("Getting all classes")
        
        try {
            val decompilerInstance = getDecompilerInstance()
            isDecompilerInit(decompilerInstance)

            val classes = decompilerInstance.classes
            log.info("Found {} classes", classes.size)
            
            val classNames = classes.map { it.fullName }
            return ServiceResponse(success = true, data = classNames)
        } catch (e: Exception) {
            val errorMsg = "Error getting all classes: ${e.message}"
            log.error(errorMsg, e)
            return ServiceResponse(success = false, error = errorMsg)
        }
    }
    
    fun searchMethodByName(methodName: String): ServiceResponse<List<String>>  {
        log.info("Searching for method: {}", methodName)
        
        try {
            val decompilerInstance = getDecompilerInstance()
            isDecompilerInit(decompilerInstance)
            
            val results = mutableListOf<String>()
            
            for (javaClass in decompilerInstance.classes) {
                for (method in javaClass.methods) {
                    if (method.fullName == methodName) {
                        results.add("${method.getReturnType().toString()} ${method.fullName}(${method.getArguments()})")
                    }
                }
            }
            
            log.info("Found {} methods named '{}'", results.size, methodName)
            
            return if (results.isEmpty()) {
                ServiceResponse(success = true, error = "No methods found with name: $methodName")
            } else {
                ServiceResponse(success = true, data = results)
            }
        } catch (e: Exception) {
            val errorMsg = "Error searching for method: ${e.message}"
            log.error(errorMsg, e)
            return ServiceResponse(success = false, error = errorMsg)
        }
    }
    
    fun getMethodsOfClass(className: String): ServiceResponse<String> {
        log.info("Getting methods of class: {}", className)
        
        try {
            val decompilerInstance = getDecompilerInstance()
            isDecompilerInit(decompilerInstance)
            
            val javaClass = decompilerInstance.classes.find { it.fullName == className }
                ?: return ServiceResponse(success = false, error = "Class not found: $className")
            
            val methodNames = javaClass.methods.map { it.fullName }
            log.info("Found {} methods in class {}", methodNames.size, className)
            
            return if (methodNames.isEmpty()) {
                ServiceResponse(success = false, error = "No methods found in class: $className")
            } else {
                ServiceResponse(success = true, data = methodNames)
            }
        } catch (e: Exception) {
            val errorMsg = "Error getting methods of class: ${e.message}"
            log.error(errorMsg, e)
            return ServiceResponse(success = false, error = errorMsg)
        }
    }
    
    fun getFieldsOfClass(className: String): ServiceResponse<String> {
        log.info("Getting fields of class: {}", className)
        
        try {
            val decompilerInstance = getDecompilerInstance()
            isDecompilerInit(decompilerInstance)
            
            val javaClass = decompilerInstance.classes.find { it.fullName == className }
                ?: return ServiceResponse(success = false, error = "Class not found: $className")
            
            val fieldNames = javaClass.fields.map { it.fullName }
            log.info("Found {} fields in class {}", fieldNames.size, className)
            
            return if (fieldNames.isEmpty()) {
                ServiceResponse(success = false, error = "No fields found in class: $className")
            } else {
                ServiceResponse(success = true, data = fieldNames)
            }
        } catch (e: Exception) {
            val errorMsg = "Error getting fields of class: ${e.message}"
            log.error(errorMsg, e)
            return ServiceResponse(success = false, error = errorMsg)
        }
    }
    
    fun getSmaliOfClass(className: String): ServiceResponse<String> {
        log.info("Getting Smali code for class: {}", className)
        
        try {
            val decompilerInstance = getDecompilerInstance()
            isDecompilerInit(decompilerInstance)
            
            val javaClass = decompilerInstance.classes.find { it.fullName == className }
                ?: return ServiceResponse(success = false, error = "Class not found: $className")
            
            val smali = javaClass.getSmali()

            return if (smali == null) {
                ServiceResponse(success = false, error = "Smali code not available for class: $className")
            } else {
                ServiceResponse(success = true, data = smali)
            }
        } catch (e: Exception) {
            val errorMsg = "Error getting Smali code: ${e.message}"
            log.error(errorMsg, e)
            return ServiceResponse(success = false, error = errorMsg)
        }
    }
    
    fun getImplementationOfInterface(interfaceName: String): ServiceResponse<List<String>> {
        log.info("Finding implementations of interface: {}", interfaceName)
        
        try {
            val decompilerInstance = getDecompilerInstance()
            isDecompilerInit(decompilerInstance)
            val implementations = mutableListOf<String>()
            val javaInterface = decompilerInstance.classes.find { 
                it.fullName == className 
            }?: return ServiceResponse(success = false, error = "Interface not found: $className")
            for (javaClass in decompilerInstance.classes) {
                if (javaClass.smali.contains(".implements ${interfaceName.replace(".", "/")}")) {
                    implementations.add(javaClass.fullName)
                }
            }
            
            log.info("Found {} implementations of interface {}", implementations.size, interfaceName)
            return if (implementations.isEmpty()) {
                ServiceResponse(success = false, error = "No implementations found for interface: $interfaceName")
            } else {
                ServiceResponse(success = true, data = implementations)
            }
        } catch (e: Exception) {
            val errorMsg = "Error finding interface implementations: ${e.message}"
            log.error(errorMsg, e)
            return ServiceResponse(success = false, error = errorMsg)
        }
    }
    
    fun getSuperclassOfClass(className: String): ServiceResponse<String> {
        log.info("Getting superclasses of class: {}", className)
        
        try {
            val decompilerInstance = getDecompilerInstance()
            isDecompilerInit(decompilerInstance)
            
            val javaClass = decompilerInstance.classes.find { it.fullName == className }
                ?: return ServiceResponse(success = false, error = "Class not found: $className")

            // Get the superclass of the class from smali code using regex
            val smali = javaClass.getSmali()
            if (smali == null) {
                return ServiceResponse(success = false, error = "Smali code not available for class: $className")
            }
            
            // Use regex to find the .super directive in smali code
            val superClassRegex = Regex("\\.super\\s+([\\w/]+)")
            val matchResult = superClassRegex.find(smali)
            
            if (matchResult == null) {
                return ServiceResponse(success = false, error = "Could not find superclass in smali code for: $className")
            }
            
            val superClassName = matchResult.groupValues[1].replace("/", ".")
            
            log.info("Found superclass for class {}: {}", className, superClassName)
            
            return ServiceResponse(success = true, data = superClassName)
        } catch (e: Exception) {
            val errorMsg = "Error getting superclasses: ${e.message}"
            log.error(errorMsg, e)
            return ServiceResponse(success = false, error = errorMsg)
        }
    }

    fun getSubclassesOfClass(className: String): ServiceResponse<List<String>> {
        log.info("Getting subclasses of class: {}", className)

        try {
            val decompilerInstance = getDecompilerInstance()
            isDecompilerInit(decompilerInstance)
            
            // First check if the class exists
            val javaClass = decompilerInstance.classes.find { it.fullName == className }
                ?: return ServiceResponse(success = false, error = "Class not found: $className")
            
            // Convert class name to smali format for searching
            val smaliClassName = className.replace(".", "/")
            val subclasses = mutableListOf<String>()
            
            // Search through all classes for those that have the target class as superclass
            for (cls in decompilerInstance.classes) {
                val smali = cls.getSmali()
                if (smali != null && smali.contains(".super $smaliClassName")) {
                    subclasses.add(cls.fullName)
                }
            }
            
            log.info("Found {} subclasses of class {}", subclasses.size, className)
            
            return if (subclasses.isEmpty()) {
                ServiceResponse(success = false, error = "No subclasses found for class: $className")
            } else {
                ServiceResponse(success = true, data = subclasses)
            }
        } catch (e: Exception) {
            val errorMsg = "Error getting subclasses: ${e.message}"
            log.error(errorMsg, e)
            return ServiceResponse(success = false, error = errorMsg)
        }
    }
    
    fun findXrefOfMethod(className: String, methodName: String): ServiceResponse<List<String>> {
        log.info("Finding cross-references of method: {} in class {}", methodName, className)
        
        try {
            val decompilerInstance = getDecompilerInstance()
            isDecompilerInit(decompilerInstance)
            
            val javaClass = decompilerInstance.classes.find { it.fullName == className }
                ?: return "Class not found: $className"
            
            val method = javaClass.methods.find { it.fullName == methodName }
                ?: return "Method not found: $methodName in class $className"
            
            val xrefs = method.useIn
            log.info("Found {} cross-references for method {} in class {}", xrefs.size, methodName, className)
            
            return if (xrefs.isEmpty()) {
                "No cross-references found for method: $methodName in class $className"
            } else {
                xrefs.joinToString("\n") { ref ->
                    val refClass = ref.declaringClass.fullName
                    val refMethod = ref.name
                    "$refClass.$refMethod"
                }
            }
        } catch (e: Exception) {
            val errorMsg = "Error finding cross-references: ${e.message}"
            log.error(errorMsg, e)
            return errorMsg
        }
    }
}