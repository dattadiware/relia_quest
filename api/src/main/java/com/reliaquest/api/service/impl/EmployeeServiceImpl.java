/**
 * This Java class, EmployeeServiceImpl, implements the EmployeeService interface and provides
 * various methods to manage employee data. It utilizes a remote service to fetch, create, delete,
 * and search for employees. The class includes methods to: - Retrieve all employees. - Get a
 * specific employee by ID. - Delete an employee by ID. - Create a new employee. - Search for
 * employees by name. - Get the highest salary among employees. - Retrieve the top ten
 * highest-earning employee names.
 * <p>
 * The class also ensures validation of employee data and IDs, and it handles exceptions for invalid
 * data or operations.
 */
package com.reliaquest.api.service.impl;

import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.model.EmployeeDto;
import com.reliaquest.api.model.EmployeeRequestDto;
import com.reliaquest.api.service.EmployeeService;
import com.reliaquest.api.service.remote.EmployeeRemoteService;
import com.reliaquest.api.service.remote.dto.DeleteEmployeeResponseDto;
import com.reliaquest.api.service.remote.dto.EmployeeResponseDto;
import com.reliaquest.api.service.remote.dto.GetAllEmployeesResponseDto;
import com.reliaquest.api.service.remote.dto.GetEmployeeResponseDto;
import com.reliaquest.api.validator.EmployeeValidator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRemoteService employeeRemoteService;
    private final EmployeeValidator employeeValidator;

    /**
     * Retrieves a list of all employees.
     *
     * @return List of EmployeeDto objects representing all employees.
     */
    @Override
    public List<EmployeeDto> getAllEmployees() {
        log.info("Getting all employees");
        CompletableFuture<GetAllEmployeesResponseDto> futureResponse = employeeRemoteService.getAllEmployees();
        GetAllEmployeesResponseDto responseDto = futureResponse.join();
        return responseDto.getData();
    }

    /**
     * Retrieves an employee's details by their unique ID.
     *
     * @param id The unique identifier of the employee.
     * @return EmployeeDto object containing the employee's details.
     */
    @Override
    public EmployeeDto getEmployeeById(String id) {
        log.info("Getting employee by id: {}", id);
        employeeValidator.validateEmployeeId(id);
        UUID uuid = getValidUUID(id);
        GetEmployeeResponseDto getEmployeeResponseDto = employeeRemoteService.getEmployeeById(uuid);
        return getEmployeeResponseDto.getData();
    }

    /**
     * Deletes an employee by their unique ID.
     *
     * @param id The unique identifier of the employee.
     * @return A message indicating the success of the deletion.
     * @throws EmployeeNotFoundException if the employee with the given ID is not found.
     */
    @Override
    public String deleteEmployeeById(String id) {
        employeeValidator.validateEmployeeId(id);
        EmployeeDto employeeDto = getEmployeeById(id);
        DeleteEmployeeResponseDto deleteEmployeeResponseDto =
                employeeRemoteService.deleteEmployeeByName(employeeDto.getEmployeeName());
        if (!deleteEmployeeResponseDto.getData()) {
            throw new EmployeeNotFoundException("Employee with id: " + id + " not found");
        }
        return "Employee deleted successfully";
    }

    /**
     * Creates a new employee with the provided data.
     *
     * @param employeeRequestDto The data required to create the employee.
     * @return EmployeeDto object containing the newly created employee's details.
     */
    @Override
    public EmployeeDto createEmployee(EmployeeRequestDto employeeRequestDto) {
        employeeValidator.validateEmployeeData(employeeRequestDto);
        EmployeeResponseDto createEmployeeResponseDto = employeeRemoteService.createEmployee(employeeRequestDto);
        return createEmployeeResponseDto.getData();
    }

    /**
     * Searches for employees whose names contain the given string.
     *
     * @param name The string to search for within employee names.
     * @return List of EmployeeDto objects representing employees whose names match the search
     * criteria.
     * @throws IllegalArgumentException if the search string is null or empty.
     */
    @Override
    public List<EmployeeDto> searchEmployeesByName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Search string(name) cannot be empty");
        }
        List<EmployeeDto> employeeDtoList = getAllEmployees();
        return employeeDtoList.stream()
                .filter(employeeDto ->
                        employeeDto.getEmployeeName().toLowerCase().contains(name.toLowerCase()))
                .toList();
    }

    /**
     * Retrieves the highest salary among all employees.
     *
     * @return The highest salary value.
     * @throws EmployeeNotFoundException if no employees are found.
     */
    @Override
    public int getHighestSalaryOfEmployees() {
        List<EmployeeDto> employeeDtoList = getAllEmployees();
        OptionalInt maxSalary = employeeDtoList.stream()
                .mapToInt(EmployeeDto::getEmployeeSalary)
                .max();
        if (maxSalary.isPresent()) {
            return maxSalary.getAsInt();
        } else {
            throw new EmployeeNotFoundException("No employees found with highest salary");
        }
    }

    /**
     * Retrieves the names of the top ten highest-earning employees.
     *
     * @return List of names of the top ten highest-earning employees.
     */
    @Override
    public List<String> getTopTenHighestEarningEmployeeNames() {
        List<EmployeeDto> employeeDtoList = getAllEmployees();
        employeeDtoList.sort(Comparator.comparingInt(EmployeeDto::getEmployeeSalary));
        Collections.reverse(employeeDtoList);
        return employeeDtoList.stream()
                .limit(10)
                .map(EmployeeDto::getEmployeeName)
                .toList();
    }

    /**
     * Validates and converts a string ID to a UUID.
     *
     * @param id The string representation of the UUID.
     * @return The UUID object.
     * @throws IllegalArgumentException if the ID is not a valid UUID.
     */
    private UUID getValidUUID(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid employee id : " + id + ", Requires employee id in UUID format.");
        }
    }
}
