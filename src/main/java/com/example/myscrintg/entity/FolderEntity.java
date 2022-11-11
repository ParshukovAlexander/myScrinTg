package com.example.myscrintg.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "folder")
@NoArgsConstructor
public class FolderEntity {

    @Id
    @SequenceGenerator(name = "folderSequence", sequenceName = "folder_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "folderSequence")
    @Column(name = "id")
    private Long id;

    @Column(name = "folder_name")
    private String folderName;

    @ManyToOne(fetch = FetchType.LAZY)
    private ClientEntity client;
}
