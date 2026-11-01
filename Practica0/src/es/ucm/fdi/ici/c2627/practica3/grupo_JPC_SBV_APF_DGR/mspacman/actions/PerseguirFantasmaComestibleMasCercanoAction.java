package es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.actions;

import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * PerseguirFantasmaComestibleMasCercano (Ataque)
 * Va a por el fantasma comestible más cercano, si llega a comérselo contando si viene de frente.
 */
public class PerseguirFantasmaComestibleMasCercanoAction extends JugarSinPeligroAction {

	public PerseguirFantasmaComestibleMasCercanoAction() {
	}

	public PerseguirFantasmaComestibleMasCercanoAction(boolean mirarDireccion) {
		super(mirarDireccion);
	}

	/** Va a por el fantasma comestible más cercano, si llega a comérselo contando si viene de frente. */
	@Override
	public MOVE execute(Game game) {
		int nodoPacman = game.getPacmanCurrentNodeIndex();
		int objetivo = -1;
		int menorDistancia = Integer.MAX_VALUE;
		for (GHOST g : GHOST.values()) {
			if (!game.isGhostEdible(g)) continue;
			int nodo = game.getGhostCurrentNodeIndex(g);
			int distancia = game.getShortestPathDistance(nodoPacman, nodo, game.getPacmanLastMoveMade());
			boolean llega = tiempoDeChoque(game, nodoPacman, game.getPacmanLastMoveMade(), g) < game.getGhostEdibleTime(g);
			if (llega && distancia < menorDistancia) {
				menorDistancia = distancia;
				objetivo = nodo;
			}
		}
		if (objetivo == -1) return null;
		return moverSeguroHacia(game, objetivo, clasificarMovimientos(game));
	}
}
