package kr.or.bit.dao;

import kr.or.bit.dto.EmpDTO;
import kr.or.bit.utils.ConnectionHelper;
import kr.or.bit.utils.DBType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmpDAO {
    public List<EmpDTO> findAll() {
        List<EmpDTO> employees = new ArrayList<>();
        String sql = "SELECT empno, ename, deptno, job, sal FROM emp ORDER BY empno DESC";

        try (Connection conn = ConnectionHelper.getConnection(DBType.ORACLE);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                employees.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to select employees.", e);
        }
        return employees;
    }

    public int insert(EmpDTO emp) {
        String sql = "INSERT INTO emp (empno, ename, job, sal, deptno, hiredate) "
                + "VALUES ((SELECT NVL(MAX(empno), 0) + 1 FROM emp), ?, ?, ?, ?, SYSDATE)";

        try (Connection conn = ConnectionHelper.getConnection(DBType.ORACLE);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, emp.getEmpName());
            pstmt.setString(2, emp.getPosition());
            pstmt.setInt(3, emp.getSalary());
            pstmt.setInt(4, parseDeptNo(emp.getDeptName()));
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to insert employee.", e);
        }
    }

    public int update(EmpDTO emp) {
        String sql = "UPDATE emp SET ename = ?, job = ?, sal = ?, deptno = ? WHERE empno = ?";

        try (Connection conn = ConnectionHelper.getConnection(DBType.ORACLE);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, emp.getEmpName());
            pstmt.setString(2, emp.getPosition());
            pstmt.setInt(3, emp.getSalary());
            pstmt.setInt(4, parseDeptNo(emp.getDeptName()));
            pstmt.setInt(5, emp.getEmpNo());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update employee.", e);
        }
    }

    public int delete(int empNo) {
        String sql = "DELETE FROM emp WHERE empno = ?";

        try (Connection conn = ConnectionHelper.getConnection(DBType.ORACLE);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, empNo);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete employee.", e);
        }
    }

    private EmpDTO mapRow(ResultSet rs) throws SQLException {
        return new EmpDTO(
                rs.getInt("empno"),
                rs.getString("ename"),
                String.valueOf(rs.getInt("deptno")),
                rs.getString("job"),
                rs.getInt("sal")
        );
    }

    private int parseDeptNo(String deptName) {
        try {
            return Integer.parseInt(deptName);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Department must be a number. Example: 10, 20, 30, 40");
        }
    }
}
