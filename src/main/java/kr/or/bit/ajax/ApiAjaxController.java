package kr.or.bit.ajax;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.bit.service.ApiService;

import java.io.IOException;

@WebServlet({"/api/weather", "/api/pension"})
public class ApiAjaxController extends HttpServlet {
    private final ApiService apiService = new ApiService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String servletPath = request.getServletPath();

        try {
            if ("/api/weather".equals(servletPath)) {
                writeJson(response, apiService.fetchWeather());
                return;
            }
            if ("/api/pension".equals(servletPath)) {
                writeJson(response, apiService.fetchPension(
                        request.getParameter("pageNo"),
                        request.getParameter("numOfRows")
                ));
                return;
            }
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(response, "{\"success\":false,\"message\":\"External API request failed.\"}");
        }
    }

    private void writeJson(HttpServletResponse response, String json) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(json);
    }
}
