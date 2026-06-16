package kr.or.bit.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.bit.config.ApiProperties;

import java.io.IOException;

@WebServlet("/api/*")
public class ApiController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();

        if (path == null || "/".equals(path) || "/dashboard.do".equals(path)) {
            request.getRequestDispatcher("/api.jsp").forward(request, response);
            return;
        }
        if ("/map.do".equals(path)) {
            request.setAttribute("kakaoMapAppKey", ApiProperties.getRequired("kakao.map.app-key"));
            request.getRequestDispatcher("/map.jsp").forward(request, response);
            return;
        }

        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
}
