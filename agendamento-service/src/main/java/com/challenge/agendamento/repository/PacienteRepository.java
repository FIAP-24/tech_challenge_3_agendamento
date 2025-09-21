package com.challenge.agendamento.repository;

import com.challenge.agendamento.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    
    /**
     * Busca paciente por CPF
     */
    Optional<Paciente> findByCpf(String cpf);
    
    /**
     * Busca paciente por email
     */
    Optional<Paciente> findByEmail(String email);
    
    /**
     * Busca pacientes por nome (case insensitive)
     */
    List<Paciente> findByNomeContainingIgnoreCase(String nome);
    
    /**
     * Busca pacientes ativos
     */
    List<Paciente> findByAtivoTrue();
    
    /**
     * Busca pacientes inativos
     */
    List<Paciente> findByAtivoFalse();
    
    /**
     * Verifica se existe paciente com CPF (excluindo o ID fornecido)
     */
    @Query("SELECT COUNT(p) > 0 FROM Paciente p WHERE p.cpf = :cpf AND p.id != :id")
    boolean existsByCpfAndIdNot(@Param("cpf") String cpf, @Param("id") Long id);
    
    /**
     * Verifica se existe paciente com email (excluindo o ID fornecido)
     */
    @Query("SELECT COUNT(p) > 0 FROM Paciente p WHERE p.email = :email AND p.id != :id")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("id") Long id);
    
    /**
     * Busca pacientes por cidade
     */
    List<Paciente> findByCidadeIgnoreCase(String cidade);
    
    /**
     * Busca pacientes por estado
     */
    List<Paciente> findByEstadoIgnoreCase(String estado);
}
