# Uso de LLMs - Práctica 4

| | |
|---|---|
| **Grupo** | Pr4APF_DGR_JPC_SBV |
| **Modelo** | Claude Opus 5.5 (Claude Code) |

---

## 1. Pasar la Práctica 3 al esqueleto de la Práctica 4

> **Nosotros**
>
> Coge nuestra Ms. Pac-Man de la Práctica 3 y ponla en el esqueleto de la Práctica 4 (el ejemplo de ICI-Practica4.zip), en el paquete de la práctica 4:
> - copia del ejemplo MaxActionSelector, TrazaBorrosa, ProbarBorroso y Comparar (llámalo CompararPr4);
> - copia las 12 acciones de la P3 con Funciones y Movimientos, cambiando el package y los imports. Que implementen Action en vez de RulesAction;
> - pasa MsPacManInput a FuzzyInput: en vez de hechos de Jess, que dé cada entrada como un número. Si no hay fantasma o pastilla, la distancia vale 150.
>
> Las acciones no se tocan. No toques MsPacManEngine. No hagas commit. Compila y asegúrate de que funciona.

> **Claude**
>
> Copia las clases cambiando solo el package y los imports. Las 12 acciones implementan `Action` y su id es el nombre de la clase sin "Action". `MsPacManInput` pasa a tener 15 entradas numéricas: peligro, amenazas, salidasSeguras, comestible, margenCaza, alcanzables, cadena, interceptar, ppill, ventajaPPill, combo, enCrucePPill, ppillUnicaSalida, pasillos y pasillosIguales. Avisa de que la P3 del repo no compila (un import de `BaseInput` del paquete equivocado), así que compila la P4 aparte. Los `.fcl` tienen que estar en `bin/`, respecto a la carpeta desde la que se lanza.

## 2. v0: las reglas del ejemplo

> **Nosotros**
>
> Haz mspacman-v00.fcl con las 6 reglas del ejemplo de la wiki, pero con nuestras acciones:
> - huir (HuirDelFantasmaMasCercano) si el fantasma está cerca;
> - comer (PastillaMasCercana) si no;
> - perseguir (PerseguirFantasmaComestibleMasCercano) si hay un comestible cerca.
>
> Las demás acciones sin salida por ahora. Que MsPacMan() cargue la v0.
> Corre un test contra todos los fantasmas y dame una tabla con los resultados comparados con la practica 3.

> **Claude**
>
> Escribe el `.fcl` con las entradas peligro y comestible (cerca, media y lejos) y las 3 salidas (poco, algo y mucho, con COG). `MaxActionSelector` elige la acción con la salida más alta. Juega 300 partidas por fantasma con CompararPr4, semilla 1:
>
> | Fantasma | P3 | v0 |
> | :---- | ----: | ----: |
> | P2 nuestros | 5221 | 4526 |
> | Avanzado | 5523 | 4704 |
> | Emboscada | 7185 | 6074 |
> | P1 nuestros | 5921 | 7587 |
> | P3 JPC_SBV | 7294 | 7839 |
> | Random | 8470 | 8771 |
> | Basico | 10853 | 9363 |
> | Intermedio | 12224 | 11296 |
> | Aggressive | 19186 | 18393 |
> | **Media de los 9** | 9097 | 8728 |
> | Turnos por vida | 2416 | 1970 |
> | Fantasmas comidos | 12,13 | 10,52 |
>
> Contra los 5 de referencia, la v0 hace 478 puntos menos (umbral 287): peor. También vive menos y come menos fantasmas. Solo gana contra los nuestros de la Práctica 1, los de la Práctica 3 de JPC_SBV y Random.

## 3. Analizar la v0

> **Nosotros**
>
> Analiza la v0. Juega 40 partidas contra cada fantasma de referencia y dime:
> - Qué acción se ejecuta más turnos.
> - Con qué acción muere más, y qué acción eligió en el último cruce antes de morir.
> - Cuántos turnos antes de morir ya no tenía ninguna salida segura.
> - En qué pierde el tiempo: cuantos turnos seguidos pasa como máximo sin comer ninguna pastilla, cuantos fantasmas come con pastilla de poder y  cuantas pastillas de poder se usan y luego no come ningún fantasma.
> - En algún momento alguna acción da error o devuelve un valor inválido (como NULL).

