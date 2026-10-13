Práctica 1:

Grupo de Juan Paredes Cruz y Sergio Barbas Vázquez

MsPacMan:
Para el desarrollo del controlador de MsPacMan no se ha usado IA. Cada versión añade funcionalidades a la anterior y no usa funciones alternativas.
El desarrollo empezó con la base de la entrega del MsPacMan de la práctica 0 que huía de fantasmas si estaban cerca, iba hacia los fantasmas si se los podía comer, y en caso contrario buscaba la pildora de poder más cercana. 
La puntuación del modelo base ha sido:


El primer cambio respecto al modelo base fue que solo se pudiese decidir en los cruces ya que son los únicos sitios donde es importante la decisión. Esto no cambia el resultado de las partidas pero ahorra tiempo de procesamiento.
Después se implementó el primer cambio del comportamiento de MsPacMan, guardar qué se encuentra en los caminos hasta el siguiente cruce y teniendo en cuenta esos datos decidir cuál es el mejor movimiento. La prioridad que se dió para elegir estos caminos fue 1º que elija el menor número de fantasmas ya que es una muerte segura (después se añadió que los fantasmas no se guardasen en el camino si iban en la misma dirección que el pacman ya que es imposible que le cojan), 2º el camino con más fantasmas comestibles para maximizar puntuación, y por último el camino con más pildoras. Esto dejó dos problemas importantes: al ir por un camino no sabía si hay fantasmas cerca que vayan a venir por delante y que vayan a cortar el paso, y segundo que en la mayoría de partidas el pacman acababa yendo en una línea recta al no encontrar ni fantasmas ni pildoras, por lo que se acababa el tiempo.
El primer problema se solucionó parcialmente implementando la función de los apuntes de ver quién llega antes, si el pacman o los fantasmas. Con esto se pueden filtrar los caminos que no son seguros por los fantasmas que no se encuentran en el propio camino. Esta es la primera versión de MsPacMan.
Sus puntuaciones son de:


Lo primero que se solucionó con la versión MsPacMan2 fue que aunque se filtren los caminos que se van a cortar antes de que llegue pacman, en un futuro cercano los fantasmas pueden bloquear las salidas del camino elegido por lo que se implementó la busqueda en dos cruces, en el que para cada camino elegido se vuelve a hacer una busqueda de "Habiendo cogido un camino, cuál va a ser el siguiente mejor". Con este razonamiento guardamos todos los datos de dos cruces y elegimos el mejor, calculando también si dentro de dos cruces va a llegar un fantasma antes que pacman. 
Para intentar mejorar la puntuación obtenida se hizo otro cambio en la selección de caminos en el que solo se elija un camino con pildoras de poder si hay fantasmas cerca, intentando maximizar el número de fantasmas comidos al final de la partida y con ello la puntuación. Este cambio se hizo debido a que por la forma en la que está diseñado el mapa del juego los caminos con píldoras de poder son los caminos que más píldoras normales tienen, por lo que en la mayoría de partidas pacman se comía las píldoras de poder antes de que los fantasmas saliesen de la cárcel.
Por último se solucionó un error del cálculo de los cruces en el que se guardaba la dirección inicial de un cruce y no la final, ignorando caminos posibles al haber acabado en una dirección distinta a la que se ha comenzado.
Sus puntuaciones son de:

El MsPacMan3 soluciona el otro problema que se dió con la primera versión y no se solucionó en la segunda versión. PacMan sigue acabando partidas huyendo infinitamente de los fantasmas en línea recta ya que no detecta ninguna pildora a dos cruces de distancia. Para solucionar esto en la selección de caminos se impuso una condición de que si en ninguno de los caminos posibles hay pildoras se debe coger el camino hacia la píldora más cercana si es segura. Este cambio hizo que pacaman ya no entrase en bucles infinitos, pero provocó que pacman ignorase a los fantasmas comestibles perjudicando en gran medida a la puntuación final. Por eso se añadió la condición de que busque la píldora más cercana si tampoco ha encontrado fantasmas comestibles en ningún camino.
En la tercera versión de MsPacMan también se planteó añadir el cálculo de ir a por un fantasma comestible calculando el tiempo que le queda de seguir en ese estado, pero tras varios intentos de implementarlo no se consiguió realizar correctamente y las puntuaciones bajaron drásticamente, en su mayoría porque pacman no iba a por los fantasmas. 
El último cambio de la versión final fue decidir cuál es la mejor HOLGURA para PacMan. Esta es la que decide si PacMan va a llegar antes que los fantasmas a los cruces ya que es el rango de colisión del propio pacman. En la versión 2 comenzó siendo 2 pero probando con otros valores se decidió que 4 era la mejor opción. Durante las partidas 2 era demasiado arriesgado y aunque los fantasmas no llegaban, los dejaba demasiado cerca siguiéndole. Con los valores 3 y 4 la diferencia visual era mínima, pero realizando las simulaciones de miles de partidas el 4 acababa con mejor puntuación. También se probó el 5 pero PacMan era demasiado precavido por lo que la mayoría de caminos válidos los ignoraba.
Sus puntuaciones son de:



