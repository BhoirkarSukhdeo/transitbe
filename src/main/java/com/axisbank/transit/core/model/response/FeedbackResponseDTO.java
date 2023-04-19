package com.axisbank.transit.core.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackResponseDTO {
    private String categoryId;
    private String categoryName;
    private String description;
    private String createdAt;
    private String createdBy;
}
