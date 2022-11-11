package com.example.myscrintg.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "client_photo")
@NoArgsConstructor
@ToString
public class PhotoEntity {

    @Id
    @SequenceGenerator(name = "client_photoSequence", sequenceName = "client_photo_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_photoSequence")
    @Column(name = "id")
    private Long id;

    @Column(name = "photo")
    private String Photo;

    @Column(name = "photo_name")
    private String photoName;

    @Column(name = "date_send")
    private LocalDateTime localDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    private FolderEntity folder;
}
