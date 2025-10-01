package com.challenge.agendamento.service;

import com.challenge.agendamento.model.Usuario;
import com.challenge.agendamento.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Busca o usuário no banco de dados
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuário não encontrado com o nome: " + username));

        // 2. Converte os perfis (roles) para o formato que o Spring Security espera
        Set<GrantedAuthority> authorities = usuario
                .getRoles()
                .stream()
                .map((role) -> new SimpleGrantedAuthority("ROLE_" + role)) // Adiciona o prefixo ROLE_
                .collect(Collectors.toSet());

        return new User(usuario.getUsername(), usuario.getPassword(), authorities);
    }
}