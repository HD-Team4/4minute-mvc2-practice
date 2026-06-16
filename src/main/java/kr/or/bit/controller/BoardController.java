package kr.or.bit.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/board/*")
public class BoardController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getPathInfo();

        if (path == null || "/".equals(path) || "/list.do".equals(path)) {
            request.getRequestDispatcher("/WEB-INF/views/board/boardList.jsp").forward(request, response);
            return;
        }
        if ("/write.do".equals(path)) {
            request.getRequestDispatcher("/WEB-INF/views/board/boardWrite.jsp").forward(request, response);
            return;
        }
        if ("/detail.do".equals(path)) {
            request.getRequestDispatcher("/WEB-INF/views/board/boardDetail.jsp").forward(request, response);
            return;
        }
        if ("/edit.do".equals(path)) {
            request.getRequestDispatcher("/WEB-INF/views/board/boardEdit.jsp").forward(request, response);
            return;
        }

        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
}
