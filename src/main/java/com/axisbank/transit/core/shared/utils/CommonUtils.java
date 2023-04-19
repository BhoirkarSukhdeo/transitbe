package com.axisbank.transit.core.shared.utils;

import com.axisbank.transit.core.FileUploadClient.FileUploadClient;
import com.axisbank.transit.core.model.DTO.FileDTO;
import com.axisbank.transit.core.shared.constants.ApiConstants;
import com.axisbank.transit.explore.shared.utils.ExploreUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import static com.axisbank.transit.core.shared.constants.UtilsConstants.COUNTRY_CODE_WITHOUT_PLUS;

@Slf4j
public class CommonUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static long getCurrentTimeSec(){
        return getCurrentTimeMillis() / 1000;
    }
    public static long getCurrentTimeMillis(){
        return System.currentTimeMillis();
    }
    public static String getCurrentDateTime(){
        return getCurrentDateTime("dd/MM/yyyy");
    }
    public static String getCurrentDateTime(String format){
        return getDateTime(getCurrentTimeMillis(), format);
    }
    public static String getDateTime(long milliseconds){
        return getDateTime(milliseconds, "dd/MM/yyyy");
    }
    public static String getDateTime(long milliseconds, String format){
        Date date = new Date(milliseconds);
        DateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }
    public static String getDateFormat(Date date, String format){
        DateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }
    public static <T>T convertJsonStringToObject(String content, Class<T> classType) throws JsonProcessingException {
        return objectMapper.readValue(content, classType);
    }

    public static String convertObjectToJsonString(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    /**
     * method to generate random alphanumeric value of specific length
     * @param length
     * @return
     */
    public static String generateRandomString(int length) {
        SecureRandom RANDOM = new SecureRandom();
        String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder returnValue = new StringBuilder();

        for (int i=0; i<length; i++) {
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return new String(returnValue);
    }

    public static User getLoggedinUser() throws Exception {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = null;
        if (principal instanceof User) {
            String username = ((User) principal).getUsername();
        } else {
            throw new Exception("Unauthorized User");
        }
        return (User) principal;
    }


    public static String getFullName(String firstName, String midName, String lastName) {
        String fullName = "";
        if (!isNullOrEmpty(firstName)) {
            fullName = fullName+firstName;
        }
        if (!isNullOrEmpty(midName)) {
            fullName = fullName+" "+midName;
        }
        if (!isNullOrEmpty(lastName)) {
            fullName = fullName+" "+lastName;
        }
        return fullName;
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static String maskString(String strText, int start, int end, char maskChar)
            throws Exception{

        if(strText == null || strText.equals(""))
            return "";

        if(start < 0)
            start = 0;

        if( end > strText.length() )
            end = strText.length();

        if(start > end)
            throw new Exception("End index cannot be greater than start index");

        int maskLength = end - start;

        if(maskLength == 0)
            return strText;

        StringBuilder sbMaskString = new StringBuilder(maskLength);

        for(int i = 0; i < maskLength; i++){
            sbMaskString.append(maskChar);
        }

        return strText.substring(0, start)
                + sbMaskString.toString()
                + strText.substring(start + maskLength);
    }



    public static String addSecondsToTime(String time, int seconds) throws ParseException {
        return addSecondsToTime("HH:mm:ss", time, seconds);
    }

    public static Double calculateMetroDistance(Double dist1, Double dist2){
        if (dist1>dist2) return dist1-dist2;
        return dist2-dist1;
    }


    public static String addSecondsToTime(String format, String time, int seconds) throws ParseException {
        DateFormat df = new SimpleDateFormat(format);
        Date date = df.parse(time);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, seconds);
        date = cal.getTime();
        return df.format(date);
    }

    public static long getTimeDiff(Time t1, Time t2){
        long time1 = t1.getTime();
        long time2 = t2.getTime();
        if (time1<time2){
            time1 = time1+(24*60*60*1000);
        }
        return (time1-time2)/1000;
    }

    public static String startDayTime(String format) {
        LocalDateTime currentDateTime = currentDateTime();
        LocalDateTime startOfDay = currentDateTime.with(LocalTime.MIN);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return startOfDay.format(formatter);
    }

    public static String endDayTime(String format) {
        LocalDateTime currentDateTime = currentDateTime();
        LocalDateTime endOfDay = currentDateTime.with(LocalTime.MAX);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return endOfDay.format(formatter);
    }


    public static Date getDate(String date) throws ParseException {
        return getDate(date, "dd/MM/yyyy");
    }

    public static Date getDate(String date, String format) throws ParseException {
        Date formattedDate = new SimpleDateFormat(format).parse(date);
        return formattedDate;
    }


    public static ZoneOffset getZoneOffset() {
        ZoneId zone = ZoneId.of("Asia/Kolkata");
        ZoneOffset zoneOffSet = zone.getRules().getOffset(LocalDateTime.now());
        return zoneOffSet;
    }

    public static Date getDateForStart(String date) throws ParseException {
       return getDateForStart(date, "dd/MM/yyyy");
    }

    public static Date getDateForStart(String date, String format) throws ParseException {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(format));

        Instant d = OffsetDateTime.of(
                localDate,
                LocalTime.MIN ,
                getZoneOffset()
        ).toInstant();
        Date formattedDate = Date.from(d);
        return formattedDate;
    }

    public static Date getDateForEnd(String date) throws ParseException {
        return getDateForEnd(date,"dd/MM/yyyy");
    }

    public static Date getDateForEnd(String date, String format) throws ParseException {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(format));
        Instant d = OffsetDateTime.of(
                localDate,
                LocalTime.MAX ,
                getZoneOffset()
        ).toInstant();
        Date formattedDate = Date.from(d);
        return formattedDate;
    }

    public static LocalDateTime currentDateTime() {
        return LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
//        return LocalDateTime.now();
    }

    public static String currentDateTime(String format) {
        LocalDateTime currentTimeIST =  LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return currentTimeIST.format(formatter);
    }

    public static boolean allCharactersSame(String sequence)
    {
        int n = sequence.length();
        for (int i = 1; i < n; i++)
            if (sequence.charAt(i) != sequence.charAt(0))
                return false;

        return true;
    }

    public static boolean checkConsecutive(String sequence) {
        char[] array = sequence.toCharArray();
        boolean result1 = true, result2 = true;
        for (int i = 0; i < array.length - 1; i++) {
            if ((array[i + 1] - array[i]) == 1) {
            } else {
                result1 = false;
                break;
            }
        }
        for (int i = 0; i < array.length - 1; i++) {
            if ((array[i + 1] - array[i]) == -1) {
            } else {
                result2 = false;
                break;
            }
        }
        return result1 || result2;
    }

    /**
     * Generates Random Integer of given length
     * @param length
     * @return
     */
    public static String generateRandInt(int length)
    {
        String numbers = "0123456789";
        SecureRandom randomMethod = new SecureRandom();
        char[] otp = new char[length];
        for (int i = 0; i < length; i++)
        {
            otp[i] = numbers.charAt(randomMethod.nextInt(numbers.length()));
        }
        return new String(otp);
    }

    public static String convertFile(MultipartFile file,String id,String fileDirectory,String filePath) throws  Exception{
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Path path;
        if(fileName.contains(id)){
            path = Paths.get(filePath + fileDirectory  + fileName);
        }
        else {
            path = Paths.get(filePath + fileDirectory + id + "_" + fileName);
        }
        File directory = new File(filePath);
        if(!directory.exists()) {
            directory.mkdir();
        }
        directory = new File(filePath+fileDirectory);
        if(!directory.exists()) {
            directory.mkdir();
        }
        try {
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw e;
        }
        return fileName;
    }

    public static String convertAndUploadFile(FileDTO file, String serversID,FileUploadClient fileUploadClient) throws Exception{
            ExploreUtils.checkFileSizeAndType(file.multipartFile);
            String fileLocation=convertFile(file.multipartFile, file.id, file.fileDirectory,file.filePath);
            if(serversID != null && !serversID.equals("")) {
                String[] serverArray = serversID.split(",");
                if (serverArray.length > 0) {
                    for (String server : serverArray) {
                        fileUploadClient.uploadFile(server + ApiConstants.BASE_URI + ApiConstants.FILE + "/" +"upload/"+ file.fileDirectory +file.id+"/"+file.id + "_" +fileLocation, file.filePath +file.fileDirectory +file.id + "_" +fileLocation) ;
                    }
                }

            }
        return file.fileDirectory +file.id+"/"+file.id + "_" +fileLocation;
    }

    public static LocalDate getLocalDate(String date, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(date, formatter);
    }

    public static boolean checkIfOfferExpired(LocalDate endDate, String endTime) throws ParseException {
        Date endDateTime = getDateObject(endDate, endTime);
        String currentDateInString = currentDateTime("yyyy-MM-dd HH:mm:ss");
        Date currentDateTime = getDate(currentDateInString, "yyyy-MM-dd HH:mm:ss");
        if (endDateTime.before(currentDateTime)) {
            return true;
        }
        return false;
    }

    public static Date getDateObject(LocalDate endDate, String endTime) throws ParseException {
        String date = endDate.toString();
        String actualEndTime = "";
        if (endTime.contains("pm") || endTime.contains("PM")) {
            String timeArray[] = endTime.split(":");
            int newHours = Integer.parseInt(timeArray[0]) + 12;
            timeArray[0] = String.valueOf(newHours);
            endTime = timeArray[0]+":"+timeArray[1];
        }
        String time = endTime.replace(" ","").replace("AM","").replace("PM","").replace("am","").replace("pm","");
        if (time.length() < 5) {
            time = time.substring(0,0) +0+ time.substring(0);
        }
        String dateTime = date+" "+time+":00";
        return getDate(dateTime, "yyyy-MM-dd HH:mm:ss");
    }

    public static String getMobileWithCountryCode(String mobile) {
        if (mobile.length() <= 11) {
            return COUNTRY_CODE_WITHOUT_PLUS+mobile;
        }
        return mobile;
    }
    public static boolean checkTimeBetween(String start, String end, String check) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            Date startime = simpleDateFormat.parse(start);
            Date endtime = simpleDateFormat.parse(end);
            Date currentTime = simpleDateFormat.parse(check);
            log.info("Current Time: {}",currentTime.toString());
            return currentTime.after(startime) && currentTime.before(endtime);
        } catch (ParseException ex){
            log.error("Failed to parse date:{}", ex.getMessage());
            return true;
        }

    }
    public static boolean isNumeric(final String str) {
        // null or empty
        if (str == null || str.length() == 0) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
}
