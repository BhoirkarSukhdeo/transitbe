package com.axisbank.transit.core.model.DAO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "feedback_category")
public class FeedbackCategoryDAO extends BaseEntity {

    @Column(name = "category_id")
    private String categoryId;

    @Column(name= "display_name")
    private String displayName;

    @Column(name= "description")
    private String description;

    @OneToMany(mappedBy = "authenticationDAO")
    private Set<FeedbackDAO> feedbackDAOSet;

}
