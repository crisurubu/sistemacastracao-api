package com.projetoong.sistema_castracao.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.projetoong.sistema_castracao.dto.DashboardSummaryDTO;
import com.projetoong.sistema_castracao.model.*;
import com.projetoong.sistema_castracao.repository.*;
import com.projetoong.sistema_castracao.service.AlarmeService;
import com.projetoong.sistema_castracao.service.DashboardService;
import com.projetoong.sistema_castracao.service.PagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Importação importante
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired private PagamentoRepository pagamentoRepository;
    @Autowired private PetRepository petRepository;
    @Autowired private TutorRepository tutorRepository;
    @Autowired private VoluntarioRepository voluntarioRepository; // Necessário para buscar o aprovador
    @Autowired private AlarmeService alarmeService;
    @Autowired private DashboardService dashboardService;
    @Autowired private CadastroCastracaoRepository cadastroCastracaoRepository;
    @Autowired private Cloudinary cloudinary;
    @Autowired private PagamentoService pagamentoService;

    @GetMapping("/pagamentos/pendentes")
    public List<Pagamento> listarPendentes() {
        return pagamentoRepository.findByConfirmadoFalse();
    }

    @PatchMapping("/pagamentos/{id}/aprovar")
    public ResponseEntity<Void> aprovarPagamento(@PathVariable Long id, Authentication authentication) {
        // 1. Extrai o e-mail de forma segura
        String emailLogado;

        // Verifica se o principal é o nosso modelo de Administrador ou apenas o nome do Security
        if (authentication.getPrincipal() instanceof com.projetoong.sistema_castracao.model.Administrador) {
            emailLogado = ((com.projetoong.sistema_castracao.model.Administrador) authentication.getPrincipal()).getEmail();
        } else {
            emailLogado = authentication.getName();
        }

        // 2. Buscamos o objeto Voluntario para passar ao Service
        // Se não encontrar (caso do Cristiano que loga como Admin), o Service
        // vai usar o 'emailLogado' para tentar achar ele na tabela de voluntários.
        Voluntario aprovador = voluntarioRepository.findByEmailContato(emailLogado).orElse(null);

        // 3. Chama o Service com os dados corrigidos
        // Passamos o objeto (se existir) e o e-mail (como garantia/emailMaster)
        pagamentoService.confirmarPagamento(id, aprovador, emailLogado);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/pagamentos/extrato")
    public ResponseEntity<List<Pagamento>> buscarExtratoCompleto() {
        return ResponseEntity.ok(pagamentoService.listarExtratoAuditoria());
    }

    @PatchMapping("/pagamentos/{id}/rejeitar")
    public ResponseEntity<Void> rejeitarPagamento(@PathVariable Long id) {
        pagamentoService.rejeitarERemoverTudo(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/fila-espera")
    public List<CadastroCastracao> listarFila() {
        return cadastroCastracaoRepository.findByStatusProcessoIgnoreCase("NA_FILA");
    }

    @GetMapping("/tutores")
    public List<Tutor> listarTutores(@RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            return tutorRepository.findByNomeContainingIgnoreCase(search);
        }
        return tutorRepository.findAll();
    }

    @GetMapping("/alarmes")
    public List<Map<String, String>> buscarAlarmes() {
        return alarmeService.gerarRelatorioAlarmes();
    }

    @GetMapping("/dashboard-summary")
    public ResponseEntity<DashboardSummaryDTO> getSummary() {
        return ResponseEntity.ok(dashboardService.getResumoCompleto());
    }

    @PostMapping("/pagamentos/upload-comprovante")
    public ResponseEntity<?> uploadComprovante(@RequestParam("file") MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", "ong_comprovantes"));
            return ResponseEntity.ok(Map.of("url", uploadResult.get("secure_url").toString()));
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Erro ao subir arquivo para a nuvem");
        }
    }
}