# ⚔️ My Hero: crie seu herói e enfrente monstros

Jogo Android **nativo em Java** em que o jogador escolhe um herói e monta o visual dele peça por peça, com cabelo, camisa, calça e calçados, num cenário com estética medieval. O personagem pode ser girado nas quatro direções e sabe andar, correr e sentar, com animação quadro a quadro e todas as roupas acompanhando os movimentos. Depois de vestido, o herói pode enfrentar monstros numa **batalha por turnos**.

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
- **Batalha por turnos**: o botão *Batalha* abre a escolha do inimigo (esqueleto, lobisomem ou orc, em ordem de dificuldade). A luta acontece numa tela deitada, com o herói usando a roupa montada pelo jogador e um cenário próprio para cada inimigo (cemitério, floresta e acampamento). No fim aparece o resultado (vitória ou derrota) e o botão para voltar ao menu.
- **Quatro ações de combate**: **Ataque Leve**; **Defender**, que reduz o golpe em 65% e tem 20% de chance de bloqueio perfeito (nenhum dano e contra-ataque); **Ataque Pesado**, que concentra a força por um turno e sai no seguinte com o dobro do dano (35% de chance de triplo), uma vez por luta; e **Poção**, que recupera 15 de vida sem gastar o turno, uma vez por luta.
- **Golpes poderosos anunciados**: de vez em quando o inimigo se prepara (com aviso na tela e um brilho alaranjado) e, no turno seguinte, solta um golpe especial: a *Estocada Certeira* do esqueleto atravessa a guarda, a *Fúria Selvagem* do lobisomem são dois golpes seguidos e o *Golpe Brutal* do orc causa o triplo do dano. Defender na hora certa é o que decide a luta.
- **Efeitos sonoros**: golpes, impactos, defesa, bloqueio perfeito, poção, avisos dos inimigos e fanfarras de vitória e derrota.
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

**Recorte de spritesheets.** Cada peça é uma única folha de 576x1088 px com as animações empilhadas em blocos de 4 linhas (uma por direção): andar nas linhas 0 a 3, correr nas linhas 4 a 7, sentar nas linhas 8 a 11 e atacar nas linhas 12 a 15. A derrota ocupa só a linha 16, porque o LPC a desenha apenas de frente. Juntar tudo numa folha só (técnica conhecida como *texture atlas*) evita um arquivo por animação. A classe `SpriteLoader` recorta o quadro certo de acordo com a animação, a direção e o momento da animação, e amplia a pixel art sem suavização para mantê-la nítida.

**Otimização de memória.** Os quadros ficam guardados no tamanho original (64x64) em dois caches: um para as folhas completas, lidas uma única vez, e outro para os recortes já processados. A ampliação acontece apenas na exibição.

**Animações descritas por um enum.** O enum `Animacao` guarda, para cada animação, em qual bloco da folha ela está, o primeiro quadro, a quantidade de quadros e a velocidade. Assim, a tela não precisa de um código diferente para cada animação: adicionar uma nova é criar mais um valor no enum.

**Animação com `Handler`.** Um único `Runnable` avança um quadro da animação atual e se reagenda no intervalo dela (100 ms ao andar, 70 ms ao correr). A pose de sentar tem um quadro só e nem usa o `Handler`. A animação é interrompida no `onPause()`, respeitando o ciclo de vida da Activity para não gastar bateria em segundo plano.

**Proteção contra peças incompletas.** Cada `Item` pode declarar animações que não possui. Se uma peça vestida não tiver a animação pedida, o botão fica apagado e um aviso explica o motivo, em vez de o personagem aparecer sem a peça.

**Duas técnicas de recoloração.**
- *Cabelo*: `ColorMatrix` aplicada na `ImageView`, que converte a imagem para tons de cinza e depois tinge com a cor escolhida, preservando luzes e sombras.
- *Pele*: manipulação pixel a pixel do `Bitmap`. Cada pixel é convertido para HSV, e apenas os que têm matiz, saturação e brilho de pele são escurecidos. Assim, olhos e contornos ficam intactos.

**Comunicação entre telas.** A tela de escolha abre a tela de vestir por `Intent`, enviando o tipo de corpo como *extra*. A tela de vestir, por sua vez, abre a batalha enviando o inimigo escolhido e a aparência do herói (corpo, id de cada peça, cor do cabelo e tom de pele).

**Batalha com máquina de estados.** A `BatalhaActivity` fica sempre em um de três estados (vez do jogador, aguardando animação ou fim), e os botões só respondem na vez do jogador. As etapas de cada turno (animação do golpe, dano, vez do inimigo) são encadeadas por *callbacks* com o `Handler`. O herói é montado uma única vez num `Canvas`, juntando espada, corpo, roupas e cabelo pintado; durante a luta basta trocar a imagem. Como o LPC não tem animação de "levar golpe", o efeito é feito com um filtro vermelho (`PorterDuff`) e um tremor com `ObjectAnimator`. O mesmo recurso de filtro mostra o estado de cada lutador: azul ao defender, dourado ao concentrar o Ataque Pesado e alaranjado quando o inimigo prepara o golpe poderoso.

