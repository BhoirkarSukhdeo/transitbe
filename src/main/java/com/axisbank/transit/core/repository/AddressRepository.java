package com.axisbank.transit.core.repository;

import com.axisbank.transit.core.model.DAO.AddressDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<AddressDAO, Long> {
}
