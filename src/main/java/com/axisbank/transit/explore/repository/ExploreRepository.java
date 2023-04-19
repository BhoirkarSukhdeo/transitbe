package com.axisbank.transit.explore.repository;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.explore.model.DAO.ExploreDAO;
import com.axisbank.transit.explore.model.DTO.NearByExploreDTOInterface;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExploreRepository extends JpaRepository<ExploreDAO, Long> {
    List<ExploreDAO> findAllByExploreTypeAndCurrentStatus(String exploreType, String currentStatus, Pageable requestedPage);
    ExploreDAO findByExploreIdAndIsActive(String exploreId, boolean isActive);
    ExploreDAO findByExploreId(String exploreId);
    List<ExploreDAO> findAllByExploreTypeLike(String exploreId, Pageable requestedPage);
    void deleteByExploreId(String exploreId);
    List<ExploreDAO> findAllByCategoryLikeAndSubTypeLikeAndExploreTypeLikeAndCurrentStatusAndAuthenticationDAOSetOrderByUpdatedAtDesc(String category, String onlineShopping, String exploreType, String currentStatus, AuthenticationDAO authenticationDAO);

    @Query(value = "SELECT e.name AS name, e.title AS title, e.category AS category, e.explore_id AS explore_id, e.sub_type AS sub_type, e.logo_link AS logo_link, a.latitude AS latitude, a.longitude AS longitude \n" +
            "FROM \n" +
            "EXPLORE e LEFT JOIN ADDRESS a ON e.ADDRESS_ID =a.id \n" +
            "WHERE \n" +
            "SDO_WITHIN_DISTANCE(SDO_GEOMETRY(2001, 8307, SDO_POINT_TYPE(:latitude, :longitude, NULL), NULL, NULL),\n" +
            "SDO_GEOMETRY(2001, 8307, SDO_POINT_TYPE(a.LATITUDE , a.LONGITUDE , NULL), NULL, NULL), :radius) = 'TRUE' AND category LIKE :category AND sub_type LIKE :subType AND explore_type LIKE :exploreType AND current_status LIKE :currentStatus", nativeQuery = true)
    List<NearByExploreDTOInterface> findAllExploresByLatLongCategorySubTypeAndExploreTypeAndCurStatus(@Param("latitude") double latitude,
                                                                                                      @Param("longitude") double longitude,
                                                                                                      @Param("radius") String radius,
                                                                                                      @Param("category") String category,
                                                                                                      @Param("subType") String subType,
                                                                                                      @Param("exploreType") String exploreType,
                                                                                                      @Param("currentStatus") String currentStatus
    );

    @Query(value = "SELECT e.explore_id \n" +
            "FROM \n" +
            "EXPLORE e LEFT JOIN ADDRESS a ON e.ADDRESS_ID =a.id \n" +
            "WHERE \n" +
            "SDO_WITHIN_DISTANCE(SDO_GEOMETRY(2001, 8307, SDO_POINT_TYPE(:latitude, :longitude, NULL), NULL, NULL),\n" +
            "SDO_GEOMETRY(2001, 8307, SDO_POINT_TYPE(a.LATITUDE , a.LONGITUDE , NULL), NULL, NULL), :radius) = 'TRUE' AND category LIKE :category AND sub_type LIKE :subType AND explore_type LIKE :exploreType AND current_status LIKE :currentStatus", nativeQuery = true)
    List<String> findAllExploreIdsByLatLongCategorySubTypeAndExploreTypeAndCurStatus(@Param("latitude") double latitude,
                                                                                          @Param("longitude") double longitude,
                                                                                          @Param("radius") String radius,
                                                                                          @Param("category") String category,
                                                                                          @Param("subType") String subType,
                                                                                          @Param("exploreType") String exploreType,
                                                                                          @Param("currentStatus") String currentStatus
                                                                                                      );


    List<ExploreDAO> findAllByExploreTypeAndCurrentStatusAndAuthenticationDAOSet(String promotionalOffer, String published, AuthenticationDAO authenticationDAO);

    List<ExploreDAO> findByExploreIdInAndAuthenticationDAOSet(List<String> exploreIds, AuthenticationDAO authenticationDAO);

    List<ExploreDAO> findAllByCurrentStatusAndIsActive(String currentStatus, boolean active);

    List<ExploreDAO> findAllByCurrentStatusAndIsActiveAndTargetAudienceIsNotNull(String published, boolean active);
}
