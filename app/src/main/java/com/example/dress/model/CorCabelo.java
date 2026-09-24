package com.example.dress.model;

/**
 * Cores de cabelo disponíveis. A ordem de declaração é a ordem em que
 * as cores aparecem a cada toque no botão.
 */
public enum CorCabelo {
    RUIVO("Ruivo", 0xFFD2601E, false),     // cor original dos sprites, sem filtro
    CASTANHO("Castanho", 0xFF7A4A2A, true),
    PRETO("Preto", 0xFF303030, true),
    LOIRO("Loiro", 0xFFF0D080, true);

    private final String nome;
    private final int cor;          // cor no formato ARGB (0xAARRGGBB)
    private final boolean tingir;   // false = mostrar a cor original do sprite

    CorCabelo(String nome, int cor, boolean tingir) {
        this.nome = nome;
        this.cor = cor;
        this.tingir = tingir;
    }

    public String getNome() {
        return nome;
    }

    public int getCor() {
        return cor;
    }

    public boolean precisaTingir() {
        return tingir;
    }

    /** Retorna a próxima cor (depois da última, volta para a primeira). */
    public CorCabelo proxima() {
        CorCabelo[] todas = values();
        return todas[(ordinal() + 1) % todas.length];
    }
}