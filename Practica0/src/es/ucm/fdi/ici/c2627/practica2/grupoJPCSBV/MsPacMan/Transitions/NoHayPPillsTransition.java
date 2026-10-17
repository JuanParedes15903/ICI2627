package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.MsPacMan.Transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.fsm.Transition;

public class NoHayPPillsTransition implements Transition{

	@Override
	public boolean evaluate(Input in) {
		// TODO Auto-generated method stub
		return false;
	}

	 @Override
	    public String toString() {
	        return "Quedan píldoras de poder";
	    }
}
