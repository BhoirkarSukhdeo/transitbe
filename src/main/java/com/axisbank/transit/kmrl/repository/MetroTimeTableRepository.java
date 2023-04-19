package com.axisbank.transit.kmrl.repository;

import com.axisbank.transit.kmrl.model.DAO.MetroStation;
import com.axisbank.transit.kmrl.model.DAO.MetroTimeTable;
import com.axisbank.transit.kmrl.model.DAO.MetroTrip;
import com.axisbank.transit.kmrl.model.DTO.MetroTimeTableDTOInterface;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Time;
import java.util.List;

public interface MetroTimeTableRepository extends JpaRepository<MetroTimeTable, Long> {

    List<MetroTimeTable> findAllByStationAndArivalTimeGreaterThanEqualAndArivalTimeLessThanEqual(MetroStation station,
                                                                                                 Time t1, Time t2);

    List<MetroTimeTable> findAllByStationAndArivalTimeGreaterThanEqual(MetroStation station, Time t1);

    List<MetroTimeTable> findAllByTripAndSrNumGreaterThanAndSrNumLessThan(MetroTrip trip, long sourceSrNum,
                                                                          long destSrNum);
    MetroTimeTable findByStationAndTripAndSrNumGreaterThan(MetroStation station, MetroTrip trip, long srNum);

    @Query(value ="SELECT TO_CHAR(mt.ARIVAL_TIME, 'hh24:mi:ss') arrival_time, mt.SR_NUM source_sr_num, mt2.SR_NUM dest_sr_num, mt.METRO_TRIP_ID FROM METRO_TIMETABLE mt," +
            " METRO_TIMETABLE mt2 WHERE mt.METRO_TRIP_ID =mt2.METRO_TRIP_ID AND mt.SR_NUM < mt2.SR_NUM AND" +
            " mt.METRO_STATION_ID = :sourceStationId AND mt2.METRO_STATION_ID = :destinationStationId" +
            " AND mt.ARIVAL_TIME > TO_DATE(:startTime, 'yyyy-MM-dd hh24:mi:ss') ORDER BY mt.ARIVAL_TIME ASC", nativeQuery = true)
    List <MetroTimeTableDTOInterface> findAllTripsBetweenStations(@Param("sourceStationId") long sourceStationId, @Param("destinationStationId")long destinationStationId,
                                                                  @Param("startTime") String startTime);

    List<MetroTimeTable> findAllByTripIdAndSrNumBetweenOrderBySrNum(long tripId, long sourceSrNum, long destSrNum);

    List<MetroTimeTable> findAllByStationAndDepartureTimeGreaterThanOrderByDepartureTimeAsc(MetroStation station, Time t1);

    List<MetroTimeTable> findAllByStationNameContainingIgnoreCaseOrMetroLineNameContainingIgnoreCase(String stationName, String metroLineName, Pageable paging);
}
