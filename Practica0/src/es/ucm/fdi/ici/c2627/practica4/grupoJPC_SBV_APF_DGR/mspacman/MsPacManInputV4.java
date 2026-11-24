package es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.mspacman;

import static pacman.game.Constants.EXTRA_LIFE_SCORE;
import static pacman.game.Constants.NUM_LIVES;

import java.util.HashMap;
import java.util.Map;

import pacman.game.Game;

public class MsPacManInputV4 extends MsPacManInput implements Encierro {

	public static final int LIMITE_TURNOS = 4000;
	public static final double TURNOS_PREVIOS = 2000;
	public static final double UMBRAL_PRUDENCIA = 4.5;

	private Movimientos prudentes;
	private boolean prudente;

	public MsPacManInputV4(Game game) {
		super(game);
	}

	@Override
	protected Movimientos calcularMovimientos() {
		Movimientos m = super.calcularMovimientos();
		prudentes = filtrarConEncierro(game, m);
		return m;
	}

	@Override
	public HashMap<String, Double> getFuzzyValues() {
		HashMap<String, Double> vars = super.getFuzzyValues();
		int vidas = Math.max(1, game.getPacmanNumberOfLivesRemaining());
		int tiempo = game.getTotalTime();
		double restantes = Math.max(0, LIMITE_TURNOS - tiempo);
		int muertes = Math.max(0, NUM_LIVES + (game.getScore() >= EXTRA_LIFE_SCORE ? 1 : 0) - vidas);
		double ritmo = Math.min(LIMITE_TURNOS, (tiempo + TURNOS_PREVIOS) / (muertes + 1));
		vars.put("vidas", (double) vidas);
		vars.put("turnosPorVida", Math.min(LIMITE_TURNOS, restantes / vidas));
		vars.put("ritmo", ritmo);
		vars.put("vidasSobrantes", Math.max(-5, Math.min(5, vidas - restantes / ritmo)));
		return vars;
	}

	@Override
	public void aplicarSalidas(Map<String, Double> salidas) {
		Double p = salidas.get("prudencia");
		prudente = p != null && p > umbral();
	}

	@Override
	public Movimientos getMovimientos() {
		return prudente ? prudentes : super.getMovimientos();
	}

	protected double umbral() {
		return UMBRAL_PRUDENCIA;
	}

	public boolean isPrudente() {
		return prudente;
	}
}
