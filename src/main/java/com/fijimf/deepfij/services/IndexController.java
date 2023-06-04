package com.fijimf.deepfij.services;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller

public class IndexController {
    @GetMapping("/")
    public ModelAndView index(Authentication authentication){
        return new ModelAndView("index.html").addObject("user",authentication.getPrincipal());
    }
}
