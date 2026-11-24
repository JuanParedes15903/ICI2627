# Diario Práctica 4 · Grupo Pr4APF\_DGR\_JPC\_SBV

Integrantes: Adrián Prieto Fernández, Diego González del Castillo Ramírez de Arellano, Juan Paredes Cruz y Sergio Barbas Vázquez  
Concurso: la v10.1 (mspacman-v10.1.fcl con MsPacManInputV4), que es la que carga MsPacMan().

* Rivales: los 5 fantasmas de referencia (Random, Aggressive, Basico, Intermedio y Avanzado). Desde la v2, también 4 fantasmas más elaborados, aparte: los nuestros de la Práctica 1 y la 2, GhostsEmboscada y los de la Práctica 3 de JPC\_SBV. En el concurso los fantasmas se parecerán más a estos.  
* Partidas: 300 por rival (150 en la v10, y menos en las pruebas rápidas). Semilla 1, y la 1001 para repetir cuando la mejora pasa el umbral por poco.  
* Métricas: puntos, turnos por vida y fantasmas comidos. La mejora es real si es mayor que el ruido entre partidas.  
* Versión de partida: nuestra MsPacMan de la Práctica 3.  
* Desde la v5 cada versión tiene dos variantes: x.0, sin la prudencia de la v4, y x.1, con ella.

# Ejecución

1. **jue 12 nov** \- Descargamos ICI-Practica4.zip. Del ejemplo copiamos MaxActionSelector, TrazaBorrosa, ProbarBorroso y Comparar.
2. **vie 13 nov** \- Copiamos de la Práctica 3 Funciones, Movimientos y las 12 acciones de MsPacMan. JugarSinPeligroAction pasa a implementar Action, sin parseFact. getActionId es el nombre de la clase sin Action, y toString devuelve getActionId.
3. **vie 13 nov** \- Pasamos MsPacManInput a FuzzyInput. Los hechos de la Práctica 3 pasan a 15 entradas numéricas.
4. **dom 15 nov** \- Hicimos mspacman-v00.fcl con las 6 reglas del ejemplo de la wiki, cambiadas a nuestras acciones.
5. **dom 15 nov** \- Adaptamos ProbarBorroso a la v0: 5 casos, todos bien.
6. **dom 15 nov** \- Prueba rápida de la v0 con 50 partidas por rival, para ver que juega sin excepciones.  
7. **mar 17 nov** \- Medimos la v0 contra la MsPacMan de la Práctica 3 (mspacman-v4.clp), 300 partidas por rival.  
8. **mar 17 nov** \- Analizamos la v0 en 200 partidas (40 por rival): qué acción gana, qué reglas se activan, por qué muere y en qué tramos pasa mucho tiempo sin comer.
9. **mar 17 nov** \- Hicimos la v1: PerseguirFantasmaComestibleMasCercano solo gana si llega a algún comestible. Casos en ProbarBorroso, traza y Comparar con las semillas 1 y 1001.
10. **mar 17 nov** \- Hicimos la v2: las salidas seguras se filtran con el encierro de nuestros fantasmas de la Práctica 2. Traza, muertes, tiempo por turno y Comparar con las semillas 1 y 1001.
11. **mar 17 nov** \- Medimos la v2 contra cuatro fantasmas elaborados (los nuestros de la Práctica 1 y la 2, GhostsEmboscada y los de la Práctica 3 de JPC\_SBV), aparte de la media.  
12. **mar 17 nov** \- Hicimos la v3: el encierro solo se usa con un fantasma peligroso cerca. Probamos 4 distancias (20, 30, 40 y 60) contra la v1, con 100 partidas por rival.
13. **mar 17 nov** \- Hicimos la v4: una salida borrosa, prudencia, decide cuándo usar el encierro según las vidas y el ritmo al que las pierde. Probamos 5 formas de calcularla y 3 umbrales, y medimos la elegida con las semillas 1 y 1001.
14. **mié 18 nov** \- Hicimos la v5.0 y la v5.1: perseguir según margenCaza, sin y con la prudencia de la v4. Casos en ProbarBorroso y traza de la v5.0.
15. **mié 18 nov** \- Medimos todas las versiones (v0 a v5.1) y la MsPacMan de la Práctica 3 contra los 9 fantasmas, con 300 partidas por rival y la semilla 1. La tabla está en el apartado Resumen de las versiones.
16. **mié 18 nov** \- Hicimos la v6 (no gastar pastillas de poder) y la v7 (huir solo con amenaza), cada una en .0 y .1 y partiendo de la v5. Preparamos la v8 con las dos. Casos en ProbarBorroso: todos bien en las 11 versiones (152 casos).
17. **mié 18 nov** \- Medimos la v6 y la v7 frente a la v5 contra los 9 fantasmas. La v6 mejora y la v7 empeora, así que no medimos la v8.
18. **mié 18 nov** \- Probamos por separado las 8 acciones que no tenían salida (variantes v9) con una criba rápida de 50 partidas por rival.
19. **mié 18 nov** \- Hicimos la v10 con las 5 acciones de la criba que no empeoran y la medimos frente a la v6 con 150 partidas por rival.

