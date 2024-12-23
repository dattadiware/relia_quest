package com.reliaquest.api.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.reliaquest.api.model.EmployeeRequestDto;
import com.reliaquest.api.testutils.TestDataBuilder;
import org.junit.jupiter.api.Test;

public class EmployeeValidatorTest {

    private EmployeeValidator validator = new EmployeeValidator();

    @Test
    void testValidEmployeeData() {
        EmployeeRequestDto validEmployee = TestDataBuilder.createMockCreateEmployeeRequestDto();
        assertDoesNotThrow(() -> validator.validateEmployeeData(validEmployee));
    }

    @Test
    void testNullAgeThrowsException() {
        EmployeeRequestDto validEmployee = TestDataBuilder.createMockCreateEmployeeRequestDto();
        validEmployee.setAge(null);

        Exception exception =
                assertThrows(IllegalArgumentException.class, () -> validator.validateEmployeeData(validEmployee));
    }

    @Test
    void testAgeBelowRangeThrowsException() {
        EmployeeRequestDto belowAge = TestDataBuilder.createMockCreateEmployeeRequestDto();
        belowAge.setAge(15);

        Exception exception =
                assertThrows(IllegalArgumentException.class, () -> validator.validateEmployeeData(belowAge));
    }

    @Test
    void testAgeAboveRangeThrowsException() {
        EmployeeRequestDto aboveAge = TestDataBuilder.createMockCreateEmployeeRequestDto();
        aboveAge.setAge(76);
        Exception exception =
                assertThrows(IllegalArgumentException.class, () -> validator.validateEmployeeData(aboveAge));
    }

    @Test
    void testNullNameThrowsException() {
        EmployeeRequestDto nullName = TestDataBuilder.createMockCreateEmployeeRequestDto();
        nullName.setName(null);
        Exception exception =
                assertThrows(IllegalArgumentException.class, () -> validator.validateEmployeeData(nullName));
    }

    @Test
    void testEmptyNameThrowsException() {
        EmployeeRequestDto emptyName = TestDataBuilder.createMockCreateEmployeeRequestDto();
        emptyName.setName("");

        Exception exception =
                assertThrows(IllegalArgumentException.class, () -> validator.validateEmployeeData(emptyName));
    }

    @Test
    void testNullSalaryThrowsException() {
        EmployeeRequestDto nullSalary = TestDataBuilder.createMockCreateEmployeeRequestDto();
        nullSalary.setSalary(null);

        Exception exception =
                assertThrows(IllegalArgumentException.class, () -> validator.validateEmployeeData(nullSalary));
    }

    @Test
    void testZeroSalaryThrowsException() {
        EmployeeRequestDto zeroSalary = TestDataBuilder.createMockCreateEmployeeRequestDto();
        zeroSalary.setSalary(0);

        Exception exception =
                assertThrows(IllegalArgumentException.class, () -> validator.validateEmployeeData(zeroSalary));
    }

    @Test
    void testNegativeSalaryThrowsException() {

        EmployeeRequestDto negativeSalary = TestDataBuilder.createMockCreateEmployeeRequestDto();
        negativeSalary.setSalary(-100);

        Exception exception =
                assertThrows(IllegalArgumentException.class, () -> validator.validateEmployeeData(negativeSalary));
    }

    @Test
    void testNullTitleThrowsException() {
        EmployeeRequestDto nullTitle = TestDataBuilder.createMockCreateEmployeeRequestDto();
        nullTitle.setTitle(null);
        Exception exception =
                assertThrows(IllegalArgumentException.class, () -> validator.validateEmployeeData(nullTitle));
    }

    @Test
    void testEmptyTitleThrowsException() {
        EmployeeRequestDto emptyTitle = TestDataBuilder.createMockCreateEmployeeRequestDto();
        emptyTitle.setTitle("");

        Exception exception =
                assertThrows(IllegalArgumentException.class, () -> validator.validateEmployeeData(emptyTitle));
    }
}
