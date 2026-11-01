package es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman;

import static pacman.game.Constants.EAT_DISTANCE;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public interface Funciones {

	int INFINITO = (int) 1e9;

	default int pastillaDePoderMasCercana(Game game, int nodo, MOVE move) {
		int mejor = -1;
		int distanciaMejor = Integer.MAX_VALUE;
		for (int pp : game.getActivePowerPillsIndices()) {
			int distancia = game.getShortestPathDistance(nodo, pp, move);
			if (distancia < distanciaMejor) {
				distanciaMejor = distancia;
				mejor = pp;
			}
		}
		return mejor;
	}

	default MOVE pasoHaciaDelante(Game game, int nodo, MOVE move) {
		if (game.getNeighbour(nodo, move) != -1) return move;
		return game.getPossibleMoves(nodo, move)[0];
	}

	default int contarPasillosConPastillas(Game game) {
		Set<String> pasillos = new HashSet<>();
		for (int pill : game.getActivePillsIndices())
			pasillos.add(pasilloDe(game, pill));
		return pasillos.size();
	}

	default String pasilloDe(Game game, int nodo) {
		if (game.isJunction(nodo)) return nodo + "-" + nodo;
		MOVE[] salidas = game.getPossibleMoves(nodo);
		int a = nextJunction(game, nodo, salidas[0]);
		int b = nextJunction(game, nodo, salidas[1]);
		return Math.min(a, b) + "-" + Math.max(a, b);
	}

	default boolean sureDeath(Game game, int curr, MOVE move) {
		int nodesToPowerPill = powerPillNodes(game, curr, move);

		for (GHOST g : GHOST.values()) {
			if (corridorCollisionTime(game, curr, g, move) < nodesToPowerPill) return true;
		}
		return false;
	}

	default int nearestEdibleGhost(Game game, int node, MOVE move) {
		int nearest = (int)1e9;
		for (GHOST g : GHOST.values()) {
			int edibleTime = game.getGhostEdibleTime(g);
			if (edibleTime == 0) continue;

			int dist = 1 + game.getShortestPathDistance(game.getNeighbour(node, move), game.getGhostCurrentNodeIndex(g), move);
			if (1 + tiempoDeChoque(game, game.getNeighbour(node, move), move, g) < edibleTime) nearest = Math.min(nearest, dist);
		}
		return nearest;
	}

	default int nextJunction(Game game, int node, MOVE move) {
		for (int i = 0; i < game.getNumberOfNodes(); i++) {
			int next = game.getNeighbour(node, move);
			if (next == -1) return -1;
			node = next;
			if (game.isJunction(node)) return node;
			MOVE[] moves = game.getPossibleMoves(node, move);
			if (moves.length == 0) {
				return -1;
			}
			move = moves[0];
		}
		return -1;
	}

	default int ghostTimeTo(Game game, GHOST g, int node) {
		int lairTime = game.getGhostLairTime(g);
		if (lairTime > 0) return lairTime + game.getShortestPathDistance(game.getGhostInitialNodeIndex(), node);

		int dist = game.getShortestPathDistance(game.getGhostCurrentNodeIndex(g), node, game.getGhostLastMoveMade(g));
		int edibleTime = game.getGhostEdibleTime(g);
		int slowNodes = Math.min(dist, edibleTime / 2);
		return dist + slowNodes;
	}

	default int corridorCollisionTime(Game game, int curr, GHOST g, MOVE move) {
		int junction = nextJunction(game, curr, move);
		int corridorLength = corridorLength(game, curr, move);
		int edibleTime = game.getGhostEdibleTime(g);

		if (game.getGhostLairTime(g) == 0) {
			int ghostNode = game.getGhostCurrentNodeIndex(g);
			int pacNextNode = game.getNeighbour(curr, move);
			int ghostSteps = 1 + game.getShortestPathDistance(pacNextNode, ghostNode, move);

			if (ghostSteps <= corridorLength) {
				boolean comingTowardsPac = game.getShortestPathDistance(ghostNode, curr, game.getGhostLastMoveMade(g)) <= ghostSteps;
				if (!comingTowardsPac) return (int)1e9;
				int contact = collisionTime(ghostSteps - EAT_DISTANCE, edibleTime);
				return edibleTime > contact ? (int)1e9 : contact;
			}
		}

		int ghostTime = ghostTimeTo(game, g, junction);
		int distThen = corridorLength - ghostTime;
		int edibleThen = Math.max(0, edibleTime - ghostTime);
		int contact = ghostTime + collisionTime(distThen - EAT_DISTANCE, edibleThen);

		if (contact > corridorLength + EAT_DISTANCE) return (int)1e9;
		if (edibleTime > contact) return (int)1e9;
		return contact;
	}

	default int collisionTime(int dist, int edibleTime) {
		if (dist <= 0) return 0;

		int pacEdibleNodes = edibleTime;
		int ghostEdibleNodes = (edibleTime + 1) / 2;
		int closedWhileEdible = pacEdibleNodes + ghostEdibleNodes;

		if (dist <= closedWhileEdible) {
			int ghostMovesFirst = edibleTime % 2;
			return (2 * dist - ghostMovesFirst + 2) / 3;
		}

		int distAfterEdible = dist - closedWhileEdible;
		return edibleTime + (distAfterEdible + 1) / 2;
	}

	default int tiempoDeChoque(Game game, int nodo, MOVE move, GHOST comestible) {
		int nodoComestible = game.getGhostCurrentNodeIndex(comestible);
		int distancia = game.getShortestPathDistance(nodo, nodoComestible, move);
		MOVE pasoComestible = pasoHaciaDelante(game, nodoComestible, game.getGhostLastMoveMade(comestible));
		boolean deFrente = game.getNextMoveTowardsTarget(nodoComestible, nodo, DM.PATH) == pasoComestible;
		if (deFrente) return collisionTime(distancia - EAT_DISTANCE, game.getGhostEdibleTime(comestible));
		return 2 * (distancia - EAT_DISTANCE);
	}

	default int powerPillNodes(Game game, int curr, MOVE move) {
		int node = curr;
		for (int steps = 1; steps <= game.getNumberOfNodes(); steps++) {
			node = game.getNeighbour(node, move);
			if (node == -1) break;

			int powerPillIndex = game.getPowerPillIndex(node);
			if (powerPillIndex != -1 && game.isPowerPillStillAvailable(powerPillIndex)) return steps;

			if (game.isJunction(node)) break;
			MOVE[] moves = game.getPossibleMoves(node, move);
			if (moves.length == 0) break;
			move = moves[0];
		}
		return (int)1e9;
	}

	default int cruceAlQueVa(Game game, GHOST g) {
		int nodo = game.getGhostCurrentNodeIndex(g);
		return nextJunction(game, nodo, pasoHaciaDelante(game, nodo, game.getGhostLastMoveMade(g)));
	}

	default GHOST fantasmaAInterceptar(Game game) {
		int nodoPacman = game.getPacmanCurrentNodeIndex();
		MOVE ultimoMovimientoPacman = game.getPacmanLastMoveMade();
		GHOST mejor = null;
		int mejorDistancia = Integer.MAX_VALUE;
		for (GHOST g : GHOST.values()) {
			if (!game.isGhostEdible(g)) continue;
			int cruce = cruceAlQueVa(game, g);
			if (cruce == -1) continue;
			int distancia = game.getShortestPathDistance(nodoPacman, cruce, ultimoMovimientoPacman);
			if (distancia < ghostTimeTo(game, g, cruce)
					&& tiempoDeChoque(game, nodoPacman, ultimoMovimientoPacman, g) < game.getGhostEdibleTime(g)
					&& distancia < mejorDistancia) {
				mejorDistancia = distancia;
				mejor = g;
			}
		}
		return mejor;
	}

	default List<GHOST> planCadena(Game game) {
		List<GHOST> comestibles = new ArrayList<>();
		for (GHOST g : GHOST.values())
			if (game.isGhostEdible(g)) comestibles.add(g);
		List<GHOST> mejor = new ArrayList<>();
		int[] mejorTiempo = {Integer.MAX_VALUE};
		buscarCadena(game, game.getPacmanCurrentNodeIndex(), 0, comestibles, new ArrayList<>(), mejor, mejorTiempo);
		return mejor;
	}

	default void buscarCadena(Game game, int nodo, int t, List<GHOST> restantes, List<GHOST> actual,
			List<GHOST> mejor, int[] mejorTiempo) {
		if (actual.size() > mejor.size() || (actual.size() == mejor.size() && !actual.isEmpty() && t < mejorTiempo[0])) {
			mejor.clear();
			mejor.addAll(actual);
			mejorTiempo[0] = t;
		}
		for (GHOST c : new ArrayList<>(restantes)) {
			int nodoC = game.getGhostCurrentNodeIndex(c);
			int distancia = actual.isEmpty()
					? game.getShortestPathDistance(nodo, nodoC, game.getPacmanLastMoveMade())
					: game.getShortestPathDistance(nodo, nodoC) + t / 2;
			int tiempo = actual.isEmpty()
					? Math.max(0, tiempoDeChoque(game, nodo, game.getPacmanLastMoveMade(), c))
					: Math.max(0, 2 * (distancia - EAT_DISTANCE));
			if (t + tiempo >= game.getGhostEdibleTime(c)) continue;
			restantes.remove(c);
			actual.add(c);
			buscarCadena(game, nodoC, t + tiempo, restantes, actual, mejor, mejorTiempo);
			actual.remove(actual.size() - 1);
			restantes.add(c);
		}
	}

	default int pillsInCorridor(Game game, int curr, MOVE move) {
		int count = 0;
		int node = curr;
		for (int i = 0; i < game.getNumberOfNodes(); i++) {
			node = game.getNeighbour(node, move);
			if (node == -1) break;

			int pillIndex = game.getPillIndex(node);
			if (pillIndex != -1 && game.isPillStillAvailable(pillIndex)) count++;

			if (game.isJunction(node)) break;
			MOVE[] moves = game.getPossibleMoves(node, move);
			if (moves.length == 0) break;
			move = moves[0];
		}
		return count;
	}

	default int corridorLength(Game game, int node, MOVE move) {
		for (int ticks = 1; ticks <= game.getNumberOfNodes(); ticks++) {
			node = game.getNeighbour(node, move);
			if (node == -1 || game.isJunction(node)) return ticks;
			MOVE[] moves = game.getPossibleMoves(node, move);
			if (moves.length == 0) return ticks;
			move = moves[0];
		}
		return -1;
	}

	default int getNearestPill(Game game, int pacNode) {
    	double closestDistance = Double.MAX_VALUE;
    	int pillNode = -1;

    	for(int pill : game.getActivePillsIndices()) {
    		double pillDistance = game.getShortestPathDistance(pacNode, pill, game.getPacmanLastMoveMade());

    		if (pillDistance<closestDistance) {
    			closestDistance=pillDistance;
    			pillNode=pill;
    		}
    	}
    	return pillNode;
    }
}
