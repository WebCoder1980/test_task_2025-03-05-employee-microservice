package ru.isands.test.estore.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ElectroEmployeeDTO {
    @NotNull
    private Long electroTypeId;

    @NotNull
    private Long employeeId;
}
