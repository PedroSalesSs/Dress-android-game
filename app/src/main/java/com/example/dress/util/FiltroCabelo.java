package com.example.dress.util;

import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;

/**
 * Cria o filtro que pinta o cabelo com a cor escolhida.
 * Fica numa classe separada porque é usado em duas telas:
 * na de vestir (aplicado na ImageView do cabelo) e na de batalha
 * (aplicado ao desenhar o cabelo por cima do corpo).
 */
public final class FiltroCabelo {

    /**
     * Quanto o tom de cinza do cabelo é "clareado" antes de ser tingido.
     * Valores maiores deixam as cores mais claras e vivas.
     */
    private static final float INTENSIDADE_TINTA = 1.5f;

    // Classe utilitária: não deve ser instanciada
    private FiltroCabelo() { }

    /**
     * Cria um filtro que primeiro deixa a imagem em tons de cinza (mantendo
     * luzes e sombras do desenho) e depois tinge esse cinza com a cor escolhida.
     */
    public static ColorMatrixColorFilter criar(int cor) {
        float r = Color.red(cor) / 255f * INTENSIDADE_TINTA;
        float g = Color.green(cor) / 255f * INTENSIDADE_TINTA;
        float b = Color.blue(cor) / 255f * INTENSIDADE_TINTA;

        ColorMatrix matriz = new ColorMatrix();
        matriz.setSaturation(0);            // 1) tira a cor: vira tons de cinza

        ColorMatrix tinta = new ColorMatrix();
        tinta.setScale(r, g, b, 1f);        // 2) multiplica o cinza pela cor
        matriz.postConcat(tinta);           // junta as duas operações numa só

        return new ColorMatrixColorFilter(matriz);
    }
}
