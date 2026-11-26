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