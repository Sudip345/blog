package com.example.demo.Service;

import com.example.demo.Entity.Files;
import com.example.demo.Repository.FileRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FilesServices {
    @Autowired
    FileRepository fileRepository;
    @Transactional
    public Files insertFile(MultipartFile file1) throws IOException {
        Files file2 = new Files();
        file2.setFileSize(file1.getSize());
        file2.setFiletype(file1.getContentType());
        file2.setFilename(file1.getOriginalFilename());
        file2.setData(file1.getBytes());
        return fileRepository.save(file2);
    }

    public Files saveFile(Files files){
        return fileRepository.save(files);
    }
    public boolean deleteFile(ObjectId id){
        if(fileRepository.findById(id).orElse(null)==null)
            return false;
        fileRepository.deleteById(id);
        return true;
    }

    public Files findFile(ObjectId id){
        return fileRepository.findById(id).orElse(null);
    }
}