Ghosts:
Se ha determinado que el mejor modelo para los fantasmas es el del archivo 
Ghosts3, que es una versión modificada del modelo de fantasmas con roles 
asignados dinámicamente, pero añadiendo predicción de bayas de poder, lo 
que significa que los fantasmas comprueban si llegan antes que MsPacMan a
una píldora de poder, en caso de que MsPacMan se encuentre a 15 nodos o 
menos de una y, en caso de que llegue MsPacMan antes/al mismo tiempo, 
empiezan la rutina de huida. Se ha elegido este modelo porque, en un span 
de 1000 partidas con cada modelo de MsPacMan, se ha visto que, aunque no 
era el que obtenía las medias de puntos más bajas (era el segundo), era 
el que obtenía las medias de turnos más bajas y con menor error estándar 
de todos los modelos, y, teniendo en cuenta el error estándar de la 
puntuación, era el mejor de los seis.

Los prompts que se han usado para generar los distintos modelos son los 
siguientes:

Ghosts:  teniendo esa clase (hace referencia a la clase game) como base para
un simulador del juego ms pacman, crea un controlador para los fantasmas que
siga una estrategia por roles de la siguiente manera:

BLINKY: persigue de forma normal a ms pacman, usando el método 
getNextMoveTowardsTarget

PINKY: intenta tapar el camino más cercano a ms pacman que le lleve a la
baya más cercana

INKY: intenta tapar el camino más cercano a ms pacman que le lleve a la 
baya de poder más cercana

SUE: va alternando entre las tres estrategias de forma aleatoria

para todos los fantasmas, si están en el tiempo en el que se les puede comer, 
huyen de ms pacman hasta que la mitad del tiempo restante (porque se mueven 
la mitad de rápido que de normal en estado comestible) es menor que la 
distancia a la que está ms pacman, en cuyo caso vuelven a su rol normal, y, 
si estando en estado normal, ms pacman está a menos de un cruce, van a por 
ella independientemente de su rol 

Este modelo de fantasmas obtuvo una puntuación media de 1542.09, con un error 
estándar de 53.92. Consiguió esta puntuación con una media de 901.75 turnos y un
error estándar de 27.76

Ghosts2:  modifica la clase que has generado anteriormente para que los roles 
no estén asignados a un fantasma en específico, si no que sea el fantasma más 
apropiado el que se encargue de cierto rol, y crea un nuevo rol que se dedique 
a patrullar cerca de la "jaula" de los fantasmas, a un cruce de distancia, 
eliminando así lo que hacía sue antes

un rol sólo puede estar asignado a un fantasma, no puede haber dos fantasmas 
haciendo lo mismo

es decir, el fantasma más cercano a ms pacman será el que la persiga, el 
fantasma más cercano al camino que lleva a ms pacman a la píldora de poder más 
cercana será el que lo tape, etc 

Este modelo de fantasmas obtuvo una puntuación media de 1497.73, con un error 
estándar de 46.89. Consiguió esta puntuación con una media de 919.4 turnos y un
error estándar de 27.95

