package com.axisbank.transit.journey.service;

import com.axisbank.transit.core.model.DAO.BaseEntity;
import com.axisbank.transit.core.shared.utils.DatabaseRawQueryUtils;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

public class GeoTagServiceTests extends BaseEntity {

    @Mock
    DatabaseRawQueryUtils databaseRawQueryUtils;
}
