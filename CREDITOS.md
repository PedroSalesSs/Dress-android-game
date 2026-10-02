# Créditos

O jogo **My Hero** foi desenvolvido como trabalho da disciplina de Dispositivos Móveis. O código, os efeitos sonoros e os cenários de batalha são de autoria própria; as imagens dos personagens, a textura de fundo e a fonte foram criadas por outros artistas e são usadas de acordo com as licenças abertas indicadas abaixo. Muito obrigado a todos os autores por disponibilizarem seus trabalhos.

Os créditos também podem ser vistos dentro do app: na tela inicial, segure o dedo sobre o título *"Escolha seu personagem"*.

## Resumo

| Recurso | Fonte | Licença |
|---|---|---|
| Personagens, cabelos, roupas, inimigos e armas | Universal LPC Spritesheet Character Generator (Liberated Pixel Cup) | OGA-BY 3.0, CC-BY-SA 3.0, GPL 3.0, CC0, GPL 2.0 (varia por peça, detalhado abaixo) |
| Textura de pedra do fundo | "stone wall 4", por West (OpenGameArt.org) | CC0 (domínio público) |
| Fonte MedievalSharp | Wojciech Kalinowski (Google Fonts) | SIL Open Font License 1.1 |
| Cenários de batalha (cemitério, floresta e acampamento) | Autoria própria (pixel art feita para o jogo) | Mesma licença do projeto |
| Efeitos sonoros | Autoria própria (sons sintetizados por código) | Mesma licença do projeto |

## Personagens, cabelos e roupas (LPC)

Todos os sprites vêm do **Universal LPC Spritesheet Character Generator**:

- Repositório: https://github.com/LiberatedPixelCup/Universal-LPC-Spritesheet-Character-Generator
- Gerador online: https://liberatedpixelcup.github.io/Universal-LPC-Spritesheet-Character-Generator/

Para cada peça foram usados os arquivos `walk.png`, `run.png`, `sit.png`, `slash.png` e `hurt.png`, juntados pelo projeto numa única folha (andar, correr, sentar, atacar e derrota, nessa ordem). Os arquivos estão em `app/src/main/res/drawable-nodpi` com os nomes indicados entre parênteses. As versões femininas de calças e calçados vêm das pastas `thin`, que é o formato de perna usado pelo corpo feminino no LPC. As imagens foram modificadas em tempo de execução pelo app (recorte de quadros, ampliação, troca da cor do cabelo e do tom de pele).

### Todos os autores dos sprites usados

bluecarrot16, JaidynReiman, Benjamin K. Smith (BenCreating), Evert, Eliza Wyatt (ElizaWy), TheraHedwig, MuffinElZangano, Durrani, Johannes Sjölund (wulax), Stephen Challener (Redshrike), Pierre Vigier (pvigier), ElizaWy, Matthew Krohn (makrohn), Manuel Riecke (MrBeast), thecilekli, Napsio (Vitruvian Studio), Michael Whitlock (bigbeargames), Joe White, Mandi Paugh, William.Thompsonj, Nila122, Napsio, Zi Ye, Sander Frenken (castelonia), Inboxninja, Fabzy, Lanea Zimmerman (Sharm), Bluecarrot16, Luke Mehl

### Base do personagem

**Corpo masculino** (`corpo_m.png`)

- Origem no LPC: `spritesheets/body/bodies/male/`
- Autores: bluecarrot16, JaidynReiman, Benjamin K. Smith (BenCreating), Evert, Eliza Wyatt (ElizaWy), TheraHedwig, MuffinElZangano, Durrani, Johannes Sjölund (wulax), Stephen Challener (Redshrike)
- Licenças: OGA-BY 3.0, CC-BY-SA 3.0, GPL 3.0
- Links:
  - https://opengameart.org/content/liberated-pixel-cup-lpc-base-assets-sprites-map-tiles
  - https://opengameart.org/content/lpc-medieval-fantasy-character-sprites
  - https://opengameart.org/content/lpc-male-jumping-animation-by-durrani
  - https://opengameart.org/content/lpc-runcycle-and-diagonal-walkcycle
  - https://opengameart.org/content/lpc-revised-character-basics
  - https://opengameart.org/content/lpc-be-seated
  - https://opengameart.org/content/lpc-runcycle-for-male-muscular-and-pregnant-character-bases-with-modular-heads
  - https://opengameart.org/content/lpc-jump-expanded
  - https://opengameart.org/content/lpc-character-bases
- Observações: see details at https://opengameart.org/content/lpc-character-bases; 'Thick' Male Revised Run/Climb by JaidynReiman (based on ElizaWy's LPC Revised)

**Corpo feminino** (`corpo_f.png`)

