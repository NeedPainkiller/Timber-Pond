package xyz.needpankiller.pond.lib.storage;

import jakarta.ws.rs.core.StreamingOutput;
import org.apache.commons.io.FilenameUtils;
import org.jboss.resteasy.reactive.server.multipart.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.needpankiller.pond.error.FileErrorCode;
import xyz.needpankiller.pond.error.FileException;
import xyz.needpankiller.pond.dto.FileInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract sealed class StorageService permits NormalStorageService {

    private static Logger logger = LoggerFactory.getLogger(StorageService.class);

    protected static final Predicate<String> judgeUUID = uuidStr -> {
        try {
            UUID.fromString(uuidStr);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    };
    protected  long sizeUsableLimit;
    protected  long sizeUploadLimit;
    protected  String realPathToSaved;


    protected StorageService() {
        sizeUsableLimit = 0L;
        sizeUploadLimit = 0L;
        realPathToSaved = ".\\files\\";
    }

    protected StorageService(
            String realPathToSaved,
            long sizeUsableLimit,
            long sizeUploadLimit) {
        this.realPathToSaved = realPathToSaved;
        this.sizeUsableLimit = sizeUsableLimit;
        this.sizeUploadLimit = sizeUploadLimit;
    }


    /**
     * 파일 저장경로 디렉터리 생성
     */
    public void mkdir() {
        File realPathToSavedDir = new File(realPathToSaved);
        if (!realPathToSavedDir.exists()) {
            realPathToSavedDir.mkdirs();
        }
    }

    /**
     * 실제 파일 존재여부 확인
     *
     * @param filePath 파일 경로
     * @return 파일존재 여부
     */
    public boolean isFileExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    public String getRealPathToSaved() {
        return realPathToSaved;
    }

    public long getUsableSpace() {
        long usableSpace;
        try {
            NumberFormat nf = NumberFormat.getNumberInstance();
            Path fileStorePath = FileSystems.getDefault().getPath(realPathToSaved);
            FileStore store = java.nio.file.Files.getFileStore(fileStorePath);
            logger.info("UsableSpace | available= {}, total={}", nf.format(store.getUsableSpace()), nf.format(store.getTotalSpace()));
            usableSpace = store.getUsableSpace();
        } catch (IOException e) {
            logger.error("error querying space: {}", e.getMessage());
            usableSpace = 0L;
        }
        return usableSpace;
    }

    public boolean isFileStorageCanSaved() {
        return sizeUsableLimit < getUsableSpace();
    }


    /**
     * 파일 복수 삭제
     * Async
     *
     * @param fileList 파일 도메인 객체 리스트
     * @return 삭제된 파일 리스트
     */
    public List<FileInfo> remove(List<FileInfo> fileList) {
        if (fileList == null || fileList.isEmpty()) {
            return new ArrayList<>();
        }
        return fileList.stream().filter(this::removeRealFile).toList();
    }

    /**
     * 파일 삭제
     * Async
     *
     * @param file 파일 도메인 객체
     * @return 삭제여부
     */
    public Boolean remove(FileInfo file) {
        return removeRealFile(file);
    }

    private boolean removeRealFile(FileInfo file) {
        String changedFileName = file.getChangedFileName();
        Path savedFile = FileSystems.getDefault().getPath(realPathToSaved, changedFileName);
        boolean exists = java.nio.file.Files.exists(savedFile);
        if (exists) {
            try {
                return java.nio.file.Files.deleteIfExists(savedFile);
            } catch (IOException e) {
                logger.error("Failed Delete File IOException : {}", e.getMessage());
                return false;
            }
        } else {
            logger.info("File Not Exists : {}", changedFileName);
            return true;
        }
    }

    public List<String> findAllExistFileUuid() {
        try {
            Path fileStorePath = FileSystems.getDefault().getPath(realPathToSaved);
            try (Stream<Path> stream = java.nio.file.Files.list(fileStorePath)) {
                return stream
                        .filter(file -> !java.nio.file.Files.isDirectory(file))
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .map(FilenameUtils::removeExtension)
                        .filter(judgeUUID)
                        .toList();
            }
        } catch (IOException e) {
            logger.error("error querying space: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    protected void validateFileItem(FileItem fileItem) throws FileException, IOException {
        if (fileItem.getFileSize() > sizeUploadLimit) {
            throw new FileException(FileErrorCode.FILE_UPLOAD_SIZE_LIMIT);
        }
    }

    /**
     * 파일 저장
     *
     * @param fileInfo 파일 정보 객체
     */
    abstract void store(FileInfo fileInfo) throws IOException;

    /**
     * 파일 불러오기
     *
     * @param fileInfo 파일 정보 객체
     */
    abstract StreamingOutput load(FileInfo fileInfo) throws IOException;

/*    protected FileInfo generateFileInfo(MultipartFile multipartFile) {
        FileInfo file = generateFileInfo(multipartFile.getOriginalFilename());
        file.setFileSize(multipartFile.getSize());
        return file;
    }

    protected FileInfo generateFileInfo(String fileName) {
        String uuid = UUID.randomUUID().toString();
        String fileExtension = FileNameUtils.getExtension(fileName);
        String changedFileName = uuid + '.' + fileExtension;

        FileInfo file = new Files();
        file.setUuid(uuid);
        file.setFileType(fileExtension);
        file.setOriginalFileName(fileName);
        file.setChangedFileName(changedFileName);
        return file;
    }*/
}
