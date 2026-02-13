package com.projetoong.sistema_castracao.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.projetoong.sistema_castracao.dto.DashboardSummaryDTO;
import com.projetoong.sistema_castracao.model.CadastroCastracao;
import com.projetoong.sistema_castracao.model.Pagamento;
import com.projetoong.sistema_castracao.model.Pet;
import com.projetoong.sistema_castracao.model.Tutor;
import com.projetoong.sistema_castracao.repository.CadastroCastracaoRepository;
import com.projetoong.sistema_castracao.repository.PagamentoRepository;
import com.projetoong.sistema_castracao.repository.PetRepository;
import com.projetoong.sistema_castracao.repository.TutorRepository;
import com.projetoong.sistema_castracao.service.AlarmeService;
import com.projetoong.sistema_castracao.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private AlarmeService alarmeService;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private CadastroCastracaoRepository cadastroCastracaoRepository;

    @Autowired
    private Cloudinary cloudinary;

    @GetMapping("/pagamentos/pendentes")
    public List<Pagamento> listarPendentes() {
        return pagamentoRepository.findByConfirmadoFalse();
    }

    @Autowired
    private com.projetoong.sistema_castracao.service.PagamentoService pagamentoService; // Injete o Service

    @PatchMapping("/pagamentos/{id}/aprovar") // Mudei para bater com o nome no React
    public ResponseEntity<Void> aprovarPagamento(@PathVariable Long id) {
        // Agora sim: confirma, muda status, entra na fila e manda e-mail!
        pagamentoService.confirmarPagamento(id);
        return ResponseEntity.ok().build();
    }
    @PatchMapping("/pagamentos/{id}/rejeitar")
    public ResponseEntity<Void> rejeitarPagamento(@PathVariable Long id) {
        // O Controller não sabe "como" deleta, ele apenas manda o Service fazer
        pagamentoService.rejeitarERemoverTudo(id);
        return ResponseEntity.ok().build();
    }


    // No AdminController.java
    @GetMapping("/fila-espera")
    public List<CadastroCastracao> listarFila() {
        // Usamos o status exato que vimos no seu log do banco
        String statusBusca = "NA_FILA";

        List<CadastroCastracao> lista = cadastroCastracaoRepository.findByStatusProcessoIgnoreCase(statusBusca);

        System.out.println("Busca no Java por: " + statusBusca);
        System.out.println("Quantidade encontrada: " + lista.size());

        return lista;
    }
    @GetMapping("/tutores")
    public List<Tutor> listarTutores(@RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            // Se houver busca, filtra pelo nome (precisa criar no Repository)
            return tutorRepository.findByNomeContainingIgnoreCase(search);
        }
        // Se não houver busca, retorna todos para a gestão
        return tutorRepository.findAll();
    }
    @GetMapping("/alarmes")
    public List<Map<String, String>> buscarAlarmes() {
        // O Controller não sabe "como" o alarme é feito, ele só pede o resultado
        return alarmeService.gerarRelatorioAlarmes();
    }

    // No seu AdminController.java

    @GetMapping("/dashboard-summary")
    public ResponseEntity<DashboardSummaryDTO> getSummary() {
        // Agora o retorno é DashboardSummaryDTO, não mais Map
        return ResponseEntity.ok(dashboardService.getResumoCompleto());
    }

    @PostMapping("/pagamentos/upload-comprovante")
    public ResponseEntity<?> uploadComprovante(@RequestParam("file") MultipartFile file) {
        try {
            // Envia para o Cloudinary na pasta 'comprovantes'
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", "ong_comprovantes"));

            // Pega o link seguro (https)
            String urlNuvem = uploadResult.get("secure_url").toString();

            // Retorna apenas a URL para o Front-end
            return ResponseEntity.ok(Map.of("url", urlNuvem));

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Erro ao subir arquivo para a nuvem");
        }
    }
}
