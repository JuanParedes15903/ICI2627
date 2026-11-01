package es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman;

import static pacman.game.Constants.EAT_DISTANCE;
import static pacman.game.Constants.EDIBLE_TIME;
import static pacman.game.Constants.EDIBLE_TIME_REDUCTION;
import static pacman.game.Constants.LEVEL_RESET_REDUCTION;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/** Lo que se lee del juego una vez por turno. Atributos declarados sin valor: se calculan en parseInput(). */
public class MsPacManInput extends BaseInput {

	/** Clasificación de JugarSinPeligro de los movimientos posibles (transiciones 1 y 2). */
	private Movimientos movimientos;
	/** Si se acaba de comer una pastilla de poder (transición 3). */
	private boolean pastillaDePoderComida;
	/** Si le queda poder (transición 4). */
	private boolean poderActivo;
	/** Cuántos fantasmas comestibles puede alcanzar a comerse (transiciones 4 y 7). */
	private int fantasmasComestiblesAlcanzables;
	/** Si está en peligro (transiciones 4, 5 y 6). */
	private boolean enPeligro;
	/** Si está en el cruce que lleva al pasillo de la pastilla de poder (transición 8). */
	private boolean enCruceDePastillaDePoder;
	/** Fórmula de distancias de todos los fantasmas a la pastilla: si ya hay combo (transición 8). */
	private boolean hayCombo;
	/** Si todos los pasillos posibles tienen el mismo número de pastillas (transición 9). */
	private boolean pasillosConMismasPastillas;
	/** Cuántos pasillos con pastillas quedan en el mapa (transición 10). */
	private int pasillosConPastillas;
	private boolean quedanPocosPasillos;
	/** Si puede cazar en cadena (transición 11). */
	private boolean cadenaPosible;
	/** Si llega a interceptar a algún fantasma comestible (transición 12). */
	private boolean intercepcionPosible;
	/** Cuántos fantasmas peligrosos tiene cerca (transiciones 13 y 14). */
	private int fantasmasCerca;
	/** Si la pueden encerrar / la probabilidad de que la encierren es alta (transiciones 13 y 15). */
	private boolean riesgoDeEncierro;
	/** Si el camino de la pastilla de poder es su única salida segura (transiciones 15 y 16). */
	private boolean pastillaUnicaSalida;
	/** Si llega a la pastilla de poder antes que los fantasmas (transición 17). */
	private boolean llegaAntesAPastillaDePoder;
	private boolean pastillaDePoderCerca;

	private int curr;
	private MOVE lastMove;
	private int pastillaDePoder;

	private static final int DISTANCIA_PELIGRO = 15;
	private static final int FANTASMAS_PARA_COMBO = 3;
	private static final int POCOS_PASILLOS = 6;
	private static final int DISTANCIA_PASTILLA_DE_PODER = 20;

	public MsPacManInput(Game game) {
		super(game);
	}

	@Override
	public void parseInput() {
		curr = game.getPacmanCurrentNodeIndex();
		lastMove = game.getPacmanLastMoveMade();
		pastillaDePoder = pastillaDePoderMasCercana(game, curr, lastMove);

		movimientos = calcularMovimientos();
		pastillaDePoderComida = game.wasPowerPillEaten();
		poderActivo = calcularPoderActivo();
		fantasmasComestiblesAlcanzables = contarComestiblesAlcanzables();
		fantasmasCerca = contarFantasmasCerca();
		enPeligro = fantasmasCerca > 0;
		enCruceDePastillaDePoder = calcularEnCruceDePastillaDePoder();
		hayCombo = calcularHayCombo();
		pasillosConMismasPastillas = calcularPasillosConMismasPastillas();
		pasillosConPastillas = contarPasillosConPastillas(game);
		quedanPocosPasillos = pasillosConPastillas < pocosPasillos();
		cadenaPosible = calcularCadenaPosible();
		intercepcionPosible = calcularIntercepcionPosible();
		riesgoDeEncierro = calcularRiesgoDeEncierro();
		pastillaUnicaSalida = calcularPastillaUnicaSalida();
		llegaAntesAPastillaDePoder = calcularLlegaAntesAPastillaDePoder();
		pastillaDePoderCerca = calcularPastillaDePoderCerca();
	}

