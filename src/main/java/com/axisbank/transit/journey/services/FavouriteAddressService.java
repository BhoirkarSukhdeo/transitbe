package com.axisbank.transit.journey.services;

import com.axisbank.transit.journey.model.DTO.FavouriteAddressDTO;

import java.util.List;

public interface FavouriteAddressService {
    public void saveFavouriteAddress(FavouriteAddressDTO favouriteAddressDTO) throws Exception;

    public List<FavouriteAddressDTO> getFavouriteAddresses(int page, int size, String favouriteType) throws Exception;

    public void deleteFavouriteAddress(String addressId) throws Exception;
}
