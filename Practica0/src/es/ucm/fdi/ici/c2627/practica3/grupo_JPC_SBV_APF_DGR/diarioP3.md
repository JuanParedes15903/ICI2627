Comparación GhostsRolesv1 y GhostsRolesv2

== puntos: v2 frente a v1 (menos es mejor) ==
rival                        v1               v2         D   2·ET_D
Basico                1959 ± 39        1871 ± 41       -88      113  ruido
Intermedio           5154 ± 116       4941 ± 111      -213      321  ruido
Avanzado              8095 ± 79       7646 ± 102      -449      258  mejor
------------------------------------------------------------------------
MEDIA (3)             5069 ± 49        4819 ± 52      -250      142  mejor
v2 es mejor contra 1 rivales, peor contra 0 y no se distingue contra 2.

== turnos por vida: v2 frente a v1 (menos es mejor) ==
rival                        v1               v2         D   2·ET_D
Basico                 722 ± 51         664 ± 46       -58      138  ruido
Intermedio            2357 ± 81        2120 ± 80      -238      227  mejor
Avanzado              3290 ± 63        3026 ± 68      -264      185  mejor
------------------------------------------------------------------------
MEDIA (3)             2123 ± 38        1936 ± 38      -187      108  mejor
v2 es mejor contra 2 rivales, peor contra 0 y no se distingue contra 1.

== fantasmas comidos: v2 frente a v1 (menos es mejor) ==
rival                        v1               v2         D   2·ET_D
Basico              1,88 ± 0,07      1,64 ± 0,07     -0,25     0,20  mejor
Intermedio          5,34 ± 0,17      5,02 ± 0,15     -0,32     0,44  ruido
Avanzado            9,96 ± 0,12      9,48 ± 0,14     -0,49     0,37  mejor
------------------------------------------------------------------------
MEDIA (3)           5,73 ± 0,07      5,38 ± 0,07     -0,35     0,20  mejor
v2 es mejor contra 2 rivales, peor contra 0 y no se distingue contra 1.

La segunda versión mejora tanto en supervivencia como en puntuación respecto a la primera contra rivales más avanzados, y además consigue acabar con pacman más rápido. Por esto la versión con la que nos quedamos es V2.


Comparación GhostRolesv2 y GhostRolesv3:

== puntos: v3 frente a v2 (menos es mejor) ==
rival                        v2               v3         D   2·ET_D
Basico                1871 ± 41        2233 ± 54      +362      135  peor
Intermedio           4941 ± 111       4154 ± 114      -787      318  mejor
Avanzado             7646 ± 102       7877 ± 117      +231      311  ruido
------------------------------------------------------------------------
MEDIA (3)             4819 ± 52        4755 ± 57       -65      155  ruido
v3 es mejor contra 1 rivales, peor contra 1 y no se distingue contra 1.


== turnos por vida: v3 frente a v2 (menos es mejor) ==
rival                        v2               v3         D   2·ET_D
Basico                 664 ± 46         531 ± 41      -133      123  mejor
Intermedio            2120 ± 80        1791 ± 79      -329      225  mejor
Avanzado              3026 ± 68        2945 ± 72       -81      198  ruido
------------------------------------------------------------------------
MEDIA (3)             1936 ± 38        1756 ± 38      -181      108  mejor
v3 es mejor contra 2 rivales, peor contra 0 y no se distingue contra 1.


== fantasmas comidos: v3 frente a v2 (menos es mejor) ==
rival                        v2               v3         D   2·ET_D
Basico              1,64 ± 0,07      2,20 ± 0,09     +0,57     0,23  peor
Intermedio          5,02 ± 0,15      4,10 ± 0,16     -0,93     0,43  mejor
Avanzado            9,48 ± 0,14      9,92 ± 0,17     +0,44     0,43  peor
------------------------------------------------------------------------
MEDIA (3)           5,38 ± 0,07      5,41 ± 0,08     +0,03     0,22  ruido
v3 es mejor contra 1 rivales, peor contra 2 y no se distingue contra 0.

Al ser más agresivo, V3 consigue acabar antes con pacman pero esa agresividad hace que haya más fantasmas comidos y en algunos casos esto perjudica a la puntuación, por lo que nos quedamos con la V2.

Se usó IA en el desarrollo de la primera versión, estos fueron los prompts usados en el orden en el que se utilizaron:

 # Prompt 1
 estoy actualizando el controlador de fantasmas del que te hablé anteriormente para que use reglas en vez de una FSM, por ahora tengo esto: *se le adjuntó el archivo clp que venía en el zip del campus*

 # Prompt 2
 crea una regla de tal forma que, en caso de que los demás roles hayan sido asignados, el fantasma que queda sin rol intente emboscar a ms pacman yendo al nodo que está dos nodos por delante de ella en la dirección en la que está yendo en ese momento 

 # Prompt 3
 sería necesario modificar la regla de huida para que, en caso de que los fantasmas estén a una cierta distancia de ms pacman, se sigan alejando de ella pero teniendo en cuenta también la distancia a los demás fantasmas, haciendo así que ms pacman lo tenga más difícil de comerse a varios fantasmas a la vez?

 # Prompt 4 y 5
 *Se le adjuntó al LLM las clases GhostInput y Ghosts que venían en el zip del campus para que fuera consciente de la base sobre la que trabajaba* 

 # Prompt 6
 implementa una clase InteceptarAction que sirva para bloquear el camino a ms pacman apuntando al nodo que está 4 nodos por delante en la dirección a la que se está dirigiendo actualmente 

 # Prompt 7
 implementa otra clase similar, llamada EmboscarAction que sirva para atacar a ms pacman por un camino inesperado, que apunte al espacio 2 nodos por delante en la dirección en la que se está moviendo ms pacman actualmente 

 # Prompt 8
 implementa una clase que sirva para que un fantasma patrulle el laberinto haciendo que se dirija a la píldora de poder más alejada de ms pacman
 en caso de no quedar píldoras de poder, hará movimientos aleatorios válidos

 # Prompt 9
 implementa una acción que haga que los fantasmas huyan de forma coordinada, es decir, que cuando estén a una cierta distancia de ms pacman, una distancia segura, se alejen también los unos de los otros, para impedir que ms pacman tenga la oportunidad de comerse varios a la vez

 # Prompt 10
 *se le otorgaron los métodos de la clase game que están explicados en el campus para que revisara las clases que había creado y usara los métodos implementados en Game, ya que había una clase que estaba usando un método que no existe* 