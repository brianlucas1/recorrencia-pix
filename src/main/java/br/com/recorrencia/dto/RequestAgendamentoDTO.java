package br.com.recorrencia.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import br.com.recorrencia.enums.PeriodicidadeEnum;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RequestAgendamentoDTO(
		
	@NotBlank(message = "Documento do pagador é obrigatório") 
	@Size(min = 11, max = 14, message = "Documento do pagador deve ter entre 11 e 14 caracteres")
	String documentoPagador,

	@NotBlank(message = "Nome do pagador é obrigatório")
	String nomePagador,

	@NotBlank(message = "Documento do recebedor é obrigatório")
	@Size(min = 11, max = 14, message = "Documento do recebedor deve ter entre 11 e 14 caracteres") 
	String documentoRecebedor,

	@NotBlank(message = "Nome do recebedor é obrigatório")
	String nomeRecebedor,

	@NotNull(message = "Valor é obrigatório")
	@DecimalMin(value = "0.01", inclusive = true, message = "Valor mínimo deve ser 0.01")
	BigDecimal valor,

	@NotNull(message = "Periodicidade é obrigatória")
	PeriodicidadeEnum periodicidade,

	@NotNull(message = "Data da primeira execução é obrigatória") @FutureOrPresent(message = "Data da primeira execução deve ser futura ou presente")
	OffsetDateTime primeiraExecucao,

	String descricao
) {}