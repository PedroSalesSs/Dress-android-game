package com.example.dress.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import androidx.annotation.DrawableRes;

import com.example.dress.model.Direcao;
import com.example.dress.model.TomPele;

import java.util.HashMap;
import java.util.Map;

/**
 * Carrega spritesheets LPC e recorta o quadro pedido (direção + quadro da animação),
 * podendo também trocar o tom de pele dos pixels de pele.
 * Os quadros ficam no tamanho original (64x64); quem mostra a imagem na tela
 * é responsável por ampliar sem suavização (setFilterBitmap(false)).
 */
public final class SpriteLoader {

    private static final int TAMANHO_QUADRO = 64; // cada quadro LPC tem 64x64 px

    /** Quadro 0 da animação walk: o personagem parado. */
    public static final int QUADRO_PARADO = 0;

    /** Quantidade de quadros do ciclo de caminhada (quadros 1 a 8 da animação walk). */
    public static final int QUADROS_ANDANDO = 8;

    // Em qual linha a animação "walk" começa dentro de cada formato de arquivo
    private static final int INICIO_WALK_ARQUIVO_WALK = 0;     // arquivo só com walk (4 linhas)
    private static final int INICIO_WALK_FOLHA_COMPLETA = 8;   // folha completa (walk começa na linha 8)

    // Faixa de cor considerada "pele" (no formato HSV)
    private static final float MATIZ_PELE_MAX = 50f;       // de 0° (vermelho) até 50° (laranja-amarelado)
    private static final float MATIZ_PELE_MIN_ROSA = 340f; // e de 340° até 360° (tons rosados)
    private static final float SATURACAO_MINIMA = 0.12f;   // abaixo disso é branco/cinza (ex.: branco dos olhos)
    private static final float BRILHO_MINIMO = 0.20f;      // abaixo disso é contorno quase preto

    // Folhas inteiras já carregadas (para não ler o mesmo arquivo várias vezes)
    private static final Map<Integer, Bitmap> cacheFolhas = new HashMap<>();

    // Quadros já recortados (e recoloridos). A chave combina imagem + direção + quadro + tom.
    private static final Map<String, Bitmap> cacheQuadros = new HashMap<>();

    // Construtor privado: classe utilitária, não deve ser instanciada
    private SpriteLoader() { }

    /** Versão simplificada: personagem parado, de frente, pele original. */
    public static Bitmap carregar(Context context, @DrawableRes int resId) {
        return carregar(context, resId, Direcao.FRENTE, QUADRO_PARADO, TomPele.CLARA);
    }

    /** Personagem parado, virado para a direção informada, pele original. */
    public static Bitmap carregar(Context context, @DrawableRes int resId, Direcao direcao) {
        return carregar(context, resId, direcao, QUADRO_PARADO, TomPele.CLARA);
    }

    /** Quadro específico da animação, pele original (usado para as roupas). */
    public static Bitmap carregar(Context context, @DrawableRes int resId, Direcao direcao, int quadro) {
        return carregar(context, resId, direcao, quadro, TomPele.CLARA);
    }

    /** Quadro específico da animação, com o tom de pele informado (usado para corpo e cabeça). */
    public static Bitmap carregar(Context context, @DrawableRes int resId,
                                  Direcao direcao, int quadro, TomPele tom) {
        String chave = resId + "_" + direcao.name() + "_" + quadro + "_" + tom.name();

        Bitmap emCache = cacheQuadros.get(chave);
        if (emCache != null) {
            return emCache;
        }

        Bitmap folha = carregarFolha(context, resId);

        int linha = descobrirInicioWalk(folha.getHeight()) + direcao.getLinha();
        int x = quadro * TAMANHO_QUADRO;
        int y = linha * TAMANHO_QUADRO;

        // Recorta o quadro 64x64: (imagem, x, y, largura, altura)
        Bitmap resultado = Bitmap.createBitmap(folha, x, y, TAMANHO_QUADRO, TAMANHO_QUADRO);

        if (tom.alteraPixels()) {
            resultado = trocarTomDePele(resultado, tom);
        }

        cacheQuadros.put(chave, resultado);
        return resultado;
    }

    /**
     * Percorre todos os pixels do quadro e escurece apenas os que têm cor de pele.
     * Olhos, contornos e outros detalhes ficam intactos.
     */
    private static Bitmap trocarTomDePele(Bitmap original, TomPele tom) {
        // Cópia editável (mutable = true), para não alterar o quadro original
        Bitmap copia = original.copy(Bitmap.Config.ARGB_8888, true);

        int largura = copia.getWidth();
        int altura = copia.getHeight();
        int[] pixels = new int[largura * altura];
        copia.getPixels(pixels, 0, largura, 0, 0, largura, altura);

        float[] hsv = new float[3]; // [matiz, saturação, brilho], reaproveitado em cada pixel

        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];
            int alfa = Color.alpha(pixel);
            if (alfa == 0) {
                continue; // pixel transparente: nada a fazer
            }

            Color.colorToHSV(pixel, hsv);
            if (ehPele(hsv)) {
                int r = Math.round(Color.red(pixel) * tom.getFatorR());
                int g = Math.round(Color.green(pixel) * tom.getFatorG());
                int b = Math.round(Color.blue(pixel) * tom.getFatorB());
                pixels[i] = Color.argb(alfa, r, g, b);
            }
        }

        copia.setPixels(pixels, 0, largura, 0, 0, largura, altura);
        return copia;
    }

    /** Decide se uma cor (em HSV) é um tom de pele. */
    private static boolean ehPele(float[] hsv) {
        float matiz = hsv[0];
        float saturacao = hsv[1];
        float brilho = hsv[2];

        boolean matizDePele = matiz <= MATIZ_PELE_MAX || matiz >= MATIZ_PELE_MIN_ROSA;
        return matizDePele && saturacao >= SATURACAO_MINIMA && brilho >= BRILHO_MINIMO;
    }

    /** Lê o arquivo PNG inteiro uma única vez e guarda no cache. */
    private static Bitmap carregarFolha(Context context, @DrawableRes int resId) {
        Bitmap folha = cacheFolhas.get(resId);
        if (folha != null) {
            return folha;
        }

        // inScaled = false impede o Android de redimensionar a imagem ao carregar
        BitmapFactory.Options opcoes = new BitmapFactory.Options();
        opcoes.inScaled = false;

        folha = BitmapFactory.decodeResource(context.getResources(), resId, opcoes);
        if (folha == null) {
            throw new IllegalArgumentException("Não foi possível carregar o recurso: " + resId);
        }

        cacheFolhas.put(resId, folha);
        return folha;
    }

    private static int descobrirInicioWalk(int altura) {
        int linhas = altura / TAMANHO_QUADRO;
        if (linhas == 4) {
            return INICIO_WALK_ARQUIVO_WALK;
        }
        if (linhas >= 21) {
            return INICIO_WALK_FOLHA_COMPLETA;
        }
        throw new IllegalArgumentException(
                "Formato de spritesheet não reconhecido (altura " + altura + "px). "
                        + "Use o arquivo da animação walk ou a folha completa.");
    }
}