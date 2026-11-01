# **Diario Práctica 3 Grupo JPC\_SBV\_APF\_DGR**

Integrantes: Adrián Prieto Fernández y Diego González del Castillo Ramírez de Arellano  
Versión a concurso: MsPacMan con mspacman-v4.clp.

# **Ejecución**

1. **vie 30 oct** \- Copiamos de la Práctica 2 el Input, las funciones comunes (Funciones y Movimientos) y las 12 acciones de MsPacMan. BaseAction pasa a implementar RulesAction, con un parseFact vacío.  
2. **vie 30 oct** \- Pasamos la máquina de estados de MsPacMan a reglas en mspacman-v1.clp: 13 reglas, una por acción, ordenadas por bloques de prioridad.  
3. **vie 30 oct** \- Añadimos getFacts() a MsPacManInput, que pasa a ser un RulesInput, y registramos las 12 acciones en MsPacMan.  
4. **vie 30 oc**t \- Adaptamos ProbarReglas a la v1: 14 casos, todos bien.  
5. **vie 30 oct** \- Contamos cuántas veces se dispara cada regla y cuántas gana cada acción.  
6. **vie 30 oct** \- Comparamos la v1 con la MsPacMan de la Práctica 2\.  
7. **dom 1 nov** \- Hicimos la v2, con un hecho FANTASMA por fantasma, y probamos cuatro variantes: con y sin rematar, y con su cálculo de si llega a un comestible o con el exacto.  
8. **dom 1 nov** \- Hicimos la v3 con lo mejor de la v1 y de la v2. Probamos una a una las constantes (K1-K4) y las mejoras de funciones (F1-F6). Nos quedamos con K1, K2, F1 y F4.  
9. **dom 1 nov** \- Analizamos la v3 en 1500 partidas: qué reglas se disparan, qué acciones ganan y la causa de cada muerte.  
10. **dom 1 nov** \- Probamos una a una las mejoras que salieron del análisis y pasamos a la v4: el Input una sola vez por turno, getNearestPill con dirección, corridorCollisionTime con el fantasma que viene de frente y sin la regla comboListo.  
11. **dom 1 nov** \- ProbarReglas para la v3 y la v4: 17 casos cada una.  
12. **dom 1 nov** \- Medimos v1, v3 y v4 con la semilla 1 y con la 1001\.

# **Métricas**

Medimos siempre con Comparar, contra los mismos rivales y con las mismas semillas, 300 partidas por rival:

* Rivales: los cinco fantasmas de referencia (Random, Aggressive, Basico, Intermedio y Avanzado).  
* Versión de partida: nuestra MsPacMan de la Práctica 2 (máquina de estados). No usamos la v0 del ejemplo.

Métricas que usamos:

* Media de puntos por partida y error típico.  
* Turnos por vida.  
* Fantasmas comidos por partida.  
* Veces que ocurre cada regla y veces que gana cada acción, con TrazaReglas.

En el banco añadimos como sexto rival nuestros fantasmas de la Práctica 2, que intentan encerrar a Ms. Pac-Man. Su resultado va aparte y no entra en la media de los cinco.

# **Versión v1: la máquina de estados de la Práctica 2 pasada a reglas**

Reutilizamos el Input y las acciones de la Práctica 2 sin cambiar los cálculos. Los estados y las 17 transiciones se convierten en 13 reglas, una por acción, ordenadas por bloques de prioridad:

* Lo normal (10): PastillaMasCercana, que se cumple siempre.  
* Oportunidades (20-59): ComerPasilloConMasPastillas, IrAPorLasUltimasPastillas y EsperarPastillaDePoderHastaCombo.  
* Ataque (60-79), solo si no hay fantasmas peligrosos cerca: PerseguirFantasmaComestibleMasCercano, InterceptarFantasmaComestible, CazarEnCadena e IrAPorPastillaDePoder cuando hay combo.  
* Supervivencia (80-99): HuirDelFantasmaMasCercano, HuirDeVariosFantasmas, IrAPorPastillaDePoder si llega antes que los fantasmas, HuirHaciaPastillaDePoder y MuerteSegura.

ProbarReglas: 14 casos, uno por regla y varios en los que se cumplen varias a la vez (interceptar frente a perseguir, comestibles con un peligroso cerca, muerte segura frente a huir). Todos BIEN.

# **Tests**

