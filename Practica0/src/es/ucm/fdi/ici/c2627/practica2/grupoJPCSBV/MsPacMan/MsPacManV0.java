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

		// 1. La máquina de dentro, completa y con su estado inicial
		
		  comer = new FSM("Comer"); 
		  SimpleState pildora = new SimpleState(new PildoraCercanaAction());
		  SimpleState poder = new SimpleState(new PildoraDePoderAction());
		  comer.add(pildora, new GrupoCercaTransition(40, 2), poder); 
		  comer.add(poder, new GrupoDisueltoTransition(40), pildora); 
		  comer.ready(pildora);
		 
		  // 2. Envuelta en un estado, se usa como cualquier otro CompoundState
		  comerState = new CompoundState("Comer", comer); 
		  SimpleState huir = new SimpleState(new HuirAction());
		  
		  fsm = new FSM("MsPacMan"); 
		  fsm.add(comerState, new AmenazaCercaTransition(12), huir); // sale de los dos de dentro 
		  fsm.add(huir, new AmenazaLejosTransition(20), comerState); 
		  fsm.ready(comerState);
		

		estandar= new FSM("Estandar"); 
		SimpleState masPildorasSinPoder=new SimpleState(new BuscarCaminosConMasPildorasSinPoder());
		SimpleState caminosConPoder=new SimpleState(new BuscarCaminosPPill());
		SimpleState BuscarCaminosConPildorasSeguros = new SimpleState(new BuscarCaminosConPildorasSeguros());
		SimpleState AcercarseAFantasmas = new SimpleState(new AcercarseAFantasmas());
		
		estandar.add(BuscarCaminosConPildorasSeguros, CaminoAPildoraBloqueadoTransition(), AcercarseAFantasmas);
		estandar.add(AcercarseAFantasmas, null, BuscarCaminosConPildorasSeguros);
		
	}

	@Override
	public void preCompute(String opponent) {
		fsm.reset();
		comer.reset(); // reset() no llama a stop(): la de dentro no se entera sola
	}

	@Override
	public MOVE getMove(Game game, long timeDue) {
		return fsm.run(new MsPacManInput(game));
	}
}
