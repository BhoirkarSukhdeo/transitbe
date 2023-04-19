package com.axisbank.transit.authentication.model.DAO;

import com.axisbank.transit.core.model.DAO.BaseEntity;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.List;

@Entity(name = "roles")
@Audited
public class Role extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToMany(targetEntity = AuthenticationDAO.class, mappedBy = "roles")
    private List<AuthenticationDAO> authenticationDAOS;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<AuthenticationDAO> getAuthenticationDAOS() {
        return authenticationDAOS;
    }

    public void setAuthenticationDAOS(List<AuthenticationDAO> authenticationDAOS) {
        this.authenticationDAOS = authenticationDAOS;
    }
}
