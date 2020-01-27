package com.choistec.cmspc.api.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/")
class InitController {
    @RequestMapping("")
    fun init () = "redirect:/swagger-ui.html"
}