- Origem no LPC: `spritesheets/body/bodies/female/`
- Autores: Benjamin K. Smith (BenCreating), bluecarrot16, TheraHedwig, Evert, MuffinElZangano, Durrani, Pierre Vigier (pvigier), ElizaWy, Matthew Krohn (makrohn), Johannes Sjölund (wulax), Stephen Challener (Redshrike)
- Licenças: OGA-BY 3.0, CC-BY-SA 3.0, GPL 3.0
- Links:
  - https://opengameart.org/content/liberated-pixel-cup-lpc-base-assets-sprites-map-tiles
  - https://opengameart.org/content/lpc-medieval-fantasy-character-sprites
  - https://opengameart.org/content/lpc-ladies
  - https://opengameart.org/content/lpc-7-womens-shirts
  - https://opengameart.org/content/lpc-jump-expanded
  - https://opengameart.org/content/lpc-be-seated
  - https://opengameart.org/content/lpc-revised-character-basics
  - https://gitlab.com/vagabondgame/lpc-characters
  - https://opengameart.org/content/lpc-male-jumping-animation-by-durrani
  - https://opengameart.org/content/lpc-runcycle-and-diagonal-walkcycle
- Observações: see details at https://opengameart.org/content/lpc-character-bases

**Cabeça masculina** (`cabeca_m.png`)

- Origem no LPC: `spritesheets/head/heads/human/male/`
- Autores: bluecarrot16, Benjamin K. Smith (BenCreating), Stephen Challener (Redshrike)
- Licenças: OGA-BY 3.0, CC-BY-SA 3.0, GPL 3.0
- Links:
  - https://opengameart.org/content/liberated-pixel-cup-lpc-base-assets-sprites-map-tiles
  - https://opengameart.org/content/lpc-character-bases
- Observações: original head by Redshrike, tweaks by BenCreating, modular version by bluecarrot16

**Cabeça feminina** (`cabeca_f.png`)

- Origem no LPC: `spritesheets/head/heads/human/female/`
- Autores: bluecarrot16, Benjamin K. Smith (BenCreating), Stephen Challener (Redshrike)
- Licenças: OGA-BY 3.0, CC-BY-SA 3.0, GPL 3.0
- Links:
  - https://opengameart.org/content/
  - https://opengameart.org/content/lpc-character-bases
- Observações: original head by Redshrike, tweaks by BenCreating, modular version by bluecarrot16

### Cabelos

**Afro** (`cabelo_afro.png`)

- Origem no LPC: `spritesheets/hair/afro/adult/`
- Autores: bluecarrot16
- Licenças: CC0
- Links:
  - https://opengameart.org/content/lpc-hair

**Bagunçado** (`cabelo_baguncado.png`)

- Origem no LPC: `spritesheets/hair/bedhead/adult/`
- Autores: JaidynReiman, Manuel Riecke (MrBeast)
- Licenças: CC-BY-SA 3.0, GPL 3.0
- Links:
  - https://opengameart.org/content/liberated-pixel-cup-lpc-base-assets-sprites-map-tiles
  - https://opengameart.org/content/lpc-expanded-hair

**Cacheado** (`cabelo_cacheado.png`)

- Origem no LPC: `spritesheets/hair/curly_long/adult/`
- Autores: ElizaWy
- Licenças: OGA-BY 3.0
- Links:
  - https://opengameart.org/content/lpc-hair

**Idol** (`cabelo_idol.png`)

- Origem no LPC: `spritesheets/hair/idol/adult/`
- Autores: thecilekli, bluecarrot16
- Licenças: CC0
- Links:
  - https://opengameart.org/content/lpc-korean-idol-hairstyle-male-with-12-colors
  - https://opengameart.org/content/lpc-hair
  - https://github.com/ElizaWy/LPC/tree/main/Characters/Hair
  - https://opengameart.org/content/lpc-expanded-sit-run-jump-more
- Observações: Original CC0 by Thecilekli. Edited by bluecarrot16 and ElizaWy. Animated by ElizaWy.

**Longo** (`cabelo_longo.png`)

- Origem no LPC: `spritesheets/hair/half_up/adult/`
- Autores: ElizaWy
- Licenças: OGA-BY 3.0
- Links:
  - https://opengameart.org/content/lpc-hair

**Solto** (`cabelo_solto.png`)

- Origem no LPC: `spritesheets/hair/long/adult/`
- Autores: JaidynReiman, Manuel Riecke (MrBeast)
- Licenças: CC-BY-SA 3.0, GPL 3.0
- Links:
  - https://opengameart.org/content/liberated-pixel-cup-lpc-base-assets-sprites-map-tiles
  - https://opengameart.org/content/lpc-expanded-hair

**Dreads curtos** (`cabelo_dreads.png`)

- Origem no LPC: `spritesheets/hair/dreadlocks_short/`
- Autores: ElizaWy, bluecarrot16
- Licenças: CC0
- Links:
  - https://opengameart.org/content/lpc-hair
  - https://github.com/ElizaWy/LPC/blob/main/Characters/Hair
  - https://opengameart.org/content/lpc-expanded-sit-run-jump-more
- Observações: Original by bluecarrot16. Animated by ElizaWy.

**Tranças nagô** (`cabelo_nago.png`)

