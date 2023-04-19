package com.axisbank.transit.transitCardAPI.model.request.topupToPrepaid;

public class RequestBody {

    private PopUpRequest popUpRequest;

    public PopUpRequest getPopUpRequest() {
        return popUpRequest;
    }

    public void setPopUpRequest(PopUpRequest popUpRequest) {
        this.popUpRequest = popUpRequest;
    }

    @Override
    public String toString() {
        return "RequestBody{" +
                "popUpRequest=" + popUpRequest +
                '}';
    }
}
