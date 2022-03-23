package com.practice.uploadDownload.controller;

import com.practice.uploadDownload.service.FileStorageService;
import com.practice.uploadDownload.util.FileUploadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
public class UploadDownloadWithFileSystem {

    @Autowired
    private FileStorageService fileStorageService;


    @PostMapping("single/upload")
    FileUploadResponse singleFileUpload(@RequestParam MultipartFile file){
        String fileName = fileStorageService.storeFile(file);

        String url = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/download/")
                .path(fileName)
                .toUriString();

        String contentType = file.getContentType();

        FileUploadResponse response = new FileUploadResponse(fileName,contentType, url);

        return response;
    }

    @GetMapping("/download/{fileName}")
    ResponseEntity<Resource> singleFileDownload(@PathVariable String fileName, HttpServletRequest request){
        Resource resource = fileStorageService.downloadFile(fileName);
        String mediaType;
        try {
             mediaType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mediaType))
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment;fileName="+resource.getFilename())
                .body(resource);
    }
}
