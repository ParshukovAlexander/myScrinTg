package com.example.myscrintg.service;

import com.example.myscrintg.entity.ClientEntity;
import com.example.myscrintg.entity.FolderEntity;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface FolderService {

    String saveFolder(Update update);

    List <FolderEntity> getAllDirectoryForId(ClientEntity client);

//    Long getFolderForName(String name);

    FolderEntity getFolderByName (String name,Update update);

    void delete(String nameFolder,Update update);

    void changeName(String newName,String oldName, Update update);
}