Comparar, 300 partidas por rival y las mismas semillas para las dos versiones.  
**Puntos:**

| Rival | Práctica 2 | v1 | D | 2·ET\_D |
| :---- | :---- | :---- | :---- | :---- |
| Random | 9079 ± 100 | 9847 ± 109 | \+768 | 296 |
| Aggressive | 13597 ± 311 | 14186 ± 369 | \+589 | 966 |
| Basico | 9059 ± 146 | 9628 ± 170 | \+569 | 448 |
| Intermedio | 8511 ± 203 | 9433 ± 228 | \+922 | 610 |
| Avanzado | 4144 ± 99 | 4353 ± 89 | \+209 | 267 |
| **Media de los cinco** | **8878 ± 85** | **9489 ± 97** | **\+611** | **258** |

La v1 es mejor contra 3 rivales y 2 (por ruido). Mejora real.  
**Turnos por vida:**

| Rival | Práctica 2 | v1 | D | 2·ET\_D |
| :---- | :---- | :---- | :---- | :---- |
| Random | 3168 ± 68 | 2967 ± 72 | −201 | 198 |
| Aggressive | 3381 ± 66 | 3253 ± 69 | −128 | 191 |
| Basico | 2068 ± 74 | 1947 ± 74 | −121 | 209 |
| Intermedio | 2317 ± 83 | 2531 ± 81 | \+214 | 232 |
| Avanzado | 679 ± 24 | 743 ± 37 | \+64 | 87 |
| **Media de los cinco** | **2323 ± 30** | **2288 ± 31** | **−35** | **85** |

La v1 es peor contra Random y los otros 4 (ruido). Ruido  
**Fantasmas comidos:**

| Rival | Práctica 2 | v1 | D | 2·ET\_D |
| :---- | :---- | :---- | :---- | :---- |
| Random | 10,63 ± 0,16 | 13,09 ± 0,17 | \+2,46 | 0,47 |
| Aggressive | 16,05 ± 0,36 | 18,44 ± 0,44 | \+2,40 | 1,14 |
| Basico | 11,15 ± 0,20 | 13,11 ± 0,22 | \+1,95 | 0,60 |
| Intermedio | 10,68 ± 0,26 | 12,67 ± 0,29 | \+1,99 | 0,77 |
| Avanzado | 4,96 ± 0,16 | 6,20 ± 0,14 | \+1,24 | 0,42 |
| **Media de los cinco** | **10,69 ± 0,11** | **12,70 ± 0,12** | **\+2,01** | **0,33** |

La v1 es mejor contra todos.

# **Traza de la v1**

10 partidas contra los fantasmas del ejemplo de la wiki (36312 turnos). Veces que se dispara cada regla y, entre paréntesis, veces que gana su acción:

* Lo norma:  
  * pastillaMasCercana 36312 (273)  
  * comerPasillo 35515 (12345)  
  * ultimasPastillas 4054 (1270)  
  * esperarCombo 328 (296).  
* Ataque:  
  * perseguirComestible 8132 (3664)  
  * interceptar 3257 (1751)  
  * cazarEnCadena 2897 (2897)  
  * comboListo 2613 y correrALaPastilla 2339 (4914 entre las dos, son la misma acción).  
* Supervivencia:  
  * huirDelMasCercano 10084 (8102)  
  * huirDeVarios 1001 (576)  
  * muerteSegura 223 (223)  
  * huirHaciaPastilla 1 (1).

Se disparan las 13 reglas y ningún turno se queda sin acción, pero huirHaciaPastilla solo se dispara una vez. comerPasillo se cumple casi siempre, porque los pasillos casi nunca tienen las mismas pastillas, así que PastillaMasCercana prácticamente solo se ejecuta cuando no quedan pastillas cerca.

# **Versión v2: un hecho por fantasma**

16 reglas. Los datos de los fantasmas salen de MSPACMAN (en getFacts) y pasan a un FANTASMA por cada fantasma (comestible, distancia y tiempo). Las reglas peligro y variosPeligros crean los hechos PELIGRO y VARIOSPELIGROS, que usan las demás reglas.  
Regla nueva: rematar: ir a por un comestible a menos de 10 aunque haya peligro.  
Para probarla, a los fantasmas de la cárcel les dimos distancia 1000 (si no, parecen pegados a Ms. Pac-Man). Probamos cuatro variantes: con y sin rematar, y con el cálculo de la v2 para saber si llega a un comestible (tiempo \> distancia) o con el exacto de la Práctica 2 (2·(d − EAT\_DISTANCE) \< tiempo).  
**Media de los cinco, frente a la v1 (300 partidas por rival):**

