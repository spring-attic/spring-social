package org.springframework.social.showcase;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SigninController {

	@RequestMapping(value = "/signin", method = RequestMethod.GET)
	public void signin() {
	}
}