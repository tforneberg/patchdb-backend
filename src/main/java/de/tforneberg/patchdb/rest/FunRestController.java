package de.tforneberg.patchdb.rest;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class FunRestController {
	
	@GetMapping("/")
	public String sayHello() {
		return "Hallo Weeeeeeelt. Zeit aufm Server is: "+LocalDateTime.now();
	}
}
