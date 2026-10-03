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

public class MsPacMan2 extends PacmanController {
	private Color[] colours = { Color.RED, Color.PINK, Color.CYAN, Color.ORANGE }; // DEPURACIÓN
	private static final int HOLGURA = 2; // tamaño del pacman

	@Override
	public MOVE getMove(Game game, long timeDue) {
		int posPacman = game.getPacmanCurrentNodeIndex();
		if (game.isJunction(posPacman)) { // Solo hace decisiones si está en un cruce

			Map<MOVE, int[]> caminos = verSiguientesCruces(posPacman, game, 0, game.getPacmanLastMoveMade());
			return seleccionCamino(game, caminos);
		} else
			return null;
	}

	public int[] siguienteCruce(int nodo, MOVE direccion, Game game) {
		int numPildoras = 0;
		int numPPoder = 0;
		int numFantasmas = 0;
		int numComestibles = 0;
		int pasos = 0;
		while (!game.isJunction(nodo)) {
			pasos++;
			int pillIndex = game.getPillIndex(nodo); // comprueba que hay pildora activa
			boolean hayPildora = pillIndex != -1 && game.isPillStillAvailable(pillIndex);
			if (hayPildora)
				numPildoras++;
			int powerIndex = game.getPowerPillIndex(nodo); // comprueba que hay pildora de poder activa
			boolean hayPPoder = powerIndex != -1 && game.isPowerPillStillAvailable(powerIndex);
			if (hayPPoder)
				numPPoder++;
			for (GHOST ghost : GHOST.values()) { // comprueba que hay un fantasma
				if (game.getGhostCurrentNodeIndex(ghost) == nodo) {
					if (game.getGhostEdibleTime(ghost) > 0)
						numComestibles++;
					else if (game.getGhostLastMoveMade(ghost) != direccion) // Solo guarda al fantasma si va en la misma
																			// direccion // direccion al Pacman
						numFantasmas++;
				}
			}
			MOVE[] direcciones = game.getPossibleMoves(nodo, direccion);
			direccion = direcciones[0];
			nodo = game.getNeighbour(nodo, direccion);
		}
		return new int[] { numPildoras, numPPoder, numFantasmas, numComestibles, pasos, nodo }; // nodo guarda la
																								// posicion del cruce
	}

	public Map<MOVE, int[]> verSiguientesCruces(int nodo, Game game, int contador, MOVE dir) {
		contador++;
		MOVE[] direcciones = game.getPossibleMoves(nodo, dir);
		Map<MOVE, int[]> salida = new HashMap<>();
		for (MOVE d : direcciones) {
			int siguienteNodo = game.getNeighbour(nodo, d);
			int[] camino = siguienteCruce(siguienteNodo, d, game);
			salida.put(d, camino);
		}
		if (contador != 2) {
			for (MOVE direccion : salida.keySet()) {
				int[] datosCamino = salida.get(direccion);
				if (datosCamino[2] == 0 && llegoAntes(game, datosCamino[5], datosCamino[4])) { // solo compruebo los
																								// siguientes cruces si
																								// no me cortan antes
					Map<MOVE, int[]> posiblesCaminos = verSiguientesCruces(datosCamino[5], game, contador, direccion);

					MOVE caminoElegido = seleccionCamino(game, posiblesCaminos);
					int[] datosCaminoElegido = posiblesCaminos.get(caminoElegido);
					if (datosCaminoElegido != null) {
						datosCamino[0] += datosCaminoElegido[0]; // Le sumamos los datos del camino elegido al camino
																	// actual
						datosCamino[1] += datosCaminoElegido[1];
						datosCamino[2] += datosCaminoElegido[2];
						datosCamino[3] += datosCaminoElegido[3];
						datosCamino[4] += datosCaminoElegido[4];
						datosCamino[5] = datosCaminoElegido[5];
						salida.put(direccion, datosCamino);
					}
				}
			}
		}
		return salida;
	}

	private GHOST getNearestChasingGhost(int limit, Game game) {
		GHOST nearestGhost = null;
		double minDistance = limit;
		for (GHOST ghostType : GHOST.values()) {
			if (game.getGhostLairTime(ghostType) <= 0 && game.getGhostEdibleTime(ghostType) <= 0) {
				double ghostDistance = game.getDistance(game.getGhostCurrentNodeIndex(ghostType),
						game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghostType), Constants.DM.PATH);
				if (ghostDistance < minDistance && ghostDistance <= limit) {
					minDistance = ghostDistance;
					nearestGhost = ghostType;
				}
			}
		}
		return nearestGhost;
	}

	private boolean llegoAntes(Game game, int cruce, int misPasos) { // funcion de los apuntes
		for (GHOST f : GHOST.values()) {
			if (game.isGhostEdible(f))
				continue; // comestible: no es amenaza
			if (game.getGhostLairTime(f) > 0)
				continue; // encerrado: no está en el mapa
			int nodo = game.getGhostCurrentNodeIndex(f);
			MOVE ult = game.getGhostLastMoveMade(f);
			int susPasos = game.getShortestPathDistance(nodo, cruce, ult);
			if (susPasos < misPasos + HOLGURA)
				return false;
		}
		return true;
	}

	private MOVE seleccionCamino(Game game, Map<MOVE, int[]> caminos) {
		int limit = 30;
		int actFantasmas = 5;
		int actPildoras = -1;
		int actPoder = -1;
		int actComestibles = -1;

		MOVE salida = game.getPacmanLastMoveMade();
		for (MOVE direccion : caminos.keySet()) {
			int[] datosCamino = caminos.get(direccion);
			if (datosCamino[2] == 0 && llegoAntes(game, datosCamino[5], datosCamino[4])) {
				if (datosCamino[2] < actFantasmas) {
					salida = direccion; // Se actualizan todos los datos a la salida actualmente elegida
					actPildoras = datosCamino[0];
					actPoder = datosCamino[1];
					actFantasmas = datosCamino[2];
					actComestibles = datosCamino[3];
				} else if (datosCamino[3] > actComestibles) {
					salida = direccion;
					actPildoras = datosCamino[0];
					actPoder = datosCamino[1];
					actFantasmas = datosCamino[2];
					actComestibles = datosCamino[3];
				} else if (getNearestChasingGhost(limit, game) != null && datosCamino[1] > actPoder) { // Solo mira
																										// pildoras de
																										// poder si hay
																										// un fantasma
																										// cerca
					salida = direccion;
					actPildoras = datosCamino[0];
					actPoder = datosCamino[1];
					actFantasmas = datosCamino[2];
					actComestibles = datosCamino[3];
				} else if (datosCamino[0] > actPildoras && datosCamino[1] == 0) { //ybusca el camino con mas pildoras si no tienen de poder
					salida = direccion;
					actPildoras = datosCamino[0];
					actPoder = datosCamino[1];
					actFantasmas = datosCamino[2];
					actComestibles = datosCamino[3];
				}
			}
			else if (getNearestChasingGhost(limit, game) != null && datosCamino[1] > 0 ) { 
				salida = direccion; 
				actPildoras = datosCamino[0];
				actPoder = datosCamino[1];
				actFantasmas = datosCamino[2];
				actComestibles = datosCamino[3];
				continue;
			}
		}
		return salida;
	}

	public String getName() {
		return "MsPacManPr1_JPC_SBV";
	}
}
