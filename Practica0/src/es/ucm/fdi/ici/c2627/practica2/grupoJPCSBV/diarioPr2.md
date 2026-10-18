#Diario Práctica 2 grupo JPCSBV
Integrantes: Juan Paredes Cruz y Sergio Barbas Vázquez
Concurso: Fantasmas V1
Medimos siempre 300 partidas contra: los enemigos proporcionados en el campus.

#MsPacMan:
## Viernes 17: versión 0
Se crea a mano MsPacmanV0 según las indicacions de la guía y se crean las clases correspondientes a cada transición y a cada estado. Se elimina el estado "Acercarse a la píldora de poder segura más cercana" ya que cumple el mismo objetivo que "ir a por píldora de poder" y se cambia la transición "Camino a pildora bloqueado" por "Camino a todas las pildoras de poder bloqueados". 

Después se usó la IA para crear la lógica de cada estado con el siguiente prompt: 
"Para todo lo que voy a escribir puedes coger métodos de la práctica 1

Crea las acciones: 
 acercarseAFantasmasAction: quiero que se acerque a los fantasmas pero que nunca vaya por un camino en el que le vaya a coger, simplemente acercarse al fantasma para atraerle y qeu deje libre las pildoras. 
BuscarCaminosConMasPildorasSinPoderAction: quiero que vaya por los caminos con más pildoras pero que evite los caminos que contengan pildoras de poder.
BuscarCaminosConPildorasSegurosAction: Que mirando todo el mapa elija el camino mas seguro hacia la pildora mas cercana, es decir que vaya hacia la pildora mas cercana evitando los fantasmas mirando en frente los caminos evitando que los fantasmas lleguen antes a los cruces siguientes.
BuscarCaminosPPillAction: Que vaya a caminos con pildoras de poder si llega al siguiente cruce antes que los fantasmas y no hay fantasmas en el propio camino
EvitarCaminosConFantasmasAction: Quiero que evite caminos con fantasmas y que evite caminos que los fantasmas puedan llegar a bloquear.
HuirhaciapildorasAction: Evitar los caminos en los que pueden llegar fantasmas o ya hay fantasmas pero yendo a la vez hacia pildoras.
IrAPPillAction: Que vaya a caminos con pildoras de poder si llega al siguiente cruce antes que los fantasmas y no hay fantasmas en el propio camino
perseguirComestiblesAction: que vaya a por los fantasmas comestibles mas cercanos si no hay fantasmas agresivos en el camino y no van a cortar el paso en el siguiente cruce

También crea las transiciones y modifica MsPacManInput  y MsPacManV0 para que cumpla los criterios:
AlgunaPildoraDesbloqueada: Hay alguna pildora en el mapa que su camino completo está libre y en ningún punto un fantasma va a cortar el paso.
CaminoAPildorasDePoderBloqueados: el camino hacia todas las pildoras de poder están bloqueados.
GrupoCercaTransaction: Hay varios fantasmas cerca de PacMan siguiendole por detrás.
GrupoDisueltoTransacion: deja de haber varios fantasmas cerca de Pacman siguiendole por detrás.
HayFantasmasPorDelante: Aplicando la lectura en dos cruces de la Practica1 dice si hay algun fantasma a dos cruces por delante de pacman.
HayFantasmasPorDelanteYMasNoComestiblesQueComestibles:Junta las condiciones de ambas transiciones
HayMasComestiblesQueNoComestibles: de los fantasmas hay mas que tienen el estado de comestible que de no comestible.
NoHayFantasmasPorDelante:Aplicando la lectura en dos cruces de la Practica1 dice si no hay algun fantasma a dos cruces por delante de pacman.
NoHayFantasmasPorDelanteYMasNoComestiblesQueComestibles:Junta las condiciones de ambas transiciones.
NoHayPPills: no quedan PPills en el mapa
TodasLasPildorasBloqueadas: el camino a todas las pildoras del mapa está bloqueado por los fantasmas. Es decir: están en medio o van a llegar antes a un cruce en medio del camino hasta la pildora.

Cuando modifiques MsPacManInput mantén la estrucutra de funciones privadas con getters publicos"

Las puntuaciones de MsPacManV0 son de:
GhostBasico: 4318,8 +- 90,17281611994333
GhostIntermedio: 4187,3 +- 110,94560183410705
GhostAvanzado: 2626,5666666666666 +- 55,63226966178925
Ghosts3: 3839,1 +- 78,35923742090766

## Sábado 18: versión 1 y 2
Para comprobar que la decisión de coger pildoras cuando hay 2 fantasmas cerca es correcta se prueban dos versiones distintas: una que se cambie a buscar pildoras de poder cuando hay 1 fantasma cerca y la otra cuando hay 3. 

Versión 1, con el cambio a 1 fantasma: 

