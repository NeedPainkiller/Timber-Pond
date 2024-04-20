package xyz.needpankiller.pond.service;

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
import xyz.needpankiller.pond.entity.FileAuthorityType;
import xyz.needpankiller.pond.entity.FileEntity;
import xyz.needpankiller.pond.error.FileErrorCode;
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
    private Emitter<FileEntity> fileStoredEmitter;

    @Channel("timber__topic-file-downloaded")
    private Emitter<FileEntity> fileDownloadedEmitter;


    public FileInfo selectFile(String uuid, Long userPk) {

        FileEntity fileEntity = fileEntityRepository.find("SELECT f FROM FileEntity f WHERE f.uuid = '" + uuid + "'").singleResult();
//        FileEntity fileEntity = FileEntity.findByUuid(uuid);
        if (fileEntity == null) {
            throw new FileException(FILE_DOWNLOAD_FILE_INFO_NOT_EXIST);
        }
        if (!fileEntity.isUseYn()) {
            throw new FileException(FileErrorCode.FILE_DOWNLOAD_CAN_NOT_USABLE);
        }
        if (!fileEntity.isFileExists()) {
            throw new FileException(FileErrorCode.FILE_DOWNLOAD_FILE_NOT_EXIST);
        }
        FileAuthorityType authorityType = fileEntity.getAccessAuthority();
        if (authorityType.equals(FileAuthorityType.PRIVATE) && !fileEntity.getCreatedBy().equals(userPk)) {
            throw new FileException(FileErrorCode.FILE_DOWNLOAD_FILE_NOT_YOU_OWN);
        }
        sendFileDownloadedMessage(fileEntity);
        return mapFileInfo(fileEntity);
    }

/*    @WithTransaction
    public Uni<FileInfo> selectFileReactive(String uuid, Long userPk) {

        return fileEntityRepository.find("SELECT f FROM FileEntity f WHERE f.uuid = '" + uuid + "'").singleResult()
                .onItem().invoke(Unchecked.consumer(fileEntity -> {
                    if (!fileEntity.isUseYn()) {
                        throw new FileException(FileErrorCode.FILE_DOWNLOAD_CAN_NOT_USABLE);
                    }
                    if (!fileEntity.isFileExists()) {
                        throw new FileException(FileErrorCode.FILE_DOWNLOAD_FILE_NOT_EXIST);
                    }
                    FileAuthorityType authorityType = fileEntity.getAccessAuthority();
                    if (authorityType.equals(FileAuthorityType.PRIVATE) && !fileEntity.getCreatedBy().equals(userPk)) {
                        throw new FileException(FileErrorCode.FILE_DOWNLOAD_FILE_NOT_YOU_OWN);
                    }
//                    sendFileDownloadedMessage(fileEntity);
                }))
                .onItem().ifNull().failWith(new FileException(FILE_DOWNLOAD_FILE_INFO_NOT_EXIST))
                .onItem().ifNotNull().transform(this::mapFileInfo);
    }*/

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

    private FileEntity mapFileEntity(FileInfo fileInfo) {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setUuid(fileInfo.getUuid());
        fileEntity.setUseYn(fileInfo.isUseYn());
        fileEntity.setFileExists(fileInfo.isFileExists());
        fileEntity.setFileSize(fileInfo.getFileSize());
        fileEntity.setFileType(fileInfo.getFileType());
        fileEntity.setOriginalFileName(fileInfo.getOriginalFileName());
        fileEntity.setChangedFileName(fileInfo.getChangedFileName());
        fileEntity.setCreatedBy(fileInfo.getCreatedBy());
        return fileEntity;

    }

    public void sendFileStoredMessage(FileInfo fileInfo) {
        FileEntity fileEntity = mapFileEntity(fileInfo);
        logger.info("sendFileStoredMessage: {}", fileEntity);
        fileStoredEmitter.send(fileEntity);
    }

    public void sendFileDownloadedMessage(FileEntity fileEntity) {
        fileDownloadedEmitter.send(fileEntity);
    }

    public List<FileInfo> parseFileInfo(MultipartFormDataInput input) {
        Map<String, Collection<FormValue>> map = input.getValues();
        List<FileInfo> fileInfoList = new ArrayList<>();
        logger.info("file upload : {}", map.size() );
        for (var entry : map.entrySet()) {
            for (FormValue value : entry.getValue()) {
                boolean isFileItem = value.isFileItem();
                if (!isFileItem) {
                    logger.info("file upload is not multipart");
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

