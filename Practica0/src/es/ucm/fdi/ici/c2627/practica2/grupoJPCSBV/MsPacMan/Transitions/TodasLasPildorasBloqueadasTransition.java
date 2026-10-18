package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.MsPacMan.Transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.MsPacMan.MsPacManInput;
import es.ucm.fdi.ici.fsm.Transition;

public class TodasLasPildorasBloqueadasTransition implements Transition{
	public TodasLasPildorasBloqueadasTransition(){
	}
	
	@Override
	public boolean evaluate(Input in) {
		MsPacManInput input = (MsPacManInput) in;
		return input.getTodasLasPildorasBloqueadas();
	}
	 @Override
	    public String toString() {
	        return "Todas las pildoras estan bloqueadas";
	    }
}
