package com.practice.uploadDownload.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    Path fileLocationPath;
    String fileStorageLocation;

    FileStorageService(@Value("${file.storage.location:temp}") String fileStorageLocation){
        fileLocationPath = Paths.get(fileStorageLocation).toAbsolutePath().normalize();
        this.fileStorageLocation = fileStorageLocation;
        try {
            Files.createDirectories(fileLocationPath);
        } catch (IOException e) {
            throw  new RuntimeException("Can not create Directory");
        }
    }

    public String storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Path filePath = Paths.get(fileLocationPath + "\\" + fileName);
        try {
            Files.copy(file.getInputStream(),filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("File upload error");
        }

        return fileName;
    }

    public Resource downloadFile(String fileName) {

        Path path = Paths.get(fileStorageLocation).toAbsolutePath().resolve(fileName);

        Resource resource;

        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("file not found",e);
        }

        if(resource.exists() && resource.isReadable()){
            return resource;
        } else {
            throw new RuntimeException("Issue in resource");
        }

    }
}
