package com.example.myscrintg.service.impl;

import com.example.myscrintg.entity.ClientEntity;
import com.example.myscrintg.entity.FolderEntity;
import com.example.myscrintg.repository.FolderRepository;
import com.example.myscrintg.repository.PhotoRepository;
import com.example.myscrintg.service.ClientService;
import com.example.myscrintg.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Service
public class FolderServiceImpl implements FolderService {

    @Autowired
    ClientService clientService;

    @Autowired
    FolderRepository folderRepository;



    @Autowired
    PhotoRepository photoRepository;

    @Override
    public void saveFolder(Update update) {

        FolderEntity folder =new FolderEntity();
        folder.setFolderName(update.getMessage().getText());

        ClientEntity client = clientService.getClient(update);

        folder.setClient(client);

        folderRepository.save(folder);
       clientService.updateWithFolder(client);
    }

    @Override
    public List<FolderEntity> getAllDirectoryForId(ClientEntity client) {
       return folderRepository.getFolderEntitiesByClient(client);
    }


    @Override
    public FolderEntity getFolderByName(String name,Update update) {
        System.out.println("client"+clientService.getClient(update));
       return folderRepository.getFolderEntityByFolderNameAndClient(name,clientService.getClient(update));
    }

    @Override
    public void delete( String nameFolder,Update update) {

        FolderEntity folderEntity = folderRepository.getFolderEntityByFolderNameAndClient(nameFolder, clientService.getClient(update));
        photoRepository.deleteAll(photoRepository.getPhotoEntitiesByFolder(folderEntity));
        folderRepository.delete(folderEntity);

    }

    @Override
    public void changeName(String newNameFolder,String oldName, Update update) {
        FolderEntity folderEntity = folderRepository.getFolderEntityByFolderNameAndClient(oldName, clientService.getClient(update));
        folderEntity.setFolderName(newNameFolder);
        folderRepository.save(folderEntity);
    }

}
