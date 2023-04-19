package com.axisbank.transit.journey.repository;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.journey.model.DAO.FavouriteAddressDAO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavouriteAddressRepository extends JpaRepository<FavouriteAddressDAO, Long> {
    FavouriteAddressDAO findByAddressId(String addressId);

    List<FavouriteAddressDAO> findAllByFavouriteTypeAndAuthenticationDAO(String favouriteType, AuthenticationDAO authenticationDAO, Pageable requestedPage);
    List<FavouriteAddressDAO> findAllByAuthenticationDAO(AuthenticationDAO authenticationDAO, Pageable requestedPage);
}
