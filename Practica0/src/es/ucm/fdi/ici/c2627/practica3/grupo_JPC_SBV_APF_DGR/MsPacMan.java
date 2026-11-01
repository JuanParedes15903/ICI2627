package es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR;

import java.util.HashMap;

import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.MsPacManInput;
import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.actions.CazarEnCadenaAction;
import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.actions.ComerPasilloConMasPastillasAction;
import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.actions.EsperarPastillaDePoderHastaComboAction;
import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.actions.HuirDeVariosFantasmasAction;
import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.actions.HuirDelFantasmaMasCercanoAction;
import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.actions.HuirHaciaPastillaDePoderAction;
import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.actions.InterceptarFantasmaComestibleAction;
import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.actions.IrAPorLasUltimasPastillasAction;
import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.actions.IrAPorPastillaDePoderAction;
import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.actions.JugarSinPeligroAction;
import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.actions.MuerteSeguraAction;
import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.actions.PastillaMasCercanaAction;
import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.actions.PerseguirFantasmaComestibleMasCercanoAction;
import es.ucm.fdi.ici.rules.RuleEngine;
import es.ucm.fdi.ici.rules.RulesAction;
import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacMan extends PacmanController {

    private static final String RULES_PATH = "es/ucm/fdi/ici/c2627/practica3/grupo_JPC_SBV_APF_DGR/mspacman/";

    private final RuleEngine motor;
    private final HashMap<String, RulesAction> acciones = new HashMap<>();

    public MsPacMan() {
        this("mspacman-v4.clp");
    }

    public MsPacMan(String ficheroReglas) {
        setName("MsPacMan JPC_SBV_APF_DGR");
        setTeam("JPC_SBV_APF_DGR");

        for (RulesAction a : new RulesAction[] {
                new ComerPasilloConMasPastillasAction(), new IrAPorLasUltimasPastillasAction(),
                new PastillaMasCercanaAction(), new IrAPorPastillaDePoderAction(),
                new EsperarPastillaDePoderHastaComboAction(), new CazarEnCadenaAction(),
                new InterceptarFantasmaComestibleAction(), new PerseguirFantasmaComestibleMasCercanoAction(),
                new HuirDelFantasmaMasCercanoAction(), new HuirDeVariosFantasmasAction(),
                new HuirHaciaPastillaDePoderAction(), new MuerteSeguraAction() })
            acciones.put(a.getActionId(), a);

        motor = new RuleEngine("MSPACMAN", RULES_PATH + ficheroReglas, acciones);
    }

    public RuleEngine getMotor() { return motor; }

    @Override
    public MOVE getMove(Game game, long timeDue) {
        MsPacManInput input = new MsPacManInput(game);
        for (RulesAction a : acciones.values())
            if (a instanceof JugarSinPeligroAction) ((JugarSinPeligroAction) a).setEntrada(input);
        motor.reset();
        motor.assertFacts(input.getFacts());
        return motor.run(game);
    }
}
