# **Diario Práctica 4 Grupo JPC\_SBV\_APF\_DGR**

Integrantes:Juan Paredes Cruz, Sergio Barbas Vázquez, Adrián Prieto Fernández y Diego González del Castillo Ramírez de Arellano  
Versión a concurso: MsPacMan con mspacman-v4.clp y Ghosts con GhostsRolesv2.fcl.

#**MsPacMan**

# **Ghosts**
Uso de IA: sí, se ha usado la IA para adaptar lo que teníamos en la práctica anterior a esta, usando los siguientes prompts
1. "serías capaz de traducir una lógica para un controlador de fantasmas del juego ms pacman basada en un sistema de reglas a una lógica basada en razonamiento borroso?" -> El output de este prompt fue una ligera explicación de cómo adaptar un sistema de reglas a uno basado en lógica borrosa
2. "traduce ese sistema de reglas a un sistema basado en lógica difusa que siga la siguiente estructura:
FUNCTION_BLOCK Ghost
VAR_INPUT //pacman, en pasos de 0 a 150 y comestible con los turnos que quedan (0: no comestible)
pacman : REAL
comestible : REAL
END_VAR
VAR_OUTPUT
perseguir : REAL
huir : REAL
END_VAR
FUZZIFY pacman
TERM cerca := (0,1) (15,1) (35, 0):
TERM media := trian 20 45 70:
TERM lejos := (55, 0) (80, 1) (150, 1);
END_FUZZIFY" *Se le proporcionó el archivo GhostRolesV3.clp de la práctica 3" -> el output de este prompt fue la primera versión del modelo de lógica difusa, GhostRolesFuzzyv0
3. "esta es una de las acciones que usaba la lógica basada en reglas, cómo y por qué habría que modificarla para que se adapte a la lógica difusa" -> *se le proprcionó al LLM la clase de CercoAction de la practica3* el output de este prompt fue la versión de lógica difusa de la acción CercoAction, aunque sin incluir uno de los métodos de la clase Action,  junto con una explicación a cerca de cómo, aunque la lógica es correcta, es necesario modificar la clase para eliminar dependencias a Jess y el método de ParseFact que se usaban en las acciones de la práctica anterior para
4. "esta es la estructura que deben seguir todas las acciones de la lógica basada en razonamiento difuso, modifica la clase que acabas de arreglar para que se ajuste a este esquema: " *se le proporcionó al LLM la clase HuirAction que venía en el zip de la práctica 4* -> el output de este prompt fue una versión revisada de la clase CercoAction con los métodos de la clase Action ya bien implementados
5. "voy a pasarte siete clases más una a una, haz lo mismo para cada una de ellas y adáptalas para que funcionen con jfl y se ajusten al esquema que te acabo de proporcionar: " *este junto con los siguientes seis prompts son las acciones de los fantasmas que estaban incluidas en la práctca 3*
6. "a continuación te voy a proporcionar el controlador, modifícalo parra que incluya cada una de las clases que te acabo de proporcionar, además de la clase PerseguiAction, que ya tenía programada: " *se le proporcionó el controlador Ghosts que venía en el zip de la p4*
7. "por último, te voy a proporcionar la clase input de la que tiene que heredar la clase GhostInput para la lógica basada en razonamiento difuso, y la clase input que se usó con la lógica basada en reglas, para que ajustes la clase a las necesidades de la lógica difusa: " -> el output de este prompt fue la clase GhostInput, adaptada al uso de la lógica difusa

# **Métricas**

Medimos siempre con Comparar, contra los mismos rivales y con las mismas semillas, 300 partidas por rival:

* Rivales: los tres MsPacMan de la wiki
* Versión de partida: ghosts-v1 del ejemplo

Métricas que usamos:

* Media de puntos por partida y error típico.  
* Turnos por vida.  
* Fantasmas comidos por partida.  
* Se prueba que funcione correctamente con ProbarBorrosoFantasma.java.

