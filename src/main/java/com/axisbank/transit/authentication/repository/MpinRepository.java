package com.axisbank.transit.authentication.repository;

import com.axisbank.transit.authentication.model.DAO.MpinLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MpinRepository extends JpaRepository<MpinLog, Long> {
    List<MpinLog> findAllByAuthenticationDAO_Id(long id, Pageable requestedPage);
}
