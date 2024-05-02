package xyz.needpankiller.timber.pond.entity;

import xyz.needpankiller.timber.pond.lib.persistence.CodeEnum;
import xyz.needpankiller.timber.pond.lib.persistence.CodeEnumConverter;

import java.io.Serializable;
import java.util.Arrays;

public enum FileServiceType implements Serializable, CodeEnum {
    NONE(0),
    DEFAULT(1),
    ATTACH(2),
    IN_LINE(3),
    OTHER(9999);

    private final int code;

    FileServiceType(int code) {
        this.code = code;
    }

    public static FileServiceType of(int code) {
        return Arrays.stream(values())
                .filter(v -> v.code == code)
                .findFirst().orElse(NONE);
    }

    public static FileServiceType nameOf(String name) {
        return Arrays.stream(values())
                .filter(v -> name.equals(v.name()))
                .findFirst().orElse(NONE);
    }


    public static boolean isExist(FileServiceType serviceType) {
        return !serviceType.equals(NONE);
    }

    public static boolean isExist(int code) {
        return !of(code).equals(NONE);
    }

    @Override
    public Integer getCode() {
        return code;
    }


    public static class Converter implements CodeEnumConverter<FileServiceType> {
        @Override
        public Integer convertToDatabaseColumn(FileServiceType attribute) {
            return attribute.getCode();
        }

        @Override
        public FileServiceType convertToEntityAttribute(Integer dbData) {
            return of(dbData);
        }
    }
}
