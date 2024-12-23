package com.reliaquest.api.service;

import com.reliaquest.api.model.EmployeeDto;
import com.reliaquest.api.model.EmployeeRequestDto;
import java.util.List;

/**
 * Employee services
 */
public interface EmployeeService {

    /**
     * Get all employees
     *
     * @return {@link EmployeeDto} Return list of Employees
     */
    List<EmployeeDto> getAllEmployees();

    /**
     * Get Employee by employee id
     *
     * @param id UUID of the employee
     * @return {@link EmployeeDto}
     */
    EmployeeDto getEmployeeById(String id);

    /**
     * Delete employee by employee id
     *
     * @param id employee id
     * @return Returns String
     */
    String deleteEmployeeById(String id);

    /**
     * Create new employee
     *
     * @param employeeRequestDto
     * @return {@link EmployeeDto}
     */
    EmployeeDto createEmployee(EmployeeRequestDto employeeRequestDto);

    /**
     * Search employee
     *
     * @param name Name of employee
     * @return {@link EmployeeDto}
     */
    List<EmployeeDto> searchEmployeesByName(String name);

    /**
     * Get highest salary
     *
     * @return Integer salary
     */
    int getHighestSalaryOfEmployees();

    /**
     * get top ten salaried employees
     *
     * @return List of Employee Names
     */
    List<String> getTopTenHighestEarningEmployeeNames();
}