> **Claude**
>
> Juega las 200 partidas en un banco aparte del repo y apunta en cada turno qué acción gana y qué salidas son seguras.
>
> - Acciones: huir gana el 46 % de los turnos, comer el 42 % y perseguir el 12 %.
> - Con qué muere: en el turno de la muerte siempre gana huir, porque ya no le queda ninguna salida segura. En el último cruce antes de morir ganaba huir en la mitad de las muertes, comer en un tercio y perseguir en el resto. En el 68 % de las muertes, al llegar a ese cruce los fantasmas ya habían cortado todas las salidas, y en un tercio había otra salida segura con más margen que la elegida.
> - Turnos antes de morir: lleva una mediana de 14 turnos sin ninguna salida segura (media 22). Cuando se queda atrapada, el fantasma está a unos 15 pasos. Solo el 6 % de las muertes son choques de 1 o 2 turnos.
> - Tiempo:
>   - Sin comer ninguna pastilla: el tramo más largo de cada partida es de unos 430 turnos de media. El peor llega a 3152 turnos, casi toda una partida contra Aggressive.
>   - Fantasmas: come 1,65 por pastilla de poder, cuando al comérsela tenía 2,44 a su alcance.
>   - Pastillas de poder gastadas: el 21 % se las come y luego no come ningún fantasma.
> - Errores: ninguna acción lanza excepciones, pero dos devuelven null. Huir, unas 8000 veces, cuando no hay ninguna salida segura. Perseguir, unas 3000, cuando hay comestibles pero no llega a ninguno a tiempo. Con null, el motor la lleva por cualquier salida.

## 4. v2: encierro de la Práctica 2

> **Nosotros**
>
> Haz la v2 solo en Java, con las reglas de la v1. Crea un MsPacManInputV2 que herede de MsPacManInput y quite las salidas por las que los fantasmas pueden encerrarla. Usa la función de encierro de nuestros fantasmas de la Práctica 2.
>
> Que MsPacMan pueda recibir qué Input usar. Compárala con la v1, con la semilla 1, contra los 5 de referencia y los 4 elaborados.

> **Claude**
>
> Pasa el encierro de la P2 a una interfaz `Encierro`, con `filtrarConEncierro`, que quita esas salidas. `MsPacManInputV2` la usa en `calcularMovimientos`. `MsPacMan` recibe ahora el `.fcl` y una función que crea el Input. Resultado contra los de referencia: −3042 puntos (umbral 262), peor, aunque vive más (+781 turnos por vida). Contra los elaborados, −480, peor. Muere la mitad (de 387 a 205 muertes), pero pasa muchos menos niveles (de 257 a 140).

## 5. v4: prudencia según las vidas

> **Nosotros**
>
> Queremos que el sistema borroso decida cuándo usar el encierro. Añade una salida nueva, prudencia (de 0 a 10):
> - con 1 vida, prudencia mucha;
> - con 2 vidas, según lo rápido que las pierde;
> - con 3 vidas, poca.
>
> Para eso, añade las entradas vidas y ritmo (turnos/muerte en la partida). Si la prudencia pasa de un umbral, las acciones solo usan las salidas en las que no la pueden encerrar.
>
> Despues prueba muchos valores posibles para el umbral y quedate con el mejor.

> **Claude**
>
> `MsPacManInputV4` añade las entradas vidas y ritmo, y calcula en cada turno las salidas normales y las prudentes (sin las que permiten encerrarla). Hace `SelectorConParametros`, que pasa la prudencia al Input antes de elegir la acción con `MaxActionSelector`: así el Input sabe qué salidas dar a las acciones. Escribe 5 reglas de prudencia: con 1 vida, mucha; con 2, mucha, algo o poca según el ritmo; con 3 o más, poca.
>
> Prueba los umbrales 4,5, 5 y 5,5, con 100 partidas por rival contra los 9 fantasmas:
> - En puntos no se distinguen (las diferencias, de −34 y −21, son ruido).
> - Con 4,5 vive más que con 5: +244 turnos por vida contra los de referencia y +239 contra los elaborados.
> - Con 5 hay un empate: con 2 vidas y un ritmo normal la prudencia vale 5,0 justo, y según el redondeo sale prudente o no.
>
> Se queda con 4,5. Resultado frente a la v1: en puntos, ruido contra los de referencia y mejor contra los elaborados con la semilla 1001 (+287). Además vive más.

## 6. v5: perseguir según el margen

> **Nosotros**
>
> Cambia la regla de perseguir para que dependa de margenCaza y no de la distancia. Divide margenCaza en justo y holgado:
> - con mucho margen, perseguir si es seguro.
> - con margen justo, solo si está cerca.
> - si hay varios comestibles alcanzables, perseguir al mas cercano
> - con peligro, perseguir poco.
>
> Haz la v5.0 sobre la v1 y la v5.1 sobre la v4, con sus casos en ProbarBorroso.