# Entradas

Se calculan en Java, en MsPacManInput, en cada turno. Si no hay fantasma o pastilla, la distancia vale 150.

| Entrada | Qué mide |
| :---- | :---- |
| peligro | Distancia del fantasma peligroso más cercano |
| amenazas | Fantasmas que hacen peligrosa alguna salida |
| salidasSeguras | Salidas seguras que tiene |
| comestible | Distancia al fantasma comestible más cercano |
| margenCaza | Turnos que le sobran para comerse un comestible (0 si no llega) |
| alcanzables | Comestibles a los que llega |
| cadena | Fantasmas que puede comerse seguidos |
| interceptar | Si llega al cruce al que va un comestible (0 o 1) |
| ppill | Distancia a la pastilla de poder más cercana |
| ventajaPPill | Turnos que llega antes que los fantasmas a esa pastilla de poder |
| combo | Fantasmas que se comería si se come ahora la pastilla de poder |
| enCrucePPill | Si está en el cruce del pasillo de la pastilla de poder (0 o 1) |
| ppillUnicaSalida | Si ir a la pastilla de poder es su única salida segura (0 o 1) |
| pasillos | Pasillos con pastillas que quedan |
| pasillosIguales | Si todas sus salidas tienen las mismas pastillas (0 o 1) |
| vidas, ritmo | Solo en las x.1: vidas que le quedan y turnos por muerte en la partida |

Al principio dejamos solo unas pocas acciones con salida en el .fcl para que fuera más sencillo: 3 hasta la v5 y 4 desde la v6. Al final añadimos más (v9 y v10).

Preferimos calcular en Java todo lo que depende del laberinto (caminos, cruces, quién llega antes) y dejar al sistema borroso solo la decisión. Así las reglas se leen fácil y, cuando algo falla, se ve enseguida si el problema está en la entrada o en la regla. Las entradas que valen 0 o 1 las tratamos como sí o no, con dos términos que no se solapan.

# Versión v0: las reglas del ejemplo

**Cambio -** Las 6 reglas del ejemplo de la wiki con nuestras acciones: huir si el fantasma está cerca, comer si no y perseguir si hay un comestible cerca.

**Casos -** 5, todos bien: nada cerca (come), peligroso a 10 (huye), peligroso a 45 (come), comestible a 10 sin peligro (persigue), comestible y peligroso a 10 (huye).

**Comparar -** (frente a la Práctica 3, referencia): puntos 11058 → 10580, −478 peor. Turnos por vida y fantasmas comidos, también peor.

**Otro -** Las 6 reglas se activan. Huir gana el 45 % de los turnos y comer el 44 %.

**Por qué muere.** MsPacMan no puede darse la vuelta (el codigo no lo tiene en cuenta y a veces lo intenta), y todas las muertes son de frente. El 68 % es llegar a un cruce donde los fantasmas ya han cortado todas las salidas: una salida cuenta como segura si llega al siguiente cruce antes que ellos, pero no se mira qué queda en ese cruce.

**Por qué pierde tiempo.** El 60 % de las partidas acaban por tiempo, así que ir lenta son puntos perdidos. No persigue comestibles a más de 35 pasos aunque llegue (come 1,65 fantasmas por pastilla de poder, cuando podría comerse 2,44), y el 21 % de las pastillas de poder se las come sin fantasmas que comer después.

