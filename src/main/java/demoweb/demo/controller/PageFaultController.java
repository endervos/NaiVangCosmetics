package demoweb.demo.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class PageFaultController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            switch (statusCode) {
                case 403:
                    return "Customer/PageFault403";
                case 404:
                    return "Customer/PageFault404";
                case 500:
                    return "Customer/PageFault500";
                default:
                    return "Customer/PageFault404";
            }
        }
        return "Customer/PageFault404";
    }
}