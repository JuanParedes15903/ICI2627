#Diario Práctica 2 grupo JPCSBV
Integrantes: Juan Paredes Cruz y Sergio Barbas Vázquez
Concurso:
Medimos siempre 300 partidas contra: los enemigos proporcionados en el campus.

#MsPacMan:
## Viernes 17: versión 0
Se crea a mano MsPacmanV0 según las indicacions de la guía y se crean las clases correspondientes a cada transición y a cada estado. Se elimina el estado "Acercarse a la píldora de poder segura más cercana" ya que cumple el mismo objetivo que "ir a por píldora de poder" y se cambia la transición "Camino a pildora bloqueado" por "Camino a todas las pildoras de poder bloqueados". 
Para hacer las Transiciones y los estados se usa el siguiente prompt con la IA: "Crea en MsPacManInput todas las funciones necesarias para realizar correctamente cada Transition según se encuentra descrita en su toString. Además, para la función de mirar fantasmas por delante aplica la lectura de caminos en dos cruces que está implementada en la práctica 1".
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

##Domingo 19: