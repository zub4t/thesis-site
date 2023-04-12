package com.up201800388.thesis.Controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.ui.Model;
@Controller
public class CustomErrorController implements ErrorController {
    @Autowired
    private ErrorAttributes errorAttributes;
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, WebRequest webRequest, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            httpStatus = HttpStatus.valueOf(statusCode);
        }

        Throwable error = errorAttributes.getError(webRequest);
        if (error != null) {
            model.addAttribute("errorMessage", error.getMessage());
        } else {
            model.addAttribute("errorMessage", "An error occurred: " + httpStatus.getReasonPhrase());
        }

        return "error";
    }

}