	@Override
	public Collection<String> getFacts() {
		List<String> facts = new ArrayList<>();
		facts.add("(MSPACMAN"
				+ " (pastillaDePoderComida " + pastillaDePoderComida + ")"
				+ " (poderActivo " + poderActivo + ")"
				+ " (fantasmasComestiblesAlcanzables " + fantasmasComestiblesAlcanzables + ")"
				+ " (enPeligro " + enPeligro + ")"
				+ " (enCruceDePastillaDePoder " + enCruceDePastillaDePoder + ")"
				+ " (hayCombo " + hayCombo + ")"
				+ " (pasillosConMismasPastillas " + pasillosConMismasPastillas + ")"
				+ " (pasillosConPastillas " + pasillosConPastillas + ")"
				+ " (quedanPocosPasillos " + quedanPocosPasillos + ")"
				+ " (cadenaPosible " + cadenaPosible + ")"
				+ " (intercepcionPosible " + intercepcionPosible + ")"
				+ " (fantasmasCerca " + fantasmasCerca + ")"
				+ " (riesgoDeEncierro " + riesgoDeEncierro + ")"
				+ " (pastillaUnicaSalida " + pastillaUnicaSalida + ")"
				+ " (llegaAntesAPastillaDePoder " + llegaAntesAPastillaDePoder + ")"
				+ " (pastillaDePoderCerca " + pastillaDePoderCerca + ")"
				+ " (movimientosSeguros " + movimientos.seguros.length + "))");
		for (GHOST g : GHOST.values())
			facts.add("(FANTASMA"
					+ " (nombre " + g.name() + ")"
					+ " (comestible " + game.isGhostEdible(g) + ")"
					+ " (distancia " + distanciaParaHechos(g) + ")"
					+ " (tiempo " + game.getGhostEdibleTime(g) + ")"
					+ " (amenaza " + amenaza(g) + ")"
					+ " (alcanzable " + alcanzable(g) + "))");
		return facts;
	}

	private int distanciaParaHechos(GHOST g) {
		int carcel = game.getGhostLairTime(g);
		if (carcel > 0) return carcel + game.getShortestPathDistance(game.getGhostInitialNodeIndex(), curr);
		if (game.isGhostEdible(g)) return game.getShortestPathDistance(curr, game.getGhostCurrentNodeIndex(g), lastMove);
		return distanciaDelFantasma(g);
	}

	private boolean amenaza(GHOST g) {
		if (game.isGhostEdible(g)) return false;
		for (MOVE move : game.getPossibleMoves(curr, lastMove))
			if (corridorCollisionTime(game, curr, g, move) < powerPillNodes(game, curr, move)) return true;
		return false;
	}

