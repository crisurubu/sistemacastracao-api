package com.projetoong.sistema_castracao.service;

import com.projetoong.sistema_castracao.model.Agendamento;
import com.projetoong.sistema_castracao.model.Clinica; // Import necessário
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Configuração fixa do e-mail da ONG conforme solicitado
    private static final String EMAIL_ONG = "sistemacastracao@gmail.com";

    // --- MÉTODO 3: NOVO! Envia Boas-vindas e Acesso para a Clínica ---
    public void enviarEmailBoasVindasClinica(Clinica clinica, String senhaPlana) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(EMAIL_ONG);
        message.setTo(clinica.getAdministrador().getEmail());
        message.setSubject("Bem-vinda ao Projeto Castração Acessível a Todos! 🐾");

        String corpoEmail = String.format(
                "Olá, %s!\n\n" +
                        "É com imensa alegria que a equipe da Sistema Castração ONG recebe sua clínica como parceira oficial.\n\n" +
                        "Sua colaboração é fundamental para o sucesso do projeto 'Castração Acessível a Todos'. " +
                        "Graças à sua estrutura e dedicação, conseguiremos oferecer serviços de qualidade para famílias " +
                        "que não teriam condições de arcar com os custos integrais, combatendo o abandono e zelando pela saúde pública.\n\n" +
                        "--- SEUS DADOS DE ACESSO AO PAINEL ---\n" +
                        "Para gerenciar os atendimentos e confirmar as castrações realizadas, utilize as credenciais abaixo:\n\n" +
                        "🔗 Link de Acesso: http://localhost:5173/admin/login\n" +
                        "👤 Usuário (E-mail): %s\n" +
                        "🔑 Senha Temporária: %s\n\n" +
                        "Por segurança, recomendamos que altere sua senha no primeiro acesso.\n\n" +
                        "Estamos muito felizes em ter vocês conosco nesta missão!\n\n" +
                        "Atenciosamente,\n" +
                        "Sistema Castração ONG",
                clinica.getNome(),
                clinica.getAdministrador().getEmail(),
                senhaPlana
        );

        message.setText(corpoEmail);
        mailSender.send(message);
    }

    // --- MÉTODOS ANTERIORES PRESERVADOS ---

    public void enviarRecomendacoes(String para, String nomePet) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(EMAIL_ONG);
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
        mailSender.send(message);
    }

    public void enviarEmailAgendamento(Agendamento agendamento) {
        SimpleMailMessage message = new SimpleMailMessage();
        String para = agendamento.getCadastro().getTutor().getEmail();
        String nomeTutor = agendamento.getCadastro().getTutor().getNome();
        String nomePet = agendamento.getCadastro().getPet().getNomeAnimal();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
        String dataFormatada = agendamento.getDataHora().format(formatter);

        message.setFrom(EMAIL_ONG);
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

    // --- MÉTODO: Pagamento Não Identificado (Recusa de Comprovante) ---
    public void enviarEmailPagamentoNaoIdentificado(String para, String nomeTutor, String nomePet, String motivo) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(EMAIL_ONG);
        message.setTo(para);
        message.setSubject("⚠️ Pendência: Pagamento não identificado para " + nomePet);

        String corpoEmail = String.format(
                "Olá, %s!\n\n" +
                        "Ao analisarmos o comprovante enviado para a castração do(a) %s, nossa equipe de voluntários não conseguiu validar o pagamento.\n\n" +
                        "O motivo informado foi: %s\n\n" +
                        "--- COMO RESOLVER ---\n" +
                        "1. Verifique se o valor transferido está correto.\n" +
                        "2. Certifique-se de que o comprovante enviado está legível e completo.\n" +
                        "3. Acesse o portal novamente e faça o reenvio do arquivo válido.\n\n" +
                        "🔗 Link do Portal: http://localhost:5173\n\n" +
                        "Sua vaga só será confirmada e o agendamento liberado após a validação correta deste pagamento.\n\n" +
                        "Atenciosamente,\n" +
                        "Equipe Sistema Castração ONG",
                nomeTutor, nomePet, motivo);

        message.setText(corpoEmail);
        mailSender.send(message);
    }
}