> **Claude**
>
> Cambia los términos de margenCaza (justo y holgado) y pasa de 1 regla de perseguir a 5:
> - margen holgado y sin peligro cerca: perseguir mucho, aunque el comestible esté lejos;
> - margen justo, comestible cerca y sin peligro cerca: perseguir mucho;
> - margen justo, comestible lejos y un solo alcanzable: perseguir poco;
> - varios alcanzables, algo de margen y sin peligro cerca: perseguir mucho. La acción ya va a por el más cercano;
> - peligro cerca: perseguir poco.
>
> Añade 4 casos: comestible lejos con mucho margen, con poco margen y 1 o 2 alcanzables, y con peligro cerca. Salen todos bien. Resultado contra los 9 rivales: v5.0 +197 (umbral 181) y v5.1 +469 (umbral 182), las dos mejor. Come unos 1,5 fantasmas más por partida.

## 7. v6: no gastar pastillas de poder

> **Nosotros**
>
> El 21 % de las pastillas de poder se las come sin fantasmas que comer después. Dale salida a EsperarPastillaDePoderHastaCombo y añade reglas:
> - si está en el cruce de una pastilla de poder, hay pocos fantasmas para el combo, no hay peligro cerca, no llega a ningún comestible y quedan muchos pasillos, esperar;
> - si hay muchos fantasmas para el combo, esperar poco.
>
> Haz la v6.0 sobre la v5.0 y la v6.1 sobre la v5.1, con sus casos, y dame los resultados de ambas contra los fantasmas.

> **Claude**
>
> Añade la salida y las 2 reglas a los dos `.fcl`. La acción ya venía de la P3: da vueltas cerca de la pastilla sin comérsela, y va a por ella cuando hay combo o después de 150 turnos. Añade 3 casos, todos bien. Puntos con 300 partidas por fantasma, semilla 1:
>
> | Fantasma | v5.0 | v6.0 | v5.1 | v6.1 |
> | :---- | ----: | ----: | ----: | ----: |
> | P2 nuestros | 4679 | 4614 | 5933 | 5977 |
> | Avanzado | 4911 | 5604 | 5289 | 5916 |
> | Emboscada | 7012 | 7231 | 6875 | 7404 |
> | P1 nuestros | 7324 | 6947 | 7182 | 6796 |
> | P3 JPC_SBV | 7792 | 8045 | 7768 | 7936 |
> | Random | 9340 | 9279 | 9490 | 9254 |
> | Basico | 10343 | 10890 | 9706 | 10585 |
> | Intermedio | 11573 | 12599 | 12165 | 12294 |
> | Aggressive | 18801 | 19710 | 17960 | 18591 |
> | **Media de los 9** | 9086 | 9435 | 9152 | 9417 |
> | Turnos por vida | 2034 | 2151 | 2304 | 2386 |
> | Fantasmas comidos | 12,08 | 12,53 | 12,27 | 12,69 |
>
> Contra los 9: v6.0 +349 (umbral 188) y v6.1 +265 (umbral 188), las dos mejor. Las pastillas de poder gastadas bajan del 21 % al 12 %.

## 8. Analizar la v6

> **Nosotros**
>
> Analiza la v6.0. Juega 40 partidas contra cada fantasma de referencia y dime:
> - Qué acción se ejecuta más turnos.
> - Con qué acción muere más, y qué acción eligió en el último cruce antes de morir.
> - Cuántos turnos antes de morir ya no tenía ninguna salida segura.
> - En qué pierde el tiempo: cuantos turnos seguidos pasa como máximo sin comer ninguna pastilla, cuantos fantasmas come con pastilla de poder y  cuantas pastillas de poder se usan y luego no come ningún fantasma.
> - En algún momento alguna acción da error o devuelve un valor inválido (como NULL).