GhostBasico: 4463.366666666667 +- 84,1440011425618		Cambio respecto a V0:  D=144.56666, ET_D= 90,17281611994333 Diferencia real: D < 2*ET_D No hay cambios notables en la puntuación
GhostIntermedio: 4304,566666666667 +- 98,56056791700915 Cambio respecto a V0: D=117,266666666667, ET_D=110,94560183410705 Diferencia real: D < 2*ET_D No hay cambios notables en la puntuación
GhostAvanzado: 2828,6 +- 48,58637242359508 		Cambio respecto a V0:  D=202,0333333333334, ET_D= 48,58637242359508 Diferencia real: D > 2*ET_D Sí hay mejora
Ghosts3: 4032,6 +- 76,22298830725737		Cambio respecto a V0: D=193,5, ET_D= 78,35923742090766 Diferencia real: D > 2*ET_D Sí hay mejora

A pesar de que no haya mejora en las dos dificultades más bajas, sí que hay una mejora en los otros dos enemigos más complicados por lo que nos quedamos con la versión 1.

Versión 2, con el cambio a 3 fantasmas:

GhostBasico: 4375.533333333334 +- 112.20464336124677		
GhostIntermedio: 3852.733333333333 +- 108.38444512475884 
GhostAvanzado: 2554.4333333333334 +- 50.31242454079846 		
Ghosts3: 3716.133333333333 +- 92.50300634884728 	

Como las puntuaciones son inferiores contra todos los enemigos y las desviaciones son más altas contra todos, sabemos que no hay mejora.




# Fantasmas:


## vie 16 oct · Acciones y Transitions originales
Actions de fantasmas (4): EsperarSalirAction, HuirDispersosAction, HuirMsPacmanAction, PerseguirAction
Transitions de fantasmas (4): ComidoTransition, GenericTransition, MasCercaAMsPacmanQueAPPTransition, TiempoComestibleCeroTransition
al principio había solo tres clases action: EsperarSalirAction, PerseguirAction y HuirAction, pero esta última se dividió en dos para que una se aleje lo máximo posible de ms pacman y la otra intente alejarse también de los demás fantasmas, para que ms pacman tenga menos posibilidades de comerselos a todos a la vez
ia: sí
se usaron dos prompts:
-en el primero, se pasó el diseño creado a papel y las clases necesarias para poder trabajar, como Action, Input, etc, y se pidió que se adaptara el diagrama a un diseño fsm usando las clases proporcionadas
-en el segundo, se usó este prompt: quiero dividir la clase HuirAction en dos, de tal forma que: 
- en caso de estar demasiado cerca de ms pacman, considerando demasiado cerca unos 20 nodos, se tomen el camino que les vaya a hacer alejarse lo máximo posible de ella
- por otro lado, si ya están lo suficientemente lejos (> 20 nodos), los fantasmas tomen los caminos que vayan a hacer que se mantengan lo más alejados los unos de los otros y de ms pacman a la vez
estas clases se llamarán HuirMsPacmanAction y HuirDispersosAction respectivamente
## sab 17 oct · Fantasmas versión 0
La clase GhostFSMController es la primera versión, o versión 0, del controlador de los fantasmas
Inicialmente, tenía 2 estados compuestos, HUIR y CAZAR, y estaba formada por 9 estados simples
No se tomaron datos a la hora de probar esta primera versión, pero se detectó un problema de flickering en la condición de "liberar" el rol de perseguidor, ya que la condición era que saliera el perseguidor más "veterano" si en algún momento había más de dos perseguidores, pero esto causaba que el que dejaba de ser perseguidor tomara como solución más óptima volver a ser perseguidor, haciendo en la práctica que todos terminaran persiguiendo a ms pacman en fila 

**Resultados tras la corrección del flickering**
            Media Puntos       Error estándar       Media turnos    Error estándar
Básico:     2325                63.94               1232            68
Intermedio: 1916                78                  3888            33
Avanzado:   10086               203                 1305            71

ia: sí, se ha usado para detectar posibles errores de lógica en las diferentes clases
en caso de querer revisar el prompt, se subirá en un archivo aparte para no enturbiar este, ya que es el código de todas las clases con una petición de localizar posibles errores de lógica

## dom 18 oct · Fantasmas versión 1
Se añade una nueva condición a la acción de perseguir para que se intente que no haya varios fantasmas persiguiendo a ms pacman por el mismo pasillo, intentando que se genere de forma orgánica una estrategia de pinza

**Resultados tras la corrección del flickering**
            Media Puntos       Error estándar       Media turnos    Error estándar
Básico:     2521                66                  1085            68
Intermedio: 2398                88                  3549            56
Avanzado:   10176               173                 1011            51

como se puede observar, esta versión es, por lo general, más rápida que la anterior y, por lo general, aunque empeora las puntuaciones, no es una cantidad desorbitada, por lo que se ha optado por aceptar esta nueva versión

ha habido un problema a la hora de gestionar las clases, ya que se han ido aplicando las modificaciones en las mismas clases siempre, por lo que, en caso de querer comprobar las clases relativas a la versión 0, hay que copiar lo que hay en el archivo v0 al que no tiene extensión

ia: sí, para añadir la nueva estrategia a la acción de perseguir, usando este prompt
integra una estrategia de coordinación espacial obteniendo los movimientos posibles y puntuándolos, haciendo que si el nodo vecino al que lleva un movimiento está a menos de 5 nodos de distancia de otro fantasma persiguiendo, se descarte o se penalice fuertemente
