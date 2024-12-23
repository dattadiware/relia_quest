package com.reliaquest.api.service.remote.dto;

import com.reliaquest.api.model.EmployeeDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmployeeResponseDto {

    private EmployeeDto data;

    private String status;
}
