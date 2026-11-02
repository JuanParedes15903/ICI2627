#Prompts Ghosts

#**V1**
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
 
 #**V2**
 # Prompt 1
 Añademe una regla que compruebe que el tiempo que le queda de ser comestible sea el suficiente para que PacMan no le pueda comer. En este caso la acción que debe tomar es la de perseguir ya implementada.
 
 #Prompt 2
 Añade una regla que compruebe el número de Píldoras de poder y de píldoras en el mapa. Si no quedan píldoras de poder y quedan pocas píldoras haz que los fantasmas persigan a PacMan
 
 #**V3**
 #Prompt 1
 Añade una regla que compruebe la distancia de MsPacMan a una PPill y haga que el fantasma más cercano intente bloquear su camino por delante si está lo suficientemente cerca. Implementa la acción correspondiente.
 #Prompt  2 
 Añade una regla que compruebe si hay otro fantasma distinto al actual que esté cerca de MsPacMan. En caso positivo se activa la acción cerco que hace que el fantasma corte el camino a PacMan en el siguiente cruce. Implementa la acción correspondiente