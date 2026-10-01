package com.example.dress.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Regras da batalha por turnos: vida, dano, defesa, poção, Ataque Pesado
 * e os golpes poderosos dos inimigos.
 *
 * Esta classe não sabe nada de telas, imagens ou sons. A BatalhaActivity
 * pede uma ação, recebe o resultado (quanto de dano, se houve bloqueio etc.)
 * e cuida só de mostrar. Separar assim permite testar as regras sem abrir
 * o app (veja BatalhaTest).
 */
public class Batalha {

    // ===================================================================== herói

    /** Vida do herói no começo de cada batalha. */
    public static final int VIDA_HEROI = 100;

    /** Dano do Ataque Leve (sorteado entre os dois valores). */
    public static final int DANO_HEROI_MINIMO = 10;
    public static final int DANO_HEROI_MAXIMO = 16;

    /** Ataque Pesado: um uso por luta; sai com o dobro do dano e tem chance de sair com o triplo. */
    public static final int USOS_ATAQUE_PESADO = 1;
    public static final int MULTIPLICADOR_PESADO = 2;
    public static final int MULTIPLICADOR_PESADO_CRITICO = 3;
    public static final double CHANCE_PESADO_CRITICO = 0.35;

    /** Poção: um uso por luta, cura um pouco e não gasta o turno. */
    public static final int USOS_POCAO = 1;
    public static final int CURA_POCAO = 15;

    /** Ao defender, o herói recebe só esta fração do dano (0.35 = bloqueia 65%). */
    public static final double FRACAO_DANO_DEFENDENDO = 0.35;

    /** Chance de, ao defender, bloquear o golpe inteiro e contra-atacar. */
    public static final double CHANCE_BLOQUEIO_PERFEITO = 0.20;

    // ===================================================================== inimigo

    /** Chance de o inimigo, no turno dele, preparar o golpe poderoso em vez de atacar. */
    public static final double CHANCE_PREPARAR_GOLPE = 0.30;

    // ===================================================================== resultados

    /** O que aconteceu no turno do inimigo. */
    public enum TipoTurno {
        PREPAROU,        // não atacou: anunciou o golpe poderoso para o próximo turno
        ATAQUE_NORMAL,
        GOLPE_PODEROSO
    }

    /**
     * Um golpe do inimigo e o que ele causou. A Fúria Selvagem tem dois golpes,
     * por isso o turno guarda uma lista. A vida de cada lado depois do golpe vem junto,
     * para a tela atualizar as barras golpe a golpe.
     */
    public static class Golpe {
        public final int dano;               // dano que o herói realmente recebeu
        public final boolean defendido;      // a defesa reduziu o golpe
        public final boolean bloqueioPerfeito;
        public final int danoContraAtaque;   // dano no inimigo, quando houve bloqueio perfeito
        public final int vidaHeroiDepois;
        public final int vidaInimigoDepois;

        Golpe(int dano, boolean defendido, boolean bloqueioPerfeito, int danoContraAtaque,
              int vidaHeroiDepois, int vidaInimigoDepois) {
            this.dano = dano;
            this.defendido = defendido;
            this.bloqueioPerfeito = bloqueioPerfeito;
            this.danoContraAtaque = danoContraAtaque;
            this.vidaHeroiDepois = vidaHeroiDepois;
            this.vidaInimigoDepois = vidaInimigoDepois;
        }
    }

    /** Resultado do turno do inimigo. */
    public static class TurnoInimigo {
        public final TipoTurno tipo;
        public final List<Golpe> golpes; // vazio quando o inimigo só preparou

        TurnoInimigo(TipoTurno tipo, List<Golpe> golpes) {
            this.tipo = tipo;
            this.golpes = Collections.unmodifiableList(golpes);
        }
    }

    /** Resultado do Ataque Pesado. */
    public static class AtaquePesado {
        public final int dano;
        public final boolean critico; // saiu com o triplo

        AtaquePesado(int dano, boolean critico) {
            this.dano = dano;
            this.critico = critico;
        }
    }

    // ===================================================================== estado

    private final Inimigo inimigo;
    private final Random sorteio;

    private int vidaHeroi = VIDA_HEROI;
    private int vidaInimigo;
    private int pocoesRestantes = USOS_POCAO;
    private int ataquesPesadosRestantes = USOS_ATAQUE_PESADO;

    private boolean heroiDefendendo = false;
    private boolean heroiConcentrando = false;  // carregando o Ataque Pesado
    private boolean inimigoPreparando = false;  // anunciou o golpe poderoso

    public Batalha(Inimigo inimigo) {
        this(inimigo, new Random());
    }

    /** Permite escolher o sorteio (usado nos testes, para os resultados serem sempre iguais). */
    public Batalha(Inimigo inimigo, Random sorteio) {
        this.inimigo = inimigo;
        this.sorteio = sorteio;
        this.vidaInimigo = inimigo.getVidaMaxima();
    }

    // ===================================================================== ações do herói

    /** Ataque Leve. Retorna o dano causado ao inimigo. */
    public int ataqueLeve() {
        int dano = sortearDanoHeroi();
        causarDanoNoInimigo(dano);
        return dano;
    }

    /** O herói se prepara para reduzir os golpes do próximo turno do inimigo. */
    public void defender() {
        heroiDefendendo = true;
    }

    /**
     * Primeiro turno do Ataque Pesado: o herói concentra a força.
     * O golpe sai no turno seguinte, com soltarAtaquePesado().
     */
    public void concentrar() {
        if (!podeUsarAtaquePesado()) {
            throw new IllegalStateException("O Ataque Pesado já foi usado nesta luta");
        }
        ataquesPesadosRestantes--;
        heroiConcentrando = true;
    }

