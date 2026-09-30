package com.example.dress;

import android.animation.ObjectAnimator;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dress.data.Catalogo;
import com.example.dress.databinding.ActivityBatalhaBinding;
import com.example.dress.model.Animacao;
import com.example.dress.model.Batalha;
import com.example.dress.model.Categoria;
import com.example.dress.model.CorCabelo;
import com.example.dress.model.Direcao;
import com.example.dress.model.Inimigo;
import com.example.dress.model.Item;
import com.example.dress.model.TipoCorpo;
import com.example.dress.model.TomPele;
import com.example.dress.util.FiltroCabelo;
import com.example.dress.util.SpriteLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Tela da batalha por turnos, sempre deitada (paisagem).
 *
 * O jogador escolhe Atacar ou Defender, a animação do herói toca, e depois é a vez
 * do inimigo. A luta termina quando a vida de um dos dois chega a zero: aparece o
 * resultado e o botão para voltar ao menu (a tela de vestir).
 *
 * As regras (vida, dano, defesa) ficam na classe Batalha; esta tela só mostra o que
 * aconteceu e controla a ordem das animações.
 */
public class BatalhaActivity extends AppCompatActivity {

    // ===== Chaves dos dados enviados pela tela de vestir =====
    public static final String EXTRA_INIMIGO = "inimigo";
    public static final String EXTRA_TIPO_CORPO = "tipo_corpo";
    public static final String EXTRA_COR_CABELO = "cor_cabelo";
    public static final String EXTRA_TOM_PELE = "tom_pele";
    private static final String PREFIXO_EXTRA_PECA = "peca_";

    /** Chave do extra com o id da peça de uma categoria (ex.: "peca_CAMISA"). */
    public static String extraDaPeca(Categoria categoria) {
        return PREFIXO_EXTRA_PECA + categoria.name();
    }

    // Ordem em que as roupas são desenhadas por cima do corpo (a mesma da tela de vestir)
    private static final Categoria[] ORDEM_ROUPAS = {
            Categoria.CALCA, Categoria.SAPATO, Categoria.CAMISA, Categoria.CABELO
    };

    // Linhas da folha da espada: cada animação tem a parte de trás e, na linha seguinte, a da frente
    private static final int ESPADA_PARADO = 0;
    private static final int ESPADA_ATAQUE = 2;
    private static final int ESPADA_DERROTA = 4;

    // ===== Tempos (em milissegundos) =====
    private static final long INTERVALO_RESPIRACAO_MS = 500; // troca de quadro do inimigo parado
    private static final long PAUSA_ENTRE_TURNOS_MS = 1200;   // tempo para ler a mensagem
    private static final long PAUSA_ANTES_DA_QUEDA_MS = 1200;
    private static final long DURACAO_EFEITO_GOLPE_MS = 250; // quanto tempo o vermelho fica
    private static final long DURACAO_TREMOR_MS = 300;
    private static final long DURACAO_BARRA_MS = 400;
    private static final long DURACAO_RESULTADO_MS = 400;

    private static final float DISTANCIA_TREMOR_DP = 8f;

    /**
     * Em que ponto a luta está. Os botões só funcionam na vez do jogador,
     * o que impede, por exemplo, atacar de novo enquanto uma animação ainda está rodando.
     */
    private enum Estado { VEZ_DO_JOGADOR, AGUARDANDO, FIM }

    private ActivityBatalhaBinding binding;
    private Batalha batalha;
    private Estado estado;
    private final Handler handler = new Handler(Looper.getMainLooper());

    // ===== Aparência do herói (vinda da tela de vestir) =====
    private TipoCorpo corpo;
    private TomPele tomPele;
    private final List<Item> roupas = new ArrayList<>();
    private final Paint pinturaCabelo = new Paint();

    // ===== Quadros já montados, prontos para mostrar =====
    private Bitmap heroiParado;
    private Bitmap[] heroiAtaque;
    private Bitmap[] heroiDerrota;
    private Bitmap[] inimigoParado;
    private Bitmap[] inimigoAtaque;
    private Bitmap[] inimigoDerrota;

