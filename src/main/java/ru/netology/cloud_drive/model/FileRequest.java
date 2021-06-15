package ru.netology.cloud_drive.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class FileRequest implements Serializable, Comparable<FileRequest> {

    private String filename;

    private long size;

    @Override
    public int compareTo(FileRequest o) {
        return (this.getFilename().compareTo(o.getFilename()));
    }
}