**Plan.** De este análisis salieron las tres ramas sobre las que trabajamos en el resto de versiones: morir menos en los cruces (v2, v3 y v4), comer más fantasmas por pastilla de poder (v1, v5 y v6) y aprovechar las acciones de la Práctica 3 que la v0 no usaba (v9 y v10).

# Versión v1: perseguir solo si llega

**Cambio -** La regla de perseguir pide además margenCaza IS NOT ninguno. Antes, a veces ganaba perseguir sin tener a quién; la acción devolvía null y el motor la llevaba por cualquier salida.

**Casos -** 2 nuevos, bien: comestible al que no llega (come) y al que llega por 1 turno (persigue).

**Comparar -** (frente a v0, referencia): Semilla 1: puntos +43 ruido; turnos por vida +115 mejor. Semilla 1001: +156 ruido y +157 mejor.

**Otro -** Los null de perseguir bajan de 2674 a 0.

**Se queda:** vive más. En puntos no se nota, pero ya no hay turnos en los que MsPacMan va a cualquier sitio, y eso simplifica las siguientes versiones.

# Versión v2: encierro de la Práctica 2

**Cambio -** Solo en Java (MsPacManInputV2). Quita las salidas por las que los fantasmas pueden encerrarla, con la función de encierro de nuestros fantasmas de la Práctica 2.

**Comparar -** (frente a v1, referencia): Semilla 1: puntos −3042 peor; turnos por vida +781 mejor. Semilla 1001: igual. Contra los elaborados, −480 peor.

**Otro -** Muere la mitad (de 387 a 205), pero pasa menos niveles (de 257 a 140).

**Se descarta:** es demasiado prudente y deja de comer. Solo gana contra fantasmas que encierran, como los nuestros de la Práctica 2.

Aun así, llegamos a la conclusión de que el encierro sí evita muertes. El problema es usarlo siempre, así que las dos versiones siguientes buscan cuándo usarlo.

# Versión v3: encierro solo con un fantasma cerca

**Cambio -** Como la v2, pero solo con un fantasma a menos de cierta distancia. Probamos 20, 30, 40 y 60 pasos (100 partidas por rival).

**Comparar -** (frente a v1): con todas las distancias, peor en puntos (entre −1737 y −2894 contra los de referencia).

**Se descarta:** Con distancias cortas el encierro llega tarde y con distancias largas es casi igual a la v2. La distancia al fantasma no es suficiente para decidir cuándo ser prudente.

# Versión v4: prudencia según las vidas

**Cambio -** Una salida borrosa nueva, prudencia, decide cuándo usar el encierro: con 1 vida siempre, con 2 según lo rápido que las pierde y con 3 nunca. Si pasa de 4,5, las acciones usan solo las salidas en las que no la pueden encerrar. Entradas nuevas: vidas y ritmo. Probamos 5 formas de calcularla y nos quedamos con la de vidas y ritmo.

**Casos -** 5 nuevos de prudencia, bien. ProbarBorroso encontró un empate: la prudencia valía 5 justo con el umbral en 5. Lo bajamos a 4,5.

**Comparar -** (frente a v1): Semilla 1: referencia −277 ruido / elaborados +144 ruido, turnos por vida mejor. Semilla 1001: referencia −29 ruido / elaborados +287 mejor.

**Otro -** Las 11 reglas se activan.

**Se queda:** vive más y gana contra los elaborados.

La idea es que con muchas vidas compensa arriesgar y comer rápido, porque la partida suele acabar por tiempo, y con la última vida compensa no morir para maximizar puntos. Desde aquí para cada versión tenemos dos variantes para compararlas y ver cuánto cambia y cuál es mejor.

# Versión v5: perseguir según el margen

**Cambio -** Perseguir depende de margenCaza (justo u holgado) en lugar de la distancia. Con margen holgado persigue aunque el comestible esté lejos. 5 reglas en lugar de 1. La v5.0 parte de la v1 y la v5.1 de la v4.

**Casos -** 4 nuevos, bien (comestible lejos con mucho margen, con poco margen y 1 o 2 alcanzables, y con peligro cerca).

**Comparar -** (los 9 rivales): v5.0 frente a v1, +197 mejor; v5.1 frente a v4, +469 mejor. Fantasmas comidos, aprox +1,5 mejor.

**Otro -** Las 10 reglas se activan. Come 2,11 fantasmas por pastilla de poder.

