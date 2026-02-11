package com.materimperium.backend.modules.processamento.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "resumo_registros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ResumoRegistro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @EqualsAndHashCode.Include
    private String codigoRegistro;
    private Long quantidade;

    @ManyToOne(fetch = FetchType.LAZY) // Lazy evita carregar o pai sem necessidade
    @JoinColumn(name = "processamento_id")
    @ToString.Exclude // Impede o loop no log/toString
    @JsonIgnore
    private ProcessamentoArquivo processamento;

    public Long getQuantidade() {
        return quantidade;
    }

    public String getCodigoRegistro() {
        return codigoRegistro;
    }

    public Long getId() {
        return id;
    }
}