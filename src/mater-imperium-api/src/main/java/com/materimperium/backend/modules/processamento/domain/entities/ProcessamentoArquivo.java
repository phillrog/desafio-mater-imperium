package com.materimperium.backend.modules.processamento.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "processamento_arquivos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessamentoArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String nomeArquivo;

    private LocalDateTime dataCriacao;

    @Enumerated(EnumType.STRING)
    private StatusProcessamento status;

    @OneToMany(mappedBy = "processamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResumoRegistro> resumos = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
    }
}