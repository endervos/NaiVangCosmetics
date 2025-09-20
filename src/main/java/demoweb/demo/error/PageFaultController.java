package demoweb.demo.error;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class PageFaultController {

    @RequestMapping("/page-fault")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            if (statusCode == 404) {
                return "PageFault/PageFault404";
            } else if (statusCode == 500) {
                return "PageFault/PageFault500";
            } else if (statusCode == 403) {
                return "PageFault/PageFault403";
            }
        }
        return "PageFault/PageFault404";
    }
}