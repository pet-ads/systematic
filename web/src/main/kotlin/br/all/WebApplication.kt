package br.all

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class WebApplication{

    //TODO Check the integration between account and review modules
    //TODO See how to remove researcher info from every endpoint
    //TODO Check how to fix API tests
    //TODO write test cases route for PET members for account
    //TODO write test case guide for checking for 401
    //TODO fix the token get claims returning 500 for invalid token format
}

fun main(args: Array<String>) {
    runApplication<WebApplication>(*args)
}


