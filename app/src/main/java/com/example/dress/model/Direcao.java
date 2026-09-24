package com.example.dress.model;

/**
 * Direções para onde o personagem pode estar virado.
 * A ordem de declaração é a ordem do giro: frente, esquerda, costas, direita.
 */
public enum Direcao {
    FRENTE(2),
    ESQUERDA(1),
    COSTAS(0),
    DIREITA(3);

    // Linha correspondente dentro do arquivo walk.png
    private final int linha;

    Direcao(int linha) {
        this.linha = linha;
    }

    public int getLinha() {
        return linha;
    }

    /** Retorna a próxima direção do giro (depois da última, volta para a primeira). */
    public Direcao proxima() {
        Direcao[] todas = values();
        return todas[(ordinal() + 1) % todas.length];
    }
}