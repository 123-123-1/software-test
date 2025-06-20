package com.tongji.sportmanagement.SocializeSubsystem.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import lombok.*;

import java.time.Instant;


@Data
@Getter
@Setter
@Entity
@Table(name = "chat")
@AllArgsConstructor
@NoArgsConstructor
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id", nullable = false)
    private Integer chatId;

    @Column(name = "chat_name", length = 50)
    private String chatName;

    @NotNull
    @ColumnDefault("'friendGroup'")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ChatType type;


    @Column(name = "creation_time")
    private Instant creatingTime;

    @Column(name = "photo", length = 100)
    private String photo;
}
