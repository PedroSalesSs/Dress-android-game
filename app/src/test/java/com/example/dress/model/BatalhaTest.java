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
    public void batalhaComecaComVidaCheia() {
        Batalha batalha = new Batalha(Inimigo.ORC);
        assertEquals(Batalha.VIDA_HEROI, batalha.getVidaHeroi());
        assertEquals(Inimigo.ORC.getVidaMaxima(), batalha.getVidaInimigo());
    }

    @Test
    public void ataqueDoHeroiFicaDentroDosLimites() {
        Batalha batalha = new Batalha(Inimigo.ORC, new Random(1));
        int dano = batalha.heroiAtaca();
        assertTrue(dano >= Batalha.DANO_HEROI_MINIMO && dano <= Batalha.DANO_HEROI_MAXIMO);
        assertEquals(Inimigo.ORC.getVidaMaxima() - dano, batalha.getVidaInimigo());
    }

    @Test
    public void defenderReduzODanoPelaMetadeSoUmaVez() {
        // Mesma semente = mesmos sorteios nas duas batalhas
        Batalha semDefesa = new Batalha(Inimigo.LOBISOMEM, new Random(7));
        Batalha comDefesa = new Batalha(Inimigo.LOBISOMEM, new Random(7));

        int danoNormal = semDefesa.inimigoAtaca();
        comDefesa.heroiDefende();
        int danoDefendido = comDefesa.inimigoAtaca();

        assertEquals(Math.round(danoNormal * Batalha.FRACAO_DANO_DEFENDENDO), danoDefendido);
        assertFalse(comDefesa.isHeroiDefendendo()); // a defesa vale para um golpe só
    }

    @Test
    public void vidaNaoFicaNegativaEInimigoEDerrotado() {
        Batalha batalha = new Batalha(Inimigo.ESQUELETO, new Random(3));
        while (!batalha.inimigoDerrotado()) {
            batalha.heroiAtaca();
        }
        assertEquals(0, batalha.getVidaInimigo());
        assertFalse(batalha.heroiDerrotado());
    }

    @Test
    public void heroiPodeSerDerrotado() {
        Batalha batalha = new Batalha(Inimigo.ORC, new Random(5));
        while (!batalha.heroiDerrotado()) {
            batalha.inimigoAtaca();
        }
        assertEquals(0, batalha.getVidaHeroi());
    }
}
