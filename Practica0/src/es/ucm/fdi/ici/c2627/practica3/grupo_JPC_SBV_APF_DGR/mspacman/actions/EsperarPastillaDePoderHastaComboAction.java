package es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.actions;

import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.Movimientos;
import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.MsPacManInput;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * EsperarPastillaDePoderHastaCombo (PastillasPoder)
 * Da vueltas cerca de la pastilla de poder sin comérsela, hasta que haya suficientes fantasmas cerca para
 * comerse el máximo posible.
 */
public class EsperarPastillaDePoderHastaComboAction extends JugarSinPeligroAction {

	private static final int ESPERA_MAXIMA = 150;

	private int inicioEspera;
	private int ultimoTick = -10;

	public EsperarPastillaDePoderHastaComboAction() {
	}

	public EsperarPastillaDePoderHastaComboAction(boolean mirarDireccion) {
		super(mirarDireccion);
	}

	/**
	 * Da vueltas cerca de la pastilla de poder sin comérsela, hasta que haya suficientes fantasmas cerca para
	 * comerse el máximo posible.
	 */
	@Override
	public MOVE execute(Game game) {
		MsPacManInput input = entrada(game);
		Movimientos movimientos = input.getMovimientos();
		int nodoPacman = game.getPacmanCurrentNodeIndex();
		MOVE ultimoMovimientoPacman = game.getPacmanLastMoveMade();

		int pastilla = pastillaDePoderMasCercana(game, nodoPacman, ultimoMovimientoPacman);
		if (pastilla == -1) return null;

		int tick = game.getTotalTime();
		if (tick != ultimoTick + 1) inicioEspera = tick;
		ultimoTick = tick;

		if (input.isHayCombo() || tick - inicioEspera > ESPERA_MAXIMA)
			return moverSeguroHacia(game, pastilla, movimientos);

		MOVE mejor = null;
		int mejorDistancia = Integer.MAX_VALUE;
		for (MOVE seguro : movimientos.seguros) {
			if (powerPillNodes(game, nodoPacman, seguro) != INFINITO) continue;
			int vecino = game.getNeighbour(nodoPacman, seguro);
			int distancia = game.getShortestPathDistance(vecino, pastilla, seguro);
			if (distancia < mejorDistancia) {
				mejorDistancia = distancia;
				mejor = seguro;
			}
		}
		return mejor != null ? mejor : moverSeguroHacia(game, pastilla, movimientos);
	}
}
