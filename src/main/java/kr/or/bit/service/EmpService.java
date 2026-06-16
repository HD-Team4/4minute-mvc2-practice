package kr.or.bit.service;

import kr.or.bit.dao.EmpDAO;
import kr.or.bit.dto.EmpDTO;

import java.util.List;

public class EmpService {
    private final EmpDAO empDAO = new EmpDAO();

    public List<EmpDTO> findAll() {
        return empDAO.findAll();
    }

    public void create(EmpDTO emp) {
        validate(emp, false);
        empDAO.insert(emp);
    }

    public void update(EmpDTO emp) {
        validate(emp, true);
        if (empDAO.update(emp) == 0) {
            throw new IllegalArgumentException("Employee not found.");
        }
    }

    public void delete(int empNo) {
        if (empNo <= 0) {
            throw new IllegalArgumentException("Invalid employee number.");
        }

        if (empDAO.delete(empNo) == 0) {
            throw new IllegalArgumentException("Employee not found.");
        }
    }

    private void validate(EmpDTO emp, boolean requireEmpNo) {
        if (requireEmpNo && emp.getEmpNo() <= 0) {
            throw new IllegalArgumentException("Invalid employee number.");
        }
        if (emp.getEmpName() == null || emp.getEmpName().isBlank()) {
            throw new IllegalArgumentException("Employee name is required.");
        }
        if (emp.getDeptName() == null || emp.getDeptName().isBlank()) {
            throw new IllegalArgumentException("Department is required.");
        }
        if (emp.getPosition() == null || emp.getPosition().isBlank()) {
            throw new IllegalArgumentException("Position is required.");
        }
        if (emp.getSalary() < 0) {
            throw new IllegalArgumentException("Salary must be zero or greater.");
        }
    }
}
