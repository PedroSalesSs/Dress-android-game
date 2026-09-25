package com.example.dress.model;

import androidx.annotation.DrawableRes;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Representa uma peça de roupa que pode ser equipada no personagem.
 * Cada peça pode ter uma imagem para o corpo masculino e outra para o feminino.
 */
public class Item {

    // Valor usado quando a peça não existe para um dos corpos
    public static final int SEM_IMAGEM = 0;

    private final String id;
    private final String nome;
    private final Categoria categoria;
    @DrawableRes private final int imagemMasculina;
    @DrawableRes private final int imagemFeminina;

    // Animações que não existem no LPC para esta peça (hoje todas as peças têm todas;
    // fica como proteção caso alguma peça nova venha incompleta)
    private final Set<Animacao> animacoesIndisponiveis;

    /**
     * Construtor para peças com versões diferentes para cada corpo (camisas, calças, sapatos).
     * O último parâmetro é opcional (varargs): as animações que a peça NÃO tem.
     */
    public Item(String id, String nome, Categoria categoria,
                @DrawableRes int imagemMasculina, @DrawableRes int imagemFeminina,
                Animacao... semAnimacao) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.imagemMasculina = imagemMasculina;
        this.imagemFeminina = imagemFeminina;

        // EnumSet.copyOf não aceita coleção vazia, por isso o caso vazio é tratado à parte
        Set<Animacao> indisponiveis = EnumSet.noneOf(Animacao.class);
        indisponiveis.addAll(Arrays.asList(semAnimacao));
        this.animacoesIndisponiveis = Collections.unmodifiableSet(indisponiveis);
    }

    // Construtor para peças que servem nos dois corpos (cabelos)
    public Item(String id, String nome, Categoria categoria, @DrawableRes int imagemUnica) {
        this(id, nome, categoria, imagemUnica, imagemUnica);
    }

    // Retorna a imagem certa de acordo com o corpo escolhido pelo jogador
    @DrawableRes
    public int getImagem(TipoCorpo corpo) {
        return corpo == TipoCorpo.MASCULINO ? imagemMasculina : imagemFeminina;
    }

    // Indica se a peça pode aparecer na lista para esse corpo
    public boolean disponivelPara(TipoCorpo corpo) {
        return getImagem(corpo) != SEM_IMAGEM;
    }

    // Indica se a peça tem sprites para a animação pedida
    public boolean suporta(Animacao animacao) {
        return !animacoesIndisponiveis.contains(animacao);
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Categoria getCategoria() {
        return categoria;
    }
}