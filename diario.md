MCTS: 
las siguientes pruebas se han realizado todas con un tiempo por jugada de 0.3 y las iteraciones por defecto con los siguientes modelos:

"mcts": lambda: MCTS(),
"mcts-c03": lambda: MCTS(c=0.3),
"mcts-voraz": lambda: MCTS(c=0.3, profundidad=0),
"mcts-profundo": lambda: MCTS(c=3, profundidad=25),
"mcts-cAlto-pBajo": lambda: MCTS(c=3, profundidad=0),
"mcts-cBajo-pAlto": lambda: MCTS(c=0.3, profundidad=25),
"ab2": lambda: Minimax(profundidad=2)

que han dado los siguientes resultados:

mcts                    100% ±0 (16-0-0)      78% ±16 (10-5-1)        3% ±6 (0-1-15)       60% ±13
mcts-c03                100% ±0 (16-0-0)      88% ±14 (13-2-1)        0% ±0 (0-0-16)       62% ±14
mcts-voraz              100% ±0 (16-0-0)      81% ±18 (12-2-2)        0% ±0 (0-0-16)       60% ±14
mcts-profundo           100% ±0 (16-0-0)      25% ±18 (2-4-10)        0% ±0 (0-0-16)       42% ±14
mcts-cAlto-pBajo        100% ±0 (16-0-0)       94% ±9 (14-2-0)       9% ±14 (1-1-14)       68% ±13
mcts-cBajo-pAlto        94% ±12 (15-0-1)       53% ±19 (5-7-4)        0% ±0 (0-0-16)       49% ±13
mctsab2                 100% ±0 (16-0-0)      100% ±0 (16-0-0)       47% ±23 (6-3-7)       82% ±11

Ninguna variante supera claramente a la base: c03 y cAlto-pBajo obtienen mejores porcentajes frente a Voraz (88% y 94% frente a 78%), pero con intervalos solapados. Lo que pareciera que es claro es que profundidad=25 empeora el rendimiento, sobre todo con c=3 (25% frente a Voraz), lo que atribuimos al ruido de las simulaciones aleatorias largas y al menor número de iteraciones. Ninguna versión puntúa contra AlfaBeta2, lo que apunta a una limitación común del diseño: el árbol no modela al rival como adversario.

Para confirmar estas teorías, se han eliminado los modelos que parecían menos prometedores, sustituyéndolos por modelos para comprobar la teoría de si mayor profundidad implica peor rendimiento, o al contrario, si menor profundidad significa mejor rendimiento, y se han mantenido aquellos que parecían que tenían algún tipo de mejora. Estos son los modelos que se han usado para esta ronda:

"mcts": lambda: MCTS(),
"mcts-c03": lambda: MCTS(c=0.3),
"mcts-p25": lambda: MCTS(profundidad=25),
"mcts-p0": lambda: MCTS(profundidad=0),
"mcts-cAlto-pBajo": lambda: MCTS(c=3, profundidad=0),
"ab2": lambda: Minimax(profundidad=2),

que han generado estos resultados:

mcts                    100% ±0 (16-0-0)      81% ±18 (12-2-2)        3% ±6 (0-1-15)       61% ±14
mcts-c03                100% ±0 (16-0-0)      88% ±14 (13-2-1)        0% ±0 (0-0-16)       62% ±14
mcts-p25                 97% ±6 (15-1-0)        3% ±6 (0-1-15)        0% ±0 (0-0-16)       33% ±13
mcts-p0                 100% ±0 (16-0-0)      81% ±15 (11-4-1)        0% ±0 (0-0-16)       60% ±14
mcts-cAlto-pBajo        100% ±0 (16-0-0)      84% ±15 (12-3-1)        0% ±0 (0-0-16)       61% ±14
ab2                      97% ±6 (15-1-0)       97% ±6 (15-1-0)       34% ±20 (3-5-8)       76% ±11

Como se puede comprobar, sin ningún atributo adicional, aumentar la profundidad empeora considerablemente los resultados. Por otro lado, ahora mcts-cAlto-pBajo y mcts-c03 se intercambian en posiciones, siendo que mcts-c03 pasa a ser el que mejor resultados obtiene, lo que podría implicar que los resultados del experimento anterior eran debido al ruido, y ambos modelos están más empatados de lo que podría haber parecido en un principio. Con esto, se decide que en los siguientes experimentos se añadirá una heurística que incentive las jugadas que toman posesión del centro del tablero, y penaliza las jugadas que dejan material expuesto a que lo capture el rival. 

