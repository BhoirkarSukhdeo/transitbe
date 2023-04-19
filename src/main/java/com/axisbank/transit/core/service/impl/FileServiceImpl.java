package com.axisbank.transit.core.service.impl;

import com.axisbank.transit.core.FileUploadClient.FileUploadClient;
import com.axisbank.transit.core.model.DTO.FileDTO;
import com.axisbank.transit.core.service.FileService;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.explore.shared.constants.ExploreConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional
public class FileServiceImpl implements FileService {
    @Value("${app.file.path}")
    String filePath;

    @Autowired
    FileUploadClient fileUploadClient;

    @Value("${app.servers.ip}")
    String serversID;


    public void uploadFile(MultipartFile multipartFile, String fileDirectory, String id, String fileName) throws Exception{
        try {
            CommonUtils.convertFile(multipartFile, id, fileDirectory, filePath);
        } catch (Exception exception){
            log.info("Exception in uploading image"+exception.getLocalizedMessage());
            throw exception;
        }
    }
    public String uploadExploreFile(MultipartFile multipartFile) throws Exception{
        String file;
        try {
            FileDTO fileDTO = new FileDTO();
            fileDTO.multipartFile= multipartFile;
            fileDTO.fileDirectory=ExploreConstants.FILE_PATH;
            fileDTO.id =CommonUtils.getCurrentDateTime("dd-MM-yyyy-HH:mm");
            fileDTO.filePath=filePath;
            file= CommonUtils.convertAndUploadFile(fileDTO,serversID,fileUploadClient);
        } catch (Exception exception){
            log.info("Exception in uploading explore image", exception.getLocalizedMessage());
            throw exception;
        }
        return file;
    }
}