**Se quedan:** Mejora obvia asi que sería absurdo no usarlo.

# Versión v6: no gastar pastillas de poder

**Cambio -** En el cruce de una pastilla de poder, si no hay fantasmas para comer después, espera (EsperarPastillaDePoderHastaCombo).

**Casos -** 3 nuevos, bien.

**Comparar -** (los 9 fantasmas): v6.0 frente a v5.0, +349 mejor; v6.1 frente a v5.1, +265 mejor.

**Otro -** Las pastillas de poder gastadas pasan del 21 % al 12 %.

**Se queda:** Donde más se nota es contra Basico, Intermedio y Aggressive: esperar a tenerlos cerca hace que cada pastilla de poder rinda más. Contra más elaborados no mejora tanto pero no hace daño.

# Versión v7: huir solo con amenaza

**Cambio -** Huir solo si alguna salida está en peligro; si no, comer.

**Casos -** 2 nuevos, bien.

**Comparar -** (los 9 fantasmas): v7.0 −611 peor; v7.1 −602 peor.

**Se descarta:** Nuestro huir también va a por los comestibles. Al cambiarlo por ir a la pastilla más cercana, come menos fantasmas. Empeora igual en las dos variantes, así que no es cosa de la prudencia, sino de la regla.

# Versión v8

La v6 y la v7 juntas. La preparamos a la vez que la v6 y la v7, pero como la v7 empeoraba no la medimos.

# Variantes v9: cada acción que faltaba, por separado

**Cambio -** Una variante por acción, sobre la v6.0 y la v6.1, con sus reglas. Prueba rápida de 50 partidas por rival, solo con las variantes .0 frente a la v6.0.

| Acción | Puntos, los 9 |
| :---- | :---- |
| InterceptarFantasmaComestible | +265 ruido |
| CazarEnCadena | +166 ruido |
| HuirHaciaPastillaDePoder | +83 ruido |
| IrAPorPastillaDePoder | +62 ruido |
| MuerteSegura | +62 ruido (vive más) |
| IrAPorLasUltimasPastillas | −222 ruido |
| ComerPasilloConMasPastillas | −342 ruido |
| HuirDeVariosFantasmas | −483 peor |

Las 8 se activan jugando. jFuzzyLogic solo admite 2 consecuentes por regla: si una regla cambia más salidas, hay que partirla en dos.

Hicimos 50 partidas para cada una. Con tan pocas casi todo queda dentro del ruido, así que no tomamos estas cifras como resultado, sino como filtro: descartamos las tres que bajan y probamos juntas las demás con más partidas.

# Versión v10: cinco acciones más

**Cambio -** Sobre la v6, las 5 acciones de la v9 que no empeoran: IrAPorPastillaDePoder, HuirHaciaPastillaDePoder, CazarEnCadena, MuerteSegura e InterceptarFantasmaComestible. Más 3 reglas para que no empaten entre ellas. Quedan 9 acciones con salida y 26 reglas (31 en la v10.1).

**Casos -** 9 nuevos, bien. En total, 203 casos en las 13 versiones, todos bien.

**Comparar -** (los 9, 150 partidas): v10.0 frente a v6.0, puntos +66 ruido y turnos por vida +129 mejor; v10.1 frente a v6.1, puntos +87 ruido y turnos por vida +144 mejor. La v10.1 contra la Práctica 3: +406 mejor.

**Otro -** Las 9 acciones ganan turnos.

**Se quedan:** vive más y el sistema borroso usa casi todas las entradas.

En puntos la mejora frente a la v6 queda dentro del ruido, pero en turnos por vida es clara en las dos variantes. Como en el concurso los fantasmas serán más parecidos a los elaborados, preferimos la versión que muere menos.

# Resumen de las versiones

Puntos por fantasma, semilla 1, de más difícil a más fácil.