Las funciones de puntos_centro, perdida_max y hacer_recompensa del fichero mcts.py son las funciones que componen la heurística para este experimento. Los modelos que se han usado son estos:

"mcts": lambda: MCTS(),
"mcts-heur-p0": lambda: MCTS(profundidad=0, recompensa=hacer_recompensa(1.0, 1.0)),
"mcts-heur-p4": lambda: MCTS(profundidad=4, recompensa=hacer_recompensa(1.0, 1.0)),
"mcts-centro-p0": lambda: MCTS(profundidad=0, recompensa=hacer_recompensa(1.0, 0.0)),
"mcts-expuesto-p0": lambda: MCTS(profundidad=0, recompensa=hacer_recompensa(0.0, 1.0)),
"ab2": lambda: Minimax(profundidad=2),

y los resultados que han salido son los siguientes:
mcts                    100% ±0 (16-0-0)       66% ±18 (7-7-2)        0% ±0 (0-0-16)       55% ±13
mcts-heur-p0            100% ±0 (16-0-0)      88% ±11 (12-4-0)        0% ±0 (0-0-16)       62% ±13
mcts-heur-p4            100% ±0 (16-0-0)      72% ±20 (10-3-3)        0% ±0 (0-0-16)       57% ±14
mcts-centro-p0           97% ±6 (15-1-0)      91% ±10 (13-3-0)        0% ±0 (0-0-16)       62% ±13
mcts-expuesto-p0        100% ±0 (16-0-0)       97% ±6 (15-1-0)       6% ±12 (1-0-15)       68% ±13
ab2                     100% ±0 (16-0-0)      100% ±0 (16-0-0)       47% ±21 (5-5-6)       82% ±10

Como se puede ver, salvo en el caso del modelo mcts-expuesto-p0, que es el que juega de la forma más segura posible, no hay un aumento muy significativo en relación al modelo base, aunque sí que se puede ver que, en este experimento, todos han sido ligeramente mejores que el modelo base, aunque lo más probable es que haya sido suerte y no mejoría. Aunque, viendo que todos los modelos tienen una profundidad baja, usar el modelo base no es la mejor comparativa, ya que usa profundidad 10, por lo que el siguiente experimento consistirá en repetir con los modelos de heurística, con más partidas (64 en total) y comparando con el modelo mcts-p0: 
mcts-p0                  99% ±2 (63-1-0)      84% ±7 (47-13-4)        2% ±3 (0-3-61)        62% ±7
mcts-heur-p0            100% ±0 (64-0-0)       92% ±5 (55-8-1)        1% ±2 (0-1-63)        64% ±7
mcts-heur-p4            100% ±0 (64-0-0)      81% ±8 (46-12-6)        0% ±0 (0-0-64)        60% ±7
mcts-centro-p0          100% ±0 (64-0-0)      87% ±7 (50-11-3)        2% ±3 (1-0-63)        63% ±7
mcts-expuesto-p0        100% ±0 (64-0-0)       92% ±6 (57-4-3)        2% ±2 (0-2-62)        65% ±7
ab2                      98% ±3 (61-3-0)      100% ±0 (64-0-0)     50% ±10 (22-20-22)        83% ±5

Como se puede ver en los resultados de la simulación, no hay una mejora real con ninguno de los modelos, siendo que el que mejores resultados tiene es mcts-expuesto-p0, pero está dentro de lo que se consideraría ruido, aunque eso sería mirando el total, ya que, si se miran las partidas contra los oponentes de forma individual, se puede observar que mcts-expuesto-p0 gana mucho más y de forma más consistente, contra el rival Voraz, por lo que sí que hay una mejoría en ese apartado, aunque luego todos pierdan contra AlfaBeta2. La partida que gana mcts-centro-p0 seguramente sea suerte y nada a tener en cuenta.

Para elegir los mejores parámetros de MCTSRivales se han hecho las siguientes pruebas:
Para elegir el mejor c se han puesto los siguientes casos: 
    "riv-c03": lambda: MCTSRivales(c=0.3, profundidad=0),
    "riv-c05": lambda: MCTSRivales(c=0.5, profundidad=0),
    "riv-c08": lambda: MCTSRivales(c=0.8, profundidad=0),
    "riv-c14": lambda: MCTSRivales(c=1.4, profundidad=0)

Que han dado los siguientes resultados:
            
