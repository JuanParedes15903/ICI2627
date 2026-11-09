package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.MsPacMan;

import java.util.HashSet;
import java.util.Set;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.MsPacMan.actions.*;
import es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.MsPacMan.Transitions.*;
import es.ucm.fdi.ici.fsm.CompoundState;
import es.ucm.fdi.ici.fsm.FSM;
import es.ucm.fdi.ici.fsm.FSMObserver;
import es.ucm.fdi.ici.fsm.SimpleState;
import es.ucm.fdi.ici.fsm.Transition;
import es.ucm.fdi.ici.fsm.observers.GraphFSMObserver;
import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacManV3 extends PacmanController {

	// true: abre 3 ventanas con los grafos de las FSM. false: no se crea ningún GraphFSMObserver.
	private static final boolean VER_GRAFOS = false;

	private final FSM fsm; // la de fuera
	private final FSM estandar; // la de dentro: hay que poder resetearla
	private final FSM huir;

	private int numCambiosEstado  = 0;
	private int numCambiosEstandar = 0;

	/** Número total de cambios de estado de la FSM exterior (MsPacMan). */
	public int getNumCambiosEstado() {
		return numCambiosEstado;
	}

	/** Número total de cambios de estado en la sub-FSM estándar (detecta el rebote). */
	public int getNumCambiosEstandar() {
		return numCambiosEstandar;
	}

	/**
	 * Salida de "Buscar caminos con PP" con histéresis: se sale por debajo del
	 * umbral de entrada. Entrada: >= 1 detrás. Salida: < 1 detrás (es decir, 0).
	 * Sustituye a GrupoDisueltoTransition, cuyo 2 va fijo y no se puede tocar.
	 */
	private static class GrupoDisueltoV3Transition implements Transition {
		private final int radio, umbral;

		GrupoDisueltoV3Transition(int radio, int umbral) {
			this.radio = radio;
			this.umbral = umbral;
		}

		@Override
		public boolean evaluate(Input in) {
			MsPacManInput input = (MsPacManInput) in;
			return input.getFantasmasCercaPorDetras() < umbral;
		}

		@Override
		public String toString() {
			return "menos de " + umbral + " a menos de " + radio;
		}
	}

	/**
	 * Envuelve una transición para darle otra etiqueta. GraphFSMObserver usa
	 * toString() como id de arista, y debe ser único dentro de cada FSM.
	 */
	private static class Etiquetada implements Transition {
		private final Transition t;
		private final String etiqueta;

		Etiquetada(Transition t, String etiqueta) {
			this.t = t;
			this.etiqueta = etiqueta;
		}

		@Override
		public boolean evaluate(Input in) {
			return t.evaluate(in);
		}

		@Override
		public String toString() {
			return etiqueta;
		}
	}

	/** Devuelve la transición tal cual si su nombre es nuevo en esa FSM; si no, le añade " (n)". */
	private static Transition unica(Set<String> usados, Transition t) {
		String base = t.toString();
		String nombre = base;
		int i = 2;
		while (!usados.add(nombre))
			nombre = base + " (" + i++ + ")";
		return nombre.equals(base) ? t : new Etiquetada(t, nombre);
	}

	public MsPacManV3() {
		setName("MsPacMan JPCSBV V3");

		Set<String> usadosEstandar = new HashSet<>();
		Set<String> usadosHuir = new HashSet<>();
		Set<String> usadosFuera = new HashSet<>();

		estandar = new FSM("Estandar V3");

		// Observer para contar cambios en la sub-FSM estándar (detecta el rebote masPildorasSinPoder <-> caminosConPoder)
		estandar.addObserver(new FSMObserver() {
			@Override public void fsmAdd(String s, String t, String u) {}
			@Override public void fsmReady(String s) {}
			@Override public void fsmReset(String s) {}
			@Override public void fsmTransition(String s, String t, String u) { numCambiosEstandar++; }
		});
		huir = new FSM("Huir V3");
		fsm = new FSM("MsPacMan V3");

		// Observer que cuenta los cambios de estado de la FSM principal
		fsm.addObserver(new FSMObserver() {
			@Override public void fsmAdd(String s, String t, String u) {}
			@Override public void fsmReady(String s) {}
			@Override public void fsmReset(String s) {}
			@Override public void fsmTransition(String s, String t, String u) { numCambiosEstado++; }
		});

		GraphFSMObserver obsEstandar = null, obsHuir = null, obsFuera = null;
		if (VER_GRAFOS) { // antes de los add(): fsmAdd se notifica al crear la transición
			obsEstandar = new GraphFSMObserver("Estandar V3");
			obsHuir = new GraphFSMObserver("Huir V3");
			obsFuera = new GraphFSMObserver("MsPacMan V3");
			estandar.addObserver(obsEstandar);
			huir.addObserver(obsHuir);
			fsm.addObserver(obsFuera);
		}

		// ---------- Estandar ----------
		SimpleState masPildorasSinPoder = new SimpleState(new BuscarCaminosConMasPildorasSinPoderAction());
		SimpleState caminosConPoder = new SimpleState(new BuscarCaminosPPillAction());
		SimpleState BuscarCaminosConPildorasSeguros = new SimpleState(new BuscarCaminosConPildorasSegurosAction());
		SimpleState AcercarseAFantasmas = new SimpleState(new AcercarseAFantasmasAction());

		estandar.add(BuscarCaminosConPildorasSeguros, unica(usadosEstandar, new TodasLasPildorasBloqueadasTransition()), AcercarseAFantasmas);
		estandar.add(AcercarseAFantasmas, unica(usadosEstandar, new AlgunaPildoraDesbloqueadaTransition()), BuscarCaminosConPildorasSeguros);
		estandar.add(masPildorasSinPoder, unica(usadosEstandar, new GrupoCercaTransition(40, 1)), caminosConPoder);
		estandar.add(caminosConPoder, unica(usadosEstandar, new GrupoDisueltoV3Transition(40, 1)), masPildorasSinPoder); // ÚNICO CAMBIO vs V1
		estandar.add(masPildorasSinPoder, unica(usadosEstandar, new NoHayPPillsTransition()), BuscarCaminosConPildorasSeguros);
		estandar.add(caminosConPoder, unica(usadosEstandar, new NoHayPPillsTransition()), BuscarCaminosConPildorasSeguros);

		estandar.ready(masPildorasSinPoder);

		CompoundState estandarState = new CompoundState("Estandar", estandar);

		// ---------- Huir ----------
		SimpleState evitarFantasmas = new SimpleState(new EvitarCaminosConFantasmasAction());
		SimpleState huirHaciaPildoras = new SimpleState(new HuirHaciaPildorasAction());
		SimpleState irAPPill = new SimpleState(new IrAPPillAction());

		huir.add(evitarFantasmas, unica(usadosHuir, new NoHayPPillsTransition()), huirHaciaPildoras);
		huir.add(evitarFantasmas, unica(usadosHuir, new GrupoCercaTransition(40, 1)), irAPPill);
		huir.add(huirHaciaPildoras, unica(usadosHuir, new CaminoAPildorasDePoderBloqueadosTransition()), evitarFantasmas);
		huir.add(irAPPill, unica(usadosHuir, new CaminoAPildorasDePoderBloqueadosTransition()), evitarFantasmas);

		huir.ready(evitarFantasmas);

		CompoundState huirState = new CompoundState("Huir", huir);

		// ---------- Principal ----------
		SimpleState perseguirComestibles = new SimpleState(new PerseguirComestiblesAction());

		fsm.add(huirState, unica(usadosFuera, new HayMasComestiblesQueNoComestiblesTransition()), perseguirComestibles);
		fsm.add(huirState, unica(usadosFuera, new NoHayFantasmasPorDelantetransition()), estandarState);
		fsm.add(estandarState, unica(usadosFuera, new HayMasComestiblesQueNoComestiblesTransition()), perseguirComestibles);
		fsm.add(estandarState, unica(usadosFuera, new HayFantasmasPorDelanteTransition()), huirState);
		fsm.add(perseguirComestibles, unica(usadosFuera, new HayFantasmasPorDelanteYMasNoComestiblesQueComestibles()), huirState);
		fsm.add(perseguirComestibles, unica(usadosFuera, new NoHayFantasmasPorDelanteYMasNoComestiblesQueComestibles()), estandarState);
		fsm.ready(estandarState);

		if (VER_GRAFOS) {
			obsEstandar.showInFrame(null);
			obsHuir.showInFrame(null);
			obsFuera.showInFrame(null);
		}
	}

	@Override
	public void preCompute(String opponent) {
		fsm.reset();
		estandar.reset(); // reset() no llama a stop(): la de dentro no se entera sola
		huir.reset();
	}

	@Override
	public MOVE getMove(Game game, long timeDue) {
		if (game.isJunction(game.getPacmanCurrentNodeIndex())) { // Solo hace decisiones si está en un cruce
			return fsm.run(new MsPacManInput(game));
		}
		else return null;
	}
}