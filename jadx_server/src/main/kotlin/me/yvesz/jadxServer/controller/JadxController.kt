package me.yvesz.jadxServer.controller

import me.yvesz.jadxServer.service.JadxService
import me.yvesz.jadxServer.model.ServiceResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity

/**
 * REST Controller for JADX decompiler API.
 * Provides endpoints for interacting with the JADX decompiler to analyze Android applications.
 * These endpoints correspond to the tools defined in the MCP Python client.
 */
@RestController
@RequestMapping("/api/jadx")
class JadxController @Autowired constructor(private val jadxService: JadxService) {

    companion object {
        private val log = LoggerFactory.getLogger(JadxController::class.java)
    }

    /**
     * Retrieves the source code of a specific method from a class.
     *
     * @param className The fully qualified name of the class
     * @param methodName The name of the method to retrieve
     * @return The decompiled source code of the method
     */
    @GetMapping("/get_method_code")
    fun getMethodCode(
        @RequestParam("class") className: String,
        @RequestParam("method") methodName: String
    ): ResponseEntity<String> {
        log.info("Received request to get method code for: {} in class {}", methodName, className)
        val result = jadxService.getMethodCode(className, methodName)
        return ResponseEntity.ok(ApiResponse(success = true, data = result))
    }

    /**
     * Gets a complete list of all classes in the decompiled project.
     *
     * @return List of fully qualified class names
     */
    @GetMapping("/get_all_classes")
    fun getAllClasses(): ResponseEntity<ApiResponse<List<String>>> {
        log.info("Received request to get all classes")
        // This method needs to be implemented in JadxService
        val result = jadxService.getAllClasses()
        return ResponseEntity.ok(ApiResponse(success = true, data = result))
    }

    /**
     * Retrieves the complete Java source code of a specified class.
     *
     * @param className The fully qualified name of the class
     * @return The decompiled source code of the class
     */
    @GetMapping("/get_class_code")
    fun getClassCode(
        @RequestParam("class") className: String
    ): ResponseEntity<ApiResponse<String>> {
        log.info("Received request to get class code for: {}", className)
        val result = jadxService.getClassCode(className)
        return ResponseEntity.ok(ApiResponse(success = true, data = result))
    }



    /**
     * Searches for methods with a specific name across all classes in the project.
     *
     * @param methodName The name of the method to search for
     * @return List of class and method names where the method was found
     */
    @GetMapping("/search_method_by_name")
    fun searchMethodByName(
        @RequestParam("method") methodName: String
    ): ResponseEntity<ApiResponse<List<String>>> {
        log.info("Received request to search for method: {}", methodName)
        // This method needs to be implemented in JadxService
        val result = jadxService.searchMethodByName(methodName)
        return ResponseEntity.ok(ApiResponse(success = true, data = result))
    }

    /**
     * Lists all methods defined in a specific class.
     *
     * @param className The fully qualified name of the class
     * @return List of method signatures in the class
     */
    @GetMapping("/get_methods_of_class")
    fun getMethodsOfClass(
        @RequestParam("class") className: String
    ): ResponseEntity<ApiResponse<List<String>>> {
        log.info("Received request to get methods of class: {}", className)
        // This method needs to be implemented in JadxService
        val result = jadxService.getMethodsOfClass(className)
        return ResponseEntity.ok(ApiResponse(success = true, data = result))
    }

    /**
     * Lists all fields and their types defined in a specific class.
     *
     * @param className The fully qualified name of the class
     * @return List of field names and types in the class
     */
    @GetMapping("/get_fields_of_class")
    fun getFieldsOfClass(
        @RequestParam("class") className: String
    ): ResponseEntity<ApiResponse<List<String>>> {
        log.info("Received request to get fields of class: {}", className)
        // This method needs to be implemented in JadxService
        val result = jadxService.getFieldsOfClass(className)
        return ResponseEntity.ok(ApiResponse(success = true, data = result))
    }

    /**
     * Retrieves the Smali (disassembled Dalvik bytecode) representation of a class.
     *
     * @param className The fully qualified name of the class
     * @return The Smali representation of the class
     */
    @GetMapping("/get_smali_of_class")
    fun getSmaliOfClass(
        @RequestParam("class") className: String
    ): ResponseEntity<ApiResponse<String>> {
        log.info("Received request to get Smali code for class: {}", className)
        // This method needs to be implemented in JadxService
        val result = jadxService.getSmaliOfClass(className)
        return ResponseEntity.ok(ApiResponse(success = true, data = result))
    }

    /**
     * Retrieves the Smali bytecode representation of a specific method.
     *
     * @param className The fully qualified name of the class
     * @param methodName The name of the method
     * @return The Smali representation of the method
     */
    @GetMapping("/get_smali_of_method")
    fun getSmaliOfMethod(
        @RequestParam("class") className: String,
        @RequestParam("method") methodName: String
    ): ResponseEntity<ApiResponse<String>> {
        log.info("Received request to get Smali code for method: {} in class {}", methodName, className)
        // This method needs to be implemented in JadxService
        val result = jadxService.getSmaliOfMethod(className, methodName)
        return ResponseEntity.ok(ApiResponse(success = true, data = result))
    }

    /**
     * Finds all classes that implement a specific interface.
     *
     * @param interfaceName The fully qualified name of the interface
     * @return List of classes implementing the interface
     */
    @GetMapping("/get_implementation_of_interface")
    fun getImplementationOfInterface(
        @RequestParam("interface") interfaceName: String
    ): ResponseEntity<List<String>> {
        log.info("Received request to find implementations of interface: {}", interfaceName)
        // This method needs to be implemented in JadxService
        val result = jadxService.getImplementationOfInterface(interfaceName)
        return ResponseEntity.ok(ApiResponse(success = true, data = result))
    }

    /**
     * Gets the inheritance hierarchy (parent classes) of a specific class.
     *
     * @param className The fully qualified name of the class
     * @return List of parent classes in the inheritance hierarchy
     */
    @GetMapping("/get_superclasses_of_class")
    fun getSuperclassesOfClass(
        @RequestParam("class") className: String
    ): ResponseEntity<ApiResponse<List<String>>> {
        log.info("Received request to get superclasses of class: {}", className)
        // This method needs to be implemented in JadxService
        val result = jadxService.getSuperclassesOfClass(className)
        return ResponseEntity.ok(ApiResponse(success = true, data = result))
    }

    /**
     * Finds all cross-references (usages) of a specific method.
     *
     * @param className The fully qualified name of the class
     * @param methodName The name of the method
     * @return List of locations where the method is used
     */
    @GetMapping("/find_xref_of_method")
    fun findXrefOfMethod(
        @RequestParam("class") className: String,
        @RequestParam("method") methodName: String
    ): ResponseEntity<ApiResponse<List<String>>> {
        log.info("Received request to find cross-references of method: {} in class {}", methodName, className)
        // This method needs to be implemented in JadxService
        val result = jadxService.findXrefOfMethod(className, methodName)
        return ResponseEntity.ok(ApiResponse(success = true, data = result))
    }
}