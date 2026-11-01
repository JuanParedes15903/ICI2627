package es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.actions;

import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.Movimientos;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * MuerteSegura
 * No hay ningún movimiento seguro: elige el mejor de los mortales (primero los mortales si los fantasmas van
 * hacia allí; si no, el que más pastillas consiga).
 */
public class MuerteSeguraAction extends JugarSinPeligroAction {

	/**
	 * No hay ningún movimiento seguro: elige el mejor de los mortales (primero los mortales si los fantasmas
	 * van hacia allí; si no, el que más pastillas consiga).
	 */
	@Override
	public MOVE execute(Game game) {
		Movimientos movimientos = clasificarMovimientos(game);
		MOVE[] candidatos = movimientos.mortalesSiVan.length > 0 ? movimientos.mortalesSiVan : movimientos.mortalesSeguros;
		MOVE mejor = null;
		int masPastillas = -1;
		for (MOVE movimiento : candidatos) {
			int pastillas = pillsInCorridor(game, game.getPacmanCurrentNodeIndex(), movimiento);
			if (pastillas > masPastillas) {
				masPastillas = pastillas;
				mejor = movimiento;
			}
		}
		return mejor;
	}
}
