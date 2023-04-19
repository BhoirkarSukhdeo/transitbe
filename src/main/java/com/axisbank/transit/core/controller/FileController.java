package com.axisbank.transit.core.controller;

import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.service.FileService;
import com.axisbank.transit.core.shared.constants.ApiConstants;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import com.axisbank.transit.explore.shared.constants.ExploreConstants;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@Slf4j
@RequestMapping(ApiConstants.BASE_URI+ ApiConstants.FILE)
public class FileController {
    @Value("${app.file.path}")
    String filePath;

    @Autowired
    FileService fileService;

    @GetMapping("/{fileDirectory}/{id}/{fileName}")
    public ResponseEntity downloadFileFromLocal(@PathVariable String fileDirectory,@PathVariable String id,@PathVariable String fileName) {
        Path path = Paths.get(filePath +fileDirectory+"/"+ fileName);
        UrlResource resource = null;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            log.error("Error in getting URI path: {}", e.getMessage());
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PostMapping("/upload/{fileDirectory}/{id}/{fileName}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<String>> uploadFile(@ModelAttribute MultipartFile multipartFile, @PathVariable String fileDirectory, @PathVariable String id, @PathVariable String fileName) throws Exception {
        fileService.uploadFile(multipartFile,fileDirectory+'/',id,fileName);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, ExploreConstants.CREATE_NOTIFCATION_SUCCESS_MESSAGE);
    }

    @PostMapping("/explore")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<String>> uploadExploreFile(@ModelAttribute MultipartFile file) throws Exception {
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, fileService.uploadExploreFile(file));
    }
}
