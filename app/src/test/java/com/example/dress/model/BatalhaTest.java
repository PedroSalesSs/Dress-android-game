package com.example.dress.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Random;

/**
 * Testes das regras da batalha. Rodam no computador, sem emulador:
 * clique com o botão direito nesta classe > Run 'BatalhaTest'.
 */
public class BatalhaTest {

    @Test
    public void batalhaComecaComVidaCheiaEUsosDisponiveis() {
        Batalha batalha = new Batalha(Inimigo.ORC);
        assertEquals(Batalha.VIDA_HEROI, batalha.getVidaHeroi());
        assertEquals(Inimigo.ORC.getVidaMaxima(), batalha.getVidaInimigo());
        assertTrue(batalha.podeUsarPocao());
        assertTrue(batalha.podeUsarAtaquePesado());
    }

    @Test
    public void ataqueLeveFicaDentroDosLimites() {
        Batalha batalha = new Batalha(Inimigo.ORC, new Random(1));
        int dano = batalha.ataqueLeve();
        assertTrue(dano >= Batalha.DANO_HEROI_MINIMO && dano <= Batalha.DANO_HEROI_MAXIMO);
        assertEquals(Inimigo.ORC.getVidaMaxima() - dano, batalha.getVidaInimigo());
    }

    @Test
    public void ataquePesadoConcentraUmTurnoEUsaUmaVezSo() {
        Batalha batalha = new Batalha(Inimigo.ORC, new Random(2));
        batalha.concentrar();
        assertTrue(batalha.isHeroiConcentrando());
        assertFalse(batalha.podeUsarAtaquePesado()); // não dá para usar de novo

        Batalha.AtaquePesado resultado = batalha.soltarAtaquePesado();
        int multiplicador = resultado.critico ? Batalha.MULTIPLICADOR_PESADO_CRITICO : Batalha.MULTIPLICADOR_PESADO;
        assertTrue(resultado.dano >= Batalha.DANO_HEROI_MINIMO * multiplicador);
        assertTrue(resultado.dano <= Batalha.DANO_HEROI_MAXIMO * multiplicador);
        assertFalse(batalha.isHeroiConcentrando());
        assertEquals(0, batalha.getAtaquesPesadosRestantes());
    }

    @Test(expected = IllegalStateException.class)
    public void ataquePesadoNaoPodeSerUsadoDuasVezes() {
        Batalha batalha = new Batalha(Inimigo.ORC, new Random(3));
        batalha.concentrar();
        batalha.soltarAtaquePesado();
        batalha.concentrar(); // segunda vez: erro
    }

    @Test
    public void pocaoCuraSemPassarDoMaximoEUsaUmaVezSo() {
        Batalha batalha = new Batalha(Inimigo.ORC, new Random(4));
        assertEquals(0, batalha.beberPocao()); // com a vida cheia, não cura nada
        assertFalse(batalha.podeUsarPocao());
    }

    @Test
    public void pocaoCuraOValorCombinado() {
        Batalha batalha = new Batalha(Inimigo.ORC, new Random(5));
        while (batalha.getVidaHeroi() > Batalha.VIDA_HEROI - Batalha.CURA_POCAO) {
            batalha.turnoDoInimigo(); // apanha até perder pelo menos o valor da cura
        }
        int antes = batalha.getVidaHeroi();
        assertEquals(Batalha.CURA_POCAO, batalha.beberPocao());
        assertEquals(antes + Batalha.CURA_POCAO, batalha.getVidaHeroi());
    }

    @Test
    public void inimigoPreparaEDepoisSoltaOGolpePoderoso() {
        Batalha batalha = new Batalha(Inimigo.LOBISOMEM, new Random(6));
        Batalha.TurnoInimigo turno;
        do {
            turno = batalha.turnoDoInimigo();
        } while (turno.tipo != Batalha.TipoTurno.PREPAROU && !batalha.heroiDerrotado());

        assertTrue(batalha.isInimigoPreparando());
        assertTrue(turno.golpes.isEmpty()); // preparando, ele não ataca

        Batalha.TurnoInimigo golpe = batalha.turnoDoInimigo();
        assertEquals(Batalha.TipoTurno.GOLPE_PODEROSO, golpe.tipo);
        assertEquals(Inimigo.LOBISOMEM.getGolpesPoderosos(), golpe.golpes.size()); // Fúria: dois golpes
    }

    @Test
    public void estocadaCerteiraNuncaEBloqueadaPorCompleto() {
        Random sorteio = new Random(7);
        for (int i = 0; i < 300; i++) {
            Batalha batalha = new Batalha(Inimigo.ESQUELETO, sorteio);
            while (!batalha.isInimigoPreparando()) {
                batalha.turnoDoInimigo();
                if (batalha.heroiDerrotado()) {
                    break;
                }
            }
            if (batalha.heroiDerrotado()) {
                continue;
            }
            batalha.defender();
            for (Batalha.Golpe golpe : batalha.turnoDoInimigo().golpes) {
                assertFalse(golpe.bloqueioPerfeito);
                assertTrue(golpe.defendido);
            }
        }
    }

    @Test
    public void defesaReduzODanoOuBloqueiaPorCompleto() {
        Random sorteio = new Random(8);
        boolean viuBloqueioPerfeito = false;
        for (int i = 0; i < 300; i++) {
            Batalha batalha = new Batalha(Inimigo.ORC, sorteio);
            batalha.defender();
            for (Batalha.Golpe golpe : batalha.turnoDoInimigo().golpes) {
                if (golpe.bloqueioPerfeito) {
                    viuBloqueioPerfeito = true;
                    assertEquals(0, golpe.dano);
                    assertTrue(golpe.danoContraAtaque > 0);
                } else {
                    // ataque normal do orc: 15 a 21, reduzido a 35%
                    assertTrue(golpe.defendido);
                    assertTrue(golpe.dano <= Math.round(Inimigo.ORC.getDanoMaximo() * Batalha.FRACAO_DANO_DEFENDENDO));
                }
            }
            assertFalse(batalha.isHeroiDefendendo()); // a defesa vale só para um turno
        }
        assertTrue(viuBloqueioPerfeito);
    }

    @Test
    public void vidaNaoFicaNegativa() {
        Batalha batalha = new Batalha(Inimigo.ORC, new Random(9));
        while (!batalha.heroiDerrotado()) {
            batalha.turnoDoInimigo();
        }
        assertEquals(0, batalha.getVidaHeroi());

        Batalha outra = new Batalha(Inimigo.ESQUELETO, new Random(10));
        while (!outra.inimigoDerrotado()) {
            outra.ataqueLeve();
        }
        assertEquals(0, outra.getVidaInimigo());
    }
}
