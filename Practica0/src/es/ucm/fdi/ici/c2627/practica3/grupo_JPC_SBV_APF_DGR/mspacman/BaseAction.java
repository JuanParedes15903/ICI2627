package es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman;

import es.ucm.fdi.ici.rules.RulesAction;
import jess.Fact;

public abstract class BaseAction implements RulesAction, Funciones {

	@Override
	public String getActionId() {
		String nombre = getClass().getSimpleName();
		return nombre.endsWith("Action") ? nombre.substring(0, nombre.length() - "Action".length()) : nombre;
	}

	@Override
	public void parseFact(Fact actionFact) {
	}
}
