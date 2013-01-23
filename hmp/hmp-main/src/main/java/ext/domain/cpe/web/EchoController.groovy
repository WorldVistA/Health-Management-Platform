package org.osehra.cpe.web

import org.osehra.cpe.vpr.web.servlet.view.ModelAndViewFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest

import static org.osehra.cpe.vpr.web.servlet.view.ModelAndViewFactory.contentNegotiatingModelAndView

@Controller
class EchoController {

    @RequestMapping("/echo")
    ModelAndView echo(HttpServletRequest request) {
        return contentNegotiatingModelAndView(request.getParameterMap());
    }
}
