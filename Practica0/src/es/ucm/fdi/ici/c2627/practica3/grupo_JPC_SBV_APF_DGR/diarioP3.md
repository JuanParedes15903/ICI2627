Comparación GhostsRolesv1 y GhostsRolesv2

== puntos: v2 frente a v1 (menos es mejor) ==
rival                        v1               v2         D   2·ET_D
Basico                1931 ± 40        1861 ± 41       -69      114  ruido
Intermedio           5293 ± 113       4974 ± 118      -319      326  ruido
Avanzado              8114 ± 75       7697 ± 106      -417      260  mejor
------------------------------------------------------------------------
MEDIA (3)             5112 ± 47        4844 ± 54      -268      144  mejor
v2 es mejor contra 1 rivales, peor contra 0 y no se distingue contra 2.

== turnos por vida: v2 frente a v1 (menos es mejor) ==
rival                        v1               v2         D   2·ET_D
Basico                 685 ± 46         631 ± 47       -53      131  ruido
Intermedio            2527 ± 77        2183 ± 80      -344      223  mejor
Avanzado              3397 ± 59        3050 ± 71      -347      184  mejor
------------------------------------------------------------------------
MEDIA (3)             2203 ± 36        1955 ± 39      -248      106  mejor
v2 es mejor contra 2 rivales, peor contra 0 y no se distingue contra 1.

== fantasmas comidos: v2 frente a v1 (menos es mejor) ==
rival                        v1               v2         D   2·ET_D
Basico              1,74 ± 0,07      1,69 ± 0,07     -0,05     0,19  ruido
Intermedio          5,42 ± 0,15      5,15 ± 0,16     -0,27     0,44  ruido
Avanzado            9,94 ± 0,12      9,40 ± 0,15     -0,54     0,38  mejor
------------------------------------------------------------------------
MEDIA (3)           5,70 ± 0,07      5,41 ± 0,08     -0,29     0,21  mejor
v2 es mejor contra 1 rivales, peor contra 0 y no se distingue contra 2.

La segunda versión mejora tanto en supervivencia como en puntuación respecto a la primera contra rivales más avanzados.


Comparación GhostRolesv2 y GhostRolesv3:

== puntos: v3 frente a v2 (menos es mejor) ==
rival                        v2               v3         D   2·ET_D
Basico                1939 ± 45        1883 ± 46       -55      128  ruido
Intermedio           5263 ± 110       5067 ± 113      -197      316  ruido
Avanzado              7894 ± 82        7984 ± 94       +90      249  ruido
------------------------------------------------------------------------
MEDIA (3)             5032 ± 48        4978 ± 51       -54      141  ruido
v3 es mejor contra 0 rivales, peor contra 0 y no se distingue contra 3.

== turnos por vida: v3 frente a v2 (menos es mejor) ==
rival                        v2               v3         D   2·ET_D
Basico                 634 ± 41         677 ± 48       +43      126  ruido
Intermedio            2386 ± 76        2331 ± 81       -55      222  ruido
Avanzado              3273 ± 66        3063 ± 70      -210      192  mejor
------------------------------------------------------------------------
MEDIA (3)             2098 ± 36        2024 ± 39       -74      106  ruido
v3 es mejor contra 1 rivales, peor contra 0 y no se distingue contra 2.

== fantasmas comidos: v3 frente a v2 (menos es mejor) ==
rival                        v2               v3         D   2·ET_D
Basico              1,81 ± 0,08      1,72 ± 0,08     -0,09     0,22  ruido
Intermedio          5,33 ± 0,15      5,34 ± 0,16     +0,01     0,44  ruido
Avanzado            9,73 ± 0,12      9,90 ± 0,14     +0,17     0,37  ruido
------------------------------------------------------------------------
MEDIA (3)           5,62 ± 0,07      5,65 ± 0,08     +0,03     0,21  ruido
v3 es mejor contra 0 rivales, peor contra 0 y no se distingue contra 3.

No hay una mejora notable.

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