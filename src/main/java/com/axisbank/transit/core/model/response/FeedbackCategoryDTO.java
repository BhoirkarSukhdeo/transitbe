package com.axisbank.transit.core.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackCategoryDTO {
   private String categoryId;
   private String categoryName;
   private String description;
}
