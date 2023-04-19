package com.axisbank.transit.journey.services.impl;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.journey.model.DAO.FavouriteAddressDAO;
import com.axisbank.transit.journey.model.DTO.FavouriteAddressDTO;
import com.axisbank.transit.journey.repository.FavouriteAddressRepository;
import com.axisbank.transit.journey.services.FavouriteAddressService;
import com.axisbank.transit.userDetails.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
public class FavouriteAddressServiceImpl implements FavouriteAddressService {
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    UserUtil userUtil;

    @Autowired
    FavouriteAddressRepository favouriteAddressRepository;

    public void saveFavouriteAddress(FavouriteAddressDTO favouriteAddressDTO) throws Exception {
        log.info("Request received in updateUserDetails method: "+favouriteAddressDTO);
        try {
            AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
            FavouriteAddressDAO favouriteAddressDAO = modelMapper.map(favouriteAddressDTO, FavouriteAddressDAO.class);
            favouriteAddressDAO.setAddressId(CommonUtils.generateRandomString(30));
            favouriteAddressDAO.setAuthenticationDAO(authenticationDAO);
            favouriteAddressRepository.save(favouriteAddressDAO);
        } catch (Exception exception) {
            log.error("Error in save Fav address: {}", exception.getMessage());
            throw exception;
        }
    }

    public List<FavouriteAddressDTO> getFavouriteAddresses(int page, int size, String favouriteType) throws Exception {
        log.info("Request received in getDeviceInfo method");
        List<FavouriteAddressDTO> favouriteAddressDTOList = new ArrayList<>();
        List<FavouriteAddressDAO> favouriteAddressDAOList = new ArrayList<>();
        try {
            AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
            Pageable requestedPage = PageRequest.of(page, size, Sort.by("updatedAt").descending());
            if (favouriteType != null) {
                favouriteAddressDAOList = favouriteAddressRepository.findAllByFavouriteTypeAndAuthenticationDAO(favouriteType, authenticationDAO, requestedPage);
            } else {
                favouriteAddressDAOList = favouriteAddressRepository.findAllByAuthenticationDAO(authenticationDAO, requestedPage);
            }

            for (FavouriteAddressDAO favouriteAddressDAO : favouriteAddressDAOList) {
                FavouriteAddressDTO favouriteAddressDTO = modelMapper.map(favouriteAddressDAO, FavouriteAddressDTO.class);
                favouriteAddressDTOList.add(favouriteAddressDTO);
            }
        } catch (Exception exception) {
            log.error("Error in getting fav addresses: {}", exception.getMessage());
            throw exception;
        }
        return favouriteAddressDTOList;
    }

    public void deleteFavouriteAddress(String addressId) throws Exception {
        log.info("Request received in deleteFavouriteAddress method");
        try {
            FavouriteAddressDAO favouriteAddressDAO = favouriteAddressRepository.findByAddressId(addressId);
            favouriteAddressRepository.deleteById(favouriteAddressDAO.getId());
        } catch (Exception exception) {
            log.error("Error in delete fav address: {}", exception.getMessage());
            throw exception;
        }
    }
}
