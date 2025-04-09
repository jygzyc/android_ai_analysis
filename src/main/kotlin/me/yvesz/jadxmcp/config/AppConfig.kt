package me.yvesz.jadxmcp.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(JadxProperties::class)
class AppConfig

@ConfigurationProperties(prefix = "jadx")
class JadxProperties {
    var inputPath: String = ""
    var outputDir: String = ""
}