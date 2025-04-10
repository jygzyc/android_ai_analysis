package me.yvesz.jadxmcp.utils

/**
 * 用于解析Java方法签名的工具类
 * 例如：test2(Ljava/lang/String;)V 将被解析为：
 * - 方法名：test2
 * - 参数类型：[java.lang.String]
 * - 返回类型：void
 */
class MethodSignatureParser(private val signature: String) {
    
    private val methodName: String
    private val parameterTypes: List<String>
    private val returnType: String
    
    init {
        // 解析方法名
        val nameEndIndex = signature.indexOf('(')
        methodName = if (nameEndIndex > 0) signature.substring(0, nameEndIndex) else signature
        
        // 解析参数类型
        val paramTypesStr = if (nameEndIndex > 0 && signature.contains(")")) {
            val paramStart = nameEndIndex + 1
            val paramEnd = signature.lastIndexOf(')')
            if (paramEnd > paramStart) signature.substring(paramStart, paramEnd) else ""
        } else ""
        
        parameterTypes = parseParameterTypes(paramTypesStr)
        
        // 解析返回类型
        val returnTypeStr = if (signature.contains(")")) {
            val returnStart = signature.lastIndexOf(')') + 1
            if (returnStart < signature.length) signature.substring(returnStart) else "V"
        } else "V"
        
        returnType = parseJavaType(returnTypeStr)
    }
    
    /**
     * 获取方法名
     */
    fun getMethodName(): String = methodName
    
    /**
     * 获取参数类型列表
     */
    fun getParameterTypes(): List<String> = parameterTypes
    
    /**
     * 获取返回类型
     */
    fun getReturnType(): String = returnType
    
    /**
     * 解析参数类型字符串为类型列表
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
     * 从给定位置解析下一个类型
     * @return Pair<解析后的类型, 下一个开始位置>
     */
    private fun parseNextType(str: String, startIndex: Int): Pair<String, Int> {
        if (startIndex >= str.length) return Pair("", str.length)
        
        val c = str[startIndex]
        return when (c) {
            'L' -> {
                // 引用类型 Ljava/lang/String;
                val endIndex = str.indexOf(';', startIndex)
                if (endIndex == -1) {
                    Pair(str.substring(startIndex), str.length)
                } else {
                    val typeDesc = str.substring(startIndex + 1, endIndex).replace('/', '.')
                    Pair(typeDesc, endIndex + 1)
                }
            }
            '[' -> {
                // 数组类型 [Ljava/lang/String;
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
     * 解析Java类型描述符
     */
    private fun parseJavaType(typeDesc: String): String {
        if (typeDesc.isEmpty()) return ""
        
        return parseNextType(typeDesc, 0).first
    }
}