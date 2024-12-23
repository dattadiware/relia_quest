package com.reliaquest.api.service.remote.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteEmployeeResponseDto {

    private Boolean data;

    private String status;
}
