package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.MsPacMan;

import es.ucm.fdi.ici.Input;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacManInput extends Input {

	private static final int HOLGURA_CAMINO = 4;

	private int[] distanciaAmenaza; // por fantasma: pasos que le faltan para llegar a mí
	private int amenazaMasCercana;
	private int amenazasCerca;
	private int pildorasDePoder;
	private boolean masComestiblesQueNoComestibles;
	private boolean fantasmasPorDelante;
	private int fantasmasCercaPorDetras;
	private boolean todasLasPildorasBloqueadas;
	private boolean algunaPildoraDesbloqueada;
	private boolean caminosAPildorasDePoderBloqueados;

	public MsPacManInput(Game game) {
		super(game);
	}

	@Override
	public void parseInput() {
		int yo = game.getPacmanCurrentNodeIndex();
		distanciaAmenaza = new int[GHOST.values().length];
		for (GHOST g : GHOST.values()) {
			if (Boolean.TRUE.equals(game.isGhostEdible(g)) || game.getGhostLairTime(g) > 0
					|| game.getGhostCurrentNodeIndex(g) < 0)
				distanciaAmenaza[g.ordinal()] = Integer.MAX_VALUE; // no me puede comer
			else
				distanciaAmenaza[g.ordinal()] = game.getShortestPathDistance(game.getGhostCurrentNodeIndex(g), yo,
						game.getGhostLastMoveMade(g));
		}
		amenazaMasCercana = calcularAmenazaMasCercana();
		amenazasCerca = calcularAmenazasA(40);
		fantasmasCercaPorDetras = calcularFantasmasCercaPorDetras(40);
		pildorasDePoder = game.getNumberOfActivePowerPills();
		masComestiblesQueNoComestibles = calcularMasComestiblesQueNoComestibles();
		fantasmasPorDelante = calcularFantasmasPorDelante();
		todasLasPildorasBloqueadas = calcularTodasLasPildorasBloqueadas();
		algunaPildoraDesbloqueada = calcularAlgunaPildoraDesbloqueada();
		caminosAPildorasDePoderBloqueados = calcularCaminosAPildorasDePoderBloqueados();
	}

	/** Pasos que le faltan a la amenaza más próxima para alcanzarme. */
	private int calcularAmenazaMasCercana() {
		int min = Integer.MAX_VALUE;
		for (int d : distanciaAmenaza)
			min = Math.min(min, d);
		return min;
	}

	/**
	 * Cuántos fantasmas que pueden comerme están a menos de {@code radio} pasos.
	 */
	private int calcularAmenazasA(int radio) {
		int n = 0;
		for (int d : distanciaAmenaza)
			if (d < radio)
				n++;
		return n;
	}

	public int getPildorasDePoder() {
		return pildorasDePoder;
	}

	public boolean getMasComestiblesQueNoComestibles() {
		return masComestiblesQueNoComestibles;
	}

	public int getAmenazaMasCercana() {
		return amenazaMasCercana;
	}

	public int getAmenazasCerca() {
		return amenazasCerca;
	}

	public int getFantasmasCercaPorDetras() {
		return fantasmasCercaPorDetras;
	}

	public boolean getFantasmasPorDelante() {
		return fantasmasPorDelante;
	}

	public boolean getTodasLasPildorasBloqueadas() {
		return todasLasPildorasBloqueadas;
	}

	public boolean getAlgunaPildoraDesbloqueada() {
		return algunaPildoraDesbloqueada;
	}

	public boolean getCaminosAPildorasDePoderBloqueados() {
		return caminosAPildorasDePoderBloqueados;
	}

	private boolean calcularMasComestiblesQueNoComestibles() {
		int comestibles = 0;
		int noComestibles = 0;

		for (GHOST ghost : GHOST.values()) {
			if (game.isGhostEdible(ghost)) {
				comestibles++;
			} else {
				noComestibles++;
			}
		}
		return comestibles > noComestibles;
	}

	private boolean calcularFantasmasPorDelante() {
		int pacman = game.getPacmanCurrentNodeIndex();
		MOVE lastMove = game.getPacmanLastMoveMade();

		if (pacman < 0 || lastMove == null) {
			return false;
		}

		for (MOVE direccion : game.getPossibleMoves(pacman, lastMove)) {
			int siguienteNodo = game.getNeighbour(pacman, direccion);
			if (siguienteNodo >= 0
					&& caminoConFantasmasEnDosCruces(siguienteNodo, direccion, 1, 1)) {
				return true;
			}
		}

		return false;
	}

	private boolean caminoConFantasmasEnDosCruces(int nodo, MOVE direccion,
			int crucesLeidos, int pasos) {
		int nodoActual = nodo;
		MOVE direccionActual = direccion;
		int pasosActuales = pasos;

		while (nodoActual >= 0) {
			if (hayFantasmaPeligrosoEn(nodoActual)) {
				return true;
			}

			if (game.isJunction(nodoActual)) {
				if (!llegoAntesAlCruce(nodoActual, pasosActuales)) {
					return true;
				}
				if (crucesLeidos >= 2) {
					return false;
				}

				for (MOVE siguienteDireccion : game.getPossibleMoves(
						nodoActual, direccionActual)) {
					int siguienteNodo = game.getNeighbour(nodoActual, siguienteDireccion);
					if (siguienteNodo >= 0
							&& caminoConFantasmasEnDosCruces(
									siguienteNodo,
									siguienteDireccion,
									crucesLeidos + 1,
									pasosActuales + 1)) {
						return true;
					}
				}
				return false;
			}

			MOVE[] siguientesMovimientos = game.getPossibleMoves(
					nodoActual, direccionActual);
			if (siguientesMovimientos.length == 0) {
				return false;
			}

			direccionActual = siguientesMovimientos[0];
			nodoActual = game.getNeighbour(nodoActual, direccionActual);
			pasosActuales++;
		}

		return false;
	}

	private boolean llegoAntesAlCruce(int cruce, int misPasos) {
		for (GHOST fantasma : GHOST.values()) {
			if (Boolean.TRUE.equals(game.isGhostEdible(fantasma))
					|| game.getGhostLairTime(fantasma) > 0) {
				continue;
			}

			int nodoFantasma = game.getGhostCurrentNodeIndex(fantasma);
			MOVE ultimoMovimiento = game.getGhostLastMoveMade(fantasma);
			if (nodoFantasma < 0 || ultimoMovimiento == null) {
				continue;
			}

			int pasosFantasma = game.getShortestPathDistance(
					nodoFantasma, cruce, ultimoMovimiento);
			if (pasosFantasma < misPasos + HOLGURA_CAMINO) {
				return false;
			}
		}

		return true;
	}

	private boolean hayFantasmaPeligrosoEn(int nodo) {
		for (GHOST fantasma : GHOST.values()) {
			if (!Boolean.TRUE.equals(game.isGhostEdible(fantasma))
					&& game.getGhostLairTime(fantasma) <= 0
					&& game.getGhostCurrentNodeIndex(fantasma) == nodo) {
				return true;
			}
		}
		return false;
	}

	private boolean calcularTodasLasPildorasBloqueadas() {
		int[] pildoras = game.getActivePillsIndices();
		int[] pildorasDePoder = game.getActivePowerPillsIndices();

		if (pildoras.length == 0 && pildorasDePoder.length == 0) {
			return false;
		}

		for (int pill : pildoras) {
			if (!estaPildoraBloqueada(pill)) {
				return false;
			}
		}
		for (int pill : pildorasDePoder) {
			if (!estaPildoraBloqueada(pill)) {
				return false;
			}
		}

		return true;
	}

	private boolean calcularAlgunaPildoraDesbloqueada() {
		for (int pill : game.getActivePillsIndices()) {
			if (!estaPildoraBloqueada(pill)) {
				return true;
			}
		}
		for (int pill : game.getActivePowerPillsIndices()) {
			if (!estaPildoraBloqueada(pill)) {
				return true;
			}
		}
		return false;
	}

	private boolean calcularCaminosAPildorasDePoderBloqueados() {
		int[] powerPills = game.getActivePowerPillsIndices();
		if (powerPills.length == 0) {
			return true;
		}
		for (int pill : powerPills) {
			if (!estaPildoraBloqueada(pill)) {
				return false;
			}
		}
		return true;
	}

	private int calcularFantasmasCercaPorDelante(int radio) {
		return calcularAmenazasA(radio);
	}

	private int calcularFantasmasCercaPorDetras(int radio) {
		int pacman = game.getPacmanCurrentNodeIndex();
		MOVE pacmanMove = game.getPacmanLastMoveMade();
		if (pacman < 0 || pacmanMove == null) {
			return 0;
		}

		int encontrados = 0;
		for (GHOST ghost : GHOST.values()) {
			if (Boolean.TRUE.equals(game.isGhostEdible(ghost))
					|| game.getGhostLairTime(ghost) > 0) {
				continue;
			}
			int ghostNode = game.getGhostCurrentNodeIndex(ghost);
			MOVE ghostMove = game.getGhostLastMoveMade(ghost);
			if (ghostNode < 0 || ghostMove == null) {
				continue;
			}

			int distancia = game.getShortestPathDistance(ghostNode, pacman, ghostMove);
			if (distancia < radio && ghostMove == pacmanMove) {
				encontrados++;
			}
		}
		return encontrados;
	}

	private boolean estaPildoraBloqueada(int pill) {
		int pacman = game.getPacmanCurrentNodeIndex();
		if (pacman < 0) {
			return false;
		}

		int[] path = game.getShortestPath(pacman, pill);
		int pacmanSteps = 0;
		for (int node : path) {
			if (hayFantasmaEnNodo(node) || fantasmaPuedeCortar(node, pacmanSteps)) {
				return true;
			}
			pacmanSteps++;
		}
		return false;
	}

	private boolean hayFantasmaEnNodo(int nodo) {
		for (GHOST ghost : GHOST.values()) {
			if (!Boolean.TRUE.equals(game.isGhostEdible(ghost))
					&& game.getGhostLairTime(ghost) <= 0
					&& game.getGhostCurrentNodeIndex(ghost) == nodo) {
				return true;
			}
		}
		return false;
	}

	private boolean fantasmaPuedeCortar(int nodo, int pasosPacman) {
		for (GHOST ghost : GHOST.values()) {
			if (Boolean.TRUE.equals(game.isGhostEdible(ghost))
					|| game.getGhostLairTime(ghost) > 0) {
				continue;
			}
			int ghostNode = game.getGhostCurrentNodeIndex(ghost);
			MOVE ghostMove = game.getGhostLastMoveMade(ghost);
			if (ghostNode >= 0 && ghostMove != null
					&& game.getShortestPathDistance(ghostNode, nodo, ghostMove)
						< pasosPacman + HOLGURA_CAMINO) {
				return true;
			}
		}
		return false;
	}
}