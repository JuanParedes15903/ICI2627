package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.MsPacMan.Transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.fsm.Transition;

public class CaminoAPildoraBloqueadoTransition implements Transition{
	 private final int pildora;
	 
	 CaminoAPildoraBloqueadoTransition(int pildora){
		 this.pildora=pildora;
	 }
	 
	@Override
	public boolean evaluate(Input in) {
		// TODO Auto-generated method stub
		return false;
	}
	 @Override
	    public String toString() {
	        return "el camino a la pildora está bloqueado";
	    }

}
