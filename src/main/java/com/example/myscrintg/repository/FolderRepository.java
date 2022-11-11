package com.example.myscrintg.repository;

import com.example.myscrintg.entity.ClientEntity;
import com.example.myscrintg.entity.FolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<FolderEntity,Long> {

    List<FolderEntity> getFolderEntitiesByClient(ClientEntity client);


    FolderEntity getFolderEntityByFolderNameAndClient(String folderName,ClientEntity client);

}
