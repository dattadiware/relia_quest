package com.reliaquest.api.service.remote.dto;

import com.reliaquest.api.model.EmployeeDto;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class GetAllEmployeesResponseDto {

    private List<EmployeeDto> data;

    private String status;
}
