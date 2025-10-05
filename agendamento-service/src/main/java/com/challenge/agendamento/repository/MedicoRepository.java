package com.challenge.agendamento.repository;

import com.challenge.agendamento.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {
    
    Optional<Medico> findByCrm(String crm);
    
    Optional<Medico> findByEmail(String email);
    
    List<Medico> findByAtivoTrue();
    
    List<Medico> findByEspecialidade(String especialidade);
    
    List<Medico> findByAtivoTrueAndEspecialidade(String especialidade);
    
    @Query("SELECT m FROM Medico m WHERE m.nome LIKE %:nome%")
    List<Medico> findByNomeContaining(@Param("nome") String nome);
    
    @Query("SELECT m FROM Medico m WHERE m.ativo = true AND m.nome LIKE %:nome%")
    List<Medico> findByAtivoTrueAndNomeContaining(@Param("nome") String nome);
    
    boolean existsByCrm(String crm);
    
    boolean existsByEmail(String email);
}
