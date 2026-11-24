package es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR;

import java.util.HashMap;

import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.mspacman.MsPacManInput;
import es.ucm.fdi.ici.fuzzy.ActionSelector;

public class SelectorConParametros implements ActionSelector {

    private final MaxActionSelector maximo;
    private MsPacManInput entrada;

    public SelectorConParametros(Action[] acciones) {
        maximo = new MaxActionSelector(acciones);
    }

    public void setEntrada(MsPacManInput entrada) {
        this.entrada = entrada;
    }

    @Override
    public Action selectAction(HashMap<String, Double> salidas) {
        if (entrada != null) entrada.aplicarSalidas(salidas);
        return maximo.selectAction(salidas);
    }
}
