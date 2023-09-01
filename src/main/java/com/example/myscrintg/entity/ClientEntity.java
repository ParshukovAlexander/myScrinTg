package com.example.myscrintg.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "client")
@NoArgsConstructor
@ToString
public class ClientEntity {

    @Id
    @SequenceGenerator(name = "clientSequence", sequenceName = "client_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "clientSequence")
    @Column(name = "id")
    private Long id;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "client_id_tg")
    private Long idClientTg;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;
}
