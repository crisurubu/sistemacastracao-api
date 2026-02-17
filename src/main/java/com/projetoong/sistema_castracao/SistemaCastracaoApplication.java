package com.projetoong.sistema_castracao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import jakarta.annotation.PostConstruct; // Importante adicionar
import java.util.TimeZone; // Importante adicionar

@SpringBootApplication
public class SistemaCastracaoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaCastracaoApplication.class, args);
	}

	@PostConstruct
	public void init() {
		// Esse é o ajuste cirúrgico: trava o sistema no horário de Brasília
		TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
		System.out.println("Sistema rodando no fuso horário: " + TimeZone.getDefault().getID());
	}

}