    /** Faz o inimigo "respirar" enquanto espera: alterna os quadros de parado. */
    private int quadroRespiracao = 0;
    private final Runnable respiracao = new Runnable() {
        @Override
        public void run() {
            quadroRespiracao = (quadroRespiracao + 1) % inimigoParado.length;
            mostrar(binding.imgInimigo, inimigoParado[quadroRespiracao]);
            handler.postDelayed(this, INTERVALO_RESPIRACAO_MS);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityBatalhaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Deitado, a câmera do celular pode ficar num dos lados da tela:
        // além das barras do sistema, também desviamos do recorte da câmera (displayCutout)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets margens = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(margens.left, margens.top, margens.right, margens.bottom);
            return insets;
        });

        Inimigo inimigo = lerEnum(EXTRA_INIMIGO, Inimigo.class, Inimigo.ORC);
        batalha = new Batalha(inimigo);

        lerAparenciaDoHeroi();
        prepararQuadrosDoHeroi();
        prepararQuadrosDoInimigo(inimigo);

        binding.txtNomeInimigo.setText(inimigo.getNome());
        binding.barraVidaHeroi.setMax(Batalha.VIDA_HEROI);
        binding.barraVidaInimigo.setMax(inimigo.getVidaMaxima());
        atualizarVidas(false);

        mostrar(binding.imgHeroi, heroiParado);
        mostrar(binding.imgInimigo, inimigoParado[0]);

        binding.btnAtacar.setOnClickListener(v -> atacar());
        binding.btnDefender.setOnClickListener(v -> defender());
        binding.btnVoltarMenu.setOnClickListener(v -> finish());