    /** Segundo turno do Ataque Pesado: o golpe sai com o dobro (ou o triplo) do dano. */
    public AtaquePesado soltarAtaquePesado() {
        if (!heroiConcentrando) {
            throw new IllegalStateException("O herói não está concentrando um Ataque Pesado");
        }
        heroiConcentrando = false;

        boolean critico = sorteio.nextDouble() < CHANCE_PESADO_CRITICO;
        int multiplicador = critico ? MULTIPLICADOR_PESADO_CRITICO : MULTIPLICADOR_PESADO;
        int dano = sortearDanoHeroi() * multiplicador;
        causarDanoNoInimigo(dano);
        return new AtaquePesado(dano, critico);
    }

    /**
     * Bebe a poção. Não gasta o turno: depois dela o jogador ainda escolhe outra ação.
     * Retorna quanto de vida foi realmente recuperado (a vida não passa do máximo).
     */
    public int beberPocao() {
        if (!podeUsarPocao()) {
            throw new IllegalStateException("A poção já foi usada nesta luta");
        }
        pocoesRestantes--;
        int antes = vidaHeroi;
        vidaHeroi = Math.min(VIDA_HEROI, vidaHeroi + CURA_POCAO);
        return vidaHeroi - antes;
    }

    // ===================================================================== turno do inimigo

    /**
     * Turno do inimigo. Se ele tinha preparado o golpe poderoso, o golpe sai agora;
     * senão, ele pode preparar o golpe (e não ataca) ou dar um ataque normal.
     * A defesa do herói vale para todo este turno e acaba no fim dele.
     */
    public TurnoInimigo turnoDoInimigo() {
        TipoTurno tipo;
        int quantidade;
        double multiplicador;
        boolean bloqueavel;

        if (inimigoPreparando) {
            inimigoPreparando = false;
            tipo = TipoTurno.GOLPE_PODEROSO;
            quantidade = inimigo.getGolpesPoderosos();
            multiplicador = inimigo.getMultiplicadorPoderoso();
            bloqueavel = inimigo.isPoderosoBloqueavel();
        } else if (sorteio.nextDouble() < CHANCE_PREPARAR_GOLPE) {
            inimigoPreparando = true;
            heroiDefendendo = false; // defender contra a preparação não serve de nada
            return new TurnoInimigo(TipoTurno.PREPAROU, new ArrayList<>());
        } else {
            tipo = TipoTurno.ATAQUE_NORMAL;
            quantidade = 1;
            multiplicador = 1.0;
            bloqueavel = true;
        }

        List<Golpe> golpes = new ArrayList<>();
        for (int i = 0; i < quantidade && !heroiDerrotado() && !inimigoDerrotado(); i++) {
            golpes.add(resolverGolpe(multiplicador, bloqueavel));
        }
        heroiDefendendo = false;
        return new TurnoInimigo(tipo, golpes);
    }

    /** Calcula um golpe do inimigo, aplicando defesa, bloqueio perfeito e contra-ataque. */
    private Golpe resolverGolpe(double multiplicador, boolean bloqueavel) {
        int dano = (int) Math.round(sortear(inimigo.getDanoMinimo(), inimigo.getDanoMaximo()) * multiplicador);
        boolean defendido = false;
        boolean bloqueioPerfeito = false;
        int contraAtaque = 0;

        if (heroiDefendendo) {
            if (bloqueavel && sorteio.nextDouble() < CHANCE_BLOQUEIO_PERFEITO) {
                bloqueioPerfeito = true;
                dano = 0;
                contraAtaque = sortearDanoHeroi();
                causarDanoNoInimigo(contraAtaque);
            } else {
                defendido = true;
                dano = (int) Math.round(dano * FRACAO_DANO_DEFENDENDO);
            }
        }

        vidaHeroi = Math.max(0, vidaHeroi - dano);
        return new Golpe(dano, defendido, bloqueioPerfeito, contraAtaque, vidaHeroi, vidaInimigo);
    }

    // ===================================================================== auxiliares

    private int sortearDanoHeroi() {
        return sortear(DANO_HEROI_MINIMO, DANO_HEROI_MAXIMO);
    }

    private void causarDanoNoInimigo(int dano) {
        vidaInimigo = Math.max(0, vidaInimigo - dano);
    }

    /** Sorteia um número inteiro entre minimo e maximo, incluindo os dois. */
    private int sortear(int minimo, int maximo) {
        return minimo + sorteio.nextInt(maximo - minimo + 1);
    }

    // ===================================================================== consultas

    public boolean inimigoDerrotado() { return vidaInimigo == 0; }
    public boolean heroiDerrotado() { return vidaHeroi == 0; }

    public boolean podeUsarPocao() { return pocoesRestantes > 0; }
    public boolean podeUsarAtaquePesado() { return ataquesPesadosRestantes > 0 && !heroiConcentrando; }

    public boolean isHeroiDefendendo() { return heroiDefendendo; }
    public boolean isHeroiConcentrando() { return heroiConcentrando; }
    public boolean isInimigoPreparando() { return inimigoPreparando; }

    public int getPocoesRestantes() { return pocoesRestantes; }
    public int getAtaquesPesadosRestantes() { return ataquesPesadosRestantes; }

    public Inimigo getInimigo() { return inimigo; }
    public int getVidaHeroi() { return vidaHeroi; }
    public int getVidaInimigo() { return vidaInimigo; }
}
