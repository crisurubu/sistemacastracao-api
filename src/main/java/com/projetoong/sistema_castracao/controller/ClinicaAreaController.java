package com.projetoong.sistema_castracao.controller;

import com.projetoong.sistema_castracao.dto.AlterarSenhaDTO;
import com.projetoong.sistema_castracao.model.Administrador;
import com.projetoong.sistema_castracao.model.Agendamento;
import com.projetoong.sistema_castracao.model.Clinica;
import com.projetoong.sistema_castracao.service.AgendamentoService;
import com.projetoong.sistema_castracao.service.ClinicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clinica")
@CrossOrigin(origins = "http://localhost:5173")
public class ClinicaAreaController {

    @Autowired
    private ClinicaService clinicaService;

    @Autowired
    private AgendamentoService agendamentoService;

    @PreAuthorize("hasAnyAuthority('CLINICA', 'ROLE_CLINICA')")
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardData(Authentication authentication) {
        try {
            // 1. Identificação do usuário logado
            String emailReal = (authentication.getPrincipal() instanceof Administrador)
                    ? ((Administrador) authentication.getPrincipal()).getEmail()
                    : authentication.getName();

            // 2. BUSCA OS DADOS CONSOLIDADOS NO SERVICE (Onde fizemos a contagem de TRUE)
            Map<String, Object> dadosService = clinicaService.obterDadosDashboard(emailReal);

            // 3. Pegamos a lista bruta de agendamentos que o Service retornou (apenas os pendentes)
            List<Agendamento> agendamentos = (List<Agendamento>) dadosService.get("agendamentos");

            // 4. Formatação da Fila para o Front-end (Mantendo sua lógica de navegação Pet/Tutor)
            List<Map<String, Object>> filaFormatada = agendamentos.stream().map(agenda -> {
                Map<String, Object> item = new HashMap<>();
                item.put("id", agenda.getId());
                item.put("dataAgendamento", agenda.getDataHora());
                item.put("hash", agenda.getCodigoHash());
                item.put("realizado", agenda.isRealizado()); // Importante para o React saber o estado

                if (agenda.getCadastro() != null) {
                    if (agenda.getCadastro().getPet() != null) {
                        item.put("animal", agenda.getCadastro().getPet().getNomeAnimal());
                        item.put("especie", agenda.getCadastro().getPet().getEspecie());
                        item.put("historico", agenda.getCadastro().getPet().getHistoricoVida());
                    }
                    if (agenda.getCadastro().getTutor() != null) {
                        item.put("tutor", agenda.getCadastro().getTutor().getNome());
                    }
                } else {
                    item.put("animal", "Sem cadastro");
                }
                return item;
            }).toList();

            // 5. RESPOSTA FINAL: Aqui enviamos o "totalVidas" que o Service contou no banco todo
            Map<String, Object> response = new HashMap<>();
            response.put("nomeClinica", dadosService.get("nomeClinica"));
            response.put("totalVidas", dadosService.get("totalVidas")); // O contador real histórico
            response.put("selo", dadosService.get("selo"));
            response.put("agendamentos", filaFormatada);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Erro crítico no Dashboard: " + e.getMessage());
            return ResponseEntity.status(500).body("Erro ao carregar dados da fila.");
        }
    }

    @PreAuthorize("hasAnyAuthority('CLINICA', 'ROLE_CLINICA')")
    @PatchMapping("/agendamentos/{id}/concluir")
    public ResponseEntity<Void> concluir(@PathVariable Long id) {
        // O agendamentoService deve marcar como realizado = true
        agendamentoService.concluirProcedimento(id);

        // Opcional: Se quiser atualizar o contador na tabela Clinica também (o campo totalCastracoes)
        // Você pode chamar clinicaService.registrarCastracaoConcluida(clinicaId) aqui se tiver o ID.

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority('CLINICA', 'ROLE_CLINICA')")
    @PutMapping("/alterar-senha")
    public ResponseEntity<?> alterarSenha(@RequestBody AlterarSenhaDTO dto, Authentication authentication) {
        try {
            // Identifica a clínica logada
            String email = (authentication.getPrincipal() instanceof Administrador)
                    ? ((Administrador) authentication.getPrincipal()).getEmail()
                    : authentication.getName();

            clinicaService.alterarSenha(email, dto.senhaAtual(), dto.novaSenha());
            return ResponseEntity.ok().body(Map.of("message", "Senha alterada com sucesso!"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Erro interno ao alterar senha."));
        }
    }
}