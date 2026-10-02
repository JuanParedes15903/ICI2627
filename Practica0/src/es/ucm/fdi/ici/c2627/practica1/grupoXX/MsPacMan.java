package es.ucm.fdi.ici.c2627.practica1.grupoXX;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pacman.controllers.PacmanController;
import pacman.game.Game;
import pacman.game.GameView;
import pacman.game.Constants;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

public class MsPacMan extends PacmanController {
	private Color[] colours = { Color.RED, Color.PINK, Color.CYAN, Color.ORANGE }; // DEPURACIÓN

	@Override
	public MOVE getMove(Game game, long timeDue) {
		int posPacman = game.getPacmanCurrentNodeIndex();
		if (game.isJunction(posPacman)) { // Solo hace decisiones si está en un cruce
			Map<MOVE, int[]> caminos = verSiguientesCruces(posPacman, game);
			int actFantasmas = 5;
			int actPildoras = -1;
			int actPoder = -1;
			int actComestibles=-1;
			MOVE salida=game.getPacmanLastMoveMade();
			for (MOVE direccion : caminos.keySet()) {
				int[] datosCamino = caminos.get(direccion);
				if(datosCamino[2]<actFantasmas) {
					salida=direccion;
					actFantasmas=datosCamino[2];
				}
				else if(datosCamino[3]>actComestibles) {
					salida=direccion;
					actComestibles=datosCamino[3];
				}
				/*else if(datosCamino[1]>actPoder) {
					salida=direccion;
					actFantasmas=datosCamino[3];
				}*/
				else if(datosCamino[0]>actPildoras) {
					salida=direccion;
					actPildoras=datosCamino[0];
				}
			}
			return salida;
		} else
			return null;
	}

	public int[] siguienteCruce(int nodo, MOVE direccion, Game game) {
		int numPildoras = 0;
		int numPPoder = 0;
		int numFantasmas = 0;
		int numComestibles = 0;
		while (!game.isJunction(nodo)) {
			int pillIndex = game.getPillIndex(nodo); // comprueba que hay pildora activa
			boolean hayPildora = pillIndex != -1 && game.isPillStillAvailable(pillIndex);
			if (hayPildora)
				numPildoras++;
			int powerIndex = game.getPowerPillIndex(nodo); // comprueba que hay pildora de poder activa
			boolean hayPPoder = powerIndex != -1 && game.isPowerPillStillAvailable(powerIndex);
			if (hayPPoder)
				numPPoder++;
			for (GHOST ghost : GHOST.values()) { // comprueba que hay un fantasma (POSIBLE comprobar en que direccion va
													// el fantasma para no contarlo)
				if (game.getGhostCurrentNodeIndex(ghost) == nodo) {
					if (game.getGhostEdibleTime(ghost) > 0)
						numComestibles++;
					else
						numFantasmas++;
				}
			}
			MOVE[] direcciones = game.getPossibleMoves(nodo, direccion);
			direccion = direcciones[0];
			nodo = game.getNeighbour(nodo, direccion);
		}
		return new int[] { numPildoras, numPPoder, numFantasmas,numComestibles };
	}

	public Map<MOVE, int[]> verSiguientesCruces(int nodo, Game game) {
		MOVE direccionActual = game.getPacmanLastMoveMade();
		MOVE[] direcciones = game.getPossibleMoves(nodo, direccionActual);
		Map<MOVE, int[]> salida = new HashMap<>();
		for (MOVE d : direcciones) {
			int siguienteNodo = game.getNeighbour(nodo, d);
			int[] camino = siguienteCruce(siguienteNodo, d, game);
			salida.put(d, camino);
		}
		return salida;
	}

	public String getName() {
		return "MsPacManPr1_JPC_SBV";
	}
}
