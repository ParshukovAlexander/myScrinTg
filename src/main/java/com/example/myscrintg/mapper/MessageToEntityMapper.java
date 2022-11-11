package com.example.myscrintg.mapper;

import com.example.myscrintg.entity.PhotoEntity;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;

@Component
public class MessageToEntityMapper {

//    public PhotoEntity getMapper(String text, InputFile inputFile, Update update){
////
////        PhotoEntity photo = new PhotoEntity();
////        photo.setIdClient(update.getMessage().getFrom().getId());
////        photo.setClientName(update.getMessage().getFrom().getUserName());
////        photo.setNamePhoto(text);
////        photo.setPhoto(inputFile.getAttachName());
////        photo.setLocalDateTime(LocalDateTime.now());
////        return  photo;
//    }

}
