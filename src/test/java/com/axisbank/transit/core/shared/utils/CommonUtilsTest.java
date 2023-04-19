package com.axisbank.transit.core.shared.utils;

import com.axisbank.transit.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Time;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;

public class CommonUtilsTest extends BaseTest {


    @Test
    public void generateRandomNoTest() {
        Assert.assertNotNull(CommonUtils.generateRandomString(5));
        Assert.assertEquals(CommonUtils.generateRandomString(5).length(),5);
        Assert.assertNotEquals(CommonUtils.generateRandomString(6).length(),5);
    }


    @Test
    public void getDateTimeTest() {
        Assert.assertNotNull(CommonUtils.getCurrentDateTime());
        Assert.assertNotNull(CommonUtils.getDateTime(System.currentTimeMillis()));
        Assert.assertNotNull(CommonUtils.getDateTime(200, "yyyy-MM-dd HH:mm"));
        Assert.assertNotNull(CommonUtils.getDateFormat(new Date(), "yyyy-MM-dd HH:mm"));
    }

    @Test
    public void getFullNameTest() {
        Assert.assertNotNull(CommonUtils.getFullName("raj","kumar","rao"));
    }

    @Test
    public void maskString() throws Exception{
        Assert.assertNotNull(CommonUtils.maskString("2233445566",1, 3, '*'));
    }

    @Test
    public void addSecondsToTimeTest() throws Exception {
        Assert.assertNotNull(CommonUtils.addSecondsToTime("02:30:00", 30));
    }

    @Test
    public void getTimeDiffTest() throws Exception {
        Assert.assertNotNull(CommonUtils.getTimeDiff(Time.valueOf("02:30:00"), Time.valueOf("03:30:00")));
    }

    @Test
    public void startDayTimeTest() {
        Assert.assertNotNull(CommonUtils.startDayTime("yyyy-MM-dd"));
    }

    @Test
    public void endDayTimeTest() {
        Assert.assertNotNull(CommonUtils.endDayTime("yyyy-MM-dd"));
    }

    @Test
    public void getDateTest() throws ParseException {
        Assert.assertNotNull(CommonUtils.getDate("23/11/1994"));
    }

    @Test
    public void getDateForStartTest() throws Exception {
        Assert.assertNotNull(CommonUtils.getDateForStart("23/11/1994"));
    }

    @Test
    public void getDateForEndTest() throws Exception {
        Assert.assertNotNull(CommonUtils.getDateForEnd("23/11/1994"));
    }

    @Test
    public void currentDateTimeTest() throws Exception {
        Assert.assertNotNull(CommonUtils.currentDateTime("yyyy-MM-dd HH:mm"));
    }

    @Test
    public void allCharactersSameTest() {
        Assert.assertEquals(false, CommonUtils.allCharactersSame("224222"));
    }

    @Test
    public void allCharactersSameTest2() {
        Assert.assertEquals(true, CommonUtils.allCharactersSame("222222"));
    }

    @Test
    public void checkConsecutiveTest() {
        Assert.assertEquals(true, CommonUtils.checkConsecutive("123456"));
    }

    @Test
    public void checkConsecutiveTest2() {
        Assert.assertEquals(false, CommonUtils.checkConsecutive("232323"));
    }

    @Test
    public void generateRandIntTest() {
        Assert.assertNotNull(CommonUtils.generateRandInt(6));
    }

    @Test
    public void getLocalDateTest() {
        Assert.assertNotNull(CommonUtils.getLocalDate("23/11/1998", "dd/MM/yyyy"));
    }

    @Test
    public void checkIfOfferExpiredTest() throws ParseException {
        String date = "2016-08-16";
        LocalDate localDate = LocalDate.parse(date);
        Assert.assertEquals(true, CommonUtils.checkIfOfferExpired(localDate, "6:30 PM"));
    }

    @Test
    public void calculateMetroDistanceTest() {
        Assert.assertNotNull(CommonUtils.calculateMetroDistance(23.0, 40.0));
    }

}
