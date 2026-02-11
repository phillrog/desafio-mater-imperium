package com.materimperium.backend.modules.processamento.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "processamento_arquivos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProcessamentoArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String nomeArquivo;

    private LocalDateTime dataCriacao;      
    private LocalDateTime dataHoraInicio;   
    private LocalDateTime dataHoraFinalizou;

    @Enumerated(EnumType.STRING)
    private StatusProcessamento status;

    private Integer usuarioId;

    @Builder.Default
    @OneToMany(mappedBy = "processamento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ResumoRegistro> resumos = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
    }

    public void adicionarResumo(String codigo, Long quantidade) {
        if (this.resumos == null) {
            this.resumos = new ArrayList<>();
        }

        ResumoRegistro novoResumo = ResumoRegistro.builder()
                .codigoRegistro(codigo)
                .quantidade(quantidade)
                .processamento(this) // Mantém a consistência bidirecional
                .build();

        this.resumos.add(novoResumo);
    }
}