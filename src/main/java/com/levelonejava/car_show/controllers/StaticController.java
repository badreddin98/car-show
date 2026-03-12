package com.levelonejava.car_show.controllers;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;

@Controller
public class StaticController {

    @RequestMapping(value = {"/", ""}, method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String index(){
        return "index";
    }
}
