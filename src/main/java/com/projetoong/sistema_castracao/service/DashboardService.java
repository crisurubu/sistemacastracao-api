package com.projetoong.sistema_castracao.service;

import com.projetoong.sistema_castracao.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {
    @Autowired private PetRepository petRepository;
    @Autowired private TutorRepository tutorRepository;
    @Autowired private PagamentoRepository pagamentoRepository;

    public Map<String, Object> getResumoCompleto() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalPets", petRepository.count());
        data.put("tutoresAtivos", tutorRepository.count());
        data.put("arrecadacaoTotal", pagamentoRepository.sumAprovados());
        data.put("filaEspera", petRepository.countByStatusFila());

        // Dados para os Gráficos (Essencial para a Dona da ONG)
        data.put("distribuicaoEspecies", petRepository.findEspeciesCount());
        data.put("fluxoMensal", pagamentoRepository.findFluxoMensal());

        return data;
    }
}