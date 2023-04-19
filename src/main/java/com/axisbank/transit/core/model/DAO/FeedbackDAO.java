package com.axisbank.transit.core.model.DAO;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "feedback")
public class FeedbackDAO extends BaseEntity {

    @Column(name= "description", length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private FeedbackCategoryDAO categoryDAO;

    @ManyToOne
    @JoinColumn(name = "authentication_id")
    private AuthenticationDAO authenticationDAO;

}
