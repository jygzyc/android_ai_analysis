package me.yvesz.jadxmcp.utils

/**
 * A utility class for parsing Java method signatures.
 * 
 * This class parses method signatures in JVM format and extracts method name,
 * parameter types, and return type information. It handles various Java types including
 * primitives, arrays, and reference types.
 * 
 * Example: "test2(Ljava/lang/String;)V" will be parsed as:
 * - Method name: test2
 * - Parameter types: [java.lang.String]
 * - Return type: void
 *
 * @property signature The method signature string in JVM format to be parsed
 */
class MethodSignatureParser(private val signature: String) {
    
    private val methodName: String
    private val parameterTypes: List<String>
    private val returnType: String
    
    init {
        val nameEndIndex = signature.indexOf('(')
        methodName = if (nameEndIndex > 0) signature.substring(0, nameEndIndex) else signature
        
        val paramTypesStr = if (nameEndIndex > 0 && signature.contains(")")) {
            val paramStart = nameEndIndex + 1
            val paramEnd = signature.lastIndexOf(')')
            if (paramEnd > paramStart) signature.substring(paramStart, paramEnd) else ""
        } else ""
        
        parameterTypes = parseParameterTypes(paramTypesStr)
        
        val returnTypeStr = if (signature.contains(")")) {
            val returnStart = signature.lastIndexOf(')') + 1
            if (returnStart < signature.length) signature.substring(returnStart) else "V"
        } else "V"
        
        returnType = parseJavaType(returnTypeStr)
    }
    
    /**
     * Gets the method name extracted from the signature.
     * 
     * @return The name of the method
     */
    fun getMethodName(): String = methodName
    
    /**
     * Gets the list of parameter types extracted from the signature.
     * 
     * @return A list of parameter type names in their Java representation
     */
    fun getParameterTypes(): List<String> = parameterTypes
    
    /**
     * Gets the return type extracted from the signature.
     * 
     * @return The return type in its Java representation
     */
    fun getReturnType(): String = returnType
    
    /**
     * Parses the parameter types string into a list of Java type names.
     * 
     * @param paramTypesStr The string containing parameter types in JVM format
     * @return A list of parameter types converted to their Java representation
     */
    private fun parseParameterTypes(paramTypesStr: String): List<String> {
        if (paramTypesStr.isEmpty()) return emptyList()
        
        val result = mutableListOf<String>()
        var index = 0
        
        while (index < paramTypesStr.length) {
            val type = parseNextType(paramTypesStr, index)
            result.add(type.first)
            index = type.second
        }
        
        return result
    }
    
    /**
     * Parses the next type descriptor from the given position in the string.
     * 
     * @param str The string containing type descriptors in JVM format
     * @param startIndex The starting position to parse from
     * @return A pair containing the parsed type and the next position to continue parsing
     */
    private fun parseNextType(str: String, startIndex: Int): Pair<String, Int> {
        if (startIndex >= str.length) return Pair("", str.length)
        
        val c = str[startIndex]
        return when (c) {
            'L' -> {
                val endIndex = str.indexOf(';', startIndex)
                if (endIndex == -1) {
                    Pair(str.substring(startIndex), str.length)
                } else {
                    val typeDesc = str.substring(startIndex + 1, endIndex).replace('/', '.')
                    Pair(typeDesc, endIndex + 1)
                }
            }
            '[' -> {
                val (componentType, nextIndex) = parseNextType(str, startIndex + 1)
                Pair("$componentType[]", nextIndex)
            }
            'Z' -> Pair("boolean", startIndex + 1)
            'B' -> Pair("byte", startIndex + 1)
            'C' -> Pair("char", startIndex + 1)
            'S' -> Pair("short", startIndex + 1)
            'I' -> Pair("int", startIndex + 1)
            'J' -> Pair("long", startIndex + 1)
            'F' -> Pair("float", startIndex + 1)
            'D' -> Pair("double", startIndex + 1)
            'V' -> Pair("void", startIndex + 1)
            else -> Pair(c.toString(), startIndex + 1) // 未知类型，返回原字符
        }
    }
    
    /**
     * Parses a Java type descriptor into its Java representation.
     * 
     * @param typeDesc The type descriptor in JVM format
     * @return The Java representation of the type
     */
    private fun parseJavaType(typeDesc: String): String {
        if (typeDesc.isEmpty()) return ""
        
        return parseNextType(typeDesc, 0).first
    }
}