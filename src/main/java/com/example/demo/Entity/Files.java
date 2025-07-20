package com.example.demo.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "files")
public class Files {
    @Id
    private ObjectId fileID;
    private String filename;
    private String filetype;
    private Long fileSize;
    private String stringID;

    private byte[] data;


}
