package xyz.needpankiller.pond.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import org.jboss.resteasy.reactive.server.multipart.FileItem;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FileInfo implements Serializable {
    private String uuid;
    private boolean useYn;
    private boolean fileExists;
    private String fileType;
    private Long fileSize;
    private String originalFileName;
    private String changedFileName;
    private Long createdBy;
    @JsonIgnore
    private FileItem fileItem;
    @JsonIgnore
    private Map<String, List<String>> headers;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isUseYn() {
        return useYn;
    }

    public void setUseYn(boolean useYn) {
        this.useYn = useYn;
    }

    public boolean isFileExists() {
        return fileExists;
    }

    public void setFileExists(boolean fileExists) {
        this.fileExists = fileExists;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }


    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getChangedFileName() {
        return changedFileName;
    }

    public void setChangedFileName(String changedFileName) {
        this.changedFileName = changedFileName;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public FileItem getFileItem() {
        return fileItem;
    }

    public void setFileItem(FileItem fileItem) {
        this.fileItem = fileItem;
        try {
            this.fileSize = fileItem.getFileSize();
        } catch (IOException e) {
            this.fileSize = 0L;
        }
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileInfo fileInfo = (FileInfo) o;
        return useYn == fileInfo.useYn && fileExists == fileInfo.fileExists && Objects.equals(uuid, fileInfo.uuid) && Objects.equals(fileType, fileInfo.fileType) && Objects.equals(fileSize, fileInfo.fileSize) && Objects.equals(originalFileName, fileInfo.originalFileName) && Objects.equals(changedFileName, fileInfo.changedFileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, useYn, fileExists, fileType, fileSize, originalFileName, changedFileName);
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "uuid='" + uuid + '\'' +
                ", useYn=" + useYn +
                ", fileExists=" + fileExists +
                ", fileType='" + fileType + '\'' +
                ", fileSize=" + fileSize +
                ", originalFileName='" + originalFileName + '\'' +
                ", changedFileName='" + changedFileName + '\'' +
                '}';
    }
}