package com.axisbank.transit.authentication.repository;

import com.axisbank.transit.authentication.model.DAO.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    public Role findByNameAndIsActive(String name, Boolean isActive);

    List<Role> findAllByNameInAndIsActive(List<String> roles, boolean isActive);

    List<Role> findAllByIsActive(boolean isActive);
}
