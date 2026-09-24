package com.example.dress.model;

/**
 * Tons de pele disponíveis. A pele original dos sprites (CLARA) é a mais clara,
 * então os outros tons são feitos escurecendo os pixels de pele.
 */
public enum TomPele {
    //        nome         amostra      fatores de escurecimento (vermelho, verde, azul)
    CLARA("Clara", 0xFFF1C9A5, 1.00f, 1.00f, 1.00f),
    BRONZEADA("Bronzeada", 0xFFDBA27A, 0.91f, 0.78f, 0.66f),
    MORENA("Morena", 0xFFB37450, 0.76f, 0.58f, 0.46f),
    NEGRA("Negra", 0xFF6E4430, 0.52f, 0.37f, 0.29f);

    private final String nome;
    private final int corAmostra;   // cor mostrada na bolinha do botão
    private final float fatorR;     // quanto sobra do vermelho (1 = nada muda)
    private final float fatorG;     // quanto sobra do verde
    private final float fatorB;     // quanto sobra do azul

    TomPele(String nome, int corAmostra, float fatorR, float fatorG, float fatorB) {
        this.nome = nome;
        this.corAmostra = corAmostra;
        this.fatorR = fatorR;
        this.fatorG = fatorG;
        this.fatorB = fatorB;
    }

    public String getNome() { return nome; }
    public int getCorAmostra() { return corAmostra; }
    public float getFatorR() { return fatorR; }
    public float getFatorG() { return fatorG; }
    public float getFatorB() { return fatorB; }

    /** Indica se o tom exige alterar os pixels (o tom original não exige). */
    public boolean alteraPixels() {
        return this != CLARA;
    }

    /** Retorna o próximo tom (depois do último, volta para o primeiro). */
    public TomPele proximo() {
        TomPele[] todos = values();
        return todos[(ordinal() + 1) % todos.length];
    }
}