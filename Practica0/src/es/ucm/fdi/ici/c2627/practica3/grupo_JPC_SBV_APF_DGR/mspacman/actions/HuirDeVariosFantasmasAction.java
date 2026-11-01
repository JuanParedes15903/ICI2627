package es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.actions;

import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.Movimientos;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * HuirDeVariosFantasmas (Defensa)
 * Entre las salidas seguras, va hacia el fantasma comestible más cercano al que llega y, si no hay ninguno, por el
 * pasillo con más pastillas. Si empatan, hacia la pastilla más cercana (v2). En la v0 elegía la salida segura con más
 * margen frente a todos los fantasmas en el siguiente cruce y, si empataban, la de mayor suma de márgenes; en la v1,
 * además, entre las salidas con margen de sobra, la de más pastillas.
 */
public class HuirDeVariosFantasmasAction extends JugarSinPeligroAction {

	private final Huida huida;

	public HuirDeVariosFantasmasAction() {
		this(Huida.COMO_PRACTICA1);
	}

	public HuirDeVariosFantasmasAction(Huida huida) {
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

		MOVE mejor = null;
		int mejorPeorMargen = Integer.MIN_VALUE;
		int mejorSuma = Integer.MIN_VALUE;
		int mejorPastillas = -1;
		for (MOVE movimiento : movimientos.seguros) {
			int peorMargen = Integer.MIN_VALUE;
			int sumaMargenes = Integer.MIN_VALUE;
			int cruce = nextJunction(game, nodoPacman, movimiento);
			if (cruce != -1) {
				int pasos = corridorLength(game, nodoPacman, movimiento);
				peorMargen = Integer.MAX_VALUE;
				sumaMargenes = 0;
				for (GHOST fantasma : GHOST.values()) {
					if (game.isGhostEdible(fantasma)) continue;
					int margen = ghostTimeTo(game, fantasma, cruce) - pasos;
					peorMargen = Math.min(peorMargen, margen);
					sumaMargenes += margen;
				}
			}
			int pastillas = pillsInCorridor(game, nodoPacman, movimiento);
			int comparacion = compararHuidas(huida, peorMargen, pastillas, mejorPeorMargen, mejorPastillas);
			if (mejor == null || comparacion > 0 || (comparacion == 0 && sumaMargenes > mejorSuma)) {
				mejor = movimiento;
				mejorPeorMargen = peorMargen;
				mejorSuma = sumaMargenes;
				mejorPastillas = pastillas;
			}
		}
		return mejor;
	}
}
