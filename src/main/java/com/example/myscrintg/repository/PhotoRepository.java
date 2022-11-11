package com.example.myscrintg.repository;

import com.example.myscrintg.entity.FolderEntity;
import com.example.myscrintg.entity.PhotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<PhotoEntity,Long> {

    PhotoEntity getPhotoEntitiesByPhotoNameAndFolder(String photoName,FolderEntity folder);

    List<PhotoEntity> getPhotoEntitiesByFolder(FolderEntity folderEntity);


}