| Versión | Reglas | Puntos | Turnos por vida | Fantasmas comidos |
| :---- | :---- | :---- | :---- | :---- |
| v1 | 13 | 9527 | 2286 | 12,76 |
| v2 tal cual | 16 | 9110, peor | 2184, peor | 12,27, peor |
| v2 sin rematar | 15 | 9316, ruido | 2111, peor | 12,45, ruido |
| v2 con el cálculo exacto | 16 | 9640, ruido | 2236, ruido | 12,91, ruido |
| v2 exacta sin rematar | 15 | 9683, ruido | 2316, ruido | 12,96, ruido |

El cálculo exacto es una mejora real frente al de la v2 (+530 puntos con 2·ET\_D \= 274, y \+0,64 fantasmas). Quitar rematar no se distingue. Ninguna variante supera a la v1, así que la v2 se descarta tal cual y nos quedamos con sus ideas para la v3.

# **Versión v3: lo mejor de la v1 y de la v2**

16 reglas. De la v2, los hechos FANTASMA y los hechos intermedios PELIGRO y VARIOSPELIGROS. De la v1, las acciones y las reglas de pastillas y de ataque. Además:

* Cálculo exacto para saber si llega a un comestible.  
* La distancia de un fantasma en la cárcel es lo que le falta para salir (getGhostLairTime) más la distancia desde la salida (getGhostInitialNodeIndex), como en ghostTimeTo.

Después, probamos a sustituir todas las constantes por un cálculo exacto en vez de un umbral y todas las funciones que se podían hacer exactas se probaron  
300 partidas por rival, media de los cinco:  
**Constantes (K) y funciones (F):**

| Cambio | Antes | Ahora | Puntos | Turnos/vida | Fantasmas comidos | Se aplica el cambio |
| :---- | :---- | :---- | :---- | :---- | :---- | :---- |
| K2 pastilla cerca | a menos de 20 | en el cruce de la pastilla | \+373 (271) mejor | \+16 (86) | \+0,90 (0,35) mejor | Sí |
| K1 peligro | distancia \< 15 | un fantasma hace mortal alguna salida | \+104 (272) | \+41 (82) | \+0,12 (0,35) | Sí |
| K3 combo | 3 fantasmas | todos que no están en la cárcel | −1450 (273) peor | −88 (84) peor | −2,37 (0,35) peor | No |
| K4 espera máxima | 150 ticks | lo que tarda el fantasma más lejano | 0 | 0 | 0 | No |
| F1 llegar a un comestible | 2·(d − EAT\_DISTANCE) \< tiempo | tiempoDeChoque | \+109 (270) | −11 (83) | \+0,23 (0,35) | Sí |
| F2 peligro | distancia actual | la mitad si va de frente | −182 (264) | −18 (84) | −0,24 (0,34) | No |
| F3 combo | distancia actual | ghostTimeTo menos lo que tarda ella | −234 (257) | \+45 (83) | −0,29 (0,33) | No |
| F4 cadena | 2·(d − EAT\_DISTANCE) | tiempoDeChoque en el primero | \+119 (269) | −5 (84) | \+0,17 (0,35) | Sí |
| F5 llega antes a la pastilla | solo a la pastilla | todo el camino | −17 (267) | \+4 (83) | −0,02 (0,35) | No |
| F6 huir de varios | comerSeguro | por margen | −832 (263) peor | \+174 (85) mejor | −1,04 (0,34) | No |

K2 es la única mejora real por sí sola. Nos quedamos también con K1, que quita la constante 15 sin empeorar, y con F1 y F4, que son más exactas y mejoran en general.  
La v3 final (K2, K1, F1 y F4) frente a la v3 con solo K2: \+250 puntos, \+82 turnos por vida (81) y \+0,42 fantasmas (0,36). Vive más y come más.

# **Análisis de la v3**

Hecho por Claude:  
1500 partidas con la traza enganchada, 300 contra cada rival:

