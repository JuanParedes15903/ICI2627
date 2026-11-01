# Uso de LLMs - Práctica 3

| | |
|---|---|
| **Grupo** | JPC_SBV_APF_DGR |
| **Modelo** | Claude Opus 5.5 (Claude Code) |

---

## 1. Traer las acciones de Ms. Pac-Man de la Práctica 2

> **Nosotros**
>
> En C:\MA\IA-Pacman, copia las acciones de Ms PacMan de la P2 al paquete de la P3 (practica3/grupo_JPC_SBV_APF_DGR/), junto con BaseAction, Funciones, Movimientos y MsPacManInput, cambiando el package y los imports. Si algún fichero ya existe en el destino, pregúntame antes de sobrescribirlo.
>
> En BaseAction, cambia "implements Action" por "implements RulesAction" y añade un parseFact(Fact actionFact) vacío. Deja getActionId como está. Las acciones no se tocan.
>
> No copies la FSM ni las transiciones. No toques MsPacManEngine. Sin comentarios y sin commit. Compila y dime los errores y la lista de ids de las acciones.
>
> Luego crea un prompts.md como el de la practica 2 y añade este como el primero.

> **Claude**
>
> Copia las 14 clases de `mspacman/actions` (las 12 acciones, `PacManAction` y `JugarSinPeligroAction`), `BaseAction`, `Funciones`, `Movimientos` y `MsPacManInput`, cambiando solo el package y los imports. `MsPacManInput` ya existía en la P3 (el del ejemplo de la wiki), así que pregunta antes de sobrescribirlo: le pedimos renombrar el de la wiki a `MsPacmanInputWiki` (con su uso en `MsPacMan.java`) y copiar también `BaseInput`, del que hereda el de la P2. Cambia `BaseAction` a `RulesAction` con un `parseFact` vacío. Compila sin errores y da la lista de ids, que salen del nombre de cada clase sin "Action".

## 2. Analizar la v3

> **Nosotros**
>
> Analiza la v3 de Ms. Pac-Man. Juega 300 partidas contra cada rival y dime:
> - qué reglas ganan más y cuáles casi nunca actúan;
> - por qué muere, agrupado por causa;
> - contra qué rival va peor;
> - si hay funciones que fallan, sobran o calculan de más.

> **Claude**
>
> Juega las 1500 partidas en un banco aparte del repo, con partidas repetibles, y apunta en cada turno qué regla gana y qué pasaba antes de cada muerte.
>
> - Reglas: `comerPasillo` gana el 40% de los turnos y `comboListo` el 12,8%. Las huidas casi nunca actúan.
> - Muertes: en el 85% Ms. Pac-Man llega a un cruce sin ninguna salida segura, porque al elegir pasillo solo mira hasta el siguiente cruce.
> - Rival: contra Avanzado va peor (4698 puntos y pierde las tres vidas).
> - Funciones: el Input se calculaba dos o tres veces por turno, `corridorCollisionTime` no veía al fantasma que venía de frente a mitad de pasillo, `getNearestPill` no tenía en cuenta la dirección, y `planEncierro` y otras no se usan (son de nuestros fantasmas de la P2).
