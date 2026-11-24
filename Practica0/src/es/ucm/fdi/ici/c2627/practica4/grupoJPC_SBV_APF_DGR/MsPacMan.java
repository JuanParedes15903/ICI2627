package es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR;

import java.util.function.Function;

import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.mspacman.MsPacManInput;
import es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.mspacman.MsPacManInputV4;
import es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.mspacman.actions.CazarEnCadenaAction;
import es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.mspacman.actions.ComerPasilloConMasPastillasAction;
import es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.mspacman.actions.EsperarPastillaDePoderHastaComboAction;
import es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.mspacman.actions.HuirDeVariosFantasmasAction;
import es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.mspacman.actions.HuirDelFantasmaMasCercanoAction;
import es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.mspacman.actions.HuirHaciaPastillaDePoderAction;
import es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.mspacman.actions.InterceptarFantasmaComestibleAction;
import es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.mspacman.actions.IrAPorLasUltimasPastillasAction;
import es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.mspacman.actions.IrAPorPastillaDePoderAction;
import es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.mspacman.actions.JugarSinPeligroAction;
import es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.mspacman.actions.MuerteSeguraAction;
import es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.mspacman.actions.PastillaMasCercanaAction;
import es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.mspacman.actions.PerseguirFantasmaComestibleMasCercanoAction;
import es.ucm.fdi.ici.fuzzy.FuzzyEngine;
import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacMan extends PacmanController {

    public static final String FCL_PATH = "bin/es/ucm/fdi/ici/c2627/practica4/grupoJPC_SBV_APF_DGR/";
    public static final String GRUPO = "Pr4APF_DGR_JPC_SBV";

    private final FuzzyEngine motor;
    private final JugarSinPeligroAction[] acciones;
    private final Function<Game, MsPacManInput> crearInput;
    private final SelectorConParametros selector;

    public MsPacMan() {
        this("mspacman-v10.1.fcl", MsPacManInputV4::new);
    }

    public MsPacMan(String ficheroFcl) {
        this(ficheroFcl, MsPacManInput::new);
    }

    public MsPacMan(String ficheroFcl, Function<Game, MsPacManInput> crearInput) {
        this.crearInput = crearInput;
        setName("MsPacMan " + GRUPO);
        setTeam(GRUPO);
        acciones = new JugarSinPeligroAction[] {
                new PastillaMasCercanaAction(), new ComerPasilloConMasPastillasAction(),
                new IrAPorLasUltimasPastillasAction(), new IrAPorPastillaDePoderAction(),
                new EsperarPastillaDePoderHastaComboAction(), new CazarEnCadenaAction(),
                new InterceptarFantasmaComestibleAction(), new PerseguirFantasmaComestibleMasCercanoAction(),
                new HuirDelFantasmaMasCercanoAction(), new HuirDeVariosFantasmasAction(JugarSinPeligroAction.Huida.POR_MARGEN),
                new HuirHaciaPastillaDePoderAction(), new MuerteSeguraAction() };
        Action[] seleccionables = acciones;
        selector = new SelectorConParametros(seleccionables);
        motor = new FuzzyEngine("MSPACMAN", FCL_PATH + ficheroFcl, "MsPacMan", selector);
    }

    public FuzzyEngine getMotor() {
        return motor;
    }

    @Override
    public MOVE getMove(Game game, long timeDue) {
        MsPacManInput input = crearInput.apply(game);
        for (JugarSinPeligroAction a : acciones)
            a.setEntrada(input);
        selector.setEntrada(input);
        return motor.run(input.getFuzzyValues(), game);
    }
}
