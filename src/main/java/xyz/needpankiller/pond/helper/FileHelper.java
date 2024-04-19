package xyz.needpankiller.pond.helper;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.needpankiller.pond.error.FileException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static xyz.needpankiller.pond.error.FileErrorCode.FILE_INVALID_NAME_FAILED;
import static xyz.needpankiller.pond.error.FileErrorCode.FILE_RESTRICT_EXTENSION;


public class FileHelper {

    private static Logger logger = LoggerFactory.getLogger(FileHelper.class);
    private static final Pattern filenamePattern = Pattern.compile("filename=\"[^\"]*\"");

    public static String getFileName(Map<String, List<String>> headers) throws RuntimeException {
        Collection<List<String>> headerValues = headers.values();

        for (List<String> headerValue : headerValues) {
            for (String headerStringValue : headerValue) {
                Matcher filenameMatcher = filenamePattern.matcher(headerStringValue);
                if (filenameMatcher.find()) {
                    String matched = filenameMatcher.group();
                    return matched.substring(matched.indexOf('"') + 1, matched.lastIndexOf('"'));
                }
            }
        }
        return "unknown";
    }

    public static String getFileExtension(String fileName) throws FileException {
        int lastIndexOf = Objects.requireNonNull(fileName).lastIndexOf(".");
        if (lastIndexOf == -1) {
            throw new FileException(FILE_INVALID_NAME_FAILED, fileName);
        }
        return fileName.substring(lastIndexOf);
    }

    public static void checkExtensionRestrict(List<String> restrictList, String fileExtension) {
        restrictList.forEach(restrictExtension -> {
            if (fileExtension.equalsIgnoreCase(restrictExtension)) {
                throw new FileException(FILE_RESTRICT_EXTENSION, String.format("%s 파일은 제한된 파일 형식입니다. ", fileExtension));
            }
        });
    }
}
