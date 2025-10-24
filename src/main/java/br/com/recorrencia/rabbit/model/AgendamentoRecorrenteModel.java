package br.com.recorrencia.rabbit.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import br.com.recorrencia.enums.PeriodicidadeEnum;
import br.com.recorrencia.enums.StatusAgendamentoEnum;
import br.com.recorrencia.enums.StatusDecisaoFraudeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_agendamento_recorrente")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgendamentoRecorrenteModel {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "referencia_externa", nullable = false, unique = true)
    private String referenciaExterna;

    @Column(name = "documento_pagador", nullable = false, length = 14)
    private String documentoPagador;

    @Column(name = "documento_recebedor", nullable = false, length = 14)
    private String documentoRecebedor;

    @Column(name = "nome_pagador", nullable = false)
    private String nomePagador;

    @Column(name = "nome_recebedor", nullable = false)
    private String nomeRecebedor;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "valor", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "periodicidade", nullable = false)
    private PeriodicidadeEnum periodicidade;

    @Column(name = "primeira_execucao", nullable = false)
    private OffsetDateTime primeiraExecucao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_fraude", nullable = false)
    private StatusDecisaoFraudeEnum statusFraude;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusAgendamentoEnum status;

    @Column(name = "motivo_risco", length = 256)
    private String motivoRisco;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm;

    @PrePersist
    public void onCreate() {
        this.criadoEm = OffsetDateTime.now();
        this.atualizadoEm = this.criadoEm;
    }

    @PreUpdate
    public void onUpdate() {
        this.atualizadoEm = OffsetDateTime.now();
    }
    
	
}
