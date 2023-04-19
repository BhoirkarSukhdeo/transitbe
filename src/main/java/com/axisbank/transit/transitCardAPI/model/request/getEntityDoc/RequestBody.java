package com.axisbank.transit.transitCardAPI.model.request.getEntityDoc;

public class RequestBody {

    public String docCode;
    public String referenceNumber;

    public String getDocCode() {
        return docCode;
    }

    public void setDocCode(String docCode) {
        this.docCode = docCode;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
}
