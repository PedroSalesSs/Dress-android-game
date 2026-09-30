package com.example.dress;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.ImageViewCompat;

import com.example.dress.data.Catalogo;
import com.example.dress.databinding.ActivityMainBinding;
import com.example.dress.databinding.DialogoInimigosBinding;
import com.example.dress.databinding.ItemInimigoBinding;
import com.example.dress.databinding.LinhaCategoriaBinding;
import com.example.dress.model.Animacao;
import com.example.dress.model.Categoria;
import com.example.dress.model.CorCabelo;
import com.example.dress.model.Direcao;
import com.example.dress.model.Inimigo;
import com.example.dress.model.Item;
import com.example.dress.model.Personagem;
import com.example.dress.model.TipoCorpo;
import com.example.dress.model.TomPele;
import com.example.dress.util.FiltroCabelo;
import com.example.dress.util.SpriteLoader;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    /** Chave usada pela tela de escolha para enviar o tipo de corpo para esta tela. */
    public static final String EXTRA_TIPO_CORPO = "tipo_corpo";

    private ActivityMainBinding binding;
    private Personagem personagem;
    private Direcao direcao = Direcao.FRENTE;
    private CorCabelo corCabelo = CorCabelo.RUIVO;
    private TomPele tomPele = TomPele.CLARA;

    // ===== Estado da animação =====
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Animacao animacaoAtual = Animacao.PARADO;
    private int passo = 0;                                // posição dentro do ciclo da animação
    private int quadroAtual = SpriteLoader.QUADRO_PARADO; // quadro real da folha que está na tela

    /** Tarefa que avança um quadro da animação atual e se agenda de novo. */
    private final Runnable passoDaAnimacao = new Runnable() {
        @Override
        public void run() {
            // Ex.: no ANDAR vai do quadro 1 ao 8 e volta ao 1; no CORRER, do 0 ao 7
            passo = (passo + 1) % animacaoAtual.getTotalQuadros();
            quadroAtual = animacaoAtual.getQuadro(passo);
            atualizarTela();
            handler.postDelayed(this, animacaoAtual.getIntervaloMs());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        personagem = new Personagem(lerTipoCorpo());
        vestirPecasIniciais();

        configurarLinha(binding.linhaCabelo, Categoria.CABELO);
        configurarLinha(binding.linhaCamisa, Categoria.CAMISA);
        configurarLinha(binding.linhaCalca, Categoria.CALCA);
        configurarLinha(binding.linhaSapato, Categoria.SAPATO);

        binding.btnAnimar.setOnClickListener(v -> {
            direcao = direcao.proxima();
            atualizarTela();
        });

        // Cada botão liga a sua animação; tocar de novo no mesmo botão volta a ficar parado
        binding.btnAndar.setOnClickListener(v -> alternarAnimacao(Animacao.ANDAR));
        binding.btnCorrer.setOnClickListener(v -> alternarAnimacao(Animacao.CORRER));
        binding.btnSentar.setOnClickListener(v -> alternarAnimacao(Animacao.SENTAR));

        // Abre a janela para escolher contra qual inimigo lutar
        binding.btnBatalha.setOnClickListener(v -> escolherInimigo());

        // Fecha esta tela e volta para a tela de escolha (que está embaixo na pilha)
        binding.btnVoltar.setOnClickListener(v -> finish());

        // Cada toque passa para a próxima cor de cabelo
        binding.btnCorCabelo.setOnClickListener(v -> {
            corCabelo = corCabelo.proxima();
            aplicarCorCabelo();
        });

        // Cada toque passa para o próximo tom de pele
        binding.btnTomPele.setOnClickListener(v -> {
            tomPele = tomPele.proximo();
            aplicarTomPele();
        });

        aplicarCorCabelo();
        aplicarTomPele();
        atualizarDisponibilidadeBotoes();
    }

    /**
     * Pinta a bolinha do botão com o tom atual e redesenha o personagem.
     * Diferente do cabelo, a pele é trocada pixel a pixel na SpriteLoader,
     * então basta redesenhar a tela com o novo tom.
     */
    private void aplicarTomPele() {
        ImageViewCompat.setImageTintList(binding.btnTomPele,
                ColorStateList.valueOf(tomPele.getCorAmostra()));
        binding.btnTomPele.setContentDescription(
                getString(R.string.btn_tom_pele) + ": " + tomPele.getNome());
        atualizarTela();
    }

    /**
     * Aplica a cor atual na camada do cabelo e pinta a bolinha do botão.
     * O filtro fica guardado na ImageView e continua valendo quando a imagem troca
     * (setas, giro e caminhada), por isso só precisa ser aplicado quando a cor muda.
     */
    private void aplicarCorCabelo() {
        if (corCabelo.precisaTingir()) {
            binding.imgCabelo.setColorFilter(FiltroCabelo.criar(corCabelo.getCor()));
        } else {
            binding.imgCabelo.clearColorFilter();  // cor original do sprite
        }

        ImageViewCompat.setImageTintList(binding.btnCorCabelo,
                ColorStateList.valueOf(corCabelo.getCor()));
        binding.btnCorCabelo.setContentDescription(
                getString(R.string.btn_cor_cabelo) + ": " + corCabelo.getNome());
    }

    /**
     * Mostra a janela de escolha do inimigo, com um cartão para cada valor do enum Inimigo.
     * Os cartões são criados pelo código: para adicionar um inimigo novo ao jogo,
     * não é preciso mexer no layout da janela.
     */
    private void escolherInimigo() {
        DialogoInimigosBinding dialogo = DialogoInimigosBinding.inflate(getLayoutInflater());
        AlertDialog janela = new AlertDialog.Builder(this)
                .setView(dialogo.getRoot())
                .create();

        for (Inimigo inimigo : Inimigo.values()) {
            // O "true" já coloca o cartão dentro da lista
            ItemInimigoBinding cartao =
                    ItemInimigoBinding.inflate(getLayoutInflater(), dialogo.listaInimigos, true);

            cartao.imgInimigo.setImageBitmap(
                    SpriteLoader.carregarRetratoInimigo(this, Catalogo.getImagemInimigo(inimigo)));
            cartao.imgInimigo.getDrawable().setFilterBitmap(false);
            cartao.txtNome.setText(inimigo.getNome());
            cartao.txtDescricao.setText(getString(R.string.ficha_inimigo,
                    inimigo.getNomeAtaque(), inimigo.getVidaMaxima(),
                    inimigo.getDanoMinimo(), inimigo.getDanoMaximo()));

            cartao.getRoot().setOnClickListener(v -> {
                janela.dismiss();
                abrirBatalha(inimigo);
            });
        }

        dialogo.btnCancelar.setOnClickListener(v -> janela.dismiss());

        // Fundo transparente: quem desenha a janela é o nosso pergaminho, não o tema padrão
        if (janela.getWindow() != null) {
            janela.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        janela.show();
    }

    /**
     * Abre a tela de batalha levando o personagem como está: corpo, peças,
     * cor do cabelo e tom de pele. Assim o herói luta com a roupa que o jogador montou.
     * As peças vão pelo id (um texto), porque o Intent não carrega objetos Item.
     */
    private void abrirBatalha(Inimigo inimigo) {
        Intent intent = new Intent(this, BatalhaActivity.class);
        intent.putExtra(BatalhaActivity.EXTRA_INIMIGO, inimigo.name());
        intent.putExtra(BatalhaActivity.EXTRA_TIPO_CORPO, personagem.getTipoCorpo().name());
        intent.putExtra(BatalhaActivity.EXTRA_COR_CABELO, corCabelo.name());
        intent.putExtra(BatalhaActivity.EXTRA_TOM_PELE, tomPele.name());

        for (Categoria categoria : Categoria.values()) {
            Item item = personagem.getEquipado(categoria);
            if (item != null) {
                intent.putExtra(BatalhaActivity.extraDaPeca(categoria), item.getId());
            }
        }
        startActivity(intent);
    }

    /**
     * Chamado pelo Android quando a tela deixa de estar em primeiro plano
     * (outro app abriu, a tela desligou, o jogador voltou...).
     * Paramos a animação para não gastar bateria com uma tela que ninguém vê.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (animacaoAtual != Animacao.PARADO) {
            pararAnimacao();
        }
    }

    /**
     * Se a animação pedida já está tocando, para. Se não, troca para ela
     * (dá para ir direto de andar para correr, sem precisar parar antes).
     */
    private void alternarAnimacao(Animacao animacao) {
        if (animacaoAtual == animacao) {
            pararAnimacao();
            return;
        }

        // Alguma peça equipada não tem essa animação no LPC? Avisa em vez de desenhar errado.
        Item semSuporte = personagem.pecaSemAnimacao(animacao);
        if (semSuporte != null) {
            Toast.makeText(this,
                    getString(R.string.aviso_sem_animacao, semSuporte.getNome()),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        iniciarAnimacao(animacao);
    }

    private void iniciarAnimacao(Animacao animacao) {
        handler.removeCallbacks(passoDaAnimacao); // interrompe a anterior, se houver
        animacaoAtual = animacao;
        passo = 0;
        quadroAtual = animacao.getQuadro(passo);
        atualizarTextosBotoes();
        atualizarTela();

        // Sentar é uma pose fixa: basta desenhar uma vez, sem ficar trocando quadros
        if (animacao.temMovimento()) {
            handler.postDelayed(passoDaAnimacao, animacao.getIntervaloMs());
        }
    }

    private void pararAnimacao() {
        handler.removeCallbacks(passoDaAnimacao);
        animacaoAtual = Animacao.PARADO;
        passo = 0;
        quadroAtual = SpriteLoader.QUADRO_PARADO;
        atualizarTextosBotoes();
        atualizarTela();
    }

    /** O botão da animação que está tocando mostra "Parar" (ou "Levantar"); os outros, o nome normal. */
    private void atualizarTextosBotoes() {
        definirTexto(binding.btnAndar, Animacao.ANDAR, R.string.btn_andar, R.string.btn_parar);
        definirTexto(binding.btnCorrer, Animacao.CORRER, R.string.btn_correr, R.string.btn_parar);
        definirTexto(binding.btnSentar, Animacao.SENTAR, R.string.btn_sentar, R.string.btn_levantar);
    }

    private void definirTexto(Button botao, Animacao animacao, int textoNormal, int textoAtivo) {
        botao.setText(animacaoAtual == animacao ? textoAtivo : textoNormal);
    }

    /**
     * Deixa o botão meio apagado quando alguma peça equipada não tem a animação.
     * Ele continua clicável para poder mostrar o aviso explicando o motivo.
     */
    private void atualizarDisponibilidadeBotoes() {
        for (Animacao animacao : new Animacao[]{Animacao.ANDAR, Animacao.CORRER, Animacao.SENTAR}) {
            boolean disponivel = personagem.pecaSemAnimacao(animacao) == null;
            botaoDa(animacao).setAlpha(disponivel ? 1f : 0.5f);
        }
    }

    private Button botaoDa(Animacao animacao) {
        switch (animacao) {
            case CORRER: return binding.btnCorrer;
            case SENTAR: return binding.btnSentar;
            default:     return binding.btnAndar;
        }
    }

    /**
     * Lê o tipo de corpo enviado pela tela de escolha.
     * Se nenhum valor for recebido, usa MASCULINO como padrão.
     */
    private TipoCorpo lerTipoCorpo() {
        String nome = getIntent().getStringExtra(EXTRA_TIPO_CORPO);
        return (nome != null) ? TipoCorpo.valueOf(nome) : TipoCorpo.MASCULINO;
    }

    /** Começa o jogo com a primeira peça de cada categoria. */
    private void vestirPecasIniciais() {
        for (Categoria categoria : Categoria.values()) {
            List<Item> opcoes = Catalogo.getPorCategoria(categoria, personagem.getTipoCorpo());
            if (!opcoes.isEmpty()) {
                personagem.equipar(opcoes.get(0));
            }
        }
    }

    /** Liga as setas de uma linha à troca de peças da categoria. */
    private void configurarLinha(LinhaCategoriaBinding linha, Categoria categoria) {
        linha.btnAnterior.setOnClickListener(v -> trocarPeca(categoria, -1));
        linha.btnProximo.setOnClickListener(v -> trocarPeca(categoria, +1));
    }

    /**
     * Passa para a peça seguinte (passo = +1) ou anterior (passo = -1) da categoria.
     * A sequência inclui a opção "Nenhum" e dá a volta nas pontas:
     * Nenhum -> peça 1 -> peça 2 -> ... -> última -> Nenhum -> ...
     */
    private void trocarPeca(Categoria categoria, int passo) {
        List<Item> opcoes = Catalogo.getPorCategoria(categoria, personagem.getTipoCorpo());
        if (opcoes.isEmpty()) {
            return;
        }

        // Posição atual: -1 = Nenhum, 0 = primeira peça, 1 = segunda...
        int atual = opcoes.indexOf(personagem.getEquipado(categoria));

        // Soma 1 para "Nenhum" virar a posição 0, faz a conta circular e desfaz a soma
        int totalPosicoes = opcoes.size() + 1;
        int nova = Math.floorMod(atual + 1 + passo, totalPosicoes) - 1;

        if (nova == -1) {
            personagem.desequipar(categoria);
        } else {
            personagem.equipar(opcoes.get(nova));
        }

        // Vestiu uma peça que não tem a animação atual (ex.: durante a corrida)? Para.
        if (personagem.pecaSemAnimacao(animacaoAtual) != null) {
            pararAnimacao();
        }
        atualizarDisponibilidadeBotoes();
        atualizarTela();
    }

    /** Redesenha o personagem e os nomes das peças de acordo com o estado atual. */
    private void atualizarTela() {
        TipoCorpo corpo = personagem.getTipoCorpo();

        // Corpo e cabeça recebem o tom de pele escolhido
        mostrarImagem(binding.imgCorpo, Catalogo.getImagemCorpo(corpo), tomPele);
        mostrarImagem(binding.imgCabeca, Catalogo.getImagemCabeca(corpo), tomPele);

        mostrarPeca(binding.imgCalca, binding.linhaCalca, Categoria.CALCA);
        mostrarPeca(binding.imgSapato, binding.linhaSapato, Categoria.SAPATO);
        mostrarPeca(binding.imgCamisa, binding.linhaCamisa, Categoria.CAMISA);
        mostrarPeca(binding.imgCabelo, binding.linhaCabelo, Categoria.CABELO);
    }

    /** Mostra a peça equipada na camada e o nome dela na linha correspondente. */
    private void mostrarPeca(ImageView camada, LinhaCategoriaBinding linha, Categoria categoria) {
        Item item = personagem.getEquipado(categoria);

        if (item == null) {
            mostrarImagem(camada, Item.SEM_IMAGEM);
            linha.txtNome.setText(R.string.nenhum);
        } else {
            mostrarImagem(camada, item.getImagem(personagem.getTipoCorpo()));
            linha.txtNome.setText(item.getNome());
        }
    }

    /** Mostra uma peça de roupa (sem troca de pele). */
    private void mostrarImagem(ImageView camada, @DrawableRes int imagem) {
        mostrarImagem(camada, imagem, TomPele.CLARA);
    }

    /** Carrega o quadro atual na direção atual e no tom informado, sem suavização. */
    private void mostrarImagem(ImageView camada, @DrawableRes int imagem, TomPele tom) {
        if (imagem == Item.SEM_IMAGEM) {
            camada.setImageDrawable(null);
            return;
        }
        camada.setImageBitmap(
                SpriteLoader.carregar(this, imagem, animacaoAtual, direcao, quadroAtual, tom));
        camada.getDrawable().setFilterBitmap(false);
    }
}