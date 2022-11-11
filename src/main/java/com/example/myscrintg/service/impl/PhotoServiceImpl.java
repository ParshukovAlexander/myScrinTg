package com.example.myscrintg.service.impl;

import com.example.myscrintg.decode.Decode;
import com.example.myscrintg.entity.ClientEntity;
import com.example.myscrintg.entity.FolderEntity;
import com.example.myscrintg.entity.PhotoEntity;
import com.example.myscrintg.repository.PhotoRepository;
import com.example.myscrintg.service.ClientService;
import com.example.myscrintg.service.FolderService;
import com.example.myscrintg.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
public class PhotoServiceImpl implements PhotoService {

    @Autowired
    ClientService clientService;

    @Autowired
    FolderService folderService;

    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    Decode coder;

    @Override
    public String savePhoto(Update update,String directoryName) {
        if (update.getMessage().hasPhoto() && update.getMessage().getCaption()!=null) {
            PhotoEntity photoEntity = new PhotoEntity();
            photoEntity.setPhoto(coder.getEncode(update.getMessage().getPhoto().get(1).getFileId()));
            photoEntity.setFolder(folderService.getFolderByName(directoryName,update));
            photoEntity.setLocalDateTime(LocalDateTime.now());
            photoEntity.setPhotoName(update.getMessage().getCaption());
            photoRepository.save(photoEntity);
            return "Фотография сохранена! Для перехода к каталогам: /all_directory";
        } else if (!update.getMessage().hasPhoto()) {
            return "Ты забыл отправить фото...";
        }else return "Ты забыл написать название фотографии...";
    }

    @Override
    public PhotoEntity getPhoto(Update update,String nameDirectory) {
        FolderEntity folderByName = folderService.getFolderByName(nameDirectory,update);
        PhotoEntity photoEntitie = photoRepository.getPhotoEntitiesByPhotoNameAndFolder(update.getCallbackQuery().getData(),folderByName);
        String decode = coder.getDecode(photoEntitie.getPhoto());
        photoEntitie.setPhoto(decode);
        return photoEntitie;
    }

    @Override
    public List<PhotoEntity> getPhotoInFolder(FolderEntity folder) {
      return  photoRepository.getPhotoEntitiesByFolder(folder);
    }

    @Override
    public void delete(String folderName, String photoName, Update update) {
        PhotoEntity entities = photoRepository.getPhotoEntitiesByPhotoNameAndFolder(photoName, folderService.getFolderByName(folderName,update));
        photoRepository.delete(entities);
    }

    @Override
    public void changeNamePhoto(String newName,String oldName, String directory, Update update) {
        PhotoEntity entities = photoRepository.getPhotoEntitiesByPhotoNameAndFolder(oldName, folderService.getFolderByName(directory,update));
        entities.setPhotoName(newName);
        photoRepository.save(entities);
    }

    @Override
    public void changePhoto(String name, String directory, Update update) {
        PhotoEntity entities = photoRepository.getPhotoEntitiesByPhotoNameAndFolder(name, folderService.getFolderByName(directory,update));
        entities.setPhoto(coder.getEncode(update.getMessage().getPhoto().get(1).getFileId()));
        photoRepository.save(entities);
    }
}
