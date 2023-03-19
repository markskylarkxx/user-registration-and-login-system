package com.spring_boot_sec_app.service;

import com.spring_boot_sec_app.exception.AppException;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sun.applet.AppletIOException;

import java.io.*;
import java.util.Arrays;
import java.util.List;

@Service
public class FileUploadService {

   // @Value("${app.upload.folder}")
    private static  String UPLOAD_FOLDER = "C:\\Uploaded_files\\";
  // @Value("${app.max.size}")
   private static  Integer MAX_UPLOAD_SIZE
          =  1000000; //1mb


    public  static String uploadFile(MultipartFile file) throws  IOException {

        //check if the file is present
        if (file.isEmpty()) throw new FileNotFoundException("File not available for upload");
        //todo; verify file size and validate file extension
        if(!verifyFileSize(file)){
            throw  new AppException("File too large for upload");
        }
   if(!validateFileExtension(file)){
       throw new AppException("Invalid file extension");
   }
   //todo; upload the file
        File path = new File(UPLOAD_FOLDER + file.getOriginalFilename());
        path.createNewFile();
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(file.getBytes());
        fos.close();
        return  path.toString();
    }






    //todo; create a method to verify maximum  file size
    private static  boolean verifyFileSize(MultipartFile file){
       // file.getSize() <= MAX_UPLOAD_SIZE;
        if (file.getSize() <= MAX_UPLOAD_SIZE){
            return true;
        }
        return  false;
    }

    //todo: create a method to validate file extension;

    private static  boolean validateFileExtension(MultipartFile file){
        List<String> validExtensions = Arrays.asList("jpg", "jpeg", "png");
        String fileName = file.getOriginalFilename();
        String fileExtension = fileName.substring(fileName.lastIndexOf("."  ) + 1);
        return  validExtensions.stream().filter( f->f.equalsIgnoreCase(fileExtension)).findAny().isPresent();
      
    }

}
