package com.example.dress.util;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import androidx.annotation.RawRes;

import com.example.dress.R;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Efeitos sonoros do jogo, tocados com o SoundPool: a classe do Android feita
 * para sons curtos de jogos. Ela carrega todos os sons na memória uma vez e,
 * depois, toca cada um sem atraso.
 *
 * Uso: crie no onCreate da tela, chame tocar(Som.X) quando precisar e
 * liberar() no onDestroy (para devolver a memória ao sistema).
 */
public class Sons {

    /** Os sons disponíveis e o arquivo de cada um (em res/raw). */
    public enum Som {
        CLIQUE(R.raw.som_clique),
        ESPADA(R.raw.som_espada),
        IMPACTO(R.raw.som_impacto),
        DEFESA(R.raw.som_defesa),
        BLOQUEIO_PERFEITO(R.raw.som_bloqueio_perfeito),
        CONCENTRAR(R.raw.som_concentrar),
        ATAQUE_PODEROSO(R.raw.som_ataque_poderoso),
        POCAO(R.raw.som_pocao),
        AVISO_INIMIGO(R.raw.som_aviso_inimigo),
        VITORIA(R.raw.som_vitoria),
        DERROTA(R.raw.som_derrota);

        @RawRes final int arquivo;

        Som(@RawRes int arquivo) {
            this.arquivo = arquivo;
        }
    }

    /** Quantos sons podem tocar ao mesmo tempo (ex.: espada e impacto juntos). */
    private static final int SONS_SIMULTANEOS = 4;

    private final SoundPool soundPool;
    private final Map<Som, Integer> ids = new EnumMap<>(Som.class);
    private final Set<Integer> carregados = new HashSet<>();

    public Sons(Context context) {
        AudioAttributes atributos = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(SONS_SIMULTANEOS)
                .setAudioAttributes(atributos)
                .build();

        // O carregamento acontece em segundo plano; só tocamos o que já terminou de carregar
        soundPool.setOnLoadCompleteListener((pool, id, status) -> {
            if (status == 0) {
                carregados.add(id);
            }
        });

        for (Som som : Som.values()) {
            ids.put(som, soundPool.load(context, som.arquivo, 1));
        }
    }

    /** Toca um som uma vez. Se ele ainda não carregou, simplesmente não toca. */
    public void tocar(Som som) {
        Integer id = ids.get(som);
        if (id != null && carregados.contains(id)) {
            soundPool.play(id, 1f, 1f, 1, 0, 1f);
        }
    }

    /** Libera a memória dos sons. Chame no onDestroy da tela. */
    public void liberar() {
        soundPool.release();
    }
}
