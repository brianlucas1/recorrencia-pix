package br.com.recorrencia.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.recorrencia.enums.StatusAgendamentoEnum;
import br.com.recorrencia.model.AgendamentoRecorrente;

public interface AgendamentoRecorrenteRepository extends JpaRepository<AgendamentoRecorrente, Long> {


    long countByDocumentoPagadorAndStatusIn(String documentoPagador, Collection<StatusAgendamentoEnum> status);

    Optional<AgendamentoRecorrente> findByReferenciaExterna(String referenciaExterna);
	
}
 