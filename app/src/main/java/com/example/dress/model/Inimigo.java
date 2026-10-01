package com.example.dress.model;

/**
 * Inimigos que podem ser enfrentados na batalha. Cada um tem a sua "ficha":
 * vida, ataque normal e um golpe poderoso próprio. A imagem e o cenário
 * de cada inimigo ficam no Catalogo.
 *
 * O golpe poderoso é anunciado um turno antes (o inimigo "prepara") e muda de
 * regra conforme o inimigo:
 *   - Esqueleto: Estocada Certeira, 2,5x o dano e a lança atravessa a guarda
 *     (a defesa reduz, mas nunca há bloqueio perfeito);
 *   - Lobisomem: Fúria Selvagem, dois golpes seguidos de 1,25x cada;
 *   - Orc: Golpe Brutal, um golpe só, com o triplo do dano.
 */
public enum Inimigo {

    // Em ordem de dificuldade (é a ordem em que aparecem na janela de escolha).
    ESQUELETO("Esqueleto", "Estocada de lança", 65, 13, 19, 8, 80,
            "Estocada Certeira", "O Esqueleto mira no seu ponto fraco…", 2.5, 1, false),

    LOBISOMEM("Lobisomem", "Golpe de garras", 80, 14, 20, 6, 90,
            "Fúria Selvagem", "O Lobisomem uiva e mostra as garras!", 1.25, 2, true),

    ORC("Orc", "Machadada", 90, 15, 21, 6, 110,
            "Golpe Brutal", "O Orc ergue o machado com fúria!", 3.0, 1, true);

    /** Quantidade de quadros da animação de parado (o inimigo "respirando"). */
    public static final int QUADROS_PARADO = 2;

    /** Quantidade de quadros da animação de derrota. */
    public static final int QUADROS_DERROTA = 6;

    // Linhas da folha de batalha de cada inimigo
    public static final int LINHA_PARADO = 0;
    public static final int LINHA_ATAQUE = 1;
    public static final int LINHA_DERROTA = 2;

    // ===== Ataque normal =====
    private final String nome;
    private final String nomeAtaque;
    private final int vidaMaxima;
    private final int danoMinimo;
    private final int danoMaximo;
    private final int quadrosAtaque;
    private final long intervaloAtaqueMs;

    // ===== Golpe poderoso =====
    private final String nomeGolpePoderoso;
    private final String avisoGolpePoderoso;   // mensagem do turno em que ele se prepara
    private final double multiplicadorPoderoso; // multiplica o dano de cada golpe
    private final int golpesPoderosos;          // quantos golpes seguidos (a Fúria Selvagem são 2)
    private final boolean poderosoBloqueavel;   // se o bloqueio perfeito funciona contra ele

    Inimigo(String nome, String nomeAtaque, int vidaMaxima, int danoMinimo, int danoMaximo,
            int quadrosAtaque, long intervaloAtaqueMs,
            String nomeGolpePoderoso, String avisoGolpePoderoso,
            double multiplicadorPoderoso, int golpesPoderosos, boolean poderosoBloqueavel) {
        this.nome = nome;
        this.nomeAtaque = nomeAtaque;
        this.vidaMaxima = vidaMaxima;
        this.danoMinimo = danoMinimo;
        this.danoMaximo = danoMaximo;
        this.quadrosAtaque = quadrosAtaque;
        this.intervaloAtaqueMs = intervaloAtaqueMs;
        this.nomeGolpePoderoso = nomeGolpePoderoso;
        this.avisoGolpePoderoso = avisoGolpePoderoso;
        this.multiplicadorPoderoso = multiplicadorPoderoso;
        this.golpesPoderosos = golpesPoderosos;
        this.poderosoBloqueavel = poderosoBloqueavel;
    }

    public String getNome() { return nome; }
    public String getNomeAtaque() { return nomeAtaque; }
    public int getVidaMaxima() { return vidaMaxima; }
    public int getDanoMinimo() { return danoMinimo; }
    public int getDanoMaximo() { return danoMaximo; }
    public int getQuadrosAtaque() { return quadrosAtaque; }
    public long getIntervaloAtaqueMs() { return intervaloAtaqueMs; }

    public String getNomeGolpePoderoso() { return nomeGolpePoderoso; }
    public String getAvisoGolpePoderoso() { return avisoGolpePoderoso; }
    public double getMultiplicadorPoderoso() { return multiplicadorPoderoso; }
    public int getGolpesPoderosos() { return golpesPoderosos; }
    public boolean isPoderosoBloqueavel() { return poderosoBloqueavel; }
}
