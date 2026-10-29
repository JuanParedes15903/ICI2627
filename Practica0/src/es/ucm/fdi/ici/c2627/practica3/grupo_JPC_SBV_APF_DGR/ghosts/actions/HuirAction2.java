package es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.ghosts.actions;

import es.ucm.fdi.ici.rules.RulesAction;
import jess.Fact;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class HuirAction2 implements RulesAction {

    private final GHOST yo;

    public HuirAction2(GHOST yo) { this.yo = yo; }

    @Override
    public MOVE execute(Game game) {
        return game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(yo),
                game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(yo), DM.PATH);
    }

    @Override
    public void parseFact(Fact actionFact) { }

    @Override
    public String getActionId() { return "huir2"; }
}
