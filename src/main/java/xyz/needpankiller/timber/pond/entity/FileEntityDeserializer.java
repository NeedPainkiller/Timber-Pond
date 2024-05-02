package xyz.needpankiller.timber.pond.entity;


import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class FileEntityDeserializer extends ObjectMapperDeserializer<FileEntity> {
    public FileEntityDeserializer() {
        super(FileEntity.class);
    }
}