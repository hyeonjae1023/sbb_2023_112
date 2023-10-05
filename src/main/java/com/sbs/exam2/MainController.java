package com.sbs.exam2;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {

    @GetMapping("/exam2")
    @ResponseBody
    public String index() {
        return "exam2";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/question/list";
    }
}
