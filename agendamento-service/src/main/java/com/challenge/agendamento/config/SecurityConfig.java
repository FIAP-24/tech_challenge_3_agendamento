package com.challenge.agendamento.config;

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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Habilita segurança em nível de método
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desabilitar CSRF para APIs (GraphQL/REST)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/graphql", "/graphiql/**", "/h2-console/**").permitAll() // Permite acesso ao GraphiQL e H2 Console
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults()); // Usa autenticação básica
        // Permite que o H2 console seja renderizado em um frame
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Para fins de teste, cria usuários em memória. O ideal seria usar um UserDetailsService com banco de dados.
    //TODO tirar e implementar a criacao de usuarios e perfil
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails medico = User.builder()
                .username("medico")
                .password(passwordEncoder().encode("password"))
                .roles("MEDICO") // Perfil Médico [cite: 16]
                .build();
        UserDetails enfermeiro = User.builder()
                .username("enfermeiro")
                .password(passwordEncoder().encode("password"))
                .roles("ENFERMEIRO") // Perfil Enfermeiro [cite: 17]
                .build();
        UserDetails paciente = User.builder()
                .username("paciente")
                .password(passwordEncoder().encode("password"))
                .roles("PACIENTE") // Perfil Paciente [cite: 18]
                .build();
        return new InMemoryUserDetailsManager(medico, enfermeiro, paciente);
    }
}