BlockingGhosts:  genera un nuevo controlador en el que se cumpla que: el primer 
fantasma que sale, BLINKY, debe perseguir a ms pacman todo el rato, intentando 
alcanzarla usando el método getNextMoveTowardsTarget; los demás fantasmas, PINKY, 
INKY y SUE, intentarán tapar las posibles salidas por las que iría ms pacman, 
determinando qué cruces están a 1 de distancia de ella, usando DM.PATH, y yendo 
cada uno de ellos a un cruce distinto, ya que ms pacman no puede dar marcha atrás, 
y cada cruce sólo tiene 4 " entradas"

a esto se le sumará que los fantasmas huirán de ms pacman mientras que estén en 
estado comestible hasta que la mitad del tiempo restante del estado comestible sea 
estrictamente menor que la distancia que los separa individualmente, es decir, cada 
fantasma tendrá que hacer esta comprobación, o hasta que se los coma, en cuyo caso 
se irán a la "jaula" y en cuanto salgan, volverán a su rol anterior 

Este modelo de fantasmas obtuvo una puntuación media de 1634.18, con un error 
estándar de 59.44. Consiguió esta puntuación con una media de 893.9 turnos y un
error estándar de 25.69

Ghosts3: modifica este controlador (el archivo de Ghosts2) para fantasmas del juego 
ms pacman de tal forma que los fantasmas evalúen si llegan antes a la píldora de poder 
que ms pacman y, en caso de no hacerlo, empezar la rutina de huida

haz también que solo pueda haber dos fantasmas persiguiendo a ms pacman simultaneamente
se eliminará de la persecución al fantasma que no tenga el rol de perseguidor en el 
momento en el que se quiera unir un tercer fantasma a la persecución

En este caso, hubo que corregir el comportamiento de los fantasmas, ya que siempre se
comprobaba si llegaba antes MsPacMan o ellos, lo que les hacía entrar en un bucle 
infinito de la rutina de huida

Este modelo de fantasmas obtuvo una puntuación media de 1476.12, con un error 
estándar de 41.5. Consiguió esta puntuación con una media de 883.95 turnos y un
error estándar de 45.1

AdvancedBlockingGhosts:  amplía la clase anterior para que el fantasma más apropiado, 
el que está a menos distancia, tape la píldora de poder más cercana a ms pacman en 
caso de que esta esté a tres cruces o menos de distancia

haz también que calcule si llega antes el fantasma o ms pacman, haciendo que si llega 
el fantasma antes, este bloquee el camino y, si tiene la oportunidad, se coma a ms 
pacman; en caso de llegar antes ms pacman, todos los fantasmas empezarán a huir, 
anticipando que ms pacman se los pueda comer

Este modelo de fantasmas obtuvo una puntuación media de 1583.21, con un error 
estándar de 41.6. Consiguió esta puntuación con una media de 934.62 turnos y un
error estándar de 25.50

GhostsZones:  haz un nuevo controlador para el mismo motor en el que los fantasmas 
estén asignados a "zonas", dividiendo el mapa en cuatro, como si se hubiera cortado 
por el medio de formas horizontal y vertical, y cada fantasma será asignado a una zona 
en sentido horario empezando por el sector de arriba a la izquierda

dentro de cada sector, los fantasmas siguen los mismos patrones: intentan tapar el camino 
más probable por el que pueda entrar ms pacman a su sector; en el caso de que ms pacman ya 
esté en su sector, tapan el camino por el que ms pacman podría llegar a la píldora de poder 
más cercana a ella, y, si ocurre que ms pacman está a un cruce o menos de distancia de la 
píldora, y ellos están más lejos, empezarán la rutina de huida de ms pacman, que durará 
hasta que el tiempo en el que son comestibles partido de dos sea menor que la distancia que 
separa a cada uno individualmente de ms pacman; por último, si detectan que llegan antes a 
un cruce hacia el que está yendo ms pacman, irán a él para intentar comerse a ms pacman 

Este modelo de fantasmas obtuvo una puntuación media de 1617.68, con un error 
estándar de 47.36. Consiguió esta puntuación con una media de 1021.59 turnos y un
error estándar de 28.97

Se ha usado el modelo de LLM Gemini Pro

Se decidió que se iba a presentar el modelo de fantasmas Ghosts3 ya que era el que había obtenido 
la media de puntuación más baja, aunque el error estándar no acompañase tanto como con Ghosts2, y 
la media de turnos más baja con diferencia. La combinación de estos dos factores fue lo que determinó
su elección.
