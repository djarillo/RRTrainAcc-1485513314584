package example.nosql;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class RefinementController {

	@RequestMapping(value = "/Refinement", method = RequestMethod.GET)
	public String refinementRedirect(ModelMap model) {
		return "refinement";
	}
}
