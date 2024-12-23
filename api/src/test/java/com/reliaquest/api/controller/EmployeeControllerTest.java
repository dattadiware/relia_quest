package com.reliaquest.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.reliaquest.api.model.EmployeeDto;
import com.reliaquest.api.model.EmployeeRequestDto;
import com.reliaquest.api.service.EmployeeService;
import com.reliaquest.api.testutils.TestDataBuilder;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class EmployeeControllerTest {

    @InjectMocks
    private EmployeeController employeeController;

    @Mock
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllEmployees_ShouldEmployeeList() {
        List<EmployeeDto> mockEmployees = TestDataBuilder.createMockEmployeeList();
        when(employeeService.getAllEmployees()).thenReturn(mockEmployees);

        ResponseEntity<List<EmployeeDto>> response = employeeController.getAllEmployees();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockEmployees, response.getBody());
        assertEquals(3, response.getBody().size());
        assertEquals("abc", response.getBody().get(0).getEmployeeName());
    }

    @Test
    void getAllEmployees_ShouldReturnEmptyList() {
        when(employeeService.getAllEmployees()).thenReturn(Collections.emptyList());

        ResponseEntity<List<EmployeeDto>> response = employeeController.getAllEmployees();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getEmployeesByNameSearch_ShouldReturnEmployee() {
        List<EmployeeDto> mockEmployees = TestDataBuilder.createMockEmployeeList();
        when(employeeService.searchEmployeesByName(anyString())).thenReturn(mockEmployees);

        ResponseEntity<List<EmployeeDto>> response = employeeController.getEmployeesByNameSearch("abc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockEmployees, response.getBody());
        assertEquals("abc", response.getBody().get(0).getEmployeeName());
    }

    @Test
    void getEmployeesByNameSearch_ShouldNotReturnEmployee() {
        when(employeeService.searchEmployeesByName(anyString())).thenReturn(Collections.emptyList());

        ResponseEntity<List<EmployeeDto>> response = employeeController.getEmployeesByNameSearch("NonExistentName");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getEmployeeById_ShouldReturnEmployee() {
        EmployeeDto mockEmployee = TestDataBuilder.createMockEmployee();
        UUID id = mockEmployee.getId();
        when(employeeService.getEmployeeById(id.toString())).thenReturn(mockEmployee);

        ResponseEntity<EmployeeDto> response = employeeController.getEmployeeById(id.toString());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockEmployee, response.getBody());
        assertEquals("abc", response.getBody().getEmployeeName());
    }

    @Test
    void getEmployeeById_ShouldNotReturnEmployee() {
        when(employeeService.getEmployeeById(anyString())).thenReturn(null);

        ResponseEntity<EmployeeDto> response =
                employeeController.getEmployeeById(UUID.randomUUID().toString());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    void getHighestSalaryOfEmployees_ShouldReturnHighestSalary() {
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(70000);

        ResponseEntity<Integer> response = employeeController.getHighestSalaryOfEmployees();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(70000, response.getBody());
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_ShouldReturnTopTen() {
        List<String> mockNames = List.of("John", "Doe");
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(mockNames);

        ResponseEntity<List<String>> response = employeeController.getTopTenHighestEarningEmployeeNames();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockNames, response.getBody());
        assertTrue(response.getBody().contains("John"));
    }

    @Test
    void createEmployee_ShouldCreateEmployee() {
        EmployeeRequestDto requestDto = TestDataBuilder.createMockCreateEmployeeRequestDto();
        EmployeeDto mockEmployee = TestDataBuilder.createMockEmployee();
        when(employeeService.createEmployee(any(EmployeeRequestDto.class))).thenReturn(mockEmployee);

        ResponseEntity<EmployeeDto> response = employeeController.createEmployee(requestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockEmployee, response.getBody());
        assertEquals("abc", response.getBody().getEmployeeName());
    }

    @Test
    void deleteEmployeeById_ShouldDeleteEmployee() {
        when(employeeService.deleteEmployeeById(anyString())).thenReturn("Deleted");

        ResponseEntity<String> response =
                employeeController.deleteEmployeeById(UUID.randomUUID().toString());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Deleted", response.getBody());
    }

    @Test
    void deleteEmployeeById_NotFoundEmployee() {
        when(employeeService.deleteEmployeeById(anyString())).thenReturn("Not Found");

        ResponseEntity<String> response =
                employeeController.deleteEmployeeById(UUID.randomUUID().toString());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Not Found", response.getBody());
    }
}
