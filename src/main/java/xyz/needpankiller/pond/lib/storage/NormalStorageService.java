package xyz.needpankiller.pond.lib.storage;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.StreamingOutput;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.server.multipart.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.needpankiller.pond.error.FileException;
import xyz.needpankiller.pond.helper.FileHelper;
import xyz.needpankiller.pond.dto.FileInfo;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import static xyz.needpankiller.pond.error.FileErrorCode.*;

@Provider
public final class NormalStorageService extends StorageService {

    private static Logger logger = LoggerFactory.getLogger(NormalStorageService.class);

    private static final Predicate<String> judgeUUID = uuidStr -> {
        try {
            UUID.fromString(uuidStr);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    };

    private final List<String> restrictList;

    @Inject
    public NormalStorageService(
            @ConfigProperty(name = "file.path") String realPathToSaved,
            @ConfigProperty(name = "file.size-usable-limit", defaultValue = "1073741824L") long sizeUsableLimit,
            @ConfigProperty(name = "file.size-upload-limit", defaultValue = "52428800L") long sizeUploadLimit,
            @ConfigProperty(name = "file.extension-restrict") List<String> restrictList) {
        super(realPathToSaved, sizeUsableLimit, sizeUploadLimit);
        this.restrictList = restrictList;
    }

//    @Inject
//    public NormalStorageService() {
//        this.realPathToSaved = "/files/";
//        this.sizeUsableLimit = 1073741824L;
//        this.sizeUploadLimit = 52428800L;
//        this.restrictList = Arrays.asList("exe","sh","js","ps1","bat","jsp","html","htm","php","cer","asp","pki","war","jar");
//    }

    /**
     * 파일 저장
     *
     * @param fileInfo 파일 정보 객체
     */
    public void store(FileInfo fileInfo) {
        try {
            validateFileItem(fileInfo.getFileItem());
            mkdir();
        } catch (IOException | FileException e) {
            logger.error("File save failed on validate : {}", e.getMessage());
            throw new FileException(FILE_STORAGE_USABLE_SIZE_LIMIT, e.getMessage());
        }
        FileHelper.checkExtensionRestrict(restrictList, fileInfo.getFileType());

        String filename = fileInfo.getChangedFileName();
        File file = new File(realPathToSaved + File.separator + filename);
        Path path = file.toPath();
        FileItem fileItem = fileInfo.getFileItem();
        try (InputStream inputStream = fileItem.getInputStream()) {
            java.nio.file.Files.copy(inputStream, path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileException(FILE_UPLOAD_FAILED, e.getMessage());
        }
    }

    /**
     * 파일 불러오기
     *
     * @param fileInfo 파일 정보 객체
     */
    public StreamingOutput load(FileInfo fileInfo) {
        String changedFileName = fileInfo.getChangedFileName();
        File file = new File(realPathToSaved + changedFileName);
        if (!file.exists()) {
            throw new FileException(FILE_DOWNLOAD_FILE_NOT_EXIST);
        }

        return outputStream -> {
            try (FileInputStream fis = new FileInputStream(file);
                 BufferedInputStream bis = new BufferedInputStream(fis);
                 BufferedOutputStream bos = new BufferedOutputStream(outputStream)
            ) {
                byte[] buff = new byte[2048];
                int bytesRead;
                while ((bytesRead = bis.read(buff)) != -1) {
                    bos.write(buff, 0, bytesRead);
                }
            } catch (IOException e) {
                throw new FileException(FILE_DOWNLOAD_FAILED, e.getMessage());
            }
        };
    }
}
