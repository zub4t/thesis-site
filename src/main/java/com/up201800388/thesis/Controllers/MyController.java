package com.up201800388.thesis.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/shopping")
public class MyController {

    @GetMapping("/test")
    public String test() {
        System.out.println("TESTE");
        return "result";
    }
}