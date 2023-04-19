package com.axisbank.transit.explore.shared.utils;

import com.axisbank.transit.explore.shared.constants.ExploreConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class ExploreUtils {
    public static void checkFileSizeAndType (MultipartFile file) throws Exception {
        String[] fileFrags = file.getOriginalFilename().split("\\.");
        String extension = fileFrags[fileFrags.length-1];
        List<String> exts = Arrays.asList("jpg", "jpeg", "png");
        if(file.getSize()/1024>2000 ||(!exts.contains(extension.toLowerCase())) || (!file.getContentType().equals("image/jpeg") && !file.getContentType().equals("image/jpg")  && !file.getContentType().equals("image/png") )){
            log.error("Provided Size: {}, ext:{}, contentType:{}", file.getSize(), extension, file.getContentType());
            throw new Exception(ExploreConstants.FILE_SIZE_AND_TYPE);
        }
    }
}