| Fantasma | P3 | v0 | v1 | v2 | v3 | v4 | v5.0 | v5.1 | v6.0 | v6.1 | v7.0 | v7.1 | v10.0 | v10.1 |
| :---- | :---- | :---- | :---- | :---- | :---- | :---- | :---- | :---- | :---- | :---- | :---- | :---- | :---- | :---- |
| P2 nuestros | 5221 | 4526 | 4704 | 6086 | 4486 | 5724 | 4679 | 5933 | 4614 | 5977 | 4461 | 5465 | 4645 | 5993 |
| Avanzado | 5523 | 4704 | 5006 | 5213 | 4976 | 5141 | 4911 | 5289 | 5604 | 5916 | 4439 | 5095 | 5481 | 5401 |
| Emboscada | 7185 | 6074 | 6191 | 5850 | 6258 | 6355 | 7012 | 6875 | 7231 | 7404 | 6430 | 6482 | 6978 | 7223 |
| P1 nuestros | 5921 | 7587 | 7442 | 6317 | 6926 | 7260 | 7324 | 7182 | 6947 | 6796 | 7335 | 7205 | 7296 | 7159 |
| P3 JPC\_SBV | 7294 | 7839 | 7911 | 5996 | 6287 | 7671 | 7792 | 7768 | 8045 | 7936 | 7487 | 7256 | 8229 | 8070 |
| Random | 8470 | 8771 | 9168 | 8611 | 9303 | 9209 | 9340 | 9490 | 9279 | 9254 | 9438 | 9230 | 9463 | 9436 |
| Basico | 10853 | 9363 | 9533 | 7815 | 8529 | 8913 | 10343 | 9706 | 10890 | 10585 | 9839 | 9425 | 11186 | 10619 |
| Intermedio | 12224 | 11296 | 11423 | 6210 | 7169 | 11339 | 11573 | 12165 | 12599 | 12294 | 11113 | 11076 | 12780 | 12547 |
| Aggressive | 19186 | 18393 | 18622 | 10293 | 11454 | 16535 | 18801 | 17960 | 19710 | 18591 | 15732 | 15718 | 19455 | 19084 |
| **Media referencia (5)** | 11251 | 10505 | 10750 | 7628 | 8286 | 10227 | 10994 | 10922 | 11616 | 11328 | 10112 | 10109 | 11673 | 11417 |
| **Media elaborados (4)** | 6405 | 6506 | 6562 | 6062 | 5989 | 6752 | 6702 | 6940 | 6709 | 7028 | 6428 | 6602 | 6787 | 7111 |
| **Media de los 9** | 9097 | 8728 | 8889 | 6932 | 7265 | 8683 | 9086 | 9152 | 9435 | 9417 | 8475 | 8550 | 9501 | 9504 |
| Turnos por vida | 2416 | 1970 | 2114 | 2944 | 2571 | 2357 | 2034 | 2304 | 2151 | 2386 | 2019 | 2286 | 2280 | 2530 |
| Fantasmas comidos | 12,13 | 10,52 | 10,67 | 8,66 | 8,91 | 10,50 | 12,08 | 12,27 | 12,53 | 12,69 | 10,92 | 11,26 | 12,60 | 12,71 |

Las dos últimas filas son la media de los 9.

**Lo que se ve en la tabla.**

* La v2 y la v3 son las que más viven, pero las que menos puntos hacen: vivir más no sirve si la partida acaba por tiempo.
* Las x.1 ganan casi siempre contra los elaborados y contra nuestros fantasmas de la Práctica 2, y las x.0 contra los de referencia. Por eso, para el concurso, nos inclinamos por una x.1.
* La v10.1 es la que mejor media tiene contra los elaborados y una de las que más viven, sin perder puntos frente a la v6.
* Contra Aggressive las diferencias son muy grandes entre versiones; es el rival que más pesa en la media de referencia.

# Lo aprendido

* Medir antes de cambiar: analizar cada versión nos ahorró probar mejoras al azar y nos llevó a atacar los problemas que de verdad tenía.
* Una mejora pequeña en puntos suele ser ruido. Repetir con otra semilla nos evitó quedarnos con cambios que no mejoraban nada.
* ProbarBorroso sirve también para encontrar empates y reglas que no se activan, no solo para comprobar los casos.
* Muchas reglas pequeñas y claras funcionan mejor que pocas reglas grandes, y además se pueden probar una a una.

# Prompts

Están en prompts\_pacman.md.

# Concurso

Va la v10.1: es la mejor contra los fantasmas elaborados (7111) y la de mejor media de los 9 (9504), y supera a la Práctica 3 en 406 puntos.  
La v10.0 hace casi los mismos puntos, pero la v10.1 vive más (2530 turnos por vida frente a 2280), y en el concurso los fantasmas se parecerán más a los elaborados.
