package com.example.dress;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dress.data.Catalogo;
import com.example.dress.databinding.ActivityEscolhaBinding;
import com.example.dress.model.Categoria;
import com.example.dress.model.Item;
import com.example.dress.model.TipoCorpo;
import com.example.dress.util.SpriteLoader;

import java.util.List;

/**
 * Tela inicial do jogo: o jogador escolhe o tipo de corpo do personagem.
 */
public class EscolhaActivity extends AppCompatActivity {

    // Ordem em que as roupas são desenhadas na prévia (a mesma das camadas da tela de vestir)
    private static final Categoria[] ORDEM_CAMADAS = {
            Categoria.CALCA, Categoria.SAPATO, Categoria.CAMISA, Categoria.CABELO
    };

    private ActivityEscolhaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityEscolhaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mostrarPrevia(binding.imgMasculino, TipoCorpo.MASCULINO);
        mostrarPrevia(binding.imgFeminino, TipoCorpo.FEMININO);

        binding.cardMasculino.setOnClickListener(v -> abrirJogo(TipoCorpo.MASCULINO));
        binding.cardFeminino.setOnClickListener(v -> abrirJogo(TipoCorpo.FEMININO));

        // Segredo: segurar o dedo sobre o título abre os créditos
        binding.txtTitulo.setOnLongClickListener(v -> {
            mostrarCreditos();
            return true;
        });
    }

    /** Abre a tela de vestir, enviando o corpo escolhido como "extra" do Intent. */
    private void abrirJogo(TipoCorpo corpo) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_TIPO_CORPO, corpo.name());
        startActivity(intent);
    }

    /** Mostra os créditos das imagens e da fonte numa janela de diálogo. */
    private void mostrarCreditos() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.creditos_titulo)
                .setMessage(R.string.creditos_texto)
                .setPositiveButton(R.string.fechar, null)
                .show();
    }

    /** Mostra no cartão a prévia do personagem, sem suavização (pixel art nítida). */
    private void mostrarPrevia(ImageView imagem, TipoCorpo corpo) {
        imagem.setImageBitmap(montarPrevia(corpo));
        imagem.getDrawable().setFilterBitmap(false);
    }

    /**
     * Junta todas as camadas numa única imagem: corpo, cabeça e a primeira
     * peça de cada categoria (a mesma roupa com que o jogo começa).
     */
    private Bitmap montarPrevia(TipoCorpo corpo) {
        Bitmap base = SpriteLoader.carregar(this, Catalogo.getImagemCorpo(corpo));

        // Imagem vazia e transparente, do mesmo tamanho dos sprites
        Bitmap resultado = Bitmap.createBitmap(base.getWidth(), base.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultado);

        desenharCamada(canvas, Catalogo.getImagemCorpo(corpo));
        desenharCamada(canvas, Catalogo.getImagemCabeca(corpo));

        for (Categoria categoria : ORDEM_CAMADAS) {
            List<Item> opcoes = Catalogo.getPorCategoria(categoria, corpo);
            if (!opcoes.isEmpty()) {
                desenharCamada(canvas, opcoes.get(0).getImagem(corpo));
            }
        }
        return resultado;
    }

    /** Desenha uma camada por cima do que já está no canvas. */
    private void desenharCamada(Canvas canvas, @DrawableRes int imagem) {
        canvas.drawBitmap(SpriteLoader.carregar(this, imagem), 0, 0, null);
    }
}