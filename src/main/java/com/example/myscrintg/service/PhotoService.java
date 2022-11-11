package com.example.myscrintg.service;

import com.example.myscrintg.entity.FolderEntity;
import com.example.myscrintg.entity.PhotoEntity;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface PhotoService {

    String savePhoto(Update update,String directoryName);

    PhotoEntity getPhoto(Update update,String nameDirectory);

    List<PhotoEntity> getPhotoInFolder(FolderEntity folder);

    void delete(String folderName,String photoName, Update update);

    void changeNamePhoto (String newName,String oldName,String directory, Update update);

    void changePhoto (String Name,String directory,Update update);
}
