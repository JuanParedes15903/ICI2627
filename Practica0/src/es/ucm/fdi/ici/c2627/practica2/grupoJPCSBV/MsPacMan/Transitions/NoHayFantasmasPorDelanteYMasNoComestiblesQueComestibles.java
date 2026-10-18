package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.MsPacMan.Transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.MsPacMan.MsPacManInput;
import es.ucm.fdi.ici.fsm.Transition;

public class NoHayFantasmasPorDelanteYMasNoComestiblesQueComestibles implements Transition {

	@Override
	public boolean evaluate(Input in) {
		MsPacManInput input = (MsPacManInput) in;
		return !input.getFantasmasPorDelante()
                && !input.getMasComestiblesQueNoComestibles();
	}

	@Override
	public String toString() {
		return "no hay fantasmas por delante y más no comestibles que comestibles";
	}
}
