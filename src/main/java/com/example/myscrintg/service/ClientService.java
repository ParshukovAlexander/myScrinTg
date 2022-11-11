package com.example.myscrintg.service;

import com.example.myscrintg.entity.ClientEntity;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface ClientService {

    void saveClient(Update update);

    ClientEntity getClient(Update update);


    void updateWithFolder(ClientEntity client);

    void deleteClient(Update update);
}


