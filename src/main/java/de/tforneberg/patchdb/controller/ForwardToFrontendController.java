package de.tforneberg.patchdb.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ForwardToFrontendController implements ErrorController {
	private static final String PATH = "/error";
	
	@Override
	public String getErrorPath() {
		return PATH;
	}
	
    @RequestMapping(value = PATH)
    public String error() {
        return "forward:/index.html";
    }

}