**Sons com SoundPool.** Os efeitos sonoros ficam em `res/raw` (formato OGG) e são tocados pela classe `Sons`, que usa o `SoundPool`: ele carrega todos os sons na memória uma vez e os toca sem atraso, o que é o recomendado para sons curtos de jogos.

**Regras separadas da tela e testadas.** Vida, dano, defesa, poção, Ataque Pesado e os golpes poderosos ficam na classe `Batalha`, que não depende de nada do Android. A tela pede uma ação e recebe um resultado (dano, bloqueio, contra-ataque), e só cuida de mostrar. Por isso ela tem testes automatizados com JUnit (`BatalhaTest`), que rodam no computador sem emulador. O sorteio de dano recebe um `Random` com semente fixa nos testes, para que o resultado seja sempre o mesmo.

**Layout que se adapta à tela.** Na tela de vestir, os controles ficam presos de baixo para cima e o quadro do personagem ocupa o espaço que sobra, mantendo o formato quadrado. Em celulares com tela mais baixa, o quadro encolhe em vez de empurrar os botões para fora da tela.

**Visual feito com XML.** Pergaminhos, placas de madeira com estado pressionado, moldura e setas douradas são *drawables* nativos (`shape`, `selector`, `layer-list` e `vector`), todos baseados numa paleta de cores centralizada em `colors.xml`. O fundo de pedra é uma textura repetida com escurecimento aplicado via `tint`.

**Modelagem orientada a objetos.** Enums com atributos e comportamento (`Direcao`, `CorCabelo` e `TomPele`, cada um com seu método de ciclo, e `Inimigo`, com a ficha de cada monstro), classes imutáveis (`Item`), sobrecarga de construtores e métodos, e um `EnumMap` para garantir uma única peça equipada por categoria.

## Estrutura do projeto

```
app/src/main/java/com/example/dress/
├── BatalhaActivity.java    # Batalha por turnos (tela deitada)
├── EscolhaActivity.java    # Tela inicial: escolha do corpo e créditos
├── MainActivity.java       # Tela de vestir, girar, animar, recolorir e escolher o inimigo
├── data/
│   └── Catalogo.java       # Lista de todas as peças do jogo
├── model/
│   ├── Animacao.java       # Parado, andar, correr, sentar, atacar e derrota
│   ├── Batalha.java        # Regras da luta: vida, dano, defesa, poção e golpes especiais
│   ├── Categoria.java      # Cabelo, camisa, calça e sapato
│   ├── CorCabelo.java      # Cores de cabelo disponíveis
│   ├── Direcao.java        # Frente, esquerda, costas e direita
│   ├── Inimigo.java        # Esqueleto, lobisomem e orc, com vida, força e golpe poderoso
│   ├── Item.java           # Uma peça de roupa (imutável)
│   ├── Personagem.java     # Corpo escolhido e peças equipadas
│   ├── TipoCorpo.java      # Masculino e feminino
│   └── TomPele.java        # Tons de pele disponíveis
└── util/
    ├── FiltroCabelo.java   # Filtro de cor do cabelo (usado nas duas telas)
    ├── Sons.java           # Efeitos sonoros (SoundPool)
    └── SpriteLoader.java   # Recorte, cache e recoloração dos sprites

app/src/test/java/com/example/dress/model/
└── BatalhaTest.java        # Testes das regras da batalha (JUnit)
```

## Como executar

1. Clone o repositório:
   ```
   git clone https://github.com/PedroSalesSs/MyHero-android-game.git
   ```
2. Abra a pasta do projeto no **Android Studio** e aguarde a sincronização do Gradle.
3. Execute em um emulador ou celular com **Android 7.0 ou superior**, pelo botão ▶ (*Run*).
4. Para rodar os testes das regras da batalha, clique com o botão direito em `BatalhaTest` e escolha *Run 'BatalhaTest'*.

## Créditos

Os sprites dos personagens, roupas, inimigos e armas vêm do [Universal LPC Spritesheet Character Generator](https://github.com/LiberatedPixelCup/Universal-LPC-Spritesheet-Character-Generator), criados por diversos artistas da comunidade *Liberated Pixel Cup*. A textura de pedra é de West (OpenGameArt.org) e a fonte MedievalSharp é de Wojciech Kalinowski.

Todos os recursos são usados de acordo com suas licenças abertas. A lista completa de autores, licenças e links de cada peça está em **[CREDITOS.md](CREDITOS.md)**.

O código-fonte do jogo é de autoria própria.

## Autor

**PedroSalesSs**: [github.com/PedroSalesSs](https://github.com/PedroSalesSs)
