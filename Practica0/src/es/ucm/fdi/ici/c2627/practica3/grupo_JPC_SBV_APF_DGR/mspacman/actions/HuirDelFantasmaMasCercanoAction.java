package es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.actions;

import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.Movimientos;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * HuirDelFantasmaMasCercano (Defensa)
 * Entre las salidas seguras, va hacia el fantasma comestible más cercano al que llega y, si no hay ninguno, por el
 * pasillo con más pastillas. Si empatan, hacia la pastilla más cercana (v2). En la v0 elegía el pasillo en el que el
 * fantasma más cercano tarda más en llegar al cruce del final y, si empataban, el de más pastillas; en la v1, además,
 * entre las salidas con margen de sobra, la de más pastillas.
 */
public class HuirDelFantasmaMasCercanoAction extends JugarSinPeligroAction {

	private final Huida huida;

	public HuirDelFantasmaMasCercanoAction() {
		this(Huida.COMO_PRACTICA1);
	}

	public HuirDelFantasmaMasCercanoAction(Huida huida) {
		this.huida = huida;
	}

	/**
	 * Entre las salidas seguras, va hacia el fantasma comestible más cercano al que llega y, si no hay ninguno, por
	 * el pasillo con más pastillas. Si empatan, hacia la pastilla más cercana.
	 */
	@Override
	public MOVE execute(Game game) {
		if (huida == Huida.COMO_PRACTICA1) return comerSeguro(game);
		Movimientos movimientos = clasificarMovimientos(game);
		if (movimientos.seguros.length == 0) return null;
		int nodoPacman = game.getPacmanCurrentNodeIndex();

		GHOST masCercano = null;
		int menorDistancia = Integer.MAX_VALUE;
		for (GHOST g : GHOST.values()) {
			if (game.isGhostEdible(g) || game.getGhostLairTime(g) > 0) continue;
			int distancia = game.getShortestPathDistance(game.getGhostCurrentNodeIndex(g), nodoPacman, game.getGhostLastMoveMade(g));
			if (distancia < menorDistancia) {
				menorDistancia = distancia;
				masCercano = g;
			}
		}
		if (masCercano == null) return movimientos.seguros[0];

		MOVE mejor = null;
		int mejorMargen = Integer.MIN_VALUE;
		int mejorPastillas = -1;
		for (MOVE movimiento : movimientos.seguros) {
			int cruce = nextJunction(game, nodoPacman, movimiento);
			int margen = cruce == -1 ? Integer.MIN_VALUE
					: ghostTimeTo(game, masCercano, cruce) - corridorLength(game, nodoPacman, movimiento);
			int pastillas = pillsInCorridor(game, nodoPacman, movimiento);
			int comparacion = compararHuidas(huida, margen, pastillas, mejorMargen, mejorPastillas);
			if (mejor == null || comparacion > 0 || (comparacion == 0 && pastillas > mejorPastillas)) {
				mejor = movimiento;
				mejorMargen = margen;
				mejorPastillas = pastillas;
			}
		}
		return mejor;
	}
}
