package com.axisbank.transit.explore.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SlotDTO {
    @JsonFormat(pattern="dd/MM/yyyy")
    private LocalDate startDate;
    @JsonFormat(pattern="dd/MM/yyyy")
    private LocalDate endDate;
    private String startTime;
    private String endTime;
}
