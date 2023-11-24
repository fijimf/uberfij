package com.fijimf.deepfij.services;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller

public class IndexController {
    @GetMapping("/")
    public ModelAndView slash() {
        return new ModelAndView("redirect:/index");
    }

    @GetMapping("/index")
    public ModelAndView index() {
        return new ModelAndView("index.html");
    }
}