- Origem no LPC: `spritesheets/hair/cornrows/`
- Autores: ElizaWy, bluecarrot16
- Licenças: CC0
- Links:
  - https://opengameart.org/content/lpc-hair
  - https://github.com/ElizaWy/LPC/blob/main/Characters/Hair
  - https://opengameart.org/content/lpc-expanded-sit-run-jump-more
- Observações: Original by bluecarrot16, animated by ElizaWy.

**Franja** (`cabelo_franja.png`)

- Origem no LPC: `spritesheets/hair/bangs/`
- Autores: JaidynReiman, Manuel Riecke (MrBeast)
- Licenças: CC-BY-SA 3.0, GPL 3.0
- Links:
  - https://opengameart.org/content/liberated-pixel-cup-lpc-base-assets-sprites-map-tiles
  - https://opengameart.org/content/lpc-expanded-hair

**Espetado** (`cabelo_espetado.png`)

- Origem no LPC: `spritesheets/hair/spiked_liberty/`
- Autores: Fabzy, bluecarrot16
- Licenças: CC-BY-SA 3.0
- Links:
  - https://opengameart.org/content/the-revolution-hair
  - https://opengameart.org/content/lpc-hair

**Calvo** (`cabelo_calvo.png`)

- Origem no LPC: `spritesheets/hair/balding/`
- Autores: ElizaWy
- Licenças: OGA-BY 3.0
- Links:
  - https://opengameart.org/content/lpc-hair

**Cachos longos** (`cabelo_cachos_longos.png e cabelo_cachos_longos_fundo.png`)

- Origem no LPC: `spritesheets/hair/curls_large_xlong/adult/`
- Autores: JaidynReiman
- Licenças: OGA-BY 3.0+, CC-BY 3.0+, GPL 3.0+
- Links:
  - https://opengameart.org/content/lpc-expanded-xlong-hair

**Jogado no ombro** (`cabelo_ombro.png e cabelo_ombro_fundo.png`)

- Origem no LPC: `spritesheets/hair/shoulderl/adult/`
- Autores: JaidynReiman, Lanea Zimmerman (Sharm), Manuel Riecke (MrBeast)
- Licenças: OGA-BY 3.0, CC-BY-SA 3.0, GPL 3.0
- Links:
  - https://opengameart.org/content/liberated-pixel-cup-lpc-base-assets-sprites-map-tiles
  - https://github.com/jrconway3/Universal-LPC-spritesheet/commit/c32ce8a7edd9fc7fe7daa62a39cf64e5946020a2
  - https://opengameart.org/content/lpc-expanded-hair

### Camisas

**Camiseta (masculina)** (`camisa_tshirt_m.png`)

- Origem no LPC: `spritesheets/torso/clothes/shortsleeve/tshirt/male/`
- Autores: ElizaWy, JaidynReiman, Stephen Challener (Redshrike), Johannes Sjölund (wulax)
- Licenças: OGA-BY 3.0
- Links:
  - https://opengameart.org/content/lpc-revised-character-basics
  - https://github.com/ElizaWy/LPC/tree/main/Characters/Clothing
  - https://opengameart.org/content/lpc-expanded-sit-run-jump-more
  - https://opengameart.org/content/lpc-expanded-simple-shirts
- Observações: original by ElizaWy; spellcast/thrust/shoot/hurt/male adapted from original by JaidynReiman

**Camiseta (feminina)** (`camisa_tshirt_f.png`)

- Origem no LPC: `spritesheets/torso/clothes/shortsleeve/tshirt/female/`
- Autores: ElizaWy, JaidynReiman, Stephen Challener (Redshrike), Johannes Sjölund (wulax)
- Licenças: OGA-BY 3.0
- Links:
  - https://opengameart.org/content/lpc-revised-character-basics
  - https://github.com/ElizaWy/LPC/tree/main/Characters/Clothing
  - https://opengameart.org/content/lpc-expanded-sit-run-jump-more
  - https://opengameart.org/content/lpc-expanded-simple-shirts
- Observações: original by ElizaWy; spellcast/thrust/shoot/hurt/male adapted from original by JaidynReiman

**Cota de malha (masculina)** (`camisa_chainmail_m.png`)

- Origem no LPC: `spritesheets/torso/chainmail/male/`
- Autores: Johannes Sjölund (wulax), Napsio (Vitruvian Studio), JaidynReiman
- Licenças: OGA-BY 3.0, CC-BY-SA 3.0, GPL 3.0
- Links:
  - https://opengameart.org/content/lpc-medieval-fantasy-character-sprites
- Observações: minor edits by bluecarrot16, reduced colors by Napsio, adjusted Male colors and Idle/Sit/Emote/Climb/Run by JaidynReiman

**Cota de malha (feminina)** (`camisa_chainmail_f.png`)

- Origem no LPC: `spritesheets/torso/chainmail/female/`
- Autores: Johannes Sjölund (wulax), Napsio (Vitruvian Studio), JaidynReiman
- Licenças: OGA-BY 3.0, CC-BY-SA 3.0, GPL 3.0
- Links:
  - https://opengameart.org/content/lpc-medieval-fantasy-character-sprites
