package kr.or.bit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class EmpDTO {
    private int empNo;
    private String empName;
    private String deptName;
    private String position;
    private int salary;
}
