package com.axisbank.transit.transitCardAPI.model.request.updateCustomer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Customer {

    @JsonProperty("CustomerNo")
    private String customerNo;
    @JsonProperty("BankingCustomerNo")
    private String bankingCustomerNo;
    @JsonProperty("OneClickId")
    private String oneClickId;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("MidName")
    private String midName;
    @JsonProperty("Surname")
    private String surname;
    @JsonProperty("BirthDate")
    private String birthDate;
    @JsonProperty("FatherName")
    private String fatherName;
    @JsonProperty("MotherMaidenName")
    private String motherMaidenName;
    @JsonProperty("Nationality")
    private String nationality;
    @JsonProperty("IssuedBy")
    private String issuedBy;
    @JsonProperty("PassportNo")
    private String passportNo;
    @JsonProperty("PassportIssuedBy")
    private String passportIssuedBy;
    @JsonProperty("PassportDateOfIssue")
    private String passportDateOfIssue;
    @JsonProperty("PassportDateOfExpire")
    private String passportDateOfExpire;
    @JsonProperty("PassportControlPeriod")
    private String passportControlPeriod;
    @JsonProperty("EmergencyContactPersonNameSurname")
    private String emergencyContactPersonNameSurname;
    @JsonProperty("ResidenceCountryCode")
    private String residenceCountryCode;
    @JsonProperty("BirthCountry")
    private String birthCountry;
    @JsonProperty("BirthCity")
    private String birthCity;
    @JsonProperty("BirthPlace")
    private String birthPlace;
    @JsonProperty("Email")
    private String email;
    @JsonProperty("CustomerType")
    private String customerType;
    @JsonProperty("Gender")
    private String gender;
    @JsonProperty("CommunicationLanguage")
    private String communicationLanguage;
    @JsonProperty("SendSMS")
    private String sendSMS;
    @JsonProperty("SendEMail")
    private String sendEMail;
    @JsonProperty("MobileNo")
    private String mobileNo;
    @JsonProperty("PhoneHome")
    private String phoneHome;
    @JsonProperty("PhoneWork")
    private String phoneWork;
    @JsonProperty("PhoneWorkExtension")
    private String phoneWorkExtension;
    @JsonProperty("WorkPlace")
    private String workPlace;
    @JsonProperty("Occupation")
    private String occupation;
    @JsonProperty("Title")
    private String title;
    @JsonProperty("AllocationDate")
    private String allocationDate;
    @JsonProperty("EmergencyPhoneFieldCode")
    private String emergencyPhoneFieldCode;
    @JsonProperty("EmergencyPhone")
    private String emergencyPhone;
    @JsonProperty("EmergencyPhoneExt")
    private String emergencyPhoneExt;
    @JsonProperty("MainBranchField")
    private String mainBranchField;
    @JsonProperty("GuaranteeFlag")
    private String guaranteeFlag;
    @JsonProperty("AssuranceType")
    private String assuranceType;
    @JsonProperty("NationalId")
    private String nationalId;
    @JsonProperty("CustomerGroup")
    private String customerGroup;
    @JsonProperty("CustodianNationalId")
    private String custodianNationalId;
    @JsonProperty("SMSOTPNo")
    private String sMSOTPNo;
    @JsonProperty("MotherName")
    private String motherName;
    @JsonProperty("ParentName")
    private String parentName;
    @JsonProperty("ParentNationalId")
    private String parentNationalId;
    @JsonProperty("ParentDescription")
    private String parentDescription;
    @JsonProperty("ChannelCode")
    private String channelCode;
    @JsonProperty("PictureFilePath")
    private String pictureFilePath;
    @JsonProperty("AdressList")
    private AdressList adressList;
    @JsonProperty("Custodian")
    private String custodian;
    @JsonProperty("FreeText1")
    private String freeText1;
    @JsonProperty("FreeText2")
    private String freeText2;
    @JsonProperty("FreeText3")
    private String freeText3;
    @JsonProperty("FreeText4")
    private String freeText4;
    @JsonProperty("FreeText5")
    private String freeText5;
    @JsonProperty("FreeText6")
    private String freeText6;
    @JsonProperty("FreeText7")
    private String freeText7;
    @JsonProperty("FreeText8")
    private String freeText8;
    @JsonProperty("FreeText9")
    private String freeText9;
    @JsonProperty("FreeText10")
    private String freeText10;
    @JsonProperty("FreeText11")
    private String freeText11;
    @JsonProperty("FreeText12")
    private String freeText12;
    @JsonProperty("FreeText13")
    private String freeText13;
    @JsonProperty("FreeText14")
    private String freeText14;
    @JsonProperty("FreeText15")
    private String freeText15;
    @JsonProperty("FreeText16")
    private String freeText16;
    @JsonProperty("FreeText17")
    private String freeText17;
    @JsonProperty("FreeText18")
    private String freeText18;
    @JsonProperty("FreeText19")
    private String freeText19;
    @JsonProperty("FreeText20")
    private String freeText20;
    @JsonProperty("FreeText21")
    private String freeText21;
    @JsonProperty("FreeText22")
    private String freeText22;
    @JsonProperty("FreeText23")
    private String freeText23;
    @JsonProperty("FreeText24")
    private String freeText24;
    @JsonProperty("FreeText25")
    private String freeText25;
    @JsonProperty("KYCStatus")
    private String kYCStatus;
    @JsonProperty("PanNumber")
    private String panNumber;
    @JsonProperty("AadharNo")
    private String aadharNo;

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public String getBankingCustomerNo() {
        return bankingCustomerNo;
    }

    public void setBankingCustomerNo(String bankingCustomerNo) {
        this.bankingCustomerNo = bankingCustomerNo;
    }

    public String getOneClickId() {
        return oneClickId;
    }

    public void setOneClickId(String oneClickId) {
        this.oneClickId = oneClickId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMidName() {
        return midName;
    }

    public void setMidName(String midName) {
        this.midName = midName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getMotherMaidenName() {
        return motherMaidenName;
    }

    public void setMotherMaidenName(String motherMaidenName) {
        this.motherMaidenName = motherMaidenName;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    public String getPassportNo() {
        return passportNo;
    }

    public void setPassportNo(String passportNo) {
        this.passportNo = passportNo;
    }

    public String getPassportIssuedBy() {
        return passportIssuedBy;
    }

    public void setPassportIssuedBy(String passportIssuedBy) {
        this.passportIssuedBy = passportIssuedBy;
    }

    public String getPassportDateOfIssue() {
        return passportDateOfIssue;
    }

    public void setPassportDateOfIssue(String passportDateOfIssue) {
        this.passportDateOfIssue = passportDateOfIssue;
    }

    public String getPassportDateOfExpire() {
        return passportDateOfExpire;
    }

    public void setPassportDateOfExpire(String passportDateOfExpire) {
        this.passportDateOfExpire = passportDateOfExpire;
    }

    public String getPassportControlPeriod() {
        return passportControlPeriod;
    }

    public void setPassportControlPeriod(String passportControlPeriod) {
        this.passportControlPeriod = passportControlPeriod;
    }

    public String getEmergencyContactPersonNameSurname() {
        return emergencyContactPersonNameSurname;
    }

    public void setEmergencyContactPersonNameSurname(String emergencyContactPersonNameSurname) {
        this.emergencyContactPersonNameSurname = emergencyContactPersonNameSurname;
    }

    public String getResidenceCountryCode() {
        return residenceCountryCode;
    }

    public void setResidenceCountryCode(String residenceCountryCode) {
        this.residenceCountryCode = residenceCountryCode;
    }

    public String getBirthCountry() {
        return birthCountry;
    }

    public void setBirthCountry(String birthCountry) {
        this.birthCountry = birthCountry;
    }

    public String getBirthCity() {
        return birthCity;
    }

    public void setBirthCity(String birthCity) {
        this.birthCity = birthCity;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCommunicationLanguage() {
        return communicationLanguage;
    }

    public void setCommunicationLanguage(String communicationLanguage) {
        this.communicationLanguage = communicationLanguage;
    }

    public String getSendSMS() {
        return sendSMS;
    }

    public void setSendSMS(String sendSMS) {
        this.sendSMS = sendSMS;
    }

    public String getSendEMail() {
        return sendEMail;
    }

    public void setSendEMail(String sendEMail) {
        this.sendEMail = sendEMail;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getPhoneHome() {
        return phoneHome;
    }

    public void setPhoneHome(String phoneHome) {
        this.phoneHome = phoneHome;
    }

    public String getPhoneWork() {
        return phoneWork;
    }

    public void setPhoneWork(String phoneWork) {
        this.phoneWork = phoneWork;
    }

    public String getPhoneWorkExtension() {
        return phoneWorkExtension;
    }

    public void setPhoneWorkExtension(String phoneWorkExtension) {
        this.phoneWorkExtension = phoneWorkExtension;
    }

    public String getWorkPlace() {
        return workPlace;
    }

    public void setWorkPlace(String workPlace) {
        this.workPlace = workPlace;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAllocationDate() {
        return allocationDate;
    }

    public void setAllocationDate(String allocationDate) {
        this.allocationDate = allocationDate;
    }

    public String getEmergencyPhoneFieldCode() {
        return emergencyPhoneFieldCode;
    }

    public void setEmergencyPhoneFieldCode(String emergencyPhoneFieldCode) {
        this.emergencyPhoneFieldCode = emergencyPhoneFieldCode;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public String getEmergencyPhoneExt() {
        return emergencyPhoneExt;
    }

    public void setEmergencyPhoneExt(String emergencyPhoneExt) {
        this.emergencyPhoneExt = emergencyPhoneExt;
    }

    public String getMainBranchField() {
        return mainBranchField;
    }

    public void setMainBranchField(String mainBranchField) {
        this.mainBranchField = mainBranchField;
    }

    public String getGuaranteeFlag() {
        return guaranteeFlag;
    }

    public void setGuaranteeFlag(String guaranteeFlag) {
        this.guaranteeFlag = guaranteeFlag;
    }

    public String getAssuranceType() {
        return assuranceType;
    }

    public void setAssuranceType(String assuranceType) {
        this.assuranceType = assuranceType;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getCustomerGroup() {
        return customerGroup;
    }

    public void setCustomerGroup(String customerGroup) {
        this.customerGroup = customerGroup;
    }

    public String getCustodianNationalId() {
        return custodianNationalId;
    }

    public void setCustodianNationalId(String custodianNationalId) {
        this.custodianNationalId = custodianNationalId;
    }

    public String getsMSOTPNo() {
        return sMSOTPNo;
    }

    public void setsMSOTPNo(String sMSOTPNo) {
        this.sMSOTPNo = sMSOTPNo;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentNationalId() {
        return parentNationalId;
    }

    public void setParentNationalId(String parentNationalId) {
        this.parentNationalId = parentNationalId;
    }

    public String getParentDescription() {
        return parentDescription;
    }

    public void setParentDescription(String parentDescription) {
        this.parentDescription = parentDescription;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getPictureFilePath() {
        return pictureFilePath;
    }

    public void setPictureFilePath(String pictureFilePath) {
        this.pictureFilePath = pictureFilePath;
    }

    public AdressList getAdressList() {
        return adressList;
    }

    public void setAdressList(AdressList adressList) {
        this.adressList = adressList;
    }

    public String getCustodian() {
        return custodian;
    }

    public void setCustodian(String custodian) {
        this.custodian = custodian;
    }

    public String getFreeText1() {
        return freeText1;
    }

    public void setFreeText1(String freeText1) {
        this.freeText1 = freeText1;
    }

    public String getFreeText2() {
        return freeText2;
    }

    public void setFreeText2(String freeText2) {
        this.freeText2 = freeText2;
    }

    public String getFreeText3() {
        return freeText3;
    }

    public void setFreeText3(String freeText3) {
        this.freeText3 = freeText3;
    }

    public String getFreeText4() {
        return freeText4;
    }

    public void setFreeText4(String freeText4) {
        this.freeText4 = freeText4;
    }

    public String getFreeText5() {
        return freeText5;
    }

    public void setFreeText5(String freeText5) {
        this.freeText5 = freeText5;
    }

    public String getFreeText6() {
        return freeText6;
    }

    public void setFreeText6(String freeText6) {
        this.freeText6 = freeText6;
    }

    public String getFreeText7() {
        return freeText7;
    }

    public void setFreeText7(String freeText7) {
        this.freeText7 = freeText7;
    }

    public String getFreeText8() {
        return freeText8;
    }

    public void setFreeText8(String freeText8) {
        this.freeText8 = freeText8;
    }

    public String getFreeText9() {
        return freeText9;
    }

    public void setFreeText9(String freeText9) {
        this.freeText9 = freeText9;
    }

    public String getFreeText10() {
        return freeText10;
    }

    public void setFreeText10(String freeText10) {
        this.freeText10 = freeText10;
    }

    public String getFreeText11() {
        return freeText11;
    }

    public void setFreeText11(String freeText11) {
        this.freeText11 = freeText11;
    }

    public String getFreeText12() {
        return freeText12;
    }

    public void setFreeText12(String freeText12) {
        this.freeText12 = freeText12;
    }

    public String getFreeText13() {
        return freeText13;
    }

    public void setFreeText13(String freeText13) {
        this.freeText13 = freeText13;
    }

    public String getFreeText14() {
        return freeText14;
    }

    public void setFreeText14(String freeText14) {
        this.freeText14 = freeText14;
    }

    public String getFreeText15() {
        return freeText15;
    }

    public void setFreeText15(String freeText15) {
        this.freeText15 = freeText15;
    }

    public String getFreeText16() {
        return freeText16;
    }

    public void setFreeText16(String freeText16) {
        this.freeText16 = freeText16;
    }

    public String getFreeText17() {
        return freeText17;
    }

    public void setFreeText17(String freeText17) {
        this.freeText17 = freeText17;
    }

    public String getFreeText18() {
        return freeText18;
    }

    public void setFreeText18(String freeText18) {
        this.freeText18 = freeText18;
    }

    public String getFreeText19() {
        return freeText19;
    }

    public void setFreeText19(String freeText19) {
        this.freeText19 = freeText19;
    }

    public String getFreeText20() {
        return freeText20;
    }

    public void setFreeText20(String freeText20) {
        this.freeText20 = freeText20;
    }

    public String getFreeText21() {
        return freeText21;
    }

    public void setFreeText21(String freeText21) {
        this.freeText21 = freeText21;
    }

    public String getFreeText22() {
        return freeText22;
    }

    public void setFreeText22(String freeText22) {
        this.freeText22 = freeText22;
    }

    public String getFreeText23() {
        return freeText23;
    }

    public void setFreeText23(String freeText23) {
        this.freeText23 = freeText23;
    }

    public String getFreeText24() {
        return freeText24;
    }

    public void setFreeText24(String freeText24) {
        this.freeText24 = freeText24;
    }

    public String getFreeText25() {
        return freeText25;
    }

    public void setFreeText25(String freeText25) {
        this.freeText25 = freeText25;
    }

    public String getkYCStatus() {
        return kYCStatus;
    }

    public void setkYCStatus(String kYCStatus) {
        this.kYCStatus = kYCStatus;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getAadharNo() {
        return aadharNo;
    }

    public void setAadharNo(String aadharNo) {
        this.aadharNo = aadharNo;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerNo='" + customerNo + '\'' +
                ", bankingCustomerNo='" + bankingCustomerNo + '\'' +
                ", oneClickId='" + oneClickId + '\'' +
                ", name='" + name + '\'' +
                ", midName='" + midName + '\'' +
                ", surname='" + surname + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", fatherName='" + fatherName + '\'' +
                ", motherMaidenName='" + motherMaidenName + '\'' +
                ", nationality='" + nationality + '\'' +
                ", issuedBy='" + issuedBy + '\'' +
                ", passportNo='" + passportNo + '\'' +
                ", passportIssuedBy='" + passportIssuedBy + '\'' +
                ", passportDateOfIssue='" + passportDateOfIssue + '\'' +
                ", passportDateOfExpire='" + passportDateOfExpire + '\'' +
                ", passportControlPeriod='" + passportControlPeriod + '\'' +
                ", emergencyContactPersonNameSurname='" + emergencyContactPersonNameSurname + '\'' +
                ", residenceCountryCode='" + residenceCountryCode + '\'' +
                ", birthCountry='" + birthCountry + '\'' +
                ", birthCity='" + birthCity + '\'' +
                ", birthPlace='" + birthPlace + '\'' +
                ", email='" + email + '\'' +
                ", customerType='" + customerType + '\'' +
                ", gender='" + gender + '\'' +
                ", communicationLanguage='" + communicationLanguage + '\'' +
                ", sendSMS='" + sendSMS + '\'' +
                ", sendEMail='" + sendEMail + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                ", phoneHome='" + phoneHome + '\'' +
                ", phoneWork='" + phoneWork + '\'' +
                ", phoneWorkExtension='" + phoneWorkExtension + '\'' +
                ", workPlace='" + workPlace + '\'' +
                ", occupation='" + occupation + '\'' +
                ", title='" + title + '\'' +
                ", allocationDate='" + allocationDate + '\'' +
                ", emergencyPhoneFieldCode='" + emergencyPhoneFieldCode + '\'' +
                ", emergencyPhone='" + emergencyPhone + '\'' +
                ", emergencyPhoneExt='" + emergencyPhoneExt + '\'' +
                ", mainBranchField='" + mainBranchField + '\'' +
                ", guaranteeFlag='" + guaranteeFlag + '\'' +
                ", assuranceType='" + assuranceType + '\'' +
                ", nationalId='" + nationalId + '\'' +
                ", customerGroup='" + customerGroup + '\'' +
                ", custodianNationalId='" + custodianNationalId + '\'' +
                ", sMSOTPNo='" + sMSOTPNo + '\'' +
                ", motherName='" + motherName + '\'' +
                ", parentName='" + parentName + '\'' +
                ", parentNationalId='" + parentNationalId + '\'' +
                ", parentDescription='" + parentDescription + '\'' +
                ", channelCode='" + channelCode + '\'' +
                ", pictureFilePath='" + pictureFilePath + '\'' +
                ", adressList=" + adressList +
                ", custodian='" + custodian + '\'' +
                ", freeText1='" + freeText1 + '\'' +
                ", freeText2='" + freeText2 + '\'' +
                ", freeText3='" + freeText3 + '\'' +
                ", freeText4='" + freeText4 + '\'' +
                ", freeText5='" + freeText5 + '\'' +
                ", freeText6='" + freeText6 + '\'' +
                ", freeText7='" + freeText7 + '\'' +
                ", freeText8='" + freeText8 + '\'' +
                ", freeText9='" + freeText9 + '\'' +
                ", freeText10='" + freeText10 + '\'' +
                ", freeText11='" + freeText11 + '\'' +
                ", freeText12='" + freeText12 + '\'' +
                ", freeText13='" + freeText13 + '\'' +
                ", freeText14='" + freeText14 + '\'' +
                ", freeText15='" + freeText15 + '\'' +
                ", freeText16='" + freeText16 + '\'' +
                ", freeText17='" + freeText17 + '\'' +
                ", freeText18='" + freeText18 + '\'' +
                ", freeText19='" + freeText19 + '\'' +
                ", freeText20='" + freeText20 + '\'' +
                ", freeText21='" + freeText21 + '\'' +
                ", freeText22='" + freeText22 + '\'' +
                ", freeText23='" + freeText23 + '\'' +
                ", freeText24='" + freeText24 + '\'' +
                ", freeText25='" + freeText25 + '\'' +
                ", kYCStatus='" + kYCStatus + '\'' +
                ", panNumber='" + panNumber + '\'' +
                ", aadharNo='" + aadharNo + '\'' +
                '}';
    }
}
