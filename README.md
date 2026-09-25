# ⚔️ Dress: jogo de criação de personagem

Jogo Android **nativo em Java** em que o jogador escolhe um herói e monta o visual dele peça por peça, com cabelo, camisa, calça e calçados, num cenário com estética medieval. O personagem pode ser girado nas quatro direções e sabe andar, correr e sentar, com animação quadro a quadro e todas as roupas acompanhando os movimentos.

Projeto desenvolvido para a disciplina de **Dispositivos Móveis** do curso de **Sistemas para Internet**.

<p align="center">
  <img src="docs/tela_escolha.png" width="260" alt="Tela de escolha do personagem">
  &nbsp;&nbsp;
  <img src="docs/tela_vestir.png" width="260" alt="Tela de vestir o personagem">
  &nbsp;&nbsp;
  <img src="docs/tela_armadura.png" width="260" alt="Personagem com armadura completa">
</p>

## Funcionalidades

- **Escolha do personagem**: corpo masculino ou feminino, com prévia de cada um já vestido.
- **Troca de peças por categoria**: setas para navegar entre cabelos, camisas, calças e calçados, incluindo a opção de não usar nenhuma peça.
- **Girar**: o personagem vira para frente, esquerda, costas e direita, com todas as camadas acompanhando.
- **Andar**: animação de caminhada em 8 quadros, que funciona em qualquer direção e continua ao trocar de roupa.
- **Correr**: corrida em 8 quadros, mais rápida que a caminhada.
- **Sentar**: o personagem senta de pernas cruzadas e pode ser girado sentado. O botão vira *"Levantar"* para voltar à pose parada.
- **Troca direta entre animações**: dá para passar de andar para correr (ou sentar) sem parar antes; tocar no botão da animação ativa faz o personagem parar.
- **Cor do cabelo**: ruivo, castanho, preto e loiro.
- **Tom de pele**: quatro tons, trocados sem alterar os olhos nem os detalhes do rosto.
- **Créditos secretos**: na tela inicial, segure o dedo sobre o título *"Escolha seu personagem"*.

Ao todo, são **milhares de combinações** possíveis de visual.

## Tecnologias

- **Java** (100% nativo, sem HTML, CSS ou WebView)
- **Android SDK**, com versão mínima **Android 7.0 (API 24)**
- **Layouts em XML** com `ConstraintLayout`, `FrameLayout` e `LinearLayout`
- **View Binding** para acesso aos componentes da tela
- **Material Components / AppCompat**
- Nenhuma biblioteca externa de imagens ou jogos: toda a renderização usa as APIs nativas do Android

## Destaques técnicos

**Personagem em camadas.** O personagem é formado por seis imagens empilhadas (corpo, cabeça, calça, calçado, camisa e cabelo). Como todos os sprites seguem a mesma grade, as peças se encaixam sem nenhum cálculo de posição.

**Recorte de spritesheets.** Cada peça é uma única folha de 576x768 px com as três animações empilhadas em blocos de 4 linhas (uma por direção): andar nas linhas 0 a 3, correr nas linhas 4 a 7 e sentar nas linhas 8 a 11. Juntar tudo numa folha só (técnica conhecida como *texture atlas*) evita um arquivo por animação. A classe `SpriteLoader` recorta o quadro certo de acordo com a animação, a direção e o momento da animação, e amplia a pixel art sem suavização para mantê-la nítida.

**Otimização de memória.** Os quadros ficam guardados no tamanho original (64x64) em dois caches: um para as folhas completas, lidas uma única vez, e outro para os recortes já processados. A ampliação acontece apenas na exibição.

**Animações descritas por um enum.** O enum `Animacao` guarda, para cada animação, em qual bloco da folha ela está, o primeiro quadro, a quantidade de quadros e a velocidade. Assim, a tela não precisa de um código diferente para cada animação: adicionar uma nova é criar mais um valor no enum.

**Animação com `Handler`.** Um único `Runnable` avança um quadro da animação atual e se reagenda no intervalo dela (100 ms ao andar, 70 ms ao correr). A pose de sentar tem um quadro só e nem usa o `Handler`. A animação é interrompida no `onPause()`, respeitando o ciclo de vida da Activity para não gastar bateria em segundo plano.

**Proteção contra peças incompletas.** Cada `Item` pode declarar animações que não possui. Se uma peça vestida não tiver a animação pedida, o botão fica apagado e um aviso explica o motivo, em vez de o personagem aparecer sem a peça.

**Duas técnicas de recoloração.**
- *Cabelo*: `ColorMatrix` aplicada na `ImageView`, que converte a imagem para tons de cinza e depois tinge com a cor escolhida, preservando luzes e sombras.
- *Pele*: manipulação pixel a pixel do `Bitmap`. Cada pixel é convertido para HSV, e apenas os que têm matiz, saturação e brilho de pele são escurecidos. Assim, olhos e contornos ficam intactos.

**Comunicação entre telas.** A tela de escolha abre a tela de vestir por `Intent`, enviando o tipo de corpo como *extra*.

**Visual feito com XML.** Pergaminhos, placas de madeira com estado pressionado, moldura e setas douradas são *drawables* nativos (`shape`, `selector`, `layer-list` e `vector`), todos baseados numa paleta de cores centralizada em `colors.xml`. O fundo de pedra é uma textura repetida com escurecimento aplicado via `tint`.

**Modelagem orientada a objetos.** Enums com atributos e comportamento (`Direcao`, `CorCabelo` e `TomPele`, cada um com seu método de ciclo), classes imutáveis (`Item`), sobrecarga de construtores e métodos, e um `EnumMap` para garantir uma única peça equipada por categoria.

## Estrutura do projeto

```
app/src/main/java/com/example/dress/
├── EscolhaActivity.java    # Tela inicial: escolha do corpo e créditos
├── MainActivity.java       # Tela de vestir, girar, animar e recolorir
├── data/
│   └── Catalogo.java       # Lista de todas as peças do jogo
├── model/
│   ├── Animacao.java       # Parado, andar, correr e sentar
│   ├── Categoria.java      # Cabelo, camisa, calça e sapato
│   ├── CorCabelo.java      # Cores de cabelo disponíveis
│   ├── Direcao.java        # Frente, esquerda, costas e direita
│   ├── Item.java           # Uma peça de roupa (imutável)
│   ├── Personagem.java     # Corpo escolhido e peças equipadas
│   ├── TipoCorpo.java      # Masculino e feminino
│   └── TomPele.java        # Tons de pele disponíveis
└── util/
    └── SpriteLoader.java   # Recorte, cache e recoloração dos sprites
```

## Como executar

1. Clone o repositório:
   ```
   git clone https://github.com/PedroSalesSs/Dress-android-game.git
   ```
2. Abra a pasta do projeto no **Android Studio** e aguarde a sincronização do Gradle.
3. Execute em um emulador ou celular com **Android 7.0 ou superior**, pelo botão ▶ (*Run*).

## Créditos

Os sprites dos personagens e roupas vêm do [Universal LPC Spritesheet Character Generator](https://github.com/LiberatedPixelCup/Universal-LPC-Spritesheet-Character-Generator), criados por diversos artistas da comunidade *Liberated Pixel Cup*. A textura de pedra é de West (OpenGameArt.org) e a fonte MedievalSharp é de Wojciech Kalinowski.

Todos os recursos são usados de acordo com suas licenças abertas. A lista completa de autores, licenças e links de cada peça está em **[CREDITOS.md](CREDITOS.md)**.

O código-fonte do jogo é de autoria própria.

## Autor

**PedroSalesSs**: [github.com/PedroSalesSs](https://github.com/PedroSalesSs)