- Observações: minor edits by bluecarrot16, reduced colors by Napsio, adjusted Male colors and Idle/Sit/Emote/Climb/Run by JaidynReiman

**Peitoral de armadura (masculino)** (`camisa_armour_m.png`)

- Origem no LPC: `spritesheets/torso/armour/plate/male/`
- Autores: Napsio (Vitruvian Studio), JaidynReiman, bluecarrot16, Michael Whitlock (bigbeargames), Johannes Sjölund (wulax)
- Licenças: OGA-BY 3.0, CC-BY-SA 3.0, GPL 3.0
- Links:
  - https://opengameart.org/content/lpc-medieval-fantasy-character-sprites
  - https://opengameart.org/content/lpc-combat-armor-for-women
- Observações: original by wulax, recolor by bigbeargames, color reduced to 7 colors and adapted to v3 bases by bluecarrot16, run/jump/sit/climb/revised combat by JaidynReiman, reduced colors to 6 (based on Napsio's Vitruvian)

**Peitoral de armadura (feminino)** (`camisa_armour_f.png`)

- Origem no LPC: `spritesheets/torso/armour/plate/female/`
- Autores: JaidynReiman, bluecarrot16, Michael Whitlock (bigbeargames), Matthew Krohn (makrohn), Johannes Sjölund (wulax)
- Licenças: OGA-BY 3.0, CC-BY-SA 3.0, GPL 3.0
- Links:
  - https://opengameart.org/content/lpc-medieval-fantasy-character-sprites
  - https://opengameart.org/content/lpc-combat-armor-for-women
  - http://opengameart.org/content/lpc-clothing-updates
- Observações: original by wulax, adapted to female base by makrohn, recolor by bigbeargames, color reduced to 7 colors and adapted to v3 bases by bluecarrot16, run/jump/sit/climb/revised combat by JaidynReiman, reduced colors to 6 (based on Napsio's Vitruvian)

**Jardineira** (`camisa_jardineira_m.png e _f.png`)

- Origem no LPC: `spritesheets/torso/aprons/overalls/`
- Autores: ElizaWy, bluecarrot16, JaidynReiman
- Licenças: OGA-BY 3.0, GPL 3.0
- Links:
  - https://opengameart.org/content/lpc-revised-character-basics
  - http://opengameart.org/content/lpc-clothing-updates
- Observações: original overalls by ElizaWy, extended to all animation frames, adapted from teen to male base, and edited for v3 bases by bluecarrot16; extended to combat animations by JaidynReiman

**Armadura de couro** (`camisa_couro_m.png e _f.png`)

- Origem no LPC: `spritesheets/torso/armour/leather/`
- Autores: Johannes Sjölund (wulax), bluecarrot16, JaidynReiman, Michael Whitlock (bigbeargames), Matthew Krohn (makrohn)
- Licenças: OGA-BY 3.0, CC-BY-SA 3.0, GPL 3.0
- Links:
  - https://opengameart.org/content/lpc-medieval-fantasy-character-sprites
  - https://opengameart.org/content/lpc-clothing-updates
  - https://opengameart.org/content/lpc-expanded-armor
  - https://opengameart.org/content/lpc-combat-armor-for-women
- Observações: adapted to v3 bases by bluecarrot16, reduced colors and climb/emote/jump by JaidynReiman / recolor of torso/armour/leather/female/brown.png, adapted to v3 bases by bluecarrot16, reduced colors and climb/emote/jump by JaidynReiman / adapted to v3 bases by bluecarrot16, teen version by JaidynReiman adapted from Male and Female versions

**Armadura de legionário** (`camisa_legionario_m.png e _f.png`)

- Origem no LPC: `spritesheets/torso/armour/legion/`
- Autores: Napsio (Vitruvian Studio), JaidynReiman, bluecarrot16, Nila122
- Licenças: OGA-BY 3.0, CC-BY-SA 3.0, GPL 2.0, GPL 3.0
- Links:
  - https://opengameart.org/content/lpc-roman-armor
- Observações: adapted to v3 bases by bluecarrot16, jump/run/sit/combat and teen by JaidynReiman, color reduction to 6 colors inspired by Napsio (Vitruvian Studio)

**Cardigã sem manga** (`camisa_cardiga_m.png e _f.png`)

- Origem no LPC: `spritesheets/torso/clothes/sleeveless/sleeveless2_cardigan/`
- Autores: ElizaWy, JaidynReiman, Stephen Challener (Redshrike), Johannes Sjölund (wulax)
- Licenças: OGA-BY 3.0
- Links:
  - http://opengameart.org/content/lpc-revised-character-basics
  - https://github.com/ElizaWy/LPC/tree/main/Characters/Clothing
  - https://opengameart.org/content/lpc-expanded-sit-run-jump-more
  - https://opengameart.org/content/lpc-expanded-simple-shirts
- Observações: original by ElizaWy; sleeveless adapted from original by JaidynReiman

### Calças

**Calça (masculina)** (`calca_pants_m.png`)

- Origem no LPC: `spritesheets/legs/pants/male/`
- Autores: bluecarrot16, JaidynReiman, ElizaWy, Matthew Krohn (makrohn), Johannes Sjölund (wulax), Stephen Challener (Redshrike)
- Licenças: OGA-BY 3.0, GPL 3.0, CC-BY-SA 3.0
- Links:
  - https://opengameart.org/content/liberated-pixel-cup-lpc-base-assets-sprites-map-tiles
  - https://opengameart.org/content/lpc-medieval-fantasy-character-sprites
  - https://opengameart.org/content/lpc-expanded-pants
- Observações: original male pants by wulax, recolors and edits to v3 base by bluecarrot16, climb/jump/run/sit/emotes/revised combat by JaidynReiman based on ElizaWy's LPC Revised

**Calça (feminina)** (`calca_pants_f.png`)

- Origem no LPC: `spritesheets/legs/pants/thin/`
- Autores: bluecarrot16, JaidynReiman, ElizaWy, Joe White, Matthew Krohn (makrohn), Johannes Sjölund (wulax), Stephen Challener (Redshrike)
- Licenças: OGA-BY 3.0, GPL 3.0, CC-BY-SA 3.0
- Links:
  - https://opengameart.org/content/liberated-pixel-cup-lpc-base-assets-sprites-map-tiles
  - https://opengameart.org/content/lpc-medieval-fantasy-character-sprites
  - http://opengameart.org/content/lpc-clothing-updates
  - https://opengameart.org/content/lpc-expanded-pants
- Observações: original male pants by wulax, edited for female by Joe White, recolors and edits to v3 base by bluecarrot16, teen legs by ElizaWy derived from base, climb/jump/run/sit/emotes/revised combat by JaidynReiman based on ElizaWy's LPC Revised

**Legging (masculina)** (`calca_legs_m.png`)

- Origem no LPC: `spritesheets/legs/leggings/male/`
- Autores: bluecarrot16, ElizaWy, JaidynReiman, Mandi Paugh, William.Thompsonj, Johannes Sjölund (wulax), Stephen Challener (Redshrike)
- Licenças: OGA-BY 3.0, GPL 3.0
- Links:
  - https://github.com/ElizaWy/LPC/tree/main/Characters/Clothing
  - https://opengameart.org/content/lpc-expanded-pants
- Observações: Original bases by Redshrike, thrust/shoot bases by Wulax, original overalls by ElizaWy, base animations adapted from v3 overalls by bluecarrot16, leggings by JaidynReiman

**Legging (feminina)** (`calca_legs_f.png`)

- Origem no LPC: `spritesheets/legs/leggings/thin/`
- Autores: bluecarrot16, ElizaWy, JaidynReiman, Mandi Paugh, William.Thompsonj, Johannes Sjölund (wulax), Stephen Challener (Redshrike)
- Licenças: OGA-BY 3.0
- Links:
  - http://opengameart.org/content/sara-wizard
  - https://opengameart.org/content/lpc-sara
  - https://opengameart.org/content/lpc-expanded-pants
- Observações: Original bases by Redshrike, thrust/shoot bases by Wulax, adapted from sara's leggings to v3 bases by bluecarrot16, jump/run/sit by JaidynReiman based on ElizaWy's and modified to match

**Saia de legionário (masculina)** (`calca_legion_m.png`)

- Origem no LPC: `spritesheets/legs/skirts/legion/male/`
- Autores: bluecarrot16, Nila122, JaidynReiman
- Licenças: OGA-BY 3.0, CC-BY-SA 3.0, GPL 2.0, GPL 3.0
- Links:
  - https://opengameart.org/content/lpc-roman-armor
  - http://opengameart.org/content/lpc-clothing-updates
  - https://opengameart.org/content/lpc-expanded-armour
- Observações: original by Nila122 to legion, adapted to v3 bases by bluecarrot16, climb/jump/sit/run by JaidynReiman

**Saia de legionário (feminina)** (`calca_legion_f.png`)

- Origem no LPC: `spritesheets/legs/skirts/legion/thin/`
- Autores: bluecarrot16, Nila122, JaidynReiman
- Licenças: OGA-BY 3.0, CC-BY-SA 3.0, GPL 2.0, GPL 3.0
- Links:
  - https://opengameart.org/content/lpc-roman-armor
  - http://opengameart.org/content/lpc-clothing-updates
  - https://opengameart.org/content/lpc-expanded-armour
- Observações: original by Nila122 to legion, adapted to v3 bases by bluecarrot16, climb/jump/sit/run by JaidynReiman

**Meia-calça** (`calca_meiacalca_m.png e _f.png`)

- Origem no LPC: `spritesheets/legs/hose/`
- Autores: JaidynReiman, ElizaWy, bluecarrot16, Johannes Sjölund (wulax), Stephen Challener (Redshrike)
- Licenças: OGA-BY 3.0, GPL 3.0
- Links:
  - https://github.com/ElizaWy/LPC/tree/main/Characters/Clothing
  - https://opengameart.org/content/lpc-expanded-pants
  - https://opengameart.org/content/lpc-expanded-sit-run-jump-more
- Observações: Original bases by Redshrike, thrust/shoot bases by Wulax, original overalls and hose by ElizaWy, base animations adapted from v3 overalls by bluecarrot16, male hose by JaidynReiman / Original bases by Redshrike, thrust/shoot bases by Wulax, original by ElizaWy, remaining frames by JaidynReiman

**Short** (`calca_short_m.png e _f.png`)

- Origem no LPC: `spritesheets/legs/shorts/short_shorts/`
- Autores: JaidynReiman, ElizaWy, Bluecarrot16, Johannes Sjölund (wulax), Stephen Challener (Redshrike)
- Licenças: OGA-BY 3.0, GPL 3.0
- Links:
  - https://github.com/ElizaWy/LPC/tree/main/Characters/Clothing
  - https://opengameart.org/content/lpc-expanded-pants
  - https://opengameart.org/content/lpc-expanded-sit-run-jump-more
- Observações: Original bases by Redshrike, thrust/shoot bases by Wulax, original overalls and shorts by ElizaWy, base animations adapted from v3 overalls by bluecarrot16, shorts by JaidynReiman / Original bases by Redshrike, thrust/shoot bases by Wulax, original shorts by ElizaWy, climb/jump/run/sit/emotes/revised combat by JaidynReiman

**Calça longa** (`calca_longa_m.png e _f.png`)

- Origem no LPC: `spritesheets/legs/pants2/`
- Autores: JaidynReiman, ElizaWy, Bluecarrot16, Johannes Sjölund (wulax), Stephen Challener (Redshrike)
- Licenças: OGA-BY 3.0, GPL 3.0
- Links:
  - https://github.com/ElizaWy/LPC/tree/main/Characters/Clothing
  - https://opengameart.org/content/lpc-expanded-sit-run-jump-more
  - https://opengameart.org/content/lpc-expanded-pants
- Observações: original overalls by ElizaWy, base animations adapted from v3 overalls by bluecarrot16, pants by JaidynReiman / Original bases by Redshrike, thrust/shoot bases by Wulax, original pants by ElizaWy, climb/jump/run/sit/emotes/revised combat by JaidynReiman

### Calçados

**Sapato (masculino)** (`pe_shoes_m.png`)

- Origem no LPC: `spritesheets/feet/shoes/basic/male/`
- Autores: JaidynReiman, bluecarrot16, Johannes Sjölund (wulax)
- Licenças: OGA-BY 3.0, CC-BY-SA 3.0, GPL 3.0
- Links:
  - https://opengameart.org/content/lpc-medieval-fantasy-character-sprites
  - http://opengameart.org/content/lpc-clothing-updates
  - https://opengameart.org/content/lpc-expanded-socks-shoes
- Observações: original by wulax, edited for v3 base by bluecarrot16, Jump/Sit/Emote/Run/Revised Combat by JaidynReiman

**Sapato (feminino)** (`pe_shoes_f.png`)

- Origem no LPC: `spritesheets/feet/shoes/basic/thin/`
- Autores: JaidynReiman, Joe White, Johannes Sjölund (wulax)
- Licenças: OGA-BY 3.0, CC-BY-SA 3.0, GPL 3.0
- Links:
  - https://opengameart.org/content/lpc-medieval-fantasy-character-sprites
  - http://opengameart.org/content/lpc-clothing-updates
  - https://opengameart.org/content/lpc-expanded-socks-shoes
- Observações: original by wulax, edited for female base by Joe White, edited for v3 base by bluecarrot16, Jump/Sit/Emote/Run/Revised Combat by JaidynReiman

**Sandália (masculina)** (`pe_sandals_m.png`)

- Origem no LPC: `spritesheets/feet/sandals/male/`
- Autores: Nila122, JaidynReiman, Matthew Krohn (makrohn), Johannes Sjölund (wulax)
- Licenças: OGA-BY 3.0, CC-BY-SA 3.0, GPL 2.0, GPL 3.0
- Links:
  - https://opengameart.org/content/lpc-roman-armor
  - http://opengameart.org/content/lpc-clothing-updates
  - https://opengameart.org/content/lpc-expanded-socks-shoes
- Observações: edited for v3 bases by bluecarrot16, Jump/Sit/Emote/Run/Revised Combat by JaidynReiman

**Sandália (feminina)** (`pe_sandals_f.png`)

- Origem no LPC: `spritesheets/feet/sandals/thin/`
- Autores: Nila122, JaidynReiman, Matthew Krohn (makrohn), Johannes Sjölund (wulax)
- Licenças: OGA-BY 3.0, CC-BY-SA 3.0, GPL 2.0, GPL 3.0
- Links:
  - https://opengameart.org/content/lpc-roman-armor
  - http://opengameart.org/content/lpc-clothing-updates
  - https://opengameart.org/content/lpc-expanded-socks-shoes
- Observações: edited for v3 bases by bluecarrot16, Jump/Sit/Emote/Run/Revised Combat by JaidynReiman

**Botas de armadura (masculinas)** (`pe_armour_m.png`)

- Origem no LPC: `spritesheets/feet/armour/plate/male/`
- Autores: Matthew Krohn (makrohn), Johannes Sjölund (wulax)
- Licenças: OGA-BY 3.0, CC-BY-SA 3.0, GPL 3.0
- Links:
  - https://opengameart.org/content/lpc-medieval-fantasy-character-sprites
- Observações: original by wulax, recolors by bigbeargames, edits for v3 base and recolors by bluecarrot16

**Botas de armadura (femininas)** (`pe_armour_f.png`)

- Origem no LPC: `spritesheets/feet/armour/plate/thin/`
- Autores: Matthew Krohn (makrohn), Johannes Sjölund (wulax)
- Licenças: OGA-BY 3.0, CC-BY-SA 3.0, GPL 3.0
- Links:
  - https://opengameart.org/content/lpc-medieval-fantasy-character-sprites
- Observações: original by wulax, recolors by bigbeargames, edits for v3 base and recolors by bluecarrot16

**Botas** (`pe_botas_m.png e _f.png`)

- Origem no LPC: `spritesheets/feet/boots/basic/`
- Autores: JaidynReiman, bluecarrot16, Nila122
- Licenças: OGA-BY 3.0, CC-BY-SA 3.0, GPL 2.0, GPL 3.0
- Links:
  - https://opengameart.org/content/lpc-clothes-and-hair
  - https://opengameart.org/content/lpc-expanded-socks-shoes
- Observações: original by Nila122, edited for male and v3 bases by bluecarrot16, Jump/Sit/Emote/Run/Revised Combat by JaidynReiman / original by Nila122, edited for v3 bases by bluecarrot16, Jump/Sit/Emote/Run/Revised Combat by JaidynReiman

**Pantufas** (`pe_pantufas_m.png e _f.png`)

- Origem no LPC: `spritesheets/feet/slippers/`
- Autores: bluecarrot16, JaidynReiman, Joe White, Luke Mehl
- Licenças: CC-BY-SA 3.0, GPL 3.0
- Links:
  - https://opengameart.org/content/thin-mage-clothing-set
  - http://opengameart.org/content/lpc-clothing-updates
  - https://opengameart.org/content/lpc-expanded-socks-shoes
- Observações: original by Luke Mehl, Joe White did thrust/shoot animations, edited for v3 bases and male sprites by bluecarrot16, Jump/Sit/Emote/Run/Revised Combat by JaidynReiman

**Meias tabi** (`pe_tabi_m.png e _f.png`)

- Origem no LPC: `spritesheets/feet/socks/tabi/`
- Autores: JaidynReiman, Stephen Challener (Redshrike), Johannes Sjölund (wulax)
- Licenças: OGA-BY 3.0+, CC-BY 3.0+, GPL 3.0
- Links:
  - https://opengameart.org/content/lpc-kimono-relm
  - https://opengameart.org/content/lpc-expanded-socks-shoes

### Batalha

Os inimigos e a espada foram montados pelo projeto a partir de várias camadas do LPC (corpo, cabeça e arma), já virados para o lado da luta e recortados em quadros de 128x64 px. Cada folha tem três linhas: parado, ataque e derrota. No orc e no lobisomem, o corpo humano foi pintado com a cor da cabeça do monstro.

**Espada longa do herói** (`arma_espada.png`)

- Origem no LPC: `spritesheets/weapon/sword/longsword/`
- Autores: Johannes Sjölund (wulax), bluecarrot16
- Licenças: OGA-BY 3.0, CC-BY-SA 3.0
- Links:
  - https://opengameart.org/content/lpc-medieval-fantasy-character-sprites
  - https://opengameart.org/content/lpc-extended-weapon-animations
- Observações: partes de trás e da frente da espada nas animações de parado, golpe (attack_slash) e derrota (hurt)

**Orc** (`inimigo_orc.png`)

- Origem no LPC: `spritesheets/body/bodies/male/`, `spritesheets/head/heads/orc/male/`, `spritesheets/weapon/blunt/waraxe/`
- Autores: bluecarrot16, JaidynReiman, Benjamin K. Smith (BenCreating), Evert, Eliza Wyatt (ElizaWy), TheraHedwig, MuffinElZangano, Durrani, Johannes Sjölund (wulax), Stephen Challener (Redshrike), Matthew Krohn (makrohn), Zi Ye, Sander Frenken (castelonia)
- Licenças: OGA-BY 3.0, CC-BY-SA 3.0, GPL 3.0
- Links:
  - https://opengameart.org/content/liberated-pixel-cup-lpc-base-assets-sprites-map-tiles
  - https://opengameart.org/content/lpc-medieval-fantasy-character-sprites
  - https://opengameart.org/content/lpc-male-jumping-animation-by-durrani
  - https://opengameart.org/content/lpc-runcycle-and-diagonal-walkcycle
  - https://opengameart.org/content/lpc-revised-character-basics
  - https://opengameart.org/content/lpc-be-seated
  - https://opengameart.org/content/lpc-runcycle-for-male-muscular-and-pregnant-character-bases-with-modular-heads
  - https://opengameart.org/content/lpc-jump-expanded
  - https://opengameart.org/content/lpc-character-bases
  - https://opengameart.org/content/four-characters-my-lpc-entries
  - https://opengameart.org/content/sinbad-the-ogre
  - https://opengameart.org/content/lpc-male-sheets
  - https://opengameart.org/content/lpc-medieval-weapons
- Observações: corpo masculino pintado de verde + cabeça de orc + machado de guerra (idle, slash e hurt)

**Esqueleto** (`inimigo_esqueleto.png`)

- Origem no LPC: `spritesheets/body/bodies/skeleton/`, `spritesheets/head/heads/skeleton/adult/`, `spritesheets/weapon/polearm/spear/`
- Autores: bluecarrot16, Napsio, JaidynReiman, Johannes Sjölund (wulax), Stephen Challener (Redshrike), Pierre Vigier (pvigier), Inboxninja
- Licenças: OGA-BY 3.0, CC-BY-SA 3.0, GPL 3.0
- Links:
  - https://opengameart.org/content/liberated-pixel-cup-lpc-base-assets-sprites-map-tiles
  - https://opengameart.org/content/lpc-medieval-fantasy-character-sprites
  - https://opengameart.org/content/lpc-skeleton
  - https://opengameart.org/content/lpc-character-bases
  - https://opengameart.org/content/lpc-spear-and-shovel-reworked
- Observações: corpo e cabeça de esqueleto + lança (idle, thrust e hurt)

**Lobisomem** (`inimigo_lobisomem.png`)

- Origem no LPC: `spritesheets/body/bodies/male/`, `spritesheets/head/heads/wolf/male/`
- Autores: bluecarrot16, JaidynReiman, Benjamin K. Smith (BenCreating), Evert, Eliza Wyatt (ElizaWy), TheraHedwig, MuffinElZangano, Durrani, Johannes Sjölund (wulax), Stephen Challener (Redshrike), Sander Frenken (castelonia), William.Thompsonj
- Licenças: OGA-BY 3.0, CC-BY-SA 3.0, GPL 3.0
- Links:
  - https://opengameart.org/content/liberated-pixel-cup-lpc-base-assets-sprites-map-tiles
  - https://opengameart.org/content/lpc-medieval-fantasy-character-sprites
  - https://opengameart.org/content/lpc-male-jumping-animation-by-durrani
  - https://opengameart.org/content/lpc-runcycle-and-diagonal-walkcycle
  - https://opengameart.org/content/lpc-revised-character-basics
  - https://opengameart.org/content/lpc-be-seated
  - https://opengameart.org/content/lpc-runcycle-for-male-muscular-and-pregnant-character-bases-with-modular-heads
  - https://opengameart.org/content/lpc-jump-expanded
  - https://opengameart.org/content/lpc-character-bases
  - https://opengameart.org/content/lpc-wolf-animation
  - https://opengameart.org/content/lpc-wolfman
- Observações: corpo masculino pintado de marrom + cabeça de lobo, atacando sem arma (idle, slash e hurt)

## Textura de pedra do fundo

- Arquivo no projeto: `app/src/main/res/drawable-mdpi/fundo_pedra.png`
- Original: "stone wall 4.png", do pacote "Wall, grass, rock, stone, wood and dirt (480)"
- Autor: West (enviado por qubodup) - OpenGameArt.org
- Licença: CC0 (domínio público; crédito opcional)

## Fonte

- Arquivo no projeto: `app/src/main/res/font/medieval.ttf`
- Fonte: MedievalSharp, por Wojciech Kalinowski
- Obtida em: Google Fonts (https://fonts.google.com/specimen/MedievalSharp)
- Licença: SIL Open Font License 1.1

## Licenças citadas

- CC0: https://creativecommons.org/publicdomain/zero/1.0/
- CC-BY 3.0: https://creativecommons.org/licenses/by/3.0/
- CC-BY 4.0: https://creativecommons.org/licenses/by/4.0/
- CC-BY-SA 3.0: https://creativecommons.org/licenses/by-sa/3.0/
- CC-BY-SA 4.0: https://creativecommons.org/licenses/by-sa/4.0/
- GPL 2.0: https://www.gnu.org/licenses/old-licenses/gpl-2.0.html
- GPL 3.0: https://www.gnu.org/licenses/gpl-3.0.html
- OGA-BY 3.0: https://opengameart.org/content/oga-by-30-faq
- SIL Open Font License 1.1: https://openfontlicense.org
