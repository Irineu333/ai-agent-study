package com.neoutils

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main

class HelloWorld : CliktCommand(name = "hello") {

    override fun run() {
        echo("Hello World!")
    }
}

fun main(args: Array<String>) = HelloWorld().main(args)