> **Claude**
>
> Juega las 200 partidas en el mismo banco que la v0.
>
> - Acciones: huir gana el 49 % de los turnos, comer el 29 %, perseguir el 22 % y esperar el 0,1 %.
> - Con qué muere: en el turno de la muerte siempre gana huir, porque ya no le queda ninguna salida segura. Justo antes de quedarse sin salidas ganaba huir en el 83 % de las muertes, comer en el 16 % y perseguir en el resto.
> - Turnos antes de morir: lleva una mediana de 14 turnos sin ninguna salida segura (media 23). Cuando se queda atrapada, el fantasma está a unos 14 pasos.
> - Tiempo:
>   - Sin comer ninguna pastilla: el tramo más largo de cada partida es de unos 490 turnos de media. El peor llega a 3204 turnos, contra Intermedio.
>   - Tramos de 40 turnos o más sin comer nada: en unos 1100 va huyendo casi todo el rato, muchas veces sin que ningún fantasma amenace sus salidas. Huir se activa solo por la distancia, aunque el fantasma no pueda cortarle el paso.
>   - Fantasmas: come 2,25 por pastilla de poder (1,65 en la v0).
>   - Pastillas de poder gastadas: el 12 % (el 21 % en la v0).
> - Errores: ninguna acción lanza excepciones. Solo huir devuelve null, unas 8400 veces, cuando no hay ninguna salida segura. Perseguir ya no devuelve null.

## 9. v7: huir solo con amenaza

> **Nosotros**
>
> Haz la v7: que huir dependa también de amenazas. Si el fantasma está cerca pero no amenaza ninguna salida, que coma en vez de huir. Haz la v7.0 y la v7.1 sobre la v5, con sus casos, y compáralas.

> **Claude**
>
> Añade "amenazas IS NOT ninguna" a las reglas de huir y una regla nueva: con peligro cerca y sin amenazas, comer mucho y huir algo. Añade 2 casos, bien. Huir pasa de la mitad de los turnos al 2 %. Resultado contra los 9: v7.0 −611 (umbral 181) y v7.1 −602 (umbral 177), las dos peor. Explica por qué: nuestro HuirDelFantasmaMasCercano también va a por los comestibles. Al cambiarlo por PastillaMasCercana come menos fantasmas: 1,92 por pastilla de poder, frente a 2,18 en la v6.

## 10. v9: cada acción que faltaba, por separado

> **Nosotros**
>
> Quedan 8 acciones sin salida en el .fcl. Haz una variante por acción sobre la v6 (v9a, v9b...), cada una con su salida y sus reglas, para ver cuáles ayudan. Haz también la versión .1 de cada una. Pruébalas rápido, con 50 partidas por rival contra los 9 rivales. Dime en qué porcentaje de turnos gana cada acción nueva y si alguna da error o devuelve NULL

> **Claude**
>
> Hace las 8 variantes (16 `.fcl`). Las 8 acciones ganan turnos, de ComerPasilloConMasPastillas (19 %) a HuirHaciaPastillaDePoder (0,4 %). Ninguna lanza excepciones, pero HuirDeVariosFantasmas devuelve null 200 veces e IrAPorLasUltimasPastillas 52. Con 50 partidas el umbral es de unos 350 puntos:
> - Dan ruido InterceptarFantasmaComestible (+265), CazarEnCadena (+166), HuirHaciaPastillaDePoder (+83), IrAPorPastillaDePoder (+62), MuerteSegura (+62, vive más), IrAPorLasUltimasPastillas (−222) y ComerPasilloConMasPastillas (−342).
> - HuirDeVariosFantasmas (−483) empeora.
>
> Avisa de que jFuzzyLogic solo admite 2 consecuentes por regla: si una regla cambia más salidas, hay que partirla en dos.

## 11. v10: juntar las acciones que no empeoran

> **Nosotros**
>
> Haz la v10 sobre la v6 con las 5 acciones de la v9 que no empeoran: IrAPorPastillaDePoder, HuirHaciaPastillaDePoder, CazarEnCadena, MuerteSegura e InterceptarFantasmaComestible. Si al juntarlas empatan entre ellas, añade reglas para desempatar. Haz la v10.0 y la v10.1, con sus casos, y compáralas con la v6 con 150 partidas por rival. Compara también la v10.1 con la Práctica 3.

> **Claude**
>
> Junta las reglas de las 5 variantes y añade 3 para que no empaten:
> - si ir a la pastilla de poder es la única salida segura, gana HuirHaciaPastillaDePoder;
> - con una cadena larga, CazarEnCadena gana a InterceptarFantasmaComestible;
> - sin salidas seguras, solo gana MuerteSegura.
>
> Quedan 9 acciones con salida y 26 reglas (31 en la v10.1). Añade 9 casos y salen todos bien (203 en total en las 13 versiones). Resultado frente a la v6: v10.0 +66 y v10.1 +87 en puntos, ruido, pero las dos viven más (+129 y +144 turnos por vida). La v10.1 frente a la Práctica 3: +406 (umbral 234), mejor.
