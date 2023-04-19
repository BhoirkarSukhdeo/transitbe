package com.axisbank.transit.core.FileUploadClient;

import com.axisbank.transit.core.shared.utils.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FileUploadClient {

    public void  uploadFile(String url,String filePath) throws Exception {
        log.info("Upload File "+ url+ " "+ filePath);
        HttpClientUtils.httpPostRequestForFile(url,filePath);
    }
}
