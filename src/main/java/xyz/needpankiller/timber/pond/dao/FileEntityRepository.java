package xyz.needpankiller.timber.pond.dao;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import xyz.needpankiller.timber.pond.entity.FileEntity;

@ApplicationScoped
public class FileEntityRepository implements PanacheRepository<FileEntity> {

}