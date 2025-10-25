package br.com.recorrencia.interfaces;

import br.com.recorrencia.dto.MensagemAgendamentoDTO;

public interface IntegracaoPixService {

	void dispararOrdemPagamento(MensagemAgendamentoDTO mensagem);

}
