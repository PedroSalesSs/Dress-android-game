package com.example.dress.model;

import java.util.Random;

/**
 * Regras da batalha por turnos: vida dos dois lados, dano e defesa.
 *
 * Esta classe não sabe nada de telas, imagens ou animações. A BatalhaActivity
 * pergunta a ela o que aconteceu e cuida só de mostrar. Separar assim permite
 * testar as regras sem abrir o app (veja BatalhaTest).
 */
public class Batalha {

    /** Vida do herói no começo de cada batalha. */
    public static final int VIDA_HEROI = 100;

    /** Dano do golpe de espada do herói (sorteado entre os dois valores). */
    public static final int DANO_HEROI_MINIMO = 10;
    public static final int DANO_HEROI_MAXIMO = 16;

    /** Ao defender, o herói recebe só esta fração do próximo golpe (0.5 = metade). */
    public static final double FRACAO_DANO_DEFENDENDO = 0.5;

    private final Inimigo inimigo;
    private final Random sorteio;

    private int vidaHeroi = VIDA_HEROI;
    private int vidaInimigo;
    private boolean heroiDefendendo = false;

    public Batalha(Inimigo inimigo) {
        this(inimigo, new Random());
    }

    /** Permite escolher o sorteio (usado nos testes, para os resultados serem sempre iguais). */
    public Batalha(Inimigo inimigo, Random sorteio) {
        this.inimigo = inimigo;
        this.sorteio = sorteio;
        this.vidaInimigo = inimigo.getVidaMaxima();
    }

    /** O herói ataca. Retorna o dano causado ao inimigo. */
    public int heroiAtaca() {
        int dano = sortear(DANO_HEROI_MINIMO, DANO_HEROI_MAXIMO);
        vidaInimigo = Math.max(0, vidaInimigo - dano);
        return dano;
    }

    /** O herói se prepara para reduzir o próximo golpe do inimigo. */
    public void heroiDefende() {
        heroiDefendendo = true;
    }

    /**
     * O inimigo ataca. Se o herói estava defendendo, o dano cai pela metade
     * e a defesa acaba (vale só para um golpe). Retorna o dano recebido pelo herói.
     */
    public int inimigoAtaca() {
        int dano = sortear(inimigo.getDanoMinimo(), inimigo.getDanoMaximo());
        if (heroiDefendendo) {
            dano = (int) Math.round(dano * FRACAO_DANO_DEFENDENDO);
            heroiDefendendo = false;
        }
        vidaHeroi = Math.max(0, vidaHeroi - dano);
        return dano;
    }

    /** Sorteia um número inteiro entre minimo e maximo, incluindo os dois. */
    private int sortear(int minimo, int maximo) {
        return minimo + sorteio.nextInt(maximo - minimo + 1);
    }

    public boolean inimigoDerrotado() {
        return vidaInimigo == 0;
    }

    public boolean heroiDerrotado() {
        return vidaHeroi == 0;
    }

    public boolean isHeroiDefendendo() {
        return heroiDefendendo;
    }

    public Inimigo getInimigo() { return inimigo; }
    public int getVidaHeroi() { return vidaHeroi; }
    public int getVidaInimigo() { return vidaInimigo; }
}