riv-c03         98% ±2 (62-2-0)       99% ±2 (63-1-0)       13% ±8 (6-5-53)        70% ±6
riv-c05         99% ±2 (63-1-0)       95% ±5 (58-5-1)       10% ±7 (5-3-56)        68% ±7
riv-c08         98% ±2 (62-2-0)      92% ±5 (54-10-0)        9% ±7 (4-4-56)        67% ±6
riv-c14        100% ±0 (64-0-0)       98% ±3 (61-3-0)        2% ±2 (0-2-62)        66% ±7

El que mejor resultado ha tenido es el c=0.3, por lo que continuamos con ese.

Después elegimos la profundidad con los siguientes casos:
    "riv-p0": lambda: MCTSRivales(c=0.3, profundidad=0),
    "riv-p2": lambda: MCTSRivales(c=0.3, profundidad=2),
    "riv-p4": lambda: MCTSRivales(c=0.3, profundidad=4),
    "riv-p6": lambda: MCTSRivales(c=0.3, profundidad=6),
    "riv-p8": lambda: MCTSRivales(c=0.3, profundidad=8)

Y han dado los siguientes resultados:
riv-p0        100% ±0 (64-0-0)       98% ±3 (61-3-0)        9% ±7 (5-2-57)        69% ±7
riv-p2        100% ±0 (64-0-0)       95% ±5 (59-3-2)        3% ±3 (0-4-60)        66% ±7
riv-p4         98% ±2 (62-2-0)       95% ±5 (59-3-2)        2% ±2 (0-2-62)        65% ±7
riv-p6         99% ±2 (63-1-0)       94% ±6 (59-2-3)        2% ±2 (0-2-62)        65% ±7
riv-p8         99% ±2 (63-1-0)       89% ±7 (55-4-5)        2% ±3 (1-1-62)        64% ±7

Por ultimo se han probado las recompensas:

    "riv-material": lambda: MCTSRivales(c=0.3, profundidad=0),
    "riv-centro": lambda: MCTSRivales(c=0.3, profundidad=0, recompensa=hacer_recompensa(1.0, 0.0)),
    "riv-expuesto": lambda: MCTSRivales(c=0.3, profundidad=0, recompensa=hacer_recompensa(0.0, 1.0)),
    "riv-ambas": lambda: MCTSRivales(c=0.3, profundidad=0, recompensa=hacer_recompensa(1.0, 1.0)),

El resultado final:
riv-material         98% ±2 (62-2-0)       93% ±4 (55-9-0)        9% ±7 (5-1-58)        67% ±7
riv-centro           98% ±2 (62-2-0)      89% ±5 (50-14-0)        6% ±5 (2-4-58)        65% ±7
riv-expuesto         99% ±2 (63-1-0)       95% ±5 (58-5-1)     26% ±10 (12-9-43)        73% ±6
riv-ambas            99% ±2 (63-1-0)       97% ±3 (60-4-0)      23% ±9 (7-15-42)        73% ±6

Para comparar con la version anterior:
    "mcts-expuesto-p0": lambda: MCTS(profundidad=0, recompensa=hacer_recompensa(0.0, 1.0)),
    "riv-expuesto": lambda: MCTSRivales(c=0.3, profundidad=0, recompensa=hacer_recompensa(0.0, 1.0))

Resultado:
mcts-expuesto-p0         98% ±2 (62-2-0)       91% ±7 (57-3-4)        8% ±6 (2-6-56)        66% ±7
riv-expuesto               98% ±2 (62-2-0)      100% ±0 (64-0-0)      18% ±8 (6-11-47)        72% ±6

La segunda version es mejor por lo que las siguientes clases heredaran de esta.

MCTSTiempo:
Para probar la c se usaron los siguientes casos:
    "tiempo-c03": lambda: MCTSTiempo(c=0.3),
    "tiempo-c05": lambda: MCTSTiempo(c=0.5),
    "tiempo-c07": lambda: MCTSTiempo(c=0.7),
    "tiempo-c10": lambda: MCTSTiempo(c=1.0)

Y se obtuvieron los siguientes resultados:
tiempo-c03         99% ±2 (63-1-0)     72% ±10 (39-14-11)        1% ±2 (0-1-63)        57% ±7
tiempo-c05        100% ±0 (64-0-0)      81% ±8 (46-12-6)        3% ±4 (1-2-61)        61% ±7
tiempo-c07        100% ±0 (64-0-0)       88% ±7 (54-5-5)        5% ±5 (2-3-59)        65% ±7
tiempo-c10         99% ±2 (63-1-0)     62% ±11 (33-13-18)        2% ±3 (1-0-63)        54% ±7