	private Movimientos calcularMovimientos() {
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

	private boolean calcularPoderActivo() {
		for (GHOST g : GHOST.values())
			if (game.isGhostEdible(g)) return true;
		return false;
	}

	private int contarComestiblesAlcanzables() {
		int total = 0;
		for (GHOST g : GHOST.values())
			if (alcanzable(g)) total++;
		return total;
	}

	private boolean alcanzable(GHOST g) {
		if (!game.isGhostEdible(g)) return false;
		return tiempoDeChoque(game, curr, lastMove, g) < game.getGhostEdibleTime(g);
	}

	private int contarFantasmasCerca() {
		int total = 0;
		for (GHOST g : GHOST.values())
			if (peligroso(g) && distanciaDelFantasma(g) < distanciaPeligro()) total++;
		return total;
	}

	private boolean peligroso(GHOST g) {
		return game.getGhostLairTime(g) == 0 && !game.isGhostEdible(g);
	}

	private int distanciaDelFantasma(GHOST g) {
		return game.getShortestPathDistance(game.getGhostCurrentNodeIndex(g), curr, game.getGhostLastMoveMade(g));
	}

	private boolean calcularEnCruceDePastillaDePoder() {
		if (!game.isJunction(curr)) return false;
		for (MOVE move : game.getPossibleMoves(curr, lastMove))
			if (powerPillNodes(game, curr, move) != INFINITO) return true;
		return false;
	}

	private boolean calcularHayCombo() {
		if (pastillaDePoder == -1) return false;
		int comibles = 0;
		for (GHOST g : GHOST.values())
			if (peligroso(g) && comibleTrasLaPastilla(g)) comibles++;
		return comibles >= FANTASMAS_PARA_COMBO;
	}

	private boolean comibleTrasLaPastilla(GHOST g) {
		int distancia = game.getShortestPathDistance(pastillaDePoder, game.getGhostCurrentNodeIndex(g));
		return 2 * (distancia - EAT_DISTANCE) < tiempoComestible();
	}

	private int tiempoComestible() {
		return (int) (EDIBLE_TIME * Math.pow(EDIBLE_TIME_REDUCTION, game.getCurrentLevel() % LEVEL_RESET_REDUCTION));
	}

	private boolean calcularPasillosConMismasPastillas() {
		MOVE[] moves = game.getPossibleMoves(curr, lastMove);
		if (moves.length < 2) return false;
		for (MOVE move : moves)
			if (pillsInCorridor(game, curr, move) != pillsInCorridor(game, curr, moves[0])) return false;
		return true;
	}

	private boolean calcularCadenaPosible() {
		return planCadena(game).size() >= 2;
	}

	private boolean calcularIntercepcionPosible() {
		return fantasmaAInterceptar(game) != null;
	}

	private boolean calcularRiesgoDeEncierro() {
		int mortales = movimientos.mortalesSeguros.length + movimientos.mortalesSiVan.length;
		return mortales > 0 && movimientos.seguros.length <= 1;
	}

	private boolean calcularPastillaUnicaSalida() {
		if (pastillaDePoder == -1 || movimientos.seguros.length != 1) return false;
		int mortales = movimientos.mortalesSeguros.length + movimientos.mortalesSiVan.length;
		MOVE haciaLaPastilla = game.getNextMoveTowardsTarget(curr, pastillaDePoder, lastMove, DM.PATH);
		return mortales > 0 && movimientos.seguros[0] == haciaLaPastilla;
	}

	private boolean calcularPastillaDePoderCerca() {
		if (pastillaDePoder == -1) return false;
		return game.getShortestPathDistance(curr, pastillaDePoder, lastMove) < DISTANCIA_PASTILLA_DE_PODER;
	}

	private boolean calcularLlegaAntesAPastillaDePoder() {
		if (pastillaDePoder == -1) return false;
		int distancia = game.getShortestPathDistance(curr, pastillaDePoder, lastMove);
		for (GHOST g : GHOST.values())
			if (!game.isGhostEdible(g) && ghostTimeTo(game, g, pastillaDePoder) <= distancia) return false;
		return true;
	}

	protected int distanciaPeligro() {
		return DISTANCIA_PELIGRO;
	}

	protected int pocosPasillos() {
		return POCOS_PASILLOS;
	}

	public Movimientos getMovimientos() {
		return movimientos;
	}
	public boolean isPastillaDePoderComida() {
		return pastillaDePoderComida;
	}
	public boolean isPoderActivo() {
		return poderActivo;
	}
	public int getFantasmasComestiblesAlcanzables() {
		return fantasmasComestiblesAlcanzables;
	}
	public boolean isEnPeligro() {
		return enPeligro;
	}
	public boolean isEnCruceDePastillaDePoder() {
		return enCruceDePastillaDePoder;
	}
	public boolean isHayCombo() {
		return hayCombo;
	}
	public boolean isPasillosConMismasPastillas() {
		return pasillosConMismasPastillas;
	}
	public int getPasillosConPastillas() {
		return pasillosConPastillas;
	}
	public boolean isQuedanPocosPasillos() {
		return quedanPocosPasillos;
	}
	public boolean isCadenaPosible() {
		return cadenaPosible;
	}
	public boolean isIntercepcionPosible() {
		return intercepcionPosible;
	}
	public int getFantasmasCerca() {
		return fantasmasCerca;
	}
	public boolean isRiesgoDeEncierro() {
		return riesgoDeEncierro;
	}
	public boolean isPastillaUnicaSalida() {
		return pastillaUnicaSalida;
	}
	public boolean isLlegaAntesAPastillaDePoder() {
		return llegaAntesAPastillaDePoder;
	}
	public boolean isPastillaDePoderCerca() {
		return pastillaDePoderCerca;
	}
}
