package com.example.demo.Control;

import com.example.demo.Entity.Files;
import com.example.demo.Service.FilesServices;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;

@RequestMapping("/files")
@RestController
public class FilesControl {
    @Autowired
    FilesServices filesServices;


    @PostMapping("/upload-files")
    public ResponseEntity<?> saveFile(@RequestParam ("file") MultipartFile file) throws IOException {
        Files files = filesServices.insertFile(file);
        files.setStringID(files.getFileID()+"");
        return new ResponseEntity<>(files,HttpStatus.OK);
    }

    public Files findFile(ObjectId id){ return  filesServices.findFile(id);}

    public boolean deleteFile(ObjectId id){
        if(filesServices.findFile(id)==null)
            return false;
        filesServices.deleteFile(id);
        return true;
    }

    @GetMapping("/get-files/file-id={id}")
    public ResponseEntity<?> downloadFile(@PathVariable ObjectId id){
        Files doc = filesServices.findFile(id);
        if(doc==null)
            return new ResponseEntity<>("file not found",HttpStatus.NOT_FOUND);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.getFiletype()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + doc.getFilename() + "\"")
                .body(doc.getData());
    }
}
