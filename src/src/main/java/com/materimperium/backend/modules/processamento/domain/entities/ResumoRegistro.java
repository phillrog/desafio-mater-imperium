package com.materimperium.backend.modules.processamento.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "resumo_registros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumoRegistro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigoRegistro;
    private Long quantidade;

    @ManyToOne
    @JoinColumn(name = "processamento_id")
    private ProcessamentoArquivo processamento;
}