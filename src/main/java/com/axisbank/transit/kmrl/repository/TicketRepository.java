package com.axisbank.transit.kmrl.repository;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.kmrl.model.DAO.TicketDAO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<TicketDAO, Long> {

    List<TicketDAO> findAllByAuthenticationDAOAndTicketStatusLikeAndFromMetroStationLikeAndToMetroStationLike(AuthenticationDAO authenticationDAO, String status, String from, String to, Pageable paging);
    List<TicketDAO> findAllByAuthenticationDAOAndTicketStatusLike(AuthenticationDAO authenticationDAO, String status, Pageable paging);
    TicketDAO findByAuthenticationDAOAndTicketRefId(AuthenticationDAO authenticationDAO,String ticketRefId);
    TicketDAO findByAuthenticationDAO_IdAndTicketRefId(long authId,String ticketRefId);

    List<TicketDAO> findAllByAuthenticationDAO_IdAndTicketStatus(long id, String ticketStatus);
}
