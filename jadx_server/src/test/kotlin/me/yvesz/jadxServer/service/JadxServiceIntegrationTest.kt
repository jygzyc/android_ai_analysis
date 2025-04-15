package me.yvesz.jadxServer.service

import me.yvesz.jadxServer.model.ServiceResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * JadxService的集成测试类
 * 使用真实的APK文件测试反编译功能
 */
class JadxServiceIntegrationTest {

    private lateinit var jadxService: JadxService
    private lateinit var testApkPath: String
    
    @TempDir
    lateinit var tempDir: Path
    
    @BeforeEach
    fun setUp() {
        jadxService = JadxService()
        // 使用测试资源目录中的APK文件
        val resourceUrl = javaClass.classLoader.getResource("test-sample.apk")
        testApkPath = resourceUrl?.path ?: ""
        assertTrue(File(testApkPath).exists(), "测试APK文件不存在")
    }
    
    @AfterEach
    fun tearDown() {
        // 清理反编译器实例
        val field = JadxService::class.java.getDeclaredField("decompilerInstance")
        field.isAccessible = true
        field.set(null, null)
    }
    
    @Test
    fun `test initJadxDecompiler with real APK`() {
        // 使用临时目录作为输出路径
        val outputPath = tempDir.toString()
        
        // 初始化反编译器
        val result = jadxService.initJadxDecompiler(testApkPath, outputPath)
        
        // 验证初始化成功
        assertTrue(result.contains("initialized successfully"), "反编译器初始化失败: $result")
    }
    
    @Test
    fun `test initJadxDecompiler with non-existent file`() {
        val nonExistentPath = "/path/to/nonexistent.apk"
        val outputPath = tempDir.toString()
        
        // 初始化反编译器
        val result = jadxService.initJadxDecompiler(nonExistentPath, outputPath)
        
        // 验证初始化失败并返回正确的错误信息
        assertTrue(result.contains("does not exist"), "应该报告文件不存在错误")
    }
    
    @Test
    fun `test end-to-end workflow`() {
        // 初始化反编译器
        val outputPath = tempDir.toString()
        jadxService.initJadxDecompiler(testApkPath, outputPath)
        
        // 获取所有类
        val classesResponse = jadxService.getAllClasses()
        assertTrue(classesResponse.success, "获取所有类失败: ${classesResponse.error}")
        
        // 如果有类，测试获取类代码
        if (classesResponse.data?.isNotEmpty() == true) {
            val className = classesResponse.data!!.first()
            val classCodeResponse = jadxService.getClassCode(className)
            assertTrue(classCodeResponse.success, "获取类代码失败: ${classCodeResponse.error}")
        }
    }
    
    @Test
    fun `test isDecompilerInit validation`() {
        // 在初始化反编译器之前调用方法
        val response = jadxService.getAllClasses()
        
        // 验证返回错误信息
        assertFalse(response.success, "未初始化反编译器时应该返回失败")
        assertTrue(response.error?.contains("not initialized") == true, "应该返回未初始化错误")
    }
}