Para probar la profundidad se usaron los siguientes casos:
    "tiempo-p0": lambda: MCTSTiempo(c=0.7, profundidad=0),
    "tiempo-p1": lambda: MCTSTiempo(c=0.7, profundidad=1),
    "tiempo-p2": lambda: MCTSTiempo(c=0.7, profundidad=2),
    "tiempo-p3": lambda: MCTSTiempo(c=0.7, profundidad=3),
    "tiempo-p4": lambda: MCTSTiempo(c=0.7, profundidad=4),
    "tiempo-p5": lambda: MCTSTiempo(c=0.7, profundidad=5),
    "tiempo-p6": lambda: MCTSTiempo(c=0.7, profundidad=6),
    "tiempo-p7": lambda: MCTSTiempo(c=0.7, profundidad=7),


Se han obtenido los siguientes resultados:
tiempo-p0         98% ±2 (62-2-0)       92% ±6 (56-6-2)       15% ±9 (8-3-53)        68% ±6
tiempo-p1         99% ±2 (63-1-0)       91% ±6 (55-6-3)        8% ±6 (2-6-56)        66% ±7
tiempo-p2        100% ±0 (64-0-0)       93% ±6 (58-3-3)        5% ±5 (2-2-60)        66% ±7
tiempo-p3         98% ±2 (62-2-0)       93% ±5 (57-5-2)        2% ±3 (1-1-62)        65% ±7
tiempo-p4        100% ±0 (64-0-0)       91% ±7 (57-3-4)        1% ±2 (0-1-63)        64% ±7
tiempo-p5         99% ±2 (63-1-0)       93% ±6 (58-3-3)        5% ±5 (2-2-60)        66% ±7
tiempo-p6         98% ±3 (62-1-1)       92% ±6 (56-6-2)        2% ±3 (1-0-63)        64% ±7
tiempo-p7         99% ±2 (63-1-0)       92% ±6 (56-6-2)        2% ±3 (0-3-61)        65% ±7

Por ultimo se han comprobado las recompensas:
    "tiempo-material": lambda: MCTSTiempo(c=0.3, profundidad=0),
    "tiempo-centro": lambda: MCTSTiempo(c=0.3, profundidad=0, recompensa=hacer_recompensa(1.0, 0.0)),
    "tiempo-expuesto": lambda: MCTSTiempo(c=0.3, profundidad=0, recompensa=hacer_recompensa(0.0, 1.0)),
    "tiempo-ambas": lambda: MCTSTiempo(c=0.3, profundidad=0, recompensa=hacer_recompensa(1.0, 1.0))

Y han dado los siguientes resultados:
tiempo-material         98% ±3 (61-3-0)       98% ±3 (61-3-0)     35% ±10 (14-17-33)        77% ±6
tiempo-centro          100% ±0 (64-0-0)       95% ±4 (58-6-0)     34% ±11 (16-11-37)        76% ±6
tiempo-expuesto         98% ±2 (62-2-0)       96% ±4 (60-3-1)      27% ±9 (10-14-40)        74% ±6
tiempo-ambas           100% ±0 (64-0-0)       99% ±2 (63-1-0)       20% ±9 (8-9-47)        73% ±6

Para comparar con las versiones anteriores usamos tanto tiempo-material como tiempo-centro
    "tiempo-material": lambda: MCTSTiempo(c=0.3, profundidad=0),
    "tiempo-centro": lambda: MCTSTiempo(c=0.3, profundidad=0, recompensa=hacer_recompensa(1.0, 0.0)),
    "mcts-expuesto-p0": lambda: MCTS(profundidad=0, recompensa=hacer_recompensa(0.0, 1.0)),
    "riv-expuesto": lambda: MCTSRivales(c=0.3, profundidad=0, recompensa=hacer_recompensa(0.0, 1.0))

Han dado los siguientes resultados:
tiempo-material          98% ±2 (62-2-0)      100% ±0 (64-0-0)     45% ±11 (22-14-28)        81% ±5
tiempo-centro            98% ±2 (62-2-0)       98% ±3 (61-3-0)     33% ±10 (15-12-37)        76% ±6
mcts-expuesto-p0         99% ±2 (63-1-0)       95% ±5 (60-2-2)        9% ±6 (2-7-55)        68% ±7
riv-expuesto            100% ±0 (64-0-0)       98% ±2 (62-2-0)      27% ±9 (8-18-38)        75% ±

Finalmente nos quedamos con tiempo-material.