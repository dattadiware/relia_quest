package com.reliaquest.api.controller;

import com.reliaquest.api.model.EmployeeDto;
import com.reliaquest.api.model.EmployeeRequestDto;
import com.reliaquest.api.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class EmployeeController implements IEmployeeController<EmployeeDto, EmployeeRequestDto> {

    private final EmployeeService employeeService;

    /**
     * Get a list of all employees.
     */
    @Operation(summary = "Get all employees")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Found the employees",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = EmployeeDto.class))
                        })
            })
    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        log.info("request for {} ", "getAllEmployees");
        List<EmployeeDto> employeeDtoList = employeeService.getAllEmployees();
        return ResponseEntity.status(HttpStatus.OK).body(employeeDtoList);
    }

    /**
     * Get the highest salary of employees.
     */
    @Operation(summary = "Get the highest salary of employees")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Retrieved highest salary",
                        content = @Content(mediaType = "application/json"))
            })
    @GetMapping("/highestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        log.info("API request to get the highest salary of employee");

        Integer maxSalary = employeeService.getHighestSalaryOfEmployees();
        return ResponseEntity.status(HttpStatus.OK).body(maxSalary);
    }

    /**
     * Get top ten highest earning employee names.
     */
    @Operation(summary = "Get top ten highest earning employee names")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Retrieved top ten employee names",
                        content = @Content(mediaType = "application/json"))
            })
    @GetMapping("/topTenHighestEarningEmployeeNames")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        log.info("API request to get the Top 10 highest salaried employees");

        List<String> employeeNamesList = employeeService.getTopTenHighestEarningEmployeeNames();
        return ResponseEntity.status(HttpStatus.OK).body(employeeNamesList);
    }

    /**
     * Get an employee by ID.
     */
    @Operation(summary = "Get an employee by ID")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Found the employee",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = EmployeeDto.class))
                        }),
                @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content)
            })
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable("id") String id) {
        log.info("API request to get employee by id: {}", id);

        EmployeeDto employeeDto = employeeService.getEmployeeById(id);
        return ResponseEntity.status(HttpStatus.OK).body(employeeDto);
    }

    /**
     * Search employees by name.
     */
    @Operation(summary = "Search employees by name")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Found the employees",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = EmployeeDto.class))
                        })
            })
    @GetMapping("/search/{searchString}")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByNameSearch(
            @PathVariable("searchString") String searchString) {
        log.info("API request to search employees by name: {}", searchString);

        List<EmployeeDto> employeeDtoList = employeeService.searchEmployeesByName(searchString);
        return ResponseEntity.status(HttpStatus.OK).body(employeeDtoList);
    }

    /**
     * Create a new employee.
     */
    @Operation(summary = "Create a new employee")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Employee successfully created",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = EmployeeDto.class))
                        })
            })
    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@RequestBody EmployeeRequestDto employeeInput) {
        log.info("API request to create employee");

        EmployeeDto createdEmployee = employeeService.createEmployee(employeeInput);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }

    /**
     * Delete an employee by ID.
     */
    @Operation(summary = "Delete an employee by ID")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Employee successfully deleted",
                        content = @Content(mediaType = "application/json")),
                @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content)
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable("id") String id) {
        log.info("API request to delete employee by id: {}", id);

        String response = employeeService.deleteEmployeeById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
