package com.axisbank.transit.journey.controllers;

import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import com.axisbank.transit.journey.constants.JourneyConstants;
import com.axisbank.transit.journey.model.DTO.FavouriteAddressDTO;
import com.axisbank.transit.journey.services.FavouriteAddressService;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.axisbank.transit.core.shared.constants.ApiConstants.BASE_URI;
import static com.axisbank.transit.core.shared.constants.ApiConstants.FAVOURITE_ADDRESS;

@RestController
@RequestMapping(BASE_URI+FAVOURITE_ADDRESS)
public class FavouriteAddressController {

    @Autowired
    FavouriteAddressService favouriteAddressService;

    @PostMapping("")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<String>> saveFavouriteAddress(@RequestBody FavouriteAddressDTO favouriteAddressDTO) throws Exception {
        favouriteAddressService.saveFavouriteAddress(favouriteAddressDTO);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, JourneyConstants.SAVE_FAVOURITE_ADDRESS_SUCCESS_MESSAGE);
    }

    @GetMapping("")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<FavouriteAddressDTO>> getFavouriteAddresses(@RequestParam(name = "page") int page,
                                                                                   @RequestParam(name = "size") int size,
                                                                                   @RequestParam(name = "favouriteType", required = false) String favouriteType) throws Exception{
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, favouriteAddressService.getFavouriteAddresses(page, size, favouriteType));
    }

    @GetMapping("/delete/{addressId}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<String>> deleteFavouriteAddress(@PathVariable String addressId) throws Exception {
        favouriteAddressService.deleteFavouriteAddress(addressId);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, JourneyConstants.FAVOURITE_ADDRESS_DELETE_SUCCESS_MESSAGE);
    }
}
