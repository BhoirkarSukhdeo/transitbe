package com.axisbank.transit.authentication.config;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.authentication.model.DAO.Role;
import com.axisbank.transit.authentication.repository.RoleRepository;
import com.axisbank.transit.core.shared.constants.RoleConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.ArgumentMatchers.any;
import static reactor.core.publisher.Mono.when;

public class ApplicationSetupDataTests extends BaseTest {

    @InjectMocks
    @Autowired
    ApplicationSetupData applicationSetupData;

    @Mock
    RoleRepository roleRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createRoleIfNotFoundTest() {
        Role role = null;
        Mockito.when(roleRepository.findByNameAndIsActive(any(String.class), any(Boolean.class))).thenReturn(role);
        role = new Role();
        role.setName(RoleConstants.ADMIN_ROLE);
        Mockito.when(roleRepository.save(role)).thenReturn(role);
        Assert.assertEquals(RoleConstants.ADMIN_ROLE, role.getName());
    }

}
