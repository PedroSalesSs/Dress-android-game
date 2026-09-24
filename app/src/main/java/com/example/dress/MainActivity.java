package com.example.dress;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.ImageViewCompat;

import com.example.dress.data.Catalogo;
import com.example.dress.databinding.ActivityMainBinding;
import com.example.dress.databinding.LinhaCategoriaBinding;
import com.example.dress.model.Categoria;
import com.example.dress.model.CorCabelo;
import com.example.dress.model.Direcao;
import com.example.dress.model.Item;
import com.example.dress.model.Personagem;
import com.example.dress.model.TipoCorpo;
import com.example.dress.model.TomPele;
import com.example.dress.util.SpriteLoader;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    /** Chave usada pela tela de escolha para enviar o tipo de corpo para esta tela. */
    public static final String EXTRA_TIPO_CORPO = "tipo_corpo";

    /** Tempo entre um quadro e outro da caminhada (100 ms = 10 quadros por segundo). */
    private static final long INTERVALO_QUADRO_MS = 100;

    /**
     * Quanto o tom de cinza do cabelo é "clareado" antes de ser tingido.
     * Valores maiores deixam as cores mais claras e vivas.
     */
    private static final float INTENSIDADE_TINTA = 1.5f;

    private ActivityMainBinding binding;
    private Personagem personagem;
    private Direcao direcao = Direcao.FRENTE;
    private CorCabelo corCabelo = CorCabelo.RUIVO;
    private TomPele tomPele = TomPele.CLARA;

    // ===== Estado da animação =====
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean andando = false;
    private int quadroAtual = SpriteLoader.QUADRO_PARADO;

    /** Tarefa que avança um quadro da caminhada e se agenda de novo. */
    private final Runnable passoDaAnimacao = new Runnable() {
        @Override
        public void run() {
            // Avança pelos quadros 1 a 8 e volta para o 1 depois do 8
            quadroAtual = (quadroAtual % SpriteLoader.QUADROS_ANDANDO) + 1;
            atualizarTela();
            handler.postDelayed(this, INTERVALO_QUADRO_MS);
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

        // Alterna entre andar e parar
        binding.btnAndar.setOnClickListener(v -> {
            if (andando) {
                pararAnimacao();
            } else {
                iniciarAnimacao();
            }
        });

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
            binding.imgCabelo.setColorFilter(criarFiltroDeCor(corCabelo.getCor()));
        } else {
            binding.imgCabelo.clearColorFilter();  // cor original do sprite
        }

        ImageViewCompat.setImageTintList(binding.btnCorCabelo,
                ColorStateList.valueOf(corCabelo.getCor()));
        binding.btnCorCabelo.setContentDescription(
                getString(R.string.btn_cor_cabelo) + ": " + corCabelo.getNome());
    }

    /**
     * Cria um filtro que primeiro deixa a imagem em tons de cinza (mantendo
     * luzes e sombras do desenho) e depois tinge esse cinza com a cor escolhida.
     */
    private ColorMatrixColorFilter criarFiltroDeCor(int cor) {
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

    /**
     * Chamado pelo Android quando a tela deixa de estar em primeiro plano
     * (outro app abriu, a tela desligou, o jogador voltou...).
     * Paramos a animação para não gastar bateria com uma tela que ninguém vê.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (andando) {
            pararAnimacao();
        }
    }

    private void iniciarAnimacao() {
        andando = true;
        binding.btnAndar.setText(R.string.btn_parar);
        handler.post(passoDaAnimacao);
    }

    private void pararAnimacao() {
        andando = false;
        handler.removeCallbacks(passoDaAnimacao);
        quadroAtual = SpriteLoader.QUADRO_PARADO;
        binding.btnAndar.setText(R.string.btn_andar);
        atualizarTela();
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
        camada.setImageBitmap(SpriteLoader.carregar(this, imagem, direcao, quadroAtual, tom));
        camada.getDrawable().setFilterBitmap(false);
    }
}