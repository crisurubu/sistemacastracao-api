package com.projetoong.sistema_castracao.service;

import com.projetoong.sistema_castracao.model.Agendamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender; // Agora o Java sabe quem ele é!

    public void enviarRecomendacoes(String para, String nomePet) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("ong@seuemail.com"); // Aqui você colocará o e-mail da ONG depois
        message.setTo(para);
        message.setSubject("Pagamento Confirmado! Próximos passos para " + nomePet);

        String corpoEmail = String.format(
                "Olá!\n\n" +
                        "Confirmamos o pagamento da taxa social para o(a) %s.\n\n" +
                        "--- INSTRUÇÕES IMPORTANTES ---\n" +
                        "1. Jejum de 8h (água e comida) antes do procedimento.\n" +
                        "2. O animal deve estar em caixa de transporte ou coleira segura.\n" +
                        "3. É obrigatório apresentar um documento com foto no dia.\n\n" +
                        "--- ACOMPANHAMENTO ---\n" +
                        "Para conferir a DATA, o LOCAL EXATO e o HORÁRIO do mutirão, acesse o nosso portal:\n" +
                        "🔗 URL: http://portal.suaong.org\n" +
                        "Instruções: Digite seu CPF para consultar o agendamento.\n\n" +
                        "Nos vemos lá!", nomePet);

        message.setText(corpoEmail);

        // Agora o comando send vai funcionar porque está dentro da classe e o mailSender foi declarado acima
        mailSender.send(message);
    }
    // MÉTODO 2: NOVO! Envia quando o Admin define a data, hora e local
    public void enviarEmailAgendamento(Agendamento agendamento) {
        SimpleMailMessage message = new SimpleMailMessage();

        // Puxando os dados através do relacionamento que criamos
        String para = agendamento.getCadastro().getTutor().getEmail();
        String nomeTutor = agendamento.getCadastro().getTutor().getNome();
        String nomePet = agendamento.getCadastro().getPet().getNomeAnimal();

        // Formatando a data para ficar bonita no e-mail (Ex: 15/02/2026 09:00)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
        String dataFormatada = agendamento.getDataHora().format(formatter);

        message.setTo(para);
        message.setSubject("CONFIRMADO: Agendamento da Castração de " + nomePet);

        String corpoEmail = String.format(
                "Olá, %s!\n\n" +
                        "Temos uma ótima notícia! O agendamento para a castração do(a) %s foi finalizado.\n\n" +
                        "--- DADOS DO PROCEDIMENTO ---\n" +
                        "📅 DATA E HORA: %s\n" +
                        "📍 LOCAL: %s\n" +
                        "🔑 CÓDIGO DE SEGURANÇA (HASH): %s\n\n" +
                        "--- IMPORTANTE ---\n" +
                        "Guarde este código ou leve este e-mail impresso. Ele é a sua garantia e será verificado no local.\n" +
                        "Lembre-se do jejum de 8h e das normas de segurança.\n\n" +
                        "Até breve!",
                nomeTutor, nomePet, dataFormatada, agendamento.getLocal(), agendamento.getCodigoHash());

        message.setText(corpoEmail);
        mailSender.send(message);
    }
}