# **Versión V1**
Mismas entradas, salidas y acciones que la v0. Cambia la base de reglas, que pasa de 12 a 14, y se añade un término (cercana) a distPPMsPacman.

Problema de la v0: Todas las reglas ponían su salida en mucho. Varias podían valer 1.0 a la vez (huir por Power Pill, cortar_pp, cerco y perseguir por ranking). En ese caso decidía el orden del array de acciones y no la lógica. Además, un fantasma comestible con mucho tiempo y lejos de Ms. Pac-Man no activaba ninguna regla, así que su acción era arbitraria.
 
Cambios:
- Tres niveles de prioridad. mucho para emergencias (cárcel, huida, cortar_pp, fin de comestible), algo para el cerco y poco para los roles. Con la defuzzificación COG, mucho da ≈8.2-8.7, algo da 5 y poco da ≈1.3-2. Funcionan como prioridades estrictas y desaparecen los empates.
- Hueco cerrado (regla 5 nueva). Comestible con mucho tiempo y lejos hace huir_disperso.
huir por Power Pill (regla 6). Exige ventajaPP IS no: solo huye si Ms. Pac-Man llega antes a la PP. Si llega antes el fantasma, corta.
- cortar_pp (regla 7). Exige que Ms. Pac-Man esté cerca de la PP, con el término nuevo cercana (60 a 100). Antes se disparaba con la PP a 250 nodos.
- Cuarto fantasma. patrullar solo si quedan PPs (regla 13). Sin PPs patrullar es aleatorio, así que hace cerco (regla 14 nueva).

**Tests**
== puntos: v1 frente a v0 (menos es mejor) ==
rival                        v0               v1         D   2·ET_D
Basico                2039 ± 47        2097 ± 50       +58      137  ruido
Intermedio           4935 ± 116       4947 ± 112       +12      324  ruido
Avanzado              7720 ± 98        7835 ± 96      +115      274  ruido
------------------------------------------------------------------------
MEDIA (3)             4898 ± 53        4959 ± 52       +62      149  ruido
v1 es mejor contra 0 rivales, peor contra 0 y no se distingue contra 3.


== turnos por vida: v1 frente a v0 (menos es mejor) ==
rival                        v0               v1         D   2·ET_D
Basico                 755 ± 56         575 ± 48      -180      147  mejor
Intermedio            2392 ± 80        1774 ± 72      -618      215  mejor
Avanzado              3119 ± 69        3065 ± 70       -54      197  ruido
------------------------------------------------------------------------
MEDIA (3)             2089 ± 40        1805 ± 37      -284      109  mejor


v1 es mejor contra 2 rivales, peor contra 0 y no se distingue contra 1.
== fantasmas comidos: v1 frente a v0 (menos es mejor) ==
rival                        v0               v1         D   2·ET_D
Basico              1,92 ± 0,08      1,94 ± 0,09     +0,02     0,24  ruido
Intermedio          4,97 ± 0,16      5,07 ± 0,16     +0,10     0,45  ruido
Avanzado            9,40 ± 0,14      9,44 ± 0,14     +0,04     0,39  ruido
------------------------------------------------------------------------
MEDIA (3)           5,43 ± 0,07      5,48 ± 0,08     +0,05     0,21  ruido
v1 es mejor contra 0 rivales, peor contra 0 y no se distingue contra 3.

Conclusión: la v1 mantiene los puntos y los fantasmas comidos (ruido en los tres rivales) y acaba con Ms. Pac-Man antes: −284 turnos por vida de media, con mejora clara contra Basico e Intermedio. Los puntos no mejoran, pero la jerarquía de prioridades hace el comportamiento predecible y medible.

Trazas v1:


# **Versión V2**
Mismas entradas, términos y niveles que la v1, mismo Input (GhostsInput). Cambia qué acción ejecutan 4 reglas. La v2 tiene 12 salidas y las 3 nuevas se registran en GhostsExt, que es como Ghosts con las acciones añadidas. Las acciones antiguas siguen declaradas y registradas para poder comparar.

