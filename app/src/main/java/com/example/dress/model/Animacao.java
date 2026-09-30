package com.example.dress.model;

/**
 * Animações que o personagem sabe fazer.
 *
 * Cada spritesheet do jogo tem 17 linhas, organizadas em blocos de 4 direções:
 *   bloco 0 (linhas 0 a 3)   = andar   (walk, 9 quadros: o 0 é a pose parada)
 *   bloco 1 (linhas 4 a 7)   = correr  (run, 8 quadros)
 *   bloco 2 (linhas 8 a 11)  = sentar  (sit, 3 poses diferentes)
 *   bloco 3 (linhas 12 a 15) = atacar  (slash, 6 quadros)
 *   bloco 4 (linha 16)       = derrota (hurt, 6 quadros; uma linha só, sem direção)
 */
public enum Animacao {

    //     bloco, 1º quadro, qtd. quadros, ms por quadro
    PARADO (0,    0,         1,            0),
    ANDAR  (0,    1,         8,            100),  // quadros 1 a 8 (o 0 é o parado)
    CORRER (1,    0,         8,            70),   // mais rápido que andar
    SENTAR (2,    1,         1,            0),    // pose fixa: pernas cruzadas no chão
    ATACAR (3,    0,         6,            90),   // golpe de espada (usado na batalha)
    DERROTA(4,    0,         6,            150, false); // cai no chão; o LPC só desenha de frente

    /** Quantas linhas cada bloco ocupa: uma por direção. */
    private static final int DIRECOES_POR_BLOCO = 4;

    private final int bloco;
    private final int primeiroQuadro;
    private final int totalQuadros;
    private final long intervaloMs;
    private final boolean direcional; // false = a animação tem uma linha só, igual para todas as direções

    Animacao(int bloco, int primeiroQuadro, int totalQuadros, long intervaloMs) {
        this(bloco, primeiroQuadro, totalQuadros, intervaloMs, true);
    }

    Animacao(int bloco, int primeiroQuadro, int totalQuadros, long intervaloMs, boolean direcional) {
        this.bloco = bloco;
        this.primeiroQuadro = primeiroQuadro;
        this.totalQuadros = totalQuadros;
        this.intervaloMs = intervaloMs;
        this.direcional = direcional;
    }

    /** Linha da folha onde esta animação começa (a direção é somada depois). */
    public int getLinhaInicial() {
        return bloco * DIRECOES_POR_BLOCO;
    }

    public int getBloco() {
        return bloco;
    }

    /**
     * Converte o passo da animação (0, 1, 2...) no quadro real da folha.
     * Ex.: no ANDAR, o passo 0 é o quadro 1; no CORRER, o passo 0 é o quadro 0.
     */
    public int getQuadro(int passo) {
        return primeiroQuadro + (passo % totalQuadros);
    }

    public int getTotalQuadros() {
        return totalQuadros;
    }

    public long getIntervaloMs() {
        return intervaloMs;
    }

    /** Indica se a animação tem uma linha para cada direção (a derrota não tem). */
    public boolean isDirecional() {
        return direcional;
    }

    /** Animações de um quadro só (parado, sentado) não precisam do Handler. */
    public boolean temMovimento() {
        return totalQuadros > 1;
    }
}
