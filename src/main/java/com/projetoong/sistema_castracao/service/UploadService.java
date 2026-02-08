package com.projetoong.sistema_castracao.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class UploadService {
    private final Cloudinary cloudinary;

    public UploadService() {
        // Você pega esses dados criando uma conta grátis no Cloudinary
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "seu_nome",
                "api_key", "sua_key",
                "api_secret", "seu_secret"));
    }

    public String upload(MultipartFile arquivo) {
        try {
            Map uploadResult = cloudinary.uploader().upload(arquivo.getBytes(), ObjectUtils.emptyMap());
            return uploadResult.get("url").toString(); // Retorna a URL real (http://res.cloudinary...)
        } catch (IOException e) {
            throw new RuntimeException("Erro ao subir comprovante");
        }
    }
}