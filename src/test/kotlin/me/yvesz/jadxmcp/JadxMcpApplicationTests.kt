package me.yvesz.jadxmcp

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
class JadxMcpApplicationTests {

    @Autowired
    private lateinit var application: JadxMcpApplication

    @Test
    fun contextLoads() {
        assertNotNull(application, "Application context should be loaded")
    }

    @Test
    fun testCommandLineRunner() {
        val runner = application.commandLineRunner(org.springframework.mock.env.MockEnvironment())
        assertNotNull(runner, "CommandLineRunner should be created")
    }
}
