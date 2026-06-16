package kr.or.bit.ajax;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.bit.dto.EmpDTO;
import kr.or.bit.service.EmpService;

import java.io.IOException;
import java.util.List;

@WebServlet("/emp/api/*")
public class EmpAjaxController extends HttpServlet {
    private final EmpService empService = new EmpService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getPathInfo();

        try {
            if ("/list".equals(path)) {
                writeJson(response, toJson(empService.findAll()));
                return;
            }
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(response, resultJson(false, "Employee list request failed."));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getPathInfo();

        try {
            if ("/create".equals(path)) {
                empService.create(toEmp(request, false));
                writeJson(response, resultJson(true, "Employee created."));
                return;
            }
            if ("/update".equals(path)) {
                empService.update(toEmp(request, true));
                writeJson(response, resultJson(true, "Employee updated."));
                return;
            }
            if ("/delete".equals(path)) {
                empService.delete(parseInt(request.getParameter("empNo")));
                writeJson(response, resultJson(true, "Employee deleted."));
                return;
            }
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(response, resultJson(false, e.getMessage()));
        } catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(response, resultJson(false, "Employee request failed."));
        }
    }

    private EmpDTO toEmp(HttpServletRequest request, boolean requireEmpNo) {
        EmpDTO emp = new EmpDTO();
        if (requireEmpNo) {
            emp.setEmpNo(parseInt(request.getParameter("empNo")));
        }
        emp.setEmpName(request.getParameter("empName"));
        emp.setDeptName(request.getParameter("deptName"));
        emp.setPosition(request.getParameter("position"));
        emp.setSalary(parseInt(request.getParameter("salary")));
        return emp;
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number.");
        }
    }

    private void writeJson(HttpServletResponse response, String json) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(json);
    }

    private String toJson(List<EmpDTO> employees) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < employees.size(); i++) {
            EmpDTO emp = employees.get(i);
            if (i > 0) json.append(',');
            json.append('{')
                    .append("\"empNo\":").append(emp.getEmpNo()).append(',')
                    .append("\"empName\":\"").append(escapeJson(emp.getEmpName())).append("\",")
                    .append("\"deptName\":\"").append(escapeJson(emp.getDeptName())).append("\",")
                    .append("\"position\":\"").append(escapeJson(emp.getPosition())).append("\",")
                    .append("\"salary\":").append(emp.getSalary())
                    .append('}');
        }
        return json.append(']').toString();
    }

    private String resultJson(boolean success, String message) {
        return "{\"success\":" + success + ",\"message\":\"" + escapeJson(message) + "\"}";
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "\\n");
    }
}
