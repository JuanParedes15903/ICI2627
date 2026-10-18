package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.MsPacMan.Transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.MsPacMan.MsPacManInput;
import es.ucm.fdi.ici.fsm.Transition;

public class NoHayPPillsTransition implements Transition{
	public NoHayPPillsTransition() {
	}
	
	
	@Override
	public boolean evaluate(Input in) {
		MsPacManInput input = (MsPacManInput) in;
        return input.getPildorasDePoder() == 0;
	}

	 @Override
	    public String toString() {
	        return "No quedan píldoras de poder";
	    }
}
