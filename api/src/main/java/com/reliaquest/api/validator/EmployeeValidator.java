package com.reliaquest.api.validator;

import com.reliaquest.api.model.EmployeeRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Employee validator
 */
@Component
@Slf4j
public class EmployeeValidator {

    public void validateEmployeeId(String employeeId) {
        Assert.notNull(employeeId, "Employee id cannot be null. Please provide valid employee id.");

        if (employeeId.isEmpty()) {
            log.error("Invalid Employee id: {}. Employee id cannot be empty", employeeId);
            throw new IllegalArgumentException("Employee id is required and cannot be empty");
        }
    }

    public void validateEmployeeData(EmployeeRequestDto employeeRequestDto) {
        Assert.notNull(employeeRequestDto, "Request body cannot be null. Please provide valid employee data.");

        String name = employeeRequestDto.getName();
        String title = employeeRequestDto.getTitle();
        Integer salary = employeeRequestDto.getSalary();
        Integer age = employeeRequestDto.getAge();

        if (age == null || age < 16 || age > 75) {
            log.error("Invalid Employee age: {}. Age must be between 16 and 75", age);
            throw new IllegalArgumentException("Employee age must be between 16 and 75 years. Provided age: " + age);
        }

        if (name == null || name.isEmpty()) {
            log.error("Invalid Employee name: {}. Name cannot be empty", name);
            throw new IllegalArgumentException("Employee name is required and cannot be empty");
        }

        if (salary == null || salary <= 0) {
            log.error("Invalid Employee salary: {}. Salary must be positive", salary);
            throw new IllegalArgumentException("Employee salary must be greater than zero. Provided salary: " + salary);
        }

        if (title == null || title.isEmpty()) {
            log.error("Invalid Employee title: {}. Title cannot be empty", title);
            throw new IllegalArgumentException("Employee title is required and cannot be empty");
        }
    }
}
