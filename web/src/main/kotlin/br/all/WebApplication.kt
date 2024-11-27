package br.all

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class WebApplication{
    //TODO fix the token get claims returning 500 for invalid token format
}

fun main(args: Array<String>) {
    runApplication<WebApplication>(*args)
}
