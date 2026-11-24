package es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.mspacman;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public interface Encierro extends Funciones {

	default Movimientos filtrarConEncierro(Game game, Movimientos m) {
		if (m.seguros.length < 2) return m;
		int curr = game.getPacmanCurrentNodeIndex();
		List<MOVE> libres = new ArrayList<>();
		List<MOVE> encerrables = new ArrayList<>();
		for (MOVE move : m.seguros) {
			Rama rama = crearRama(game, curr, move, 0);
			List<Rama> cola = new ArrayList<>();
			if (rama != null) cola.add(rama);
			if (rama != null && !planDesde(game, cola).isEmpty()) encerrables.add(move);
			else libres.add(move);
		}
		if (libres.isEmpty() || encerrables.isEmpty()) return m;
		List<MOVE> siVan = new ArrayList<>(Arrays.asList(m.mortalesSiVan));
		siVan.addAll(encerrables);
		Movimientos f = new Movimientos();
		f.seguros = libres.toArray(new MOVE[0]);
		f.mortalesSeguros = m.mortalesSeguros;
		f.mortalesSiVan = siVan.toArray(new MOVE[0]);
		return f;
	}

	default MOVE movimientoAlLlegar(Game game, int nodo, MOVE move) {
		if (move == MOVE.NEUTRAL) return move;
		for (int i = 0; i < game.getNumberOfNodes(); i++) {
			int next = game.getNeighbour(nodo, move);
			if (next == -1) {
				MOVE[] moves = game.getPossibleMoves(nodo, move);
				if (moves.length != 1) return move;
				move = moves[0];
				continue;
			}
			nodo = next;
			if (game.isJunction(nodo)) return move;
			move = game.getPossibleMoves(nodo, move)[0];
		}
		return move;
	}

	default int pastillaEnPasillo(Game game, int nodo, MOVE move) {
		for (int i = 0; i < game.getNumberOfNodes(); i++) {
			nodo = game.getNeighbour(nodo, move);
			if (nodo == -1) return -1;
			int indice = game.getPowerPillIndex(nodo);
			if (indice != -1 && game.isPowerPillStillAvailable(indice)) return nodo;
			if (game.isJunction(nodo)) return -1;
			MOVE[] moves = game.getPossibleMoves(nodo, move);
			if (moves.length == 0) return -1;
			move = moves[0];
		}
		return -1;
	}

	default int nodoAnterior(Game game, int origen, MOVE salida, int objetivo) {
		int anterior = origen;
		int nodo = origen;
		MOVE move = salida;
		for (int i = 0; i < game.getNumberOfNodes(); i++) {
			nodo = game.getNeighbour(nodo, move);
			if (nodo == -1) return -1;
			if (nodo == objetivo) return anterior;
			if (game.isJunction(nodo)) return -1;
			MOVE[] moves = game.getPossibleMoves(nodo, move);
			if (moves.length == 0) return -1;
			move = moves[0];
			anterior = nodo;
		}
		return -1;
	}

	class Rama {
		final int cruce;
		final int pasos;
		final MOVE movimiento;
		final int previoCruce;
		final int pastilla;
		final int pasosPastilla;
		final int previoPastilla;

		Rama(int cruce, int pasos, MOVE movimiento, int previoCruce, int pastilla, int pasosPastilla, int previoPastilla) {
			this.cruce = cruce;
			this.pasos = pasos;
			this.movimiento = movimiento;
			this.previoCruce = previoCruce;
			this.pastilla = pastilla;
			this.pasosPastilla = pasosPastilla;
			this.previoPastilla = previoPastilla;
		}

		int punto() {
			return pastilla != -1 ? pastilla : cruce;
		}

		int pasosHastaPunto() {
			return pastilla != -1 ? pasosPastilla : pasos;
		}

		int previoPunto() {
			return pastilla != -1 ? previoPastilla : previoCruce;
		}
	}

	default Rama crearRama(Game game, int origen, MOVE salida, int pasosOrigen) {
		int cruce = nextJunction(game, origen, salida);
		if (cruce == -1) return null;
		int pastilla = pastillaEnPasillo(game, origen, salida);
		return new Rama(cruce,
				pasosOrigen + corridorLength(game, origen, salida),
				movimientoAlLlegar(game, origen, salida),
				nodoAnterior(game, origen, salida, cruce),
				pastilla,
				pasosOrigen + powerPillNodes(game, origen, salida),
				pastilla == -1 ? -1 : nodoAnterior(game, origen, salida, pastilla));
	}

	default EnumMap<GHOST, Integer> planEncierro(Game game) {
		int nodoPacman = game.getPacmanCurrentNodeIndex();
		MOVE ultimoMovimientoPacman = game.getPacmanLastMoveMade();

		List<Rama> cola = new ArrayList<>();
		if (game.isJunction(nodoPacman)) {
			for (MOVE salida : game.getPossibleMoves(nodoPacman, ultimoMovimientoPacman)) {
				Rama rama = crearRama(game, nodoPacman, salida, 0);
				if (rama != null) cola.add(rama);
			}
		} else {
			Rama rama = crearRama(game, nodoPacman, pasoHaciaDelante(game, nodoPacman, ultimoMovimientoPacman), 0);
			if (rama != null) cola.add(rama);
		}
		return planDesde(game, cola);
	}

	default EnumMap<GHOST, Integer> planDesde(Game game, List<Rama> cola) {
		List<GHOST> libres = new ArrayList<>();
		for (GHOST g : GHOST.values())
			if (!game.isGhostEdible(g)) libres.add(g);

		EnumMap<GHOST, Integer> plan = new EnumMap<>(GHOST.class);
		if (cola.isEmpty() || !cortar(game, cola, libres, plan, new HashSet<>())) plan.clear();
		return plan;
	}

	default boolean cortar(Game game, List<Rama> cola, List<GHOST> libres, EnumMap<GHOST, Integer> plan, Set<Integer> visitados) {
		if (cola.isEmpty()) return true;
		Set<Integer> puntos = new HashSet<>();
		for (Rama rama : cola) puntos.add(rama.punto());
		if (puntos.size() > libres.size()) return false;

		Rama primera = cola.get(0);
		int punto = primera.punto();

		for (GHOST g : libres) {
			if (!puedeCortar(game, g, primera)) continue;
			plan.put(g, punto);
			List<Rama> resto = new ArrayList<>();
			for (Rama rama : cola)
				if (rama.punto() != punto) resto.add(rama);
			List<GHOST> otrosLibres = new ArrayList<>(libres);
			otrosLibres.remove(g);
			if (cortar(game, resto, otrosLibres, plan, visitados)) return true;
			plan.remove(g);
		}

		if (primera.pastilla == -1 && !visitados.contains(primera.cruce)) {
			List<Rama> siguiente = new ArrayList<>(cola.subList(1, cola.size()));
			for (MOVE salida : game.getPossibleMoves(primera.cruce, primera.movimiento)) {
				Rama hija = crearRama(game, primera.cruce, salida, primera.pasos);
				if (hija != null) siguiente.add(hija);
			}
			Set<Integer> nuevosVisitados = new HashSet<>(visitados);
			nuevosVisitados.add(primera.cruce);
			if (cortar(game, siguiente, libres, plan, nuevosVisitados)) return true;
		}
		return false;
	}

	default boolean puedeCortar(Game game, GHOST g, Rama rama) {
		int punto = rama.punto();
		if (ghostTimeTo(game, g, punto) >= rama.pasosHastaPunto()) return false;

		boolean enCarcel = game.getGhostLairTime(g) > 0;
		int origen = enCarcel ? game.getGhostInitialNodeIndex() : game.getGhostCurrentNodeIndex(g);
		MOVE movimiento = enCarcel ? MOVE.NEUTRAL : game.getGhostLastMoveMade(g);

		if (origen == punto) {
			MOVE haciaPacman = game.getMoveToMakeToReachDirectNeighbour(punto, rama.previoPunto());
			for (MOVE posible : game.getPossibleMoves(punto, movimiento))
				if (posible == haciaPacman) return true;
			return false;
		}

		int[] camino = game.getShortestPath(origen, punto, movimiento);
		if (camino.length == 0) return false;
		int previo = camino.length == 1 ? origen : camino[camino.length - 2];
		return previo != rama.previoPunto();
	}
}
