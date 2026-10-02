package com.example.dress;

import android.animation.ObjectAnimator;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.annotation.ColorRes;
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
import com.example.dress.util.Sons;
import com.example.dress.util.Sons.Som;
import com.example.dress.util.SpriteLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Tela da batalha por turnos, sempre deitada (paisagem).
 *
 * Na vez do jogador há quatro ações: Ataque Leve, Defender, Ataque Pesado
 * (concentra num turno e solta no seguinte) e Poção (não gasta o turno).
 * Depois é a vez do inimigo, que ataca ou prepara o golpe poderoso dele.
 * A luta termina quando a vida de um dos dois chega a zero: aparece o
 * resultado e o botão para voltar ao menu (a tela de vestir).
 *
 * As regras (vida, dano, defesa, golpes) ficam na classe Batalha; esta tela
 * só mostra o que aconteceu, toca os sons e controla a ordem das animações.
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
    private static final long PAUSA_ENTRE_TURNOS_MS = 2000;  // tempo para ler a mensagem
    private static final long PAUSA_MENSAGEM_ESPECIAL_MS = 3200; // golpes especiais, bloqueio perfeito e avisos
    private static final long PAUSA_ANTES_DA_QUEDA_MS = 1200;
    private static final long PAUSA_ENTRE_GOLPES_MS = 900;   // entre os dois golpes da Fúria Selvagem
    private static final long INTERVALO_ATAQUE_PESADO_MS = 120; // o Ataque Pesado é um pouco mais lento
    private static final long DURACAO_EFEITO_GOLPE_MS = 300; // quanto tempo o vermelho fica
    private static final long DURACAO_TREMOR_MS = 300;
    private static final long DURACAO_BARRA_MS = 400;
    private static final long DURACAO_RESULTADO_MS = 400;

    private static final float DISTANCIA_TREMOR_DP = 8f;
    private static final float MULTIPLICADOR_TREMOR_FORTE = 2f; // golpes poderosos tremem mais

    /**
     * Em que ponto a luta está. Os botões só funcionam na vez do jogador,
     * o que impede, por exemplo, atacar de novo enquanto uma animação ainda está rodando.
     */
    private enum Estado { VEZ_DO_JOGADOR, AGUARDANDO, FIM }

    private ActivityBatalhaBinding binding;
    private Batalha batalha;
    private Estado estado;
    private Sons sons;
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

        sons = new Sons(this);

        Inimigo inimigo = lerEnum(EXTRA_INIMIGO, Inimigo.class, Inimigo.ORC);
        batalha = new Batalha(inimigo);

        lerAparenciaDoHeroi();
        prepararQuadrosDoHeroi();
        prepararQuadrosDoInimigo(inimigo);
        mostrarCenario(inimigo);

        binding.txtNomeInimigo.setText(inimigo.getNome());
        binding.barraVidaHeroi.setMax(Batalha.VIDA_HEROI);
        binding.barraVidaInimigo.setMax(inimigo.getVidaMaxima());
        atualizarVidas(batalha.getVidaHeroi(), batalha.getVidaInimigo(), false);

        mostrar(binding.imgHeroi, heroiParado);
        mostrar(binding.imgInimigo, inimigoParado[0]);

        binding.btnAtaqueLeve.setOnClickListener(v -> ataqueLeve());
        binding.btnDefender.setOnClickListener(v -> defender());
        binding.btnAtaquePesado.setOnClickListener(v -> concentrarAtaquePesado());
        binding.btnPocao.setOnClickListener(v -> beberPocao());
        binding.btnVoltarMenu.setOnClickListener(v -> {
            sons.tocar(Som.CLIQUE);
            finish();
        });

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
        sons.liberar();
    }

    // ================================================================ ações do jogador

    private void ataqueLeve() {
        if (estado != Estado.VEZ_DO_JOGADOR) {
            return;
        }
        sons.tocar(Som.CLIQUE);
        mudarEstado(Estado.AGUARDANDO);
        mensagem(getString(R.string.msg_heroi_ataca));
        sons.tocar(Som.ESPADA);

        tocarAnimacao(binding.imgHeroi, heroiAtaque, Animacao.ATACAR.getIntervaloMs(), () -> {
            mostrar(binding.imgHeroi, heroiParado);

            int dano = batalha.ataqueLeve();
            sons.tocar(Som.IMPACTO);
            efeitoGolpe(binding.imgInimigo, false);
            atualizarVidas(batalha.getVidaHeroi(), batalha.getVidaInimigo(), true);
            mensagem(getString(R.string.msg_dano_causado, dano, batalha.getInimigo().getNome()));

            seguirDepoisDoHeroi(PAUSA_ENTRE_TURNOS_MS);
        });
    }

    private void defender() {
        if (estado != Estado.VEZ_DO_JOGADOR) {
            return;
        }
        sons.tocar(Som.CLIQUE);
        mudarEstado(Estado.AGUARDANDO);
        batalha.defender();

        // O brilho azulado mostra que o herói está protegido durante o próximo turno do inimigo
        aplicarFiltroDeEstado(binding.imgHeroi);
        mensagem(getString(R.string.msg_heroi_defende));

        handler.postDelayed(this::turnoDoInimigo, PAUSA_ENTRE_TURNOS_MS);
    }

    /** Primeiro turno do Ataque Pesado: o herói concentra a força (brilho dourado). */
    private void concentrarAtaquePesado() {
        if (estado != Estado.VEZ_DO_JOGADOR || !batalha.podeUsarAtaquePesado()) {
            return;
        }
        sons.tocar(Som.CLIQUE);
        mudarEstado(Estado.AGUARDANDO);
        batalha.concentrar();

        sons.tocar(Som.CONCENTRAR);
        aplicarFiltroDeEstado(binding.imgHeroi);
        mensagem(getString(R.string.msg_concentrar));

        handler.postDelayed(this::turnoDoInimigo, PAUSA_ENTRE_TURNOS_MS);
    }

    /** Segundo turno do Ataque Pesado: sai sozinho, sem o jogador escolher nada. */
    private void soltarAtaquePesado() {
        mensagem(getString(R.string.msg_ataque_pesado));
        sons.tocar(Som.ESPADA);

        tocarAnimacao(binding.imgHeroi, heroiAtaque, INTERVALO_ATAQUE_PESADO_MS, () -> {
            mostrar(binding.imgHeroi, heroiParado);

            Batalha.AtaquePesado resultado = batalha.soltarAtaquePesado();
            aplicarFiltroDeEstado(binding.imgHeroi); // tira o brilho dourado
            sons.tocar(Som.ATAQUE_PODEROSO);
            efeitoGolpe(binding.imgInimigo, true);
            atualizarVidas(batalha.getVidaHeroi(), batalha.getVidaInimigo(), true);

            if (resultado.critico) {
                mensagem(getString(R.string.msg_dano_pesado_critico, resultado.dano));
            } else {
                mensagem(getString(R.string.msg_dano_pesado, resultado.dano, batalha.getInimigo().getNome()));
            }
            seguirDepoisDoHeroi(PAUSA_MENSAGEM_ESPECIAL_MS); // resultado do golpe especial fica mais tempo
        });
    }

    /** A poção não gasta o turno: depois dela o jogador continua escolhendo a ação. */
    private void beberPocao() {
        if (estado != Estado.VEZ_DO_JOGADOR || !batalha.podeUsarPocao()) {
            return;
        }
        sons.tocar(Som.CLIQUE);
        int cura = batalha.beberPocao();

        sons.tocar(Som.POCAO);
        efeitoCor(binding.imgHeroi, R.color.efeito_cura);
        atualizarVidas(batalha.getVidaHeroi(), batalha.getVidaInimigo(), true);
        mensagem(getString(R.string.msg_pocao, cura));

        mudarEstado(Estado.VEZ_DO_JOGADOR); // continua a vez; só atualiza os botões
    }

    /**
     * Depois do golpe do herói: o inimigo cai ou é a vez dele.
     * A pausa é o tempo que a mensagem do golpe fica na tela antes da vez do inimigo.
     */
    private void seguirDepoisDoHeroi(long pausa) {
        if (batalha.inimigoDerrotado()) {
            handler.postDelayed(this::derrotarInimigo, PAUSA_ANTES_DA_QUEDA_MS);
        } else {
            handler.postDelayed(this::turnoDoInimigo, pausa);
        }
    }

    // ================================================================ vez do inimigo

    private void turnoDoInimigo() {
        Inimigo inimigo = batalha.getInimigo();

        // As regras resolvem o turno inteiro de uma vez; aqui só mostramos passo a passo
        Batalha.TurnoInimigo turno = batalha.turnoDoInimigo();

        if (turno.tipo == Batalha.TipoTurno.PREPAROU) {
            // Não ataca: anuncia o golpe poderoso e ganha o brilho alaranjado
            sons.tocar(Som.AVISO_INIMIGO);
            aplicarFiltroDeEstado(binding.imgInimigo);
            aplicarFiltroDeEstado(binding.imgHeroi); // defender contra a preparação não serve
            mensagem(inimigo.getAvisoGolpePoderoso());
            // O aviso fica mais tempo e continua na tela na vez do jogador (ver voltarParaOJogador)
            handler.postDelayed(this::voltarParaOJogador, PAUSA_MENSAGEM_ESPECIAL_MS);
            return;
        }

        boolean poderoso = turno.tipo == Batalha.TipoTurno.GOLPE_PODEROSO;
        String nomeGolpe = poderoso ? inimigo.getNomeGolpePoderoso() : inimigo.getNomeAtaque();

        pararRespiracao(); // a animação de ataque assume a imagem do inimigo
        aplicarFiltroDeEstado(binding.imgInimigo); // o golpe saiu: some o brilho alaranjado
        mensagem(getString(R.string.msg_inimigo_ataca, inimigo.getNome(), nomeGolpe));
        animarGolpe(turno, 0, poderoso);
    }

    /**
     * Mostra um golpe do inimigo e, se houver outro (Fúria Selvagem), encadeia o próximo.
     * Cada golpe toca a animação de ataque inteira e depois mostra o resultado.
     */
    private void animarGolpe(Batalha.TurnoInimigo turno, int indice, boolean poderoso) {
        Inimigo inimigo = batalha.getInimigo();
        sons.tocar(Som.ESPADA);

        tocarAnimacao(binding.imgInimigo, inimigoAtaque, inimigo.getIntervaloAtaqueMs(), () -> {
            mostrar(binding.imgInimigo, inimigoParado[0]);
            Batalha.Golpe golpe = turno.golpes.get(indice);
            mostrarResultadoDoGolpe(golpe, poderoso);

            boolean temOutroGolpe = indice + 1 < turno.golpes.size();
            if (temOutroGolpe) {
                // Se este golpe foi um bloqueio perfeito, dá mais tempo para ler antes do próximo
                long pausa = golpe.bloqueioPerfeito ? PAUSA_MENSAGEM_ESPECIAL_MS : PAUSA_ENTRE_GOLPES_MS;
                handler.postDelayed(() -> animarGolpe(turno, indice + 1, poderoso), pausa);
            } else {
                terminarTurnoDoInimigo(poderoso || golpe.bloqueioPerfeito);
            }
        });
    }

    /** Som, efeito e mensagem de um golpe, conforme o que aconteceu. */
    private void mostrarResultadoDoGolpe(Batalha.Golpe golpe, boolean poderoso) {
        if (golpe.bloqueioPerfeito) {
            sons.tocar(Som.BLOQUEIO_PERFEITO);
            efeitoGolpe(binding.imgInimigo, false); // o contra-ataque atinge o inimigo
            mensagem(getString(R.string.msg_bloqueio_perfeito, golpe.danoContraAtaque));
        } else if (golpe.defendido) {
            sons.tocar(Som.DEFESA);
            efeitoGolpe(binding.imgHeroi, false);
            mensagem(getString(R.string.msg_dano_defendido, golpe.dano));
        } else {
            sons.tocar(poderoso ? Som.ATAQUE_PODEROSO : Som.IMPACTO);
            efeitoGolpe(binding.imgHeroi, poderoso);
            mensagem(getString(R.string.msg_dano_recebido, golpe.dano));
        }
        atualizarVidas(golpe.vidaHeroiDepois, golpe.vidaInimigoDepois, true);
    }

    /**
     * Fim do turno do inimigo. Se foi um golpe especial ou terminou num bloqueio perfeito,
     * a última mensagem fica mais tempo na tela.
     */
    private void terminarTurnoDoInimigo(boolean especial) {
        aplicarFiltroDeEstado(binding.imgHeroi); // a defesa acabou junto com o turno

        if (batalha.heroiDerrotado()) {
            handler.postDelayed(this::derrotarHeroi, PAUSA_ANTES_DA_QUEDA_MS);
        } else if (batalha.inimigoDerrotado()) {
            // O contra-ataque de um bloqueio perfeito pode derrubar o inimigo
            handler.postDelayed(this::derrotarInimigo, PAUSA_ANTES_DA_QUEDA_MS);
        } else {
            handler.postDelayed(this::voltarParaOJogador,
                    especial ? PAUSA_MENSAGEM_ESPECIAL_MS : PAUSA_ENTRE_TURNOS_MS);
        }
    }

    private void voltarParaOJogador() {
        iniciarRespiracao();

        // Se o herói concentrou no turno anterior, o Ataque Pesado sai sozinho agora
        if (batalha.isHeroiConcentrando()) {
            soltarAtaquePesado();
            return;
        }
        // Se o inimigo acabou de anunciar o golpe poderoso, o aviso continua na tela
        // (é ele que o jogador precisa ler para decidir se defende); senão, "Sua vez!"
        if (!batalha.isInimigoPreparando()) {
            mensagem(getString(R.string.msg_sua_vez));
        }
        mudarEstado(Estado.VEZ_DO_JOGADOR);
    }

    // ================================================================ fim da luta

    private void derrotarInimigo() {
        pararRespiracao();
        mudarEstado(Estado.FIM);
        binding.imgInimigo.clearColorFilter();
        tocarAnimacao(binding.imgInimigo, inimigoDerrota, Animacao.DERROTA.getIntervaloMs(),
                () -> mostrarResultado(true));
    }

    private void derrotarHeroi() {
        pararRespiracao();
        mudarEstado(Estado.FIM);
        binding.imgHeroi.clearColorFilter();
        tocarAnimacao(binding.imgHeroi, heroiDerrota, Animacao.DERROTA.getIntervaloMs(),
                () -> mostrarResultado(false));
    }

    /** Mostra o painel de vitória ou derrota, aparecendo aos poucos (fade in). */
    private void mostrarResultado(boolean vitoria) {
        sons.tocar(vitoria ? Som.VITORIA : Som.DERROTA);

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
     * fica avermelhado por um instante e treme para os lados (mais forte nos golpes poderosos).
     */
    private void efeitoGolpe(ImageView alvo, boolean forte) {
        efeitoCor(alvo, R.color.efeito_golpe);

        float d = DISTANCIA_TREMOR_DP * getResources().getDisplayMetrics().density;
        if (forte) {
            d *= MULTIPLICADOR_TREMOR_FORTE;
        }
        ObjectAnimator tremor = ObjectAnimator.ofFloat(alvo, "translationX", 0, d, -d, d / 2, -d / 2, 0);
        tremor.setDuration(DURACAO_TREMOR_MS);
        tremor.start();
    }

    /** Pinta o lutador com uma cor por um instante e depois volta ao brilho do estado atual. */
    private void efeitoCor(ImageView alvo, @ColorRes int cor) {
        alvo.setColorFilter(getColor(cor), PorterDuff.Mode.SRC_ATOP);
        handler.postDelayed(() -> aplicarFiltroDeEstado(alvo), DURACAO_EFEITO_GOLPE_MS);
    }

    /**
     * Aplica o brilho que representa o estado do lutador:
     * herói dourado (concentrando) ou azul (defendendo); inimigo laranja (preparando o golpe).
     * Sem estado especial, o filtro é removido.
     */
    private void aplicarFiltroDeEstado(ImageView alvo) {
        int cor = 0;
        if (alvo == binding.imgHeroi) {
            if (batalha.isHeroiConcentrando()) {
                cor = R.color.efeito_concentrar;
            } else if (batalha.isHeroiDefendendo()) {
                cor = R.color.efeito_defesa;
            }
        } else if (batalha.isInimigoPreparando()) {
            cor = R.color.efeito_preparo;
        }

        if (cor != 0 && estado != Estado.FIM) {
            alvo.setColorFilter(getColor(cor), PorterDuff.Mode.SRC_ATOP);
        } else {
            alvo.clearColorFilter();
        }
    }

    private void iniciarRespiracao() {
        handler.removeCallbacks(respiracao); // evita duas "respirações" ao mesmo tempo
        handler.postDelayed(respiracao, INTERVALO_RESPIRACAO_MS);
    }

    private void pararRespiracao() {
        handler.removeCallbacks(respiracao);
    }

    // ================================================================ interface

    /**
     * Liga ou desliga os botões conforme o estado da luta. O Ataque Pesado e a Poção
     * também dependem de ainda terem usos, e mostram quantos restam.
     */
    private void mudarEstado(Estado novo) {
        estado = novo;
        boolean vez = novo == Estado.VEZ_DO_JOGADOR;

        ativarBotao(binding.btnAtaqueLeve, vez);
        ativarBotao(binding.btnDefender, vez);
        ativarBotao(binding.btnAtaquePesado, vez && batalha.podeUsarAtaquePesado());
        ativarBotao(binding.btnPocao, vez && batalha.podeUsarPocao());

        binding.btnAtaquePesado.setText(getString(R.string.btn_ataque_pesado, batalha.getAtaquesPesadosRestantes()));
        binding.btnPocao.setText(getString(R.string.btn_pocao, batalha.getPocoesRestantes()));
    }

    private void ativarBotao(View botao, boolean ativo) {
        botao.setEnabled(ativo);
        botao.setAlpha(ativo ? 1f : 0.5f);
    }

    private void mensagem(String texto) {
        binding.txtMensagem.setText(texto);
    }

    private void atualizarVidas(int vidaHeroi, int vidaInimigo, boolean animar) {
        atualizarBarra(binding.barraVidaHeroi, binding.txtVidaHeroi, vidaHeroi, Batalha.VIDA_HEROI, animar);
        atualizarBarra(binding.barraVidaInimigo, binding.txtVidaInimigo,
                vidaInimigo, batalha.getInimigo().getVidaMaxima(), animar);
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

    /** Mostra uma imagem ampliada sem suavização, para a pixel art continuar nítida. */
    private void mostrar(ImageView alvo, Bitmap quadro) {
        alvo.setImageBitmap(quadro);
        alvo.getDrawable().setFilterBitmap(false);
    }

    /** Coloca o cenário do inimigo (cemitério, floresta ou acampamento) no fundo da arena. */
    private void mostrarCenario(Inimigo inimigo) {
        BitmapFactory.Options opcoes = new BitmapFactory.Options();
        opcoes.inScaled = false; // mantém os 320 x 64 px originais; a ampliação é feita pela ImageView
        Bitmap cenario = BitmapFactory.decodeResource(getResources(), Catalogo.getImagemCenario(inimigo), opcoes);
        mostrar(binding.imgCenario, cenario);
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
     * parte de trás da espada, mecha de trás do cabelo, corpo, cabeça, roupas
     * e parte da frente da espada.
     * O herói fica na metade esquerda; a direita é o espaço do golpe.
     */
    private Bitmap montarQuadroHeroi(Animacao animacao, int quadro, int linhaEspada, int colunaEspada) {
        Bitmap resultado = Bitmap.createBitmap(SpriteLoader.LARGURA_QUADRO_BATALHA,
                SpriteLoader.ALTURA_QUADRO_BATALHA, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultado);

        int espada = Catalogo.getImagemEspada();
        canvas.drawBitmap(SpriteLoader.carregarQuadroBatalha(this, espada, linhaEspada, colunaEspada), 0, 0, null);

        // Mecha do cabelo que fica atrás do corpo (só alguns cabelos têm)
        for (Item item : roupas) {
            if (item.getImagemFundo() != Item.SEM_IMAGEM) {
                desenharCamada(canvas, item.getImagemFundo(), animacao, quadro, TomPele.CLARA, pinturaCabelo);
            }
        }

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