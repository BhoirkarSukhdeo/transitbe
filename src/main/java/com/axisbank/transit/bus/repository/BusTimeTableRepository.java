package com.axisbank.transit.bus.repository;

import com.axisbank.transit.bus.model.DAO.BusTimeTable;
import com.axisbank.transit.bus.model.DTO.BusTimeTableFareInterface;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.util.List;

@Repository
public interface BusTimeTableRepository extends JpaRepository<BusTimeTable, Long> {
    @Query(value = "select " +
            "bt.trip_number as trip_number, bt.route_type as trip_type, " +
            "TO_CHAR(bt.arival_time, 'hh24:mi:ss') as source_arival, TO_CHAR(bt2.arival_time, 'hh24:mi:ss') as destination_arival, " +
            "bf.fare as fare, br.route_name as route_name, br.bus_type as bus_type, " +
            "bs.display_name as source_display_name, bs2.display_name as destination_display_name, " +
            "bs.latitude as source_latitude,bs.longitude as source_longitude, " +
            "bs2.latitude as destination_latitude, bs2.longitude as destination_longitude " +
            "from " +
            "bus_timetable bt, bus_timetable bt2, bus_station bs , bus_station bs2 ,bus_route br, " +
            "bus_fare bf " +
            "where " +
            "bs.station_id = :sourceStationCode " +
            "and " +
            "bs2.station_id= :destinationStationCode " +
            "and " +
            "br.route_code like :routeCode " +
            "and " +
            "bt.ROUTE_ID =br.id " +
            "and " +
            "bt2.ROUTE_ID =br.id " +
            "and " +
            "bt.station_id =bs.id " +
            "and " +
            "bt2.station_id =bs2.id " +
            "and " +
            "bf.ROUTE_ID = br.id " +
            "and " +
            "bf.from_station_id = bs.id " +
            "and " +
            "bf.to_station_id =bs2.id " +
            "and " +
            "bt.ROUTE_ID = bt2.ROUTE_ID " +
            "and " +
            "bt.route_type = bt2.route_type " +
            "and " +
            "bt.sr_num < bt2.sr_num " +
            "and " +
            "bt.trip_number=bt2.trip_number " +
            "AND bt.arival_time > TO_DATE(:startTime, 'yyyy-MM-dd hh24:mi:ss') "+
            "ORDER by bt.arival_time asc", nativeQuery = true)
    List<BusTimeTableFareInterface> getMetroRouteStationsTimetableAndFare(@Param("sourceStationCode") String sourceStationCode,
                                                                          @Param("destinationStationCode")String destinationStationCode,
                                                                          @Param("routeCode") String routeCode,
                                                                          @Param("startTime") String startTime);

    List<BusTimeTable> findAllByBusStation_StationIdAndArivalTimeGreaterThanOrderByArivalTimeAsc(String sourceStation,
                                                                                               Time currentTime);

    List<BusTimeTable> findAllByBusRoute_RouteCodeContainingIgnoreCaseOrBusStation_DisplayNameContainingIgnoreCase(String routeCode, String stationName, Pageable paging);

    BusTimeTable findByTimeTableId(String timeTableId);
}
