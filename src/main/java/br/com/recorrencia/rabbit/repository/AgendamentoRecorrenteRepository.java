package br.com.recorrencia.rabbit.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.recorrencia.enums.StatusAgendamentoEnum;
import br.com.recorrencia.rabbit.model.AgendamentoRecorrenteModel;

public interface AgendamentoRecorrenteRepository extends JpaRepository<AgendamentoRecorrenteModel, Long> {


    long countByDocumentoPagadorAndStatusIn(String documentoPagador, Collection<StatusAgendamentoEnum> status);

    Optional<AgendamentoRecorrenteModel> findByReferenciaExterna(String referenciaExterna);
	
}