Problema de la v1. Los niveles ordenaban bien las reglas, pero algunas acciones se movían mal:

- HuirAction huye en línea recta con getApproximateNextMoveAwayFromTarget y puede acabar en un callejón.
- InterceptarAction apunta 4 nodos en línea recta e ignora los cruces.
- PatrullarAction va a la PP más lejana del propio fantasma, sin objetivo táctico, o se mueve al azar.

Cambios:

- HuirSeguroAction (huir_seguro). Sustituye a huir en las reglas 2 (comestible en pánico) y 6 (peligro de PP). Para cada dirección mira el siguiente cruce, que es donde se decide en este motor, y calcula el margen distancia de Ms. Pac-Man al cruce − pasos que yo tardo. Penaliza los cruces donde ella llega antes y suma un bonus de separación respecto a otros comestibles.
- InterceptarCruceAction (interceptar_cruce). Sustituye a interceptar en la regla 11. Va al siguiente cruce de Ms. Pac-Man en su dirección actual solo si llega estrictamente antes que ella. Si no, persigue.
- GuardiaPPAction (guardia_pp). Sustituye a patrullar en la regla 13. Se coloca a menos de 4 pasos de la PP más cercana a Ms. Pac-Man y presiona desde ahí.

**Tests** 
== puntos: v2 frente a v1 (menos es mejor) ==
rival                        v1               v2         D   2·ET_D
Basico                2097 ± 50        2012 ± 46       -84      137  ruido
Intermedio           4947 ± 112       4639 ± 119      -307      327  ruido
Avanzado              7835 ± 96       7770 ± 107       -65      287  ruido
------------------------------------------------------------------------
MEDIA (3)             4959 ± 52        4807 ± 55      -152      152  mejor
v2 es mejor contra 0 rivales, peor contra 0 y no se distingue contra 3.


== turnos por vida: v2 frente a v1 (menos es mejor) ==
rival                        v1               v2         D   2·ET_D
Basico                 575 ± 48         544 ± 44       -31      130  ruido
Intermedio            1774 ± 72        1669 ± 72      -105      203  ruido
Avanzado              3065 ± 70        2824 ± 75      -241      204  mejor
------------------------------------------------------------------------
MEDIA (3)             1805 ± 37        1679 ± 37      -126      105  mejor
v2 es mejor contra 1 rivales, peor contra 0 y no se distingue contra 2.


== fantasmas comidos: v2 frente a v1 (menos es mejor) ==
rival                        v1               v2         D   2·ET_D
Basico              1,94 ± 0,09      1,82 ± 0,08     -0,12     0,23  ruido
Intermedio          5,07 ± 0,16      4,58 ± 0,16     -0,49     0,46  mejor
Avanzado            9,44 ± 0,14      9,65 ± 0,15     +0,21     0,40  ruido
------------------------------------------------------------------------
MEDIA (3)           5,48 ± 0,08      5,35 ± 0,08     -0,13     0,22  ruido
v2 es mejor contra 1 rivales, peor contra 0 y no se distingue contra 2.


Conclusión: la v2 mejora en la media de puntos (−152) y de turnos por vida (−126). Es la mejor de las tres, pero con matices:

Puntos: la mejora en la media está justo en el límite (D = −152 y 2·ET_D = 152) y ningún rival por separado lo confirma. El efecto es pequeño y no concluyente.
Turnos por vida: la mejora es más clara. Contra Avanzado, el rival más difícil, acabamos con Ms. Pac-Man 241 turnos antes por vida.
Fantasmas comidos: no hay diferencia en la media. Solo mejora contra Intermedio (−0,49), y contra Avanzado sube ligeramente (+0,21, ruido). Esperábamos que huir_seguro redujera más los fantasmas comidos y no se ha visto con claridad.

La v1 no se distingue de la v0 en puntos, pero la v2 acumula una mejora pequeña sobre la v1 en puntos y turnos. Nos quedamos con la v2.

Trazas v2:
