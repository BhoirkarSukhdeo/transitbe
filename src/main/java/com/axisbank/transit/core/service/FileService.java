package com.axisbank.transit.core.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    public void uploadFile(MultipartFile multipartFile,String fileDirectory,String id, String fileName) throws Exception;
    public String uploadExploreFile(MultipartFile multipartFile) throws Exception;
}
