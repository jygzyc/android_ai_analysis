package me.yvesz.jadxServer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JadxServerApplication

fun main(args: Array<String>) {
	runApplication<JadxServerApplication>(*args)
}