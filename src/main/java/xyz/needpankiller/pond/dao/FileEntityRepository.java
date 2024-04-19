package xyz.needpankiller.pond.dao;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import xyz.needpankiller.pond.entity.FileEntity;

@ApplicationScoped
public class FileEntityRepository implements PanacheRepository<FileEntity> {

}