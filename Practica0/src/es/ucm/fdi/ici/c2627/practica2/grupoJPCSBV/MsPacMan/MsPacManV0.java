package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.MsPacMan;

import es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.MsPacMan.actions.*;
import es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.MsPacMan.Transitions.*;
import es.ucm.fdi.ici.fsm.CompoundState;
import es.ucm.fdi.ici.fsm.FSM;
import es.ucm.fdi.ici.fsm.SimpleState;
import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacManV0 extends PacmanController {
	private final FSM fsm; // la de fuera
	private final FSM estandar; // la de dentro: hay que poder resetearla
	private final FSM huir;

	public MsPacManV0() {
		setName("MsPacMan JPCSBV");

		estandar = new FSM("Estandar");
		SimpleState masPildorasSinPoder = new SimpleState(new BuscarCaminosConMasPildorasSinPoderAction());
		SimpleState caminosConPoder = new SimpleState(new BuscarCaminosPPillAction());
		SimpleState BuscarCaminosConPildorasSeguros = new SimpleState(new BuscarCaminosConPildorasSegurosAction());
		SimpleState AcercarseAFantasmas = new SimpleState(new AcercarseAFantasmasAction());

		estandar.add(BuscarCaminosConPildorasSeguros, new TodasLasPildorasBloqueadasTransition(), AcercarseAFantasmas);
		estandar.add(AcercarseAFantasmas, new AlgunaPildoraDesbloqueadaTransition(), BuscarCaminosConPildorasSeguros);
		estandar.add(masPildorasSinPoder, new GrupoCercaTransition(40, 2), caminosConPoder);
		estandar.add(caminosConPoder, new GrupoDisueltoTransition(40), masPildorasSinPoder);
		estandar.add(masPildorasSinPoder, new NoHayPPillsTransition(), BuscarCaminosConPildorasSeguros);
		estandar.add(caminosConPoder, new NoHayPPillsTransition(), BuscarCaminosConPildorasSeguros);

		estandar.ready(masPildorasSinPoder);

		CompoundState estandarState = new CompoundState("Estandar", estandar);

		huir = new FSM("Huir");
		SimpleState evitarFantasmas = new SimpleState(new EvitarCaminosConFantasmasAction());
		SimpleState huirHaciaPildoras = new SimpleState(new HuirHaciaPildorasAction());
		SimpleState irAPPill = new SimpleState(new IrAPPillAction());

		huir.add(evitarFantasmas, new NoHayPPillsTransition(), huirHaciaPildoras);
		huir.add(evitarFantasmas, new GrupoCercaTransition(40, 2), irAPPill);
		huir.add(huirHaciaPildoras, new CaminoAPildorasDePoderBloqueadosTransition(), evitarFantasmas);
		huir.add(irAPPill, new CaminoAPildorasDePoderBloqueadosTransition(), evitarFantasmas);

		huir.ready(evitarFantasmas);

		CompoundState huirState = new CompoundState("Huir", huir);

		SimpleState perseguirComestibles = new SimpleState(new PerseguirComestiblesAction());

		fsm = new FSM("MsPacMan");
		
		fsm.add(huirState, new HayMasComestiblesQueNoComestiblesTransition(), perseguirComestibles);
		fsm.add(huirState, new NoHayFantasmasPorDelantetransition(), estandarState);
		fsm.add(estandarState, new HayMasComestiblesQueNoComestiblesTransition(), perseguirComestibles);
		fsm.add(estandarState, new HayFantasmasPorDelanteTransition(), huirState);
		fsm.add(perseguirComestibles, new HayFantasmasPorDelanteYMasNoComestiblesQueComestibles(), huirState);
		fsm.add(perseguirComestibles, new NoHayFantasmasPorDelanteYMasNoComestiblesQueComestibles(), estandarState);
		fsm.ready(estandarState);
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