* comerPasillo gana el 40% de los turnos: en mitad de un pasillo, con un solo movimiento posible, los pasillos nunca tienen las mismas pastillas. comboListo gana el 12,8%.  
* Las huidas casi no actúan (entre el 0,1% y el 0,5% de los turnos). Cuando hay peligro, muerteSegura les gana más veces de las que ganan ellas: el peligro llega tarde.  
* Muertes (2621): en el 85% Ms. Pac-Man llega a un cruce sin ninguna salida segura. Como no puede darse la vuelta, la trampa empieza antes, al elegir un pasillo: sureDeath y corridorCollisionTime solo miran el siguiente cruce. Ninguna muerte se debe a una acción que devuelva null.  
* Contra Random y Aggressive casi todas las partidas acaban por tiempo (94-97%), así que ahí solo se gana comiendo más rápido. Contra Avanzado pierde las tres vidas en unos 2000 ticks. Contra nuestros fantasmas de la Práctica 2, el 95% de las muertes son encierros.  
* El Input se calculaba dos o tres veces por turno, porque clasificarMovimientos creaba otro MsPacManInput en cada acción. Y corridorCollisionTime no veía al fantasma que venía de frente cuando Ms. Pac-Man estaba a mitad de pasillo.  
* planEncierro y planEncierroPosible no los usa Ms. Pac-Man: son de nuestros fantasmas de la Práctica 2, que los usan para encerrarla, y llegaron con Funciones.

# **Versión v4**

15 reglas. La v3 con cuatro cambios, probados uno a uno en el banco:

* El Input se calcula una vez por turno(antes ocurría varias veces por accidente) y las acciones lo reutilizan. Juega exactamente igual y el turno tarda menos.  
* getNearestPill mide la distancia con dirección (getShortestPathDistance con el último movimiento), sin darse la vuelta (antes no lo hacía).  
* corridorCollisionTime ve al fantasma que viene de frente también a mitad de pasillo (antes no lo hacía).  
* Sin la regla comboListo: con combo ya no va directa a la pastilla de poder y sigue comiendo (mejora clara).

**Pruebas en el banco, una a una (300 partidas por rival). D frente a la versión anterior, media de los cinco, y los fantasmas de la Práctica 2 aparte:**

| Cambio | Puntos | Turnos/vida | Fantasmas | Puntos contra P2 | Se queda |
| :---- | :---- | :---- | :---- | :---- | :---- |
| getNearestPill con dirección | \+213 (284) | −88 (79) peor | \+0,24 (0,37) | \+351 (341) mejor | Sí |
| Fantasma de frente en corridorCollisionTime | \+3 (286) | \+111 (78) mejor | 0,00 (0,37) | \+41 (329) | Sí |
| Las dos juntas | \+251 (284) | \+39 (77) | \+0,29 (0,37) | \+288 (339) | Sí |
| Sin comboListo, sobre las dos | \+699 (295) mejor | \+59 (78) | \+0,47 (0,37) mejor | −216 (342) | Sí |
| Evitar las salidas donde pueden encerrarla, si hay otra | −2110 (256) peor | \+701 (71) mejor | −2,65 (0,34) peor | \+945 (377) mejor | No |
| Lo mismo, sin excepción | −2462 (260) peor | −152 (82) peor | −3,13 (0,34) peor | \+596 (361) mejor | No |
| Mirar dos pasillos, si hay otra salida | −1086 (271) peor | \+611 (73) mejor | −1,52 (0,36) peor | sin medir | No |
| Mirar dos pasillos, solo con peligro | −193 (283) | \+162 (79) mejor | −0,24 (0,37) | \+153 (345) | No |
| Combo con 2 fantasmas | −371 (274) peor | −124 (80) peor | −0,38 (0,36) peor | sin medir | No |
| Combo con 4 fantasmas | −182 (296) | −12 (79) | −0,63 (0,38) peor | sin medir | No |
| Pocos pasillos \= 3 | −314 (287) peor | \+42 (78) | −0,41 (0,38) peor | sin medir | No |
| Pocos pasillos \= 10 | \+92 (285) | −4 (79) | \+0,10 (0,37) | sin medir | No |
| Combo con la vuelta de los fantasmas | −403 (295) peor | \+178 (77) mejor | −1,51 (0,38) peor | −684 (336) peor | No |
| ghostSteps exacto en corridorCollisionTime | 0 | 0 | 0 | 0 | No |

