package es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.actions;

import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.Movimientos;
import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.MsPacManInput;
import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * JugarSinPeligro
 * Base de todas las acciones. Su función divide los movimientos posibles (getPossibleMoves)
 * en tres arrays: seguros, mortales seguros y mortales si los fantasmas van hacia allí.
 * Ningún estado debería elegir un movimiento mortal seguro, salvo MuerteSegura.
 */
public abstract class JugarSinPeligroAction extends PacManAction {

	/** Cómo eligen las huidas de Defensa en cada versión. */
	public enum Huida {
		/** v0: la salida con más margen frente a los fantasmas. */
		POR_MARGEN,
		/** v1: si hay salidas con margen de sobra, la de más pastillas; si no, la de más margen. */
		MARGEN_Y_PASTILLAS,
		/** v2: como la MsPacMan de la Práctica 1 (comerSeguro). */
		COMO_PRACTICA1
	}

	protected static final int MARGEN_PARA_COMER = 10;

	private final boolean mirarDireccion;

	private MsPacManInput entradaDelTurno;

	protected JugarSinPeligroAction() {
		this(true);
	}

	/** Con mirarDireccion = false, moverSeguroHacia elige como hasta la v1, para que esas versiones jueguen igual. */
	protected JugarSinPeligroAction(boolean mirarDireccion) {
		this.mirarDireccion = mirarDireccion;
	}

	/**
	 * Base de todas las acciones. Su función divide los movimientos posibles (getPossibleMoves) en tres arrays:
	 * seguros, mortales seguros y mortales si los fantasmas van hacia allí. Ningún estado debería elegir un
	 * movimiento mortal seguro, salvo MuerteSegura.
	 */
	protected Movimientos clasificarMovimientos(Game game) {
		return entrada(game).getMovimientos();
	}

	public void setEntrada(MsPacManInput entrada) {
		entradaDelTurno = entrada;
	}

	protected MsPacManInput entrada(Game game) {
		if (entradaDelTurno == null || entradaDelTurno.getGame() != game) entradaDelTurno = new MsPacManInput(game);
		return entradaDelTurno;
	}

	/** Entre los movimientos seguros, el que la deja más cerca del objetivo sin darse la vuelta. */
	protected MOVE moverSeguroHacia(Game game, int objetivo, Movimientos movimientos) {
		if (!mirarDireccion) return moverSeguroHaciaSinMirarDireccion(game, objetivo, movimientos);
		int nodoPacman = game.getPacmanCurrentNodeIndex();
		MOVE mejor = null;
		int mejorDistancia = Integer.MAX_VALUE;
		for (MOVE seguro : movimientos.seguros) {
			int vecino = game.getNeighbour(nodoPacman, seguro);
			int distancia = vecino == objetivo ? 0 : game.getShortestPathDistance(vecino, objetivo, seguro);
			if (distancia < mejorDistancia) {
				mejorDistancia = distancia;
				mejor = seguro;
			}
		}
		return mejor != null ? mejor : game.getNextMoveTowardsTarget(nodoPacman, objetivo, game.getPacmanLastMoveMade(), DM.PATH);
	}

	/** El de la v0 y la v1: getNextMoveTowardsTarget mide las distancias como si MsPacMan pudiera darse la vuelta. */
	private MOVE moverSeguroHaciaSinMirarDireccion(Game game, int objetivo, Movimientos movimientos) {
		int nodoPacman = game.getPacmanCurrentNodeIndex();
		MOVE haciaObjetivo = game.getNextMoveTowardsTarget(nodoPacman, objetivo, game.getPacmanLastMoveMade(), DM.PATH);

		for (MOVE seguro : movimientos.seguros)
			if (seguro == haciaObjetivo) return seguro;

		MOVE mejor = null;
		int mejorDistancia = Integer.MAX_VALUE;
		for (MOVE seguro : movimientos.seguros) {
			int vecino = game.getNeighbour(nodoPacman, seguro);
			int distancia = game.getShortestPathDistance(vecino, objetivo, seguro);
			if (distancia < mejorDistancia) {
				mejorDistancia = distancia;
				mejor = seguro;
			}
		}
		return mejor != null ? mejor : haciaObjetivo;
	}

	/**
	 * Con MARGEN_Y_PASTILLAS, si las dos huidas tienen margen de sobra gana la de más pastillas; en otro caso, la de
	 * más margen.
	 */
	protected int compararHuidas(Huida huida, int margen, int pastillas, int otroMargen, int otrasPastillas) {
		if (huida == Huida.MARGEN_Y_PASTILLAS) {
			boolean comoda = margen >= MARGEN_PARA_COMER;
			boolean otraComoda = otroMargen >= MARGEN_PARA_COMER;
			if (comoda != otraComoda) return comoda ? 1 : -1;
			if (comoda && pastillas != otrasPastillas) return Integer.compare(pastillas, otrasPastillas);
		}
		return Integer.compare(margen, otroMargen);
	}

	/**
	 * Entre los movimientos seguros, va hacia el fantasma comestible más cercano al que llega y, si no hay ninguno,
	 * por el pasillo con más pastillas. Si empatan, hacia la pastilla más cercana.
	 */
	protected MOVE comerSeguro(Game game) {
		Movimientos movimientos = clasificarMovimientos(game);
		if (movimientos.seguros.length == 0) return null;
		int nodoPacman = game.getPacmanCurrentNodeIndex();
		int pastilla = getNearestPill(game, nodoPacman);
		MOVE mejor = null;
		int mejorPuntos = Integer.MIN_VALUE;
		int mejorDistancia = Integer.MAX_VALUE;
		for (MOVE movimiento : movimientos.seguros) {
			int comestible = nearestEdibleGhost(game, nodoPacman, movimiento);
			int puntos = (comestible != INFINITO ? -comestible : -1000) + pillsInCorridor(game, nodoPacman, movimiento);
			int distancia = pastilla == -1 ? 0
					: game.getShortestPathDistance(game.getNeighbour(nodoPacman, movimiento), pastilla, movimiento);
			if (mejor == null || puntos > mejorPuntos || (puntos == mejorPuntos && distancia < mejorDistancia)) {
				mejor = movimiento;
				mejorPuntos = puntos;
				mejorDistancia = distancia;
			}
		}
		return mejor;
	}
}
