package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.MsPacMan.Transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.MsPacMan.MsPacManInput;
import es.ucm.fdi.ici.fsm.Transition;

public class CaminoAPildorasDePoderBloqueadosTransition implements Transition{
	 
	 public CaminoAPildorasDePoderBloqueadosTransition(){
	 }
	 
	@Override
	public boolean evaluate(Input in) {
		MsPacManInput input = (MsPacManInput) in;
		return input.getCaminosAPildorasDePoderBloqueados();
	}
	 @Override
	    public String toString() {
	        return "todos los caminos a las píldoras de poder están bloqueados";
	    }

}
