package es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR;

import java.util.EnumMap;

import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.ghosts.GhostsInput;
import es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.ghosts.actions.CercoAction;
import es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.ghosts.actions.CortarCaminoPPAction;
import es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.ghosts.actions.EmboscarAction;
import es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.ghosts.actions.HuirAction;
import es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.ghosts.actions.HuirDispersoAction;
import es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.ghosts.actions.InterceptarAction;
import es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.ghosts.actions.PatrullarAction;
import es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.ghosts.actions.PerseguirAction;
import es.ucm.fdi.ici.fuzzy.FuzzyEngine;
import pacman.controllers.GhostController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Ghosts extends GhostController {

    private final EnumMap<GHOST, FuzzyEngine> motores = new EnumMap<>(GHOST.class);

    public Ghosts() {
        this("GhostsRolesFuzzyv0.fcl");                    // la versión que va al concurso
    }

    /** Un motor por fantasma, todos con el mismo .fcl: cada uno con sus acciones y sus valores. */
    public Ghosts(String ficheroFcl) {
        setName("Ghosts JPC_SBV_APF_DGR");
        setTeam("Grupo JPC_SBV_APF_DGR");
        for (GHOST g : GHOST.values()) {
            // Se añaden todas las acciones adaptadas al array del selector
            Action[] acciones = { 
                new PerseguirAction(g), 
                new HuirAction(g),
                new CercoAction(g),
                new CortarCaminoPPAction(g),
                new EmboscarAction(g),
                new HuirDispersoAction(g),
                new InterceptarAction(g),
                new PatrullarAction(g)
            };
            motores.put(g, new FuzzyEngine(g.name(), MsPacMan.FCL_PATH + ficheroFcl, "Ghost",
                    new MaxActionSelector(acciones)));
        }
    }

    public FuzzyEngine getMotor(GHOST g) { return motores.get(g); }

    @Override
    public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
        EnumMap<GHOST, MOVE> movimientos = new EnumMap<>(GHOST.class);
        for (GHOST g : GHOST.values()) {
            if (!game.doesGhostRequireAction(g)) continue;       // fuera de los cruces no hay nada que decidir
            movimientos.put(g, motores.get(g).run(new GhostsInput(game, g).getFuzzyValues(), game));
        }
        return movimientos;
    }
}