package com.projetoong.sistema_castracao.model;

public enum SeloParceiro {
    INICIANTE(0, "Iniciante"),
    BRONZE(1, "Bronze"),
    PRATA(20, "Prata"),
    OURO(50, "Ouro");

    private final int meta;
    private final String descricao;

    SeloParceiro(int meta, String descricao) {
        this.meta = meta;
        this.descricao = descricao;
    }

    public int getMeta() { return meta; }
    public String getDescricao() { return descricao; }

    public static SeloParceiro calcular(int totalCastracoes) {
        if (totalCastracoes >= OURO.meta) return OURO;
        if (totalCastracoes >= PRATA.meta) return PRATA;
        if (totalCastracoes >= BRONZE.meta) return BRONZE;
        return INICIANTE;
    }
}