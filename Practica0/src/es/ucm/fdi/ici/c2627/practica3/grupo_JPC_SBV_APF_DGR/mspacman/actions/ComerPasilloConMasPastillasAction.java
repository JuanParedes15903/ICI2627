package es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.actions;

import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * ComerPasilloConMasPastillas (Pastillas)
 * Elige el movimiento que le lleva por el pasillo con más pastillas.
 */
public class ComerPasilloConMasPastillasAction extends JugarSinPeligroAction {

	/** Elige el movimiento que le lleva por el pasillo con más pastillas. */
	@Override
	public MOVE execute(Game game) {
		MOVE mejor = null;
		int masPastillas = -1;
		for (MOVE movimiento : clasificarMovimientos(game).seguros) {
			int pastillas = pillsInCorridor(game, game.getPacmanCurrentNodeIndex(), movimiento);
			if (pastillas > masPastillas) {
				masPastillas = pastillas;
				mejor = movimiento;
			}
		}
		return mejor;
	}
}
