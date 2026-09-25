package com.example.dress.model;

import java.util.EnumMap;
import java.util.Map;

/**
 * Representa o personagem do jogador: o tipo de corpo escolhido
 * e as peças equipadas em cada categoria (no máximo uma por categoria).
 */
public class Personagem {

    private final TipoCorpo tipoCorpo;
    private final Map<Categoria, Item> equipados = new EnumMap<>(Categoria.class);

    public Personagem(TipoCorpo tipoCorpo) {
        this.tipoCorpo = tipoCorpo;
    }

    public TipoCorpo getTipoCorpo() {
        return tipoCorpo;
    }

    /**
     * Equipa uma peça. Se já houver outra peça na mesma categoria,
     * ela é substituída automaticamente.
     */
    public void equipar(Item item) {
        if (!item.disponivelPara(tipoCorpo)) {
            throw new IllegalArgumentException(
                    "A peça " + item.getNome() + " não existe para o corpo " + tipoCorpo);
        }
        equipados.put(item.getCategoria(), item);
    }

    /** Remove a peça equipada na categoria (se houver). */
    public void desequipar(Categoria categoria) {
        equipados.remove(categoria);
    }

    /**
     * Comportamento de toque na lista: se a peça já está equipada, tira;
     * se não está, veste. Retorna true se a peça ficou equipada.
     */
    public boolean alternar(Item item) {
        if (estaEquipado(item)) {
            desequipar(item.getCategoria());
            return false;
        }
        equipar(item);
        return true;
    }

    /** Retorna a peça equipada na categoria, ou null se estiver vazia. */
    public Item getEquipado(Categoria categoria) {
        return equipados.get(categoria);
    }

    /**
     * Retorna a primeira peça equipada que não tem a animação pedida,
     * ou null se todas as peças suportam (e a animação pode ser tocada).
     */
    public Item pecaSemAnimacao(Animacao animacao) {
        for (Item item : equipados.values()) {
            if (!item.suporta(animacao)) {
                return item;
            }
        }
        return null;
    }

    public boolean estaEquipado(Item item) {
        return item.equals(equipados.get(item.getCategoria()));
    }
}