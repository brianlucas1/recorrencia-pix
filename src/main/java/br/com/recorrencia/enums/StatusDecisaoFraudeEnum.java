package br.com.recorrencia.enums;

public enum StatusDecisaoFraudeEnum {
	APROVADO(0), REVISAO_MANUAL(1), REJEITADO(2);

	private final int severidade;

	StatusDecisaoFraudeEnum(int severidade) {
	        this.severidade = severidade;
	    }

	public boolean maisSeveroQue(StatusDecisaoFraudeEnum outro) {
		return this.severidade > outro.severidade;
	}
}