Evitar las salidas donde los fantasmas pueden encerrarla (planDesde y crearRama, las funciones de encierro de nuestros fantasmas de la Práctica 2, al revés) es lo único que ataca la causa principal de muerte. Contra los fantasmas de la Práctica 2 mejora en todo, pero contra los de referencia se vuelve demasiado prudente y pierde más de 2000 puntos. Queda pendiente probarla solo en algunos casos.  
La v4 en el banco: 11016 puntos y 14,61 fantasmas de media contra los cinco (Random 8274, Aggressive 19032, Basico 10801, Intermedio 11510, Avanzado 5463), y 4977 puntos contra los fantasmas de la Práctica 2\.  
ProbarReglas: 17 casos para la v3 y los mismos para la v4, con el del combo cambiado: en la v3 se come la pastilla y en la v4 sigue comiendo. Los 34 dan BIEN, y entre todos se disparan las 15 reglas de la v4.

# **Tests con Comparar: v1, v3 y v4**

Comparar, 300 partidas por rival. Fila de la media de cada métrica y recuento de rivales, tal cual los saca, con la semilla 1 y repitiendo con la 1001:  
**Puntos:**

| Comparación | Antes | Después | D | 2·ET\_D | En media | Rivales mejor / peor / ruido |
| :---- | :---- | :---- | :---- | :---- | :---- | :---- |
| v3 frente a v1, semilla 1 | 10498 | 10384 | −114 | 269 | ruido | 0 / 1 / 4 |
| v3 frente a v1, semilla 1001 | 10550 | 10508 | −43 | 266 | ruido | 2 / 1 / 2 |
| v4 frente a v3, semilla 1 | 10384 | 11083 | \+699 | 286 | mejor | 3 / 1 / 1 |
| v4 frente a v3, semilla 1001 | 10508 | 11433 | \+925 | 290 | mejor | 3 / 0 / 2 |

**Turnos por vida:**

| Comparación | Antes | Después | D | 2·ET\_D | En media | Rivales mejor / peor / ruido |
| :---- | :---- | :---- | :---- | :---- | :---- | :---- |
| v3 frente a v1, semilla 1 | 2260 | 2523 | \+263 | 80 | mejor | 5 / 0 / 0 |
| v3 frente a v1, semilla 1001 | 2260 | 2514 | \+253 | 81 | mejor | 3 / 0 / 2 |
| v4 frente a v3, semilla 1 | 2523 | 2595 | \+73 | 80 | ruido | 0 / 0 / 5 |
| v4 frente a v3, semilla 1001 | 2514 | 2605 | \+92 | 79 | mejor | 1 / 0 / 4 |

**Fantasmas comidos:**

| Comparación | Antes | Después | D | 2·ET\_D | En media | Rivales mejor / peor / ruido |
| :---- | :---- | :---- | :---- | :---- | :---- | :---- |
| v3 frente a v1, semilla 1 | 14,13 | 14,28 | \+0,15 | 0,34 | ruido | 2 / 1 / 2 |
| v3 frente a v1, semilla 1001 | 14,17 | 14,37 | \+0,20 | 0,34 | ruido | 3 / 1 / 1 |
| v4 frente a v3, semilla 1 | 14,28 | 14,78 | \+0,50 | 0,36 | mejor | 2 / 1 / 2 |
| v4 frente a v3, semilla 1001 | 14,37 | 15,17 | \+0,80 | 0,37 | mejor | 2 / 1 / 2 |

# **Traza de la v4**

125 partidas, 25 contra cada rival (438489 turnos). Se disparan las 15 reglas y ningún turno se queda sin acción. Disparos por cada 100 turnos (perseguirComestible se dispara una vez por cada comestible al que llega):

* Lo normal y oportunidades: pastillaMasCercana 100, comerPasillo 97,4, ultimasPastillas 27,5, esperarCombo 0,20.  
* Ataque: perseguirComestible 56,1, interceptar 8,7, cazarEnCadena 10,2.  
* Supervivencia: peligro 4,7, variosPeligros 6,1, huirDelMasCercano 1,75, huirDeVarios 1,07, correrALaPastilla 0,56, pastillaUnicaSalida 0,34, encerrada 0,12, muerteSegura 1,43.

esperarCombo y encerrada son las que menos se disparan, pero se disparan en todas las tandas.

# **Uso de IA generativa**

Modelo: Claude Opus 5.5 (Claude Code), trabajando directamente sobre el repositorio.  
Los prompts están en el archivo *prompts.md*.