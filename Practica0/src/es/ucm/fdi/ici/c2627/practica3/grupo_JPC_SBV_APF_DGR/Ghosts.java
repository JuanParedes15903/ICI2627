package es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.ghosts.GhostsInput;
import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.ghosts.actions.HuirAction;
import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.ghosts.actions.PerseguirAction;
// Añadimos los imports de las nuevas acciones
import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.ghosts.actions.InterceptarAction;
import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.ghosts.actions.PatrullarAction;
import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.ghosts.actions.EmboscarAction;
import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.ghosts.actions.SalirCarcelAction;
import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.ghosts.actions.HuirDispersoAction;

import es.ucm.fdi.ici.rules.RuleEngine;
import es.ucm.fdi.ici.rules.RulesAction;
import pacman.controllers.GhostController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Ghosts extends GhostController {

    private static final String RULES_PATH = "es/ucm/fdi/ici/c2627/practica3/grupo_JPC_SBV_APF_DGR/";

    private final EnumMap<GHOST, RuleEngine> motores = new EnumMap<>(GHOST.class);

    public Ghosts() {
        // OJO: Asegúrate de que el archivo se llame exactamente así.
        // En tus errores anteriores ponía "GhostRoles.clp" y aquí pone "GhostsRolesv1.clp".
        this("GhostsRolesv2.clp");                    
    }

    public Ghosts(String ficheroReglas) {
        setName("Ghosts YY");
        setTeam("Grupo YY");
        for (GHOST g : GHOST.values()) {
            HashMap<String, RulesAction> acciones = new HashMap<>();   // las de ESTE fantasma
            
            // ¡AQUÍ ESTABA EL FALLO! Añadimos todas las acciones al array
            RulesAction[] arrayAcciones = new RulesAction[] { 
                new PerseguirAction(g), 
                new HuirAction(g),
                new InterceptarAction(g),
                new PatrullarAction(g),
                new EmboscarAction(g),
                new SalirCarcelAction(g),
                new HuirDispersoAction(g)
            };
            
            for (RulesAction a : arrayAcciones) {
                acciones.put(a.getActionId(), a);
            }
                
            motores.put(g, new RuleEngine(g.name(), RULES_PATH + ficheroReglas, acciones));
        }
    }

    public RuleEngine getMotor(GHOST g) { 
        return motores.get(g); 
    }

    @Override
    public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
        GhostsInput input = new GhostsInput(game);               // una vez para los cuatro
        EnumMap<GHOST, MOVE> movimientos = new EnumMap<>(GHOST.class);
        for (GHOST g : GHOST.values()) {
            if (!game.doesGhostRequireAction(g)) continue;       // fuera de los cruces no hay nada que decidir
            RuleEngine motor = motores.get(g);
            motor.reset();
            motor.assertFacts(input.getFacts());
            motor.assertFacts(List.of("(YO (nombre " + g + "))"));
            movimientos.put(g, motor.run(game));
        }
        return movimientos;
    }
}