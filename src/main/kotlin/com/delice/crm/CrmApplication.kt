package com.delice.crm

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "com.delice.crm",
        "org.jetbrains.exposed.spring"
    ],
)
class CrmApplication

fun main(args: Array<String>) {
    runApplication<CrmApplication>(*args)
}
