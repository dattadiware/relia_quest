package com.reliaquest.api.testutils;

import com.reliaquest.api.model.EmployeeDto;
import com.reliaquest.api.model.EmployeeRequestDto;
import com.reliaquest.api.service.remote.dto.DeleteEmployeeResponseDto;
import com.reliaquest.api.service.remote.dto.EmployeeResponseDto;
import com.reliaquest.api.service.remote.dto.GetAllEmployeesResponseDto;
import com.reliaquest.api.service.remote.dto.GetEmployeeResponseDto;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestDataBuilder {

    public static EmployeeRequestDto createMockCreateEmployeeRequestDto() {
        return EmployeeRequestDto.builder()
                .name("abc")
                .salary(50000)
                .age(30)
                .title("Software Engineer")
                .build();
    }

    public static EmployeeDto createMockEmployee() {
        return EmployeeDto.builder()
                .id(UUID.randomUUID())
                .employeeName("abc")
                .employeeSalary(5000)
                .build();
    }

    public static List<EmployeeDto> createMockEmployeeList() {

        List<EmployeeDto> employeeList = new ArrayList<>();

        employeeList.add(EmployeeDto.builder()
                .id(UUID.randomUUID())
                .employeeName("abc")
                .employeeSalary(5000)
                .build());

        employeeList.add(EmployeeDto.builder()
                .id(UUID.randomUUID())
                .employeeName("xyz")
                .employeeSalary(7000)
                .build());

        employeeList.add(EmployeeDto.builder()
                .id(UUID.randomUUID())
                .employeeName("pqr")
                .employeeSalary(3000)
                .build());
        return employeeList;
    }

    public static GetAllEmployeesResponseDto createGetAllEmployeesResponse() {
        return GetAllEmployeesResponseDto.builder()
                .data(createMockEmployeeList())
                .status("SUCCESS")
                .build();
    }

    public static GetEmployeeResponseDto createGetEmployeeResponse() {
        return GetEmployeeResponseDto.builder()
                .data(EmployeeDto.builder()
                        .id(UUID.randomUUID())
                        .employeeName("abc")
                        .employeeSalary(5000)
                        .build())
                .status("SUCCESS")
                .build();
    }

    public static EmployeeResponseDto createCreateEmployeeResponse() {
        return EmployeeResponseDto.builder()
                .data(EmployeeDto.builder()
                        .id(UUID.randomUUID())
                        .employeeName("abc")
                        .employeeSalary(4000)
                        .email("abc@xyz.com")
                        .build())
                .status("CREATED")
                .build();
    }

    public static DeleteEmployeeResponseDto createDeleteEmployeeResponse() {
        return DeleteEmployeeResponseDto.builder().data(true).status("DELETED").build();
    }
}
