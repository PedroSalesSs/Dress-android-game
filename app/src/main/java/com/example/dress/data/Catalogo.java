package com.example.dress.data;

import androidx.annotation.DrawableRes;

import com.example.dress.R;
import com.example.dress.model.Categoria;
import com.example.dress.model.Item;
import com.example.dress.model.TipoCorpo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Reúne todas as peças disponíveis no jogo e as imagens da base do personagem.
 * Para adicionar uma roupa nova, basta colocar a imagem na drawable-nodpi
 * e criar mais um Item na lista abaixo.
 */
public final class Catalogo {

    private static final List<Item> ITENS = Collections.unmodifiableList(Arrays.asList(

            // ===== CABELOS (servem nos dois corpos) =====
            new Item("cabelo_afro", "Afro", Categoria.CABELO, R.drawable.cabelo_afro),
            new Item("cabelo_baguncado", "Bagunçado", Categoria.CABELO, R.drawable.cabelo_baguncado),
            new Item("cabelo_cacheado", "Cacheado", Categoria.CABELO, R.drawable.cabelo_cacheado),
            new Item("cabelo_idol", "Idol", Categoria.CABELO, R.drawable.cabelo_idol),
            new Item("cabelo_longo", "Longo", Categoria.CABELO, R.drawable.cabelo_longo),
            new Item("cabelo_solto", "Solto", Categoria.CABELO, R.drawable.cabelo_solto),

            // ===== CAMISAS (versão masculina, versão feminina) =====
            new Item("camisa_tshirt", "Camiseta", Categoria.CAMISA,
                    R.drawable.camisa_tshirt_m, R.drawable.camisa_tshirt_f),
            new Item("camisa_chainmail", "Cota de malha", Categoria.CAMISA,
                    R.drawable.camisa_chainmail_m, R.drawable.camisa_chainmail_f),
            new Item("camisa_armour", "Peitoral de armadura", Categoria.CAMISA,
                    R.drawable.camisa_armour_m, R.drawable.camisa_armour_f),

            // ===== CALÇAS =====
            new Item("calca_pants", "Calça", Categoria.CALCA,
                    R.drawable.calca_pants_m, R.drawable.calca_pants_f),
            new Item("calca_legs", "Legging", Categoria.CALCA,
                    R.drawable.calca_legs_m, R.drawable.calca_legs_f),
            new Item("calca_armour", "Perneira de armadura", Categoria.CALCA,
                    R.drawable.calca_armour_m, R.drawable.calca_armour_f),

            // ===== SAPATOS =====
            new Item("pe_shoes", "Sapato", Categoria.SAPATO,
                    R.drawable.pe_shoes_m, R.drawable.pe_shoes_f),
            new Item("pe_sandals", "Sandália", Categoria.SAPATO,
                    R.drawable.pe_sandals_m, R.drawable.pe_sandals_f),
            new Item("pe_armour", "Botas de armadura", Categoria.SAPATO,
                    R.drawable.pe_armour_m, R.drawable.pe_armour_f)
    ));

    // Classe utilitária: não deve ser instanciada
    private Catalogo() { }

    /** Todas as peças do jogo (lista somente leitura). */
    public static List<Item> getTodos() {
        return ITENS;
    }

    /** Peças de uma categoria que existem para o corpo escolhido. */
    public static List<Item> getPorCategoria(Categoria categoria, TipoCorpo corpo) {
        List<Item> resultado = new ArrayList<>();
        for (Item item : ITENS) {
            if (item.getCategoria() == categoria && item.disponivelPara(corpo)) {
                resultado.add(item);
            }
        }
        return resultado;
    }

    /** Busca uma peça pelo id (usado ao carregar o que foi salvo). Retorna null se não existir. */
    public static Item buscarPorId(String id) {
        for (Item item : ITENS) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }

    // ===== BASE DO PERSONAGEM (sempre visível, não é equipável) =====

    @DrawableRes
    public static int getImagemCorpo(TipoCorpo corpo) {
        return corpo == TipoCorpo.MASCULINO ? R.drawable.corpo_m : R.drawable.corpo_f;
    }

    @DrawableRes
    public static int getImagemCabeca(TipoCorpo corpo) {
        return corpo == TipoCorpo.MASCULINO ? R.drawable.cabeca_m : R.drawable.cabeca_f;
    }
}