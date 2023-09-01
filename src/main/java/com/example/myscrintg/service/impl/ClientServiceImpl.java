package com.example.myscrintg.service.impl;

import com.example.myscrintg.entity.ClientEntity;
import com.example.myscrintg.entity.FolderEntity;
import com.example.myscrintg.entity.PhotoEntity;
import com.example.myscrintg.entity.Role;
import com.example.myscrintg.repository.ClientRepository;
import com.example.myscrintg.repository.FolderRepository;
import com.example.myscrintg.repository.PhotoRepository;
import com.example.myscrintg.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Objects;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    FolderRepository folderRepository;

    @Autowired
    PhotoRepository photoRepository;
    

    @Override
    public void saveClient(Update update,Long idAdmin) {


        if (clientRepository.getClientEntityByIdClientTg(update.getMessage().getFrom().getId())==null) {
            ClientEntity client = new ClientEntity();
            client.setClientName(update.getMessage().getFrom().getUserName());
            client.setIdClientTg(update.getMessage().getFrom().getId());
            if (Objects.equals(idAdmin, update.getMessage().getFrom().getId())){
                client.setRole(Role.ADMIN);
            }else {
                client.setRole(Role.USER);
            }
            clientRepository.save(client);
        }
    }

    @Override
    public ClientEntity getClient(Update update) {
        Long tgID;
        if (update.hasCallbackQuery()){
            tgID=update.getCallbackQuery().getFrom().getId();
        }else {
            tgID=update.getMessage().getFrom().getId();
        }
        System.out.println("tg"+tgID);
        return clientRepository.getClientEntityByIdClientTg(tgID);
    }



    @Override
    public void updateWithFolder(ClientEntity client) {
        clientRepository.save(client);
    }

    @Override
    public void deleteClient(Update update) {
        Long id = update.getMessage().getFrom().getId();
        ClientEntity clientEntityByIdClientTg = clientRepository.getClientEntityByIdClientTg(id);
        List<FolderEntity> folderEntitiesByClient = folderRepository.getFolderEntitiesByClient(clientEntityByIdClientTg);

        for (FolderEntity f:folderEntitiesByClient) {
            List<PhotoEntity> photoEntitiesByFolder = photoRepository.getPhotoEntitiesByFolder(f);
            photoRepository.deleteAll(photoEntitiesByFolder);
        }


        folderRepository.deleteAll(folderEntitiesByClient);
        clientRepository.delete(clientEntityByIdClientTg);

    }


}
