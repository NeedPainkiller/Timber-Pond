package xyz.needpankiller.timber.pond.entity;

import xyz.needpankiller.timber.pond.lib.persistence.CodeEnum;
import xyz.needpankiller.timber.pond.lib.persistence.CodeEnumConverter;

import java.io.Serializable;
import java.util.Arrays;

public enum FileAuthorityType implements Serializable, CodeEnum {
    NONE(0),
    PRIVATE(1),
    PUBLIC(2),
    LOGON(3);

    private final int code;

    FileAuthorityType(int code) {
        this.code = code;
    }

    public static FileAuthorityType of(int code) {
        return Arrays.stream(values())
                .filter(v -> v.code == code)
                .findFirst().orElse(NONE);
    }

    public static FileAuthorityType nameOf(String name) {
        return Arrays.stream(values())
                .filter(v -> name.equals(v.name()))
                .findFirst().orElse(NONE);
    }


    public static boolean isExist(FileAuthorityType authorityType) {
        return !authorityType.equals(NONE);
    }

    public static boolean isExist(int code) {
        return !of(code).equals(NONE);
    }
    @Override
    public Integer getCode() {
        return code;
    }

    public static class Converter implements CodeEnumConverter<FileAuthorityType> {
        @Override
        public Integer convertToDatabaseColumn(FileAuthorityType attribute) {
            return attribute.getCode();
        }

        @Override
        public FileAuthorityType convertToEntityAttribute(Integer dbData) {
            return of(dbData);
        }
    }
}
