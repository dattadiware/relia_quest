package com.reliaquest.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.model.EmployeeDto;
import com.reliaquest.api.model.EmployeeRequestDto;
import com.reliaquest.api.service.impl.EmployeeServiceImpl;
import com.reliaquest.api.service.remote.EmployeeRemoteService;
import com.reliaquest.api.service.remote.dto.GetAllEmployeesResponseDto;
import com.reliaquest.api.testutils.TestDataBuilder;
import com.reliaquest.api.validator.EmployeeValidator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class EmployeeServiceImplTest {

    @Mock
    private EmployeeRemoteService employeeRemoteService;

    @Mock
    private EmployeeValidator employeeValidator;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private List<EmployeeDto> mockEmployeeList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockEmployeeList = TestDataBuilder.createGetAllEmployeesResponse().getData();
    }

    @Test
    void testGetAllEmployees_ShouldReturnAllEmployees() {
        GetAllEmployeesResponseDto mockResponse =
                TestDataBuilder.createGetAllEmployeesResponse(); // Create the mock response
        CompletableFuture<GetAllEmployeesResponseDto> mockFuture = CompletableFuture.completedFuture(mockResponse);

        when(employeeRemoteService.getAllEmployees()).thenReturn(mockFuture);

        // Call the method under test
        List<EmployeeDto> result = employeeService.getAllEmployees();

        // Verify the results
        assertEquals(3, result.size());
        assertEquals("abc", result.get(0).getEmployeeName());
    }

    @Test
    void testGetEmployeeById_ShouldReturnEmployee() {
        UUID uuid = mockEmployeeList.get(0).getId();
        when(employeeRemoteService.getEmployeeById(uuid)).thenReturn(TestDataBuilder.createGetEmployeeResponse());

        EmployeeDto result = employeeService.getEmployeeById(uuid.toString());

        assertEquals("abc", result.getEmployeeName());
    }

    @Test
    void testGetEmployeeById_WithInvalidId_ShouldThrowIllegalArgumentException() {
        String invalidId = "invalid-uuid";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.getEmployeeById(invalidId);
        });
        assertEquals(
                "Invalid employee id : " + invalidId + ", Requires employee id in UUID format.",
                exception.getMessage());
    }

    @Test
    void testDeleteEmployeeById_ShouldDeleteEmployee() {
        UUID uuid = mockEmployeeList.get(0).getId();
        when(employeeRemoteService.getEmployeeById(uuid)).thenReturn(TestDataBuilder.createGetEmployeeResponse());
        when(employeeRemoteService.deleteEmployeeByName(any()))
                .thenReturn(TestDataBuilder.createDeleteEmployeeResponse());

        String result = employeeService.deleteEmployeeById(uuid.toString());

        assertEquals("Employee deleted successfully", result);
    }

    @Test
    void testDeleteEmployeeById_WithNonExistentId_ShouldThrowEmployeeNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();
        when(employeeRemoteService.getEmployeeById(nonExistentId))
                .thenThrow(new EmployeeNotFoundException("Employee with id: " + nonExistentId + " not found"));

        Exception exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.deleteEmployeeById(nonExistentId.toString());
        });
        assertEquals("Employee with id: " + nonExistentId + " not found", exception.getMessage());
    }

    @Test
    void testCreateEmployee_ShouldCreateEmployee() {
        EmployeeRequestDto request = TestDataBuilder.createMockCreateEmployeeRequestDto();
        when(employeeRemoteService.createEmployee(any())).thenReturn(TestDataBuilder.createCreateEmployeeResponse());

        EmployeeDto result = employeeService.createEmployee(request);

        assertEquals("abc", result.getEmployeeName());
    }

    @Test
    void testCreateEmployee_InvalidData_ShouldThrowValidationException() {
        EmployeeRequestDto invalidRequest = EmployeeRequestDto.builder().build();
        doThrow(new IllegalArgumentException("Invalid employee data"))
                .when(employeeValidator)
                .validateEmployeeData(invalidRequest);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.createEmployee(invalidRequest);
        });
        assertEquals("Invalid employee data", exception.getMessage());
    }

    @Test
    void testSearchEmployeesByName_ShouldReturnEmployee() {
        GetAllEmployeesResponseDto mockResponse =
                TestDataBuilder.createGetAllEmployeesResponse(); // Create the mock response
        CompletableFuture<GetAllEmployeesResponseDto> mockFuture = CompletableFuture.completedFuture(mockResponse);

        when(employeeRemoteService.getAllEmployees()).thenReturn(mockFuture);

        List<EmployeeDto> result = employeeService.searchEmployeesByName("abc");

        assertEquals(1, result.size());
        assertEquals("abc", result.get(0).getEmployeeName());
    }

    @Test
    void testSearchEmployeesByName_EmptyName_ShouldThrowIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.searchEmployeesByName("");
        });
        assertEquals("Search string(name) cannot be empty", exception.getMessage());
    }

    @Test
    void testGetHighestSalaryOfEmployees_ShouldReturnEmployee() {
        GetAllEmployeesResponseDto mockResponse =
                TestDataBuilder.createGetAllEmployeesResponse(); // Create the mock response
        CompletableFuture<GetAllEmployeesResponseDto> mockFuture = CompletableFuture.completedFuture(mockResponse);

        when(employeeRemoteService.getAllEmployees()).thenReturn(mockFuture);

        int highestSalary = employeeService.getHighestSalaryOfEmployees();

        assertEquals(7000, highestSalary);
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames_ShouldReturn10Employees() {
        GetAllEmployeesResponseDto mockResponse =
                TestDataBuilder.createGetAllEmployeesResponse(); // Create the mock response
        CompletableFuture<GetAllEmployeesResponseDto> mockFuture = CompletableFuture.completedFuture(mockResponse);
        when(employeeRemoteService.getAllEmployees()).thenReturn(mockFuture);

        List<String> result = employeeService.getTopTenHighestEarningEmployeeNames();

        assertEquals(3, result.size());
        assertEquals("xyz", result.get(0));
    }
}
