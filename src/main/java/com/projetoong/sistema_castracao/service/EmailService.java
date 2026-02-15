package com.projetoong.sistema_castracao.service;

import com.projetoong.sistema_castracao.model.Agendamento;
import com.projetoong.sistema_castracao.model.Clinica;
import com.projetoong.sistema_castracao.model.Voluntario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Configuração com Nome de Exibição para evitar SPAM
    private static final String EMAIL_ONG = "Sistema Castracao ong <sistemacastracao@gmail.com>";

    // =========================================================================
    // 1. COMUNICAÇÃO COM CLÍNICAS (PARCEIROS)
    // =========================================================================

    public void enviarEmailBoasVindasClinica(Clinica clinica, String senhaPlana) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(EMAIL_ONG);
            message.setTo(clinica.getAdministrador().getEmail());
            message.setSubject("Bem-vinda ao Projeto Castração Acessível a Todos! 🐾");

            String corpoEmail = String.format(
                    "Olá, %s!\n\n" +
                            "É com imensa alegria que a equipe da Sistema Castração ONG recebe sua clínica como parceira oficial.\n\n" +
                            "Sua colaboração é fundamental para o sucesso do projeto. Graças à sua estrutura, " +
                            "zelaremos pela saúde pública e bem-estar animal.\n\n" +
                            "--- SEUS DADOS DE ACESSO AO PAINEL ---\n" +
                            "🔗 Link: https://sistema-castracao-app.onrender.com/admin/login\n" +
                            "👤 Usuário: %s\n" +
                            "🔑 Senha Temporária: %s\n\n" +
                            "Recomendamos que altere sua senha no primeiro acesso.\n\n" +
                            "Atenciosamente,\nSistema Castração ONG",
                    clinica.getNome(), clinica.getAdministrador().getEmail(), senhaPlana
            );

            message.setText(corpoEmail);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("ERRO EMAIL CLINICA: " + e.getMessage());
        }
    }

    public void enviarEmailSenhaAlteradaClinica(Clinica clinica, String novaSenhaPlana) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(EMAIL_ONG);
            message.setTo(clinica.getAdministrador().getEmail());
            message.setSubject("⚠️ Alteração de Credenciais: Clínica - Sistema Castração ONG");

            String corpoEmail = String.format(
                    "Olá, %s!\n\n" +
                            "Informamos que a senha de acesso da sua clínica foi alterada pelo administrador da ONG.\n\n" +
                            "--- NOVAS CREDENCIAIS ---\n" +
                            "👤 Usuário: %s\n" +
                            "🔑 Nova Senha: %s\n\n" +
                            "🔗 Acesse aqui: https://sistema-castracao-app.onrender.com/admin/login\n\n" +
                            "Atenciosamente,\nSistema Castração ONG",
                    clinica.getNome(), clinica.getAdministrador().getEmail(), novaSenhaPlana
            );

            message.setText(corpoEmail);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("ERRO EMAIL SENHA CLINICA: " + e.getMessage());
        }
    }

    // =========================================================================
    // 2. COMUNICAÇÃO COM VOLUNTÁRIOS (EQUIPE INTERNA)
    // =========================================================================

    public void enviarEmailBoasVindasVoluntario(Voluntario voluntario, String senhaPlana) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(EMAIL_ONG);
            message.setTo(voluntario.getAdministrador().getEmail());
            message.setSubject("Bem-vindo(a) à Equipe de Voluntários! 🐾");

            String corpoEmail = String.format(
                    "Olá, %s!\n\n" +
                            "Seja muito bem-vindo(a) ao time da Sistema Castração ONG!\n\n" +
                            "Você terá um papel fundamental na organização dos mutirões e suporte aos tutores.\n\n" +
                            "--- SEUS DADOS DE ACESSO ---\n" +
                            "🔗 Link: https://sistema-castracao-app.onrender.com/admin/login\n" +
                            "👤 Usuário: %s\n" +
                            "🔑 Senha Temporária: %s\n\n" +
                            "Atenciosamente,\nSistema Castração ONG",
                    voluntario.getNome(), voluntario.getAdministrador().getEmail(), senhaPlana
            );

            message.setText(corpoEmail);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("ERRO EMAIL VOLUNTARIO: " + e.getMessage());
        }
    }

    public void enviarEmailSenhaAlterada(Voluntario voluntario, String novaSenhaPlana) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(EMAIL_ONG);
            message.setTo(voluntario.getAdministrador().getEmail());
            message.setSubject("⚠️ Sua senha de acesso foi alterada - Sistema Castração ONG");

            String corpoEmail = String.format(
                    "Olá, %s!\n\n" +
                            "Informamos que a sua senha de acesso ao painel administrativo foi alterada recentemente.\n\n" +
                            "--- NOVAS CREDENCIAIS ---\n" +
                            "👤 Usuário: %s\n" +
                            "🔑 Nova Senha: %s\n\n" +
                            "Se você não reconhece este procedimento, entre em contato com a coordenação.\n\n" +
                            "Atenciosamente,\nSistema Castração ONG",
                    voluntario.getNome(), voluntario.getAdministrador().getEmail(), novaSenhaPlana
            );

            message.setText(corpoEmail);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("ERRO EMAIL ALTERAR SENHA: " + e.getMessage());
        }
    }

    // =========================================================================
    // 3. COMUNICAÇÃO COM TUTORES (OPERAÇÃO E PETS)
    // =========================================================================

    public void enviarRecomendacoes(String para, String nomePet) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(EMAIL_ONG);
            message.setTo(para);
            message.setSubject("Pagamento Confirmado! Próximos passos para " + nomePet);

            String corpoEmail = String.format(
                    "Olá!\n\nConfirmamos o pagamento da taxa social para o(a) %s.\n\n" +
                            "--- INSTRUÇÕES IMPORTANTES ---\n" +
                            "1. Jejum de 8h (água e comida).\n" +
                            "2. Uso de caixa de transporte ou coleira.\n" +
                            "3. Documento com foto obrigatório.\n\n" +
                            "\n\nNos vemos lá!", nomePet);

            message.setText(corpoEmail);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("ERRO EMAIL RECOMENDACOES: " + e.getMessage());
        }
    }

    public void enviarEmailAgendamento(Agendamento agendamento) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

            message.setFrom(EMAIL_ONG);
            message.setTo(agendamento.getCadastro().getTutor().getEmail());
            message.setSubject("CONFIRMADO: Agendamento da Castração de " + agendamento.getCadastro().getPet().getNomeAnimal());

            String corpoEmail = String.format(
                    "Olá, %s!\n\nAgendamento confirmado para o(a) %s.\n\n" +
                            "📅 DATA/HORA: %s\n" +
                            "📍 LOCAL: %s\n" +
                            "🔑 HASH: %s\n\nAté breve!",
                    agendamento.getCadastro().getTutor().getNome(),
                    agendamento.getCadastro().getPet().getNomeAnimal(),
                    agendamento.getDataHora().format(formatter),
                    agendamento.getLocal(),
                    agendamento.getCodigoHash()
            );

            message.setText(corpoEmail);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("ERRO EMAIL AGENDAMENTO: " + e.getMessage());
        }
    }

    public void enviarEmailPagamentoNaoIdentificado(String para, String nomeTutor, String nomePet, String motivo) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(EMAIL_ONG);
            message.setTo(para);
            message.setSubject("⚠️ Pendência: Pagamento não identificado para " + nomePet);

            String corpoEmail = String.format(
                    "Olá, %s!\n\nNão conseguimos validar o pagamento para %s.\n\n" +
                            "Motivo: %s\n\nPor favor, refaça o cadastro e reenvie o comprovante pelo portal:https://sistema-castracao-app.onrender.com/",
                    nomeTutor, nomePet, motivo);

            message.setText(corpoEmail);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("ERRO EMAIL PAGAMENTO NEGADO: " + e.getMessage());
        }
    }
}
