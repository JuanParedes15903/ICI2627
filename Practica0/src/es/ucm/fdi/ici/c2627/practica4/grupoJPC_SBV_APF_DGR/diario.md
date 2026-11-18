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
