package com.konkuk.vecto;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultController {

	@GetMapping("/")
	public String introduction() {
		return "introduction";
	}

	@GetMapping("/privacy-policy")
	public String privacyPolicy() {
		return "privacy-policy";
	}
}
