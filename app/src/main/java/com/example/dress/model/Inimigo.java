package com.example.dress.model;

/**
 * Inimigos que podem ser enfrentados na batalha. Cada um tem a sua "ficha":
 * nome, ataque, vida e força. A imagem de cada inimigo fica no Catalogo.
 *
 * Os três fazem o mesmo tipo de movimento dos sprites LPC; o que muda é a arma
 * e o golpe: o orc dá um golpe lateral com machado, o esqueleto faz uma estocada
 * com lança e o lobisomem ataca com as garras (golpe lateral sem arma).
 */
public enum Inimigo {

    // Em ordem de dificuldade (é a ordem em que aparecem na janela de escolha).
    //         nome         ataque               vida  dano mín/máx  quadros do ataque  ms por quadro
    ESQUELETO ("Esqueleto", "Estocada de lança", 55,   13, 19,       8,                 80),
    LOBISOMEM ("Lobisomem", "Golpe de garras",   65,   14, 20,       6,                 90),
    ORC       ("Orc",       "Machadada",         75,   15, 21,       6,                 110);

    /** Quantidade de quadros da animação de parado (o inimigo "respirando"). */
    public static final int QUADROS_PARADO = 2;

    /** Quantidade de quadros da animação de derrota. */
    public static final int QUADROS_DERROTA = 6;

    // Linhas da folha de batalha de cada inimigo
    public static final int LINHA_PARADO = 0;
    public static final int LINHA_ATAQUE = 1;
    public static final int LINHA_DERROTA = 2;

    private final String nome;
    private final String nomeAtaque;
    private final int vidaMaxima;
    private final int danoMinimo;
    private final int danoMaximo;
    private final int quadrosAtaque;
    private final long intervaloAtaqueMs;

    Inimigo(String nome, String nomeAtaque, int vidaMaxima, int danoMinimo, int danoMaximo,
            int quadrosAtaque, long intervaloAtaqueMs) {
        this.nome = nome;
        this.nomeAtaque = nomeAtaque;
        this.vidaMaxima = vidaMaxima;
        this.danoMinimo = danoMinimo;
        this.danoMaximo = danoMaximo;
        this.quadrosAtaque = quadrosAtaque;
        this.intervaloAtaqueMs = intervaloAtaqueMs;
    }

    public String getNome() { return nome; }
    public String getNomeAtaque() { return nomeAtaque; }
    public int getVidaMaxima() { return vidaMaxima; }
    public int getDanoMinimo() { return danoMinimo; }
    public int getDanoMaximo() { return danoMaximo; }
    public int getQuadrosAtaque() { return quadrosAtaque; }
    public long getIntervaloAtaqueMs() { return intervaloAtaqueMs; }
}
