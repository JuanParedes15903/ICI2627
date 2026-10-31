package es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.ghosts.actions;

import es.ucm.fdi.ici.rules.RulesAction;
import jess.Fact;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class SalirCarcelAction implements RulesAction {

    private final GHOST yo;

    public SalirCarcelAction(GHOST yo) { 
        this.yo = yo; 
    }

    @Override
    public MOVE execute(Game game) {
        // Mientras el fantasma está en la base (LairTime > 0), el motor
        // del juego ignora nuestras decisiones. Devolver Neutral es lo más seguro.
        return MOVE.NEUTRAL;
    }

    @Override
    public void parseFact(Fact actionFact) { }

    @Override
    public String getActionId() { 
        return "salir_carcel"; 
    }
}