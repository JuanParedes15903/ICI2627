package es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.Movimientos;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * IrAPorLasUltimasPastillas (Pastillas)
 * Va hacia los pasillos que aún tienen pastillas, priorizándolos sobre todo lo demás. Puede tomar más riesgo:
 * acepta movimientos mortales si los fantasmas van hacia allí.
 */
public class IrAPorLasUltimasPastillasAction extends JugarSinPeligroAction {

	/**
	 * Va hacia los pasillos que aún tienen pastillas, priorizándolos sobre todo lo demás. Puede tomar más
	 * riesgo: acepta movimientos mortales si los fantasmas van hacia allí.
	 */
	@Override
	public MOVE execute(Game game) {
		Movimientos movimientos = clasificarMovimientos(game);
		int nodoPacman = game.getPacmanCurrentNodeIndex();

		List<MOVE> permitidos = new ArrayList<>(Arrays.asList(movimientos.seguros));
		if (contarPasillosConPastillas(game) == 1) permitidos.addAll(Arrays.asList(movimientos.mortalesSiVan));

		MOVE mejor = null;
		int mejorDistancia = Integer.MAX_VALUE;
		for (MOVE movimiento : permitidos) {
			int vecino = game.getNeighbour(nodoPacman, movimiento);
			int distancia = Integer.MAX_VALUE;
			for (int pastilla : game.getActivePillsIndices())
				distancia = Math.min(distancia, game.getShortestPathDistance(vecino, pastilla, movimiento));
			if (distancia < mejorDistancia) {
				mejorDistancia = distancia;
				mejor = movimiento;
			}
		}
		return mejor;
	}
}
