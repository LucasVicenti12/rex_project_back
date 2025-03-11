package com.delice.crm.core.config.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class WebPanel {
    @RequestMapping("/")
    fun ok(): String {
        return "index"
    }

    @RequestMapping("/web/**")
    fun index(): String {
        return "forward:/index.html"
    }
}