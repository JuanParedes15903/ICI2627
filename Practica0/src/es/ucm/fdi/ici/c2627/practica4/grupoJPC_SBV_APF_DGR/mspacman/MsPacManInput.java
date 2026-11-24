package es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.mspacman;

import static pacman.game.Constants.EAT_DISTANCE;
import static pacman.game.Constants.EDIBLE_TIME;
import static pacman.game.Constants.EDIBLE_TIME_REDUCTION;
import static pacman.game.Constants.LEVEL_RESET_REDUCTION;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ucm.fdi.ici.fuzzy.FuzzyInput;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacManInput extends FuzzyInput implements Funciones {

	public static final double LEJOS = 150;
	public static final double VENTAJA_MAXIMA = 50;

	private static final int FANTASMAS_PARA_COMBO = 3;

	private int curr;
	private MOVE lastMove;
	private int pastillaDePoder;
	private Movimientos movimientos;

	private double peligro;
	private double amenazas;
	private double comestible;
	private double margenCaza;
	private double alcanzables;
	private double cadena;
	private double interceptar;
	private double ppill;
	private double ventajaPPill;
	private double combo;
	private double enCrucePPill;
	private double ppillUnicaSalida;
	private double pasillos;
	private double pasillosIguales;

	public MsPacManInput(Game game) {
		super(game);
	}

	@Override
	public void parseInput() {
		curr = game.getPacmanCurrentNodeIndex();
		lastMove = game.getPacmanLastMoveMade();
		pastillaDePoder = pastillaDePoderMasCercana(game, curr, lastMove);
		movimientos = calcularMovimientos();

		peligro = LEJOS;
		amenazas = 0;
		comestible = LEJOS;
		margenCaza = 0;
		alcanzables = 0;
		for (GHOST g : GHOST.values()) {
			if (!game.isGhostEdible(g) && amenaza(g)) amenazas++;
			if (peligroso(g)) {
				peligro = Math.min(peligro, distanciaDelFantasma(g));
			} else if (game.isGhostEdible(g)) {
				comestible = Math.min(comestible, game.getShortestPathDistance(curr, game.getGhostCurrentNodeIndex(g), lastMove));
				int sobra = game.getGhostEdibleTime(g) - tiempoDeChoque(game, curr, lastMove, g);
				margenCaza = Math.max(margenCaza, Math.min(LEJOS, sobra));
				if (alcanzable(g)) alcanzables++;
			}
		}

		cadena = planCadena(game).size();
		interceptar = fantasmaAInterceptar(game) != null ? 1 : 0;

		ppill = pastillaDePoder == -1 ? LEJOS : Math.min(LEJOS, game.getShortestPathDistance(curr, pastillaDePoder, lastMove));
		ventajaPPill = calcularVentajaPPill();
		combo = contarComboTrasLaPastilla();
		enCrucePPill = calcularEnCruceDePastillaDePoder() ? 1 : 0;
		ppillUnicaSalida = calcularPastillaUnicaSalida() ? 1 : 0;

		pasillos = Math.min(LEJOS, contarPasillosConPastillas(game));
		pasillosIguales = calcularPasillosConMismasPastillas() ? 1 : 0;
	}

	@Override
	public HashMap<String, Double> getFuzzyValues() {
		HashMap<String, Double> vars = new HashMap<>();
		vars.put("peligro", peligro);
		vars.put("amenazas", amenazas);
		vars.put("salidasSeguras", (double) movimientos.seguros.length);
		vars.put("comestible", comestible);
		vars.put("margenCaza", margenCaza);
		vars.put("alcanzables", alcanzables);
		vars.put("cadena", cadena);
		vars.put("interceptar", interceptar);
		vars.put("ppill", ppill);
		vars.put("ventajaPPill", ventajaPPill);
		vars.put("combo", combo);
		vars.put("enCrucePPill", enCrucePPill);
		vars.put("ppillUnicaSalida", ppillUnicaSalida);
		vars.put("pasillos", pasillos);
		vars.put("pasillosIguales", pasillosIguales);
		return vars;
	}

	public Movimientos getMovimientos() {
		return movimientos;
	}

	public void aplicarSalidas(Map<String, Double> salidas) {
	}

	public boolean isHayCombo() {
		return combo >= FANTASMAS_PARA_COMBO;
	}

	protected Movimientos calcularMovimientos() {
		List<MOVE> seguros = new ArrayList<>();
		List<MOVE> mortalesSeguros = new ArrayList<>();
		List<MOVE> mortalesSiVan = new ArrayList<>();
		for (MOVE move : game.getPossibleMoves(curr, lastMove)) {
			if (mortalSeguro(move)) mortalesSeguros.add(move);
			else if (sureDeath(game, curr, move)) mortalesSiVan.add(move);
			else seguros.add(move);
		}
		Movimientos m = new Movimientos();
		m.seguros = seguros.toArray(new MOVE[0]);
		m.mortalesSeguros = mortalesSeguros.toArray(new MOVE[0]);
		m.mortalesSiVan = mortalesSiVan.toArray(new MOVE[0]);
		return m;
	}

	private boolean mortalSeguro(MOVE move) {
		int nodosAPastillaDePoder = powerPillNodes(game, curr, move);
		for (GHOST g : GHOST.values())
			if (fantasmaEnPasillo(g, move) && corridorCollisionTime(game, curr, g, move) < nodosAPastillaDePoder) return true;
		return false;
	}

	private boolean fantasmaEnPasillo(GHOST g, MOVE move) {
		if (game.getGhostLairTime(g) > 0) return false;
		int siguiente = game.getNeighbour(curr, move);
		int pasos = 1 + game.getShortestPathDistance(siguiente, game.getGhostCurrentNodeIndex(g), move);
		return pasos <= corridorLength(game, curr, move);
	}

	private boolean peligroso(GHOST g) {
		return game.getGhostLairTime(g) == 0 && !game.isGhostEdible(g);
	}

	private int distanciaDelFantasma(GHOST g) {
		return game.getShortestPathDistance(game.getGhostCurrentNodeIndex(g), curr, game.getGhostLastMoveMade(g));
	}

	private boolean amenaza(GHOST g) {
		for (MOVE move : game.getPossibleMoves(curr, lastMove))
			if (corridorCollisionTime(game, curr, g, move) < powerPillNodes(game, curr, move)) return true;
		return false;
	}

	private boolean alcanzable(GHOST g) {
		int distancia = game.getShortestPathDistance(curr, game.getGhostCurrentNodeIndex(g), lastMove);
		return 2 * (distancia - EAT_DISTANCE) < game.getGhostEdibleTime(g);
	}

	private double calcularVentajaPPill() {
		if (pastillaDePoder == -1) return -VENTAJA_MAXIMA;
		int distancia = game.getShortestPathDistance(curr, pastillaDePoder, lastMove);
		int fantasma = INFINITO;
		for (GHOST g : GHOST.values())
			if (!game.isGhostEdible(g)) fantasma = Math.min(fantasma, ghostTimeTo(game, g, pastillaDePoder));
		double ventaja = (double) fantasma - distancia;
		return Math.max(-VENTAJA_MAXIMA, Math.min(VENTAJA_MAXIMA, ventaja));
	}

	private int contarComboTrasLaPastilla() {
		if (pastillaDePoder == -1) return 0;
		int comibles = 0;
		for (GHOST g : GHOST.values())
			if (peligroso(g) && comibleTrasLaPastilla(g)) comibles++;
		return comibles;
	}

	private boolean comibleTrasLaPastilla(GHOST g) {
		int distancia = game.getShortestPathDistance(pastillaDePoder, game.getGhostCurrentNodeIndex(g));
		return 2 * (distancia - EAT_DISTANCE) < tiempoComestible();
	}

	private int tiempoComestible() {
		return (int) (EDIBLE_TIME * Math.pow(EDIBLE_TIME_REDUCTION, game.getCurrentLevel() % LEVEL_RESET_REDUCTION));
	}

	private boolean calcularEnCruceDePastillaDePoder() {
		if (!game.isJunction(curr)) return false;
		for (MOVE move : game.getPossibleMoves(curr, lastMove))
			if (powerPillNodes(game, curr, move) != INFINITO) return true;
		return false;
	}

	private boolean calcularPastillaUnicaSalida() {
		if (pastillaDePoder == -1 || movimientos.seguros.length != 1) return false;
		int mortales = movimientos.mortalesSeguros.length + movimientos.mortalesSiVan.length;
		MOVE haciaLaPastilla = game.getNextMoveTowardsTarget(curr, pastillaDePoder, lastMove, DM.PATH);
		return mortales > 0 && movimientos.seguros[0] == haciaLaPastilla;
	}

	private boolean calcularPasillosConMismasPastillas() {
		MOVE[] moves = game.getPossibleMoves(curr, lastMove);
		if (moves.length < 2) return false;
		for (MOVE move : moves)
			if (pillsInCorridor(game, curr, move) != pillsInCorridor(game, curr, moves[0])) return false;
		return true;
	}
}
