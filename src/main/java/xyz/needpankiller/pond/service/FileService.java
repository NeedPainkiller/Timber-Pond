package xyz.needpankiller.pond.service;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.ext.Provider;
import org.apache.commons.compress.utils.FileNameUtils;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.resteasy.reactive.server.multipart.FileItem;
import org.jboss.resteasy.reactive.server.multipart.FormValue;
import org.jboss.resteasy.reactive.server.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.needpankiller.pond.dao.FileEntityRepository;
import xyz.needpankiller.pond.dto.FileInfo;
import xyz.needpankiller.pond.entity.FileEntity;
import xyz.needpankiller.pond.error.FileException;
import xyz.needpankiller.pond.helper.FileHelper;

import java.util.*;

import static xyz.needpankiller.pond.error.FileErrorCode.FILE_DOWNLOAD_FILE_INFO_NOT_EXIST;
import static xyz.needpankiller.pond.error.FileErrorCode.FILE_UPLOAD_IS_NOT_MULTIPART;

@Provider
public class FileService {
    private static Logger logger = LoggerFactory.getLogger(FileService.class);

    @Inject
    private FileEntityRepository fileEntityRepository;
    @Channel("timber__topic-file-stored")
    private Emitter<FileInfo> fileInfoEmitter;

    @WithSession
    public Uni<FileInfo> selectFile(String uuid) {
        return fileEntityRepository.find("SELECT f FROM FileEntity f WHERE f.uuid = '" + uuid+"'").singleResult()
                .onItem().ifNotNull().transform(this::mapFileInfo)
                .onItem().ifNull().failWith(new FileException(FILE_DOWNLOAD_FILE_INFO_NOT_EXIST));
    }

    private FileInfo mapFileInfo(FileEntity fileEntity) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setUuid(fileEntity.getUuid());
        fileInfo.setUseYn(fileEntity.isUseYn());
        fileInfo.setFileExists(fileEntity.isFileExists());
        fileInfo.setFileType(fileEntity.getFileType());
        fileInfo.setOriginalFileName(fileEntity.getOriginalFileName());
        fileInfo.setChangedFileName(fileEntity.getChangedFileName());
        return fileInfo;

    }

    public void sendFileStoredMessage(FileInfo fileInfo) throws InterruptedException {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setUuid(fileInfo.getUuid());
        fileEntity.setUseYn(fileInfo.isUseYn());
        fileEntity.setFileExists(fileInfo.isFileExists());
        fileEntity.setFileType(fileInfo.getFileType());
        fileEntity.setOriginalFileName(fileInfo.getOriginalFileName());
        fileEntity.setChangedFileName(fileInfo.getChangedFileName());
        logger.info("sendFileStoredMessage: {}", fileInfo);
        fileInfoEmitter.send(fileInfo);
    }

    public List<FileInfo> parseFileInfo(MultipartFormDataInput input) {
        HashMap<String, Collection<FormValue>> map = (HashMap<String, Collection<FormValue>>) input.getValues();
        List<FileInfo> fileInfoList = new ArrayList<>();
        for (var entry : map.entrySet()) {
            for (FormValue value : entry.getValue()) {
                boolean isFileItem = value.isFileItem();
                if (!isFileItem) {
                    throw new FileException(FILE_UPLOAD_IS_NOT_MULTIPART);
                }
                FileItem fileItem = value.getFileItem();

                Map<String, List<String>> headers = value.getHeaders();
                String fileName = FileHelper.getFileName(headers);

                String uuid = UUID.randomUUID().toString();
                String fileExtension = FileNameUtils.getExtension(fileName);
                String changedFileName = uuid + '.' + fileExtension;

                FileInfo uploadFileInfoInfo = new FileInfo();
                uploadFileInfoInfo.setUuid(uuid);
                uploadFileInfoInfo.setUseYn(true);
                uploadFileInfoInfo.setFileExists(false);
                uploadFileInfoInfo.setFileType(fileExtension);
                uploadFileInfoInfo.setOriginalFileName(fileName);
                uploadFileInfoInfo.setChangedFileName(changedFileName);
                uploadFileInfoInfo.setFileItem(fileItem);
                uploadFileInfoInfo.setHeaders(headers);
                fileInfoList.add(uploadFileInfoInfo);
            }
        }
        return fileInfoList;
    }
}