        mensagem(getString(R.string.msg_inicio, inimigo.getNome()));
        mudarEstado(Estado.VEZ_DO_JOGADOR);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (estado == Estado.VEZ_DO_JOGADOR) {
            iniciarRespiracao();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pararRespiracao(); // não gasta bateria com a tela fora de vista
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); // cancela qualquer animação pendente
    }

    // ================================================================ ações do jogador

    private void atacar() {
        if (estado != Estado.VEZ_DO_JOGADOR) {
            return;
        }
        mudarEstado(Estado.AGUARDANDO);
        mensagem(getString(R.string.msg_heroi_ataca));

        tocarAnimacao(binding.imgHeroi, heroiAtaque, Animacao.ATACAR.getIntervaloMs(), () -> {
            mostrar(binding.imgHeroi, heroiParado);

            int dano = batalha.heroiAtaca();
            efeitoGolpe(binding.imgInimigo);
            atualizarVidas(true);
            mensagem(getString(R.string.msg_dano_causado, dano, batalha.getInimigo().getNome()));

            if (batalha.inimigoDerrotado()) {
                handler.postDelayed(this::derrotarInimigo, PAUSA_ANTES_DA_QUEDA_MS);
            } else {
                handler.postDelayed(this::turnoDoInimigo, PAUSA_ENTRE_TURNOS_MS);
            }
        });
    }

    private void defender() {
        if (estado != Estado.VEZ_DO_JOGADOR) {
            return;
        }
        mudarEstado(Estado.AGUARDANDO);
        batalha.heroiDefende();

        // Um brilho azulado mostra que o herói está protegido até o próximo golpe
        binding.imgHeroi.setColorFilter(getColor(R.color.efeito_defesa), PorterDuff.Mode.SRC_ATOP);
        mensagem(getString(R.string.msg_heroi_defende));

        handler.postDelayed(this::turnoDoInimigo, PAUSA_ENTRE_TURNOS_MS);
    }

    // ================================================================ vez do inimigo

    private void turnoDoInimigo() {
        pararRespiracao(); // a animação de ataque assume a imagem do inimigo
        Inimigo inimigo = batalha.getInimigo();
        mensagem(getString(R.string.msg_inimigo_ataca, inimigo.getNome(), inimigo.getNomeAtaque()));

        tocarAnimacao(binding.imgInimigo, inimigoAtaque, inimigo.getIntervaloAtaqueMs(), () -> {
            mostrar(binding.imgInimigo, inimigoParado[0]);

            // Precisa perguntar antes do ataque: o próprio ataque encerra a defesa
            boolean defendeu = batalha.isHeroiDefendendo();
            int dano = batalha.inimigoAtaca();

            binding.imgHeroi.clearColorFilter(); // tira o brilho da defesa
            efeitoGolpe(binding.imgHeroi);
            atualizarVidas(true);
            mensagem(getString(defendeu ? R.string.msg_dano_defendido : R.string.msg_dano_recebido, dano));

            if (batalha.heroiDerrotado()) {
                handler.postDelayed(this::derrotarHeroi, PAUSA_ANTES_DA_QUEDA_MS);
            } else {
                handler.postDelayed(this::voltarParaOJogador, PAUSA_ENTRE_TURNOS_MS);
            }
        });
    }

    private void voltarParaOJogador() {
        iniciarRespiracao();
        mensagem(getString(R.string.msg_sua_vez));
        mudarEstado(Estado.VEZ_DO_JOGADOR);
    }

    // ================================================================ fim da luta

    private void derrotarInimigo() {
        pararRespiracao();
        mudarEstado(Estado.FIM);
        tocarAnimacao(binding.imgInimigo, inimigoDerrota, Animacao.DERROTA.getIntervaloMs(),
                () -> mostrarResultado(true));
    }

    private void derrotarHeroi() {
        pararRespiracao();
        mudarEstado(Estado.FIM);
        tocarAnimacao(binding.imgHeroi, heroiDerrota, Animacao.DERROTA.getIntervaloMs(),
                () -> mostrarResultado(false));
    }

    /** Mostra o painel de vitória ou derrota, aparecendo aos poucos (fade in). */
    private void mostrarResultado(boolean vitoria) {
        String nomeInimigo = batalha.getInimigo().getNome();
        binding.txtResultadoTitulo.setText(vitoria ? R.string.resultado_vitoria : R.string.resultado_derrota);
        binding.txtResultadoTexto.setText(getString(
                vitoria ? R.string.texto_vitoria : R.string.texto_derrota, nomeInimigo));

        binding.painelResultado.setAlpha(0f);
        binding.painelResultado.setVisibility(View.VISIBLE);
        binding.painelResultado.animate().alpha(1f).setDuration(DURACAO_RESULTADO_MS).start();
    }

    // ================================================================ animações e efeitos

    /**
     * Mostra os quadros um após o outro na ImageView e, no fim, executa aoTerminar.
     * É assim que a tela encadeia os passos da luta: ataque -> dano -> vez do outro.
     */
    private void tocarAnimacao(ImageView alvo, Bitmap[] quadros, long intervaloMs, Runnable aoTerminar) {
        handler.post(new Runnable() {
            private int proximo = 0;

            @Override
            public void run() {
                if (proximo < quadros.length) {
                    mostrar(alvo, quadros[proximo]);
                    proximo++;
                    handler.postDelayed(this, intervaloMs);
                } else {
                    aoTerminar.run();
                }
            }
        });
    }

    /**
     * Efeito de "levar um golpe": o LPC não tem essa animação, então o lutador
     * fica avermelhado por um instante e treme para os lados.
     */
    private void efeitoGolpe(ImageView alvo) {
        alvo.setColorFilter(getColor(R.color.efeito_golpe), PorterDuff.Mode.SRC_ATOP);
        handler.postDelayed(alvo::clearColorFilter, DURACAO_EFEITO_GOLPE_MS);

        float d = DISTANCIA_TREMOR_DP * getResources().getDisplayMetrics().density;
        ObjectAnimator tremor = ObjectAnimator.ofFloat(alvo, "translationX", 0, d, -d, d / 2, -d / 2, 0);
        tremor.setDuration(DURACAO_TREMOR_MS);
        tremor.start();
    }

    private void iniciarRespiracao() {
        handler.removeCallbacks(respiracao); // evita duas "respirações" ao mesmo tempo
        handler.postDelayed(respiracao, INTERVALO_RESPIRACAO_MS);
    }

    private void pararRespiracao() {
        handler.removeCallbacks(respiracao);
    }

    // ================================================================ interface

    /** Liga ou desliga os botões de ação conforme o estado da luta. */
    private void mudarEstado(Estado novo) {
        estado = novo;
        boolean podeAgir = novo == Estado.VEZ_DO_JOGADOR;
        binding.btnAtacar.setEnabled(podeAgir);
        binding.btnDefender.setEnabled(podeAgir);
        binding.btnAtacar.setAlpha(podeAgir ? 1f : 0.5f);
        binding.btnDefender.setAlpha(podeAgir ? 1f : 0.5f);
    }

    private void mensagem(String texto) {
        binding.txtMensagem.setText(texto);
    }

    private void atualizarVidas(boolean animar) {
        atualizarBarra(binding.barraVidaHeroi, binding.txtVidaHeroi,
                batalha.getVidaHeroi(), Batalha.VIDA_HEROI, animar);
        atualizarBarra(binding.barraVidaInimigo, binding.txtVidaInimigo,
                batalha.getVidaInimigo(), batalha.getInimigo().getVidaMaxima(), animar);
    }

    /** Atualiza o número, a cor e o tamanho de uma barra de vida (deslizando, se animar = true). */
    private void atualizarBarra(ProgressBar barra, TextView texto, int vida, int vidaMaxima, boolean animar) {
        texto.setText(getString(R.string.vida_valor, vida, vidaMaxima));
        barra.setProgressTintList(ColorStateList.valueOf(corDaVida(vida, vidaMaxima)));

        if (animar) {
            ObjectAnimator.ofInt(barra, "progress", vida).setDuration(DURACAO_BARRA_MS).start();
        } else {
            barra.setProgress(vida);
        }
    }

    /** Verde com mais da metade da vida, dourado até um quarto, vermelho abaixo disso. */
    private int corDaVida(int vida, int vidaMaxima) {
        float fracao = (float) vida / vidaMaxima;
        if (fracao > 0.5f) {
            return getColor(R.color.vida_cheia);
        }
        if (fracao > 0.25f) {
            return getColor(R.color.vida_media);
        }
        return getColor(R.color.vida_baixa);
    }

    /** Mostra um quadro ampliado sem suavização, para a pixel art continuar nítida. */
    private void mostrar(ImageView alvo, Bitmap quadro) {
        alvo.setImageBitmap(quadro);
        alvo.getDrawable().setFilterBitmap(false);
    }

    // ================================================================ montagem dos quadros

    /** Lê corpo, peças, cor do cabelo e tom de pele enviados pela tela de vestir. */
    private void lerAparenciaDoHeroi() {
        corpo = lerEnum(EXTRA_TIPO_CORPO, TipoCorpo.class, TipoCorpo.MASCULINO);
        tomPele = lerEnum(EXTRA_TOM_PELE, TomPele.class, TomPele.CLARA);

        CorCabelo corCabelo = lerEnum(EXTRA_COR_CABELO, CorCabelo.class, CorCabelo.RUIVO);
        if (corCabelo.precisaTingir()) {
            pinturaCabelo.setColorFilter(FiltroCabelo.criar(corCabelo.getCor()));
        }

        for (Categoria categoria : ORDEM_ROUPAS) {
            String id = getIntent().getStringExtra(extraDaPeca(categoria));
            Item item = (id != null) ? Catalogo.buscarPorId(id) : null;
            if (item != null) {
                roupas.add(item); // categoria "Nenhum" simplesmente não entra na lista
            }
        }
    }

    /**
     * Monta, uma única vez, todos os quadros do herói já com a roupa, o cabelo
     * pintado, o tom de pele e a espada. Durante a luta basta trocar a imagem.
     */
    private void prepararQuadrosDoHeroi() {
        heroiParado = montarQuadroHeroi(Animacao.PARADO, SpriteLoader.QUADRO_PARADO, ESPADA_PARADO, 0);

        heroiAtaque = new Bitmap[Animacao.ATACAR.getTotalQuadros()];
        for (int i = 0; i < heroiAtaque.length; i++) {
            heroiAtaque[i] = montarQuadroHeroi(Animacao.ATACAR, Animacao.ATACAR.getQuadro(i), ESPADA_ATAQUE, i);
        }

        heroiDerrota = new Bitmap[Animacao.DERROTA.getTotalQuadros()];
        for (int i = 0; i < heroiDerrota.length; i++) {
            heroiDerrota[i] = montarQuadroHeroi(Animacao.DERROTA, Animacao.DERROTA.getQuadro(i), ESPADA_DERROTA, i);
        }
    }

    /**
     * Desenha um quadro do herói (128 x 64) em camadas, de trás para a frente:
     * parte de trás da espada, corpo, cabeça, roupas e parte da frente da espada.
     * O herói fica na metade esquerda; a direita é o espaço do golpe.
     */
    private Bitmap montarQuadroHeroi(Animacao animacao, int quadro, int linhaEspada, int colunaEspada) {
        Bitmap resultado = Bitmap.createBitmap(SpriteLoader.LARGURA_QUADRO_BATALHA,
                SpriteLoader.ALTURA_QUADRO_BATALHA, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultado);

        int espada = Catalogo.getImagemEspada();
        canvas.drawBitmap(SpriteLoader.carregarQuadroBatalha(this, espada, linhaEspada, colunaEspada), 0, 0, null);

        desenharCamada(canvas, Catalogo.getImagemCorpo(corpo), animacao, quadro, tomPele, null);
        desenharCamada(canvas, Catalogo.getImagemCabeca(corpo), animacao, quadro, tomPele, null);
        for (Item item : roupas) {
            Paint pintura = (item.getCategoria() == Categoria.CABELO) ? pinturaCabelo : null;
            desenharCamada(canvas, item.getImagem(corpo), animacao, quadro, TomPele.CLARA, pintura);
        }

        canvas.drawBitmap(SpriteLoader.carregarQuadroBatalha(this, espada, linhaEspada + 1, colunaEspada), 0, 0, null);
        return resultado;
    }

    /** Desenha uma camada do herói virado para a direita (de frente para o inimigo). */
    private void desenharCamada(Canvas canvas, @DrawableRes int imagem, Animacao animacao,
                                int quadro, TomPele tom, Paint pintura) {
        Bitmap camada = SpriteLoader.carregar(this, imagem, animacao, Direcao.DIREITA, quadro, tom);
        canvas.drawBitmap(camada, 0, 0, pintura);
    }

    /** Recorta os quadros do inimigo: parado, ataque e derrota. */
    private void prepararQuadrosDoInimigo(Inimigo inimigo) {
        @DrawableRes int folha = Catalogo.getImagemInimigo(inimigo);
        inimigoParado = recortarLinha(folha, Inimigo.LINHA_PARADO, Inimigo.QUADROS_PARADO);
        inimigoAtaque = recortarLinha(folha, Inimigo.LINHA_ATAQUE, inimigo.getQuadrosAtaque());
        inimigoDerrota = recortarLinha(folha, Inimigo.LINHA_DERROTA, Inimigo.QUADROS_DERROTA);
    }

    private Bitmap[] recortarLinha(@DrawableRes int folha, int linha, int quantidade) {
        Bitmap[] quadros = new Bitmap[quantidade];
        for (int i = 0; i < quantidade; i++) {
            quadros[i] = SpriteLoader.carregarQuadroBatalha(this, folha, linha, i);
        }
        return quadros;
    }

    /**
     * Lê um extra do Intent e converte para o valor do enum.
     * Se o extra não veio, usa o valor padrão (útil ao abrir a tela direto, em testes).
     */
    private <T extends Enum<T>> T lerEnum(String chave, Class<T> tipo, T padrao) {
        String nome = getIntent().getStringExtra(chave);
        return (nome != null) ? Enum.valueOf(tipo, nome) : padrao;
    }
}
