package com.challenge.historico.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Configuração de segurança para o serviço de histórico.
 * Define autenticação básica e controle de acesso baseado em roles.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Permite acesso ao GraphiQL (interface web) apenas em desenvolvimento
                        .requestMatchers("/graphiql/**", "/actuator/**").permitAll()
                        // GraphQL requer autenticação - a autorização é feita nos métodos com @PreAuthorize
                        .requestMatchers("/graphql").authenticated()
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults());
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuração de usuários em memória para testes.
     * Em produção, isso deveria vir de um banco de dados ou serviço de autenticação.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails medico = User.builder()
                .username("medico1")
                .password(passwordEncoder().encode("password123"))
                .roles("MEDICO")
                .build();

        UserDetails enfermeiro = User.builder()
                .username("enfermeiro1")
                .password(passwordEncoder().encode("password123"))
                .roles("ENFERMEIRO")
                .build();

        UserDetails paciente = User.builder()
                .username("1") // Username = ID do paciente para facilitar controle de acesso
                .password(passwordEncoder().encode("password123"))
                .roles("PACIENTE")
                .build();

        return new InMemoryUserDetailsManager(medico, enfermeiro, paciente);
    }
}



