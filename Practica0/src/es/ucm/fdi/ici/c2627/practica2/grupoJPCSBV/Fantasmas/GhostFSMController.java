package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.Fantasmas;

import java.util.EnumMap;
import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.Fantasmas.actions.*;
import es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.Fantasmas.transitions.*;
import es.ucm.fdi.ici.fsm.CompoundState;
import es.ucm.fdi.ici.fsm.FSM;
import es.ucm.fdi.ici.fsm.SimpleState;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.controllers.GhostController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GhostFSMController extends GhostController {

    private final EnumMap<GHOST, FSM> fsms;

    public GhostFSMController() {
        this.fsms = new EnumMap<>(GHOST.class);
        for (GHOST ghost : GHOST.values()) {
            this.fsms.put(ghost, buildFSM(ghost));
        }
    }

    private FSM buildFSM(GHOST ghost) {
        FSM mainFSM = new FSM(ghost + "_MainFSM");

        // Instanciación de acciones (incluyendo la versión de persecución coordinada)
        Action perseguirAct = new PerseguirAction(ghost);
        Action huirMsPacmanAct = new HuirMsPacmanAction(ghost);
        Action huirDispersosAct = new HuirDispersosAction(ghost);
        Action emboscarAct = new EmboscarAction(ghost); 
        Action esperarAct = new EsperarSalirAction(ghost);

        SimpleState perseguirState = new SimpleState("Perseguir", perseguirAct);
        SimpleState esperarState = new SimpleState("Esperar a salir", esperarAct);

        // =========================================================================
        // 1. ESTADO COMPUESTO SUPERIOR (ESTRATEGIA NORMAL)
        // =========================================================================
        FSM fsmSuperior = new FSM("FSM_Superior_" + ghost);
        
        SimpleState movAleatoriosState = new SimpleState("Movimientos aleatorios", huirDispersosAct);
        SimpleState cercarPPState = new SimpleState("Cercar PP activas", perseguirAct);
        SimpleState cercarPillsState = new SimpleState("Cercar pills activas", perseguirAct);
        SimpleState emboscarState = new SimpleState("Emboscar", emboscarAct);

        fsmSuperior.add(movAleatoriosState, 
            new GenericTransition("PP activas > 1", in -> ((GhostInput)in).getNumPPActivas() > 1), 
            cercarPPState);

        fsmSuperior.add(movAleatoriosState, 
            new GenericTransition("Pills activas > 1 && PP <= 1", in -> ((GhostInput)in).getNumPillsActivas() > 1 && ((GhostInput)in).getNumPPActivas() <= 1), 
            cercarPillsState);

        fsmSuperior.add(movAleatoriosState, 
            new GenericTransition("PP y pills ya asignadas", in -> ((GhostInput)in).getNumPPActivas() == 0 && ((GhostInput)in).getNumPillsActivas() <= 1), 
            emboscarState);

        fsmSuperior.add(cercarPPState, 
            new GenericTransition("Otro fantasma más cerca a la mejor PP", in -> !((GhostInput)in).isEsElMasCercaAMejorPP()), 
            cercarPillsState);

        fsmSuperior.add(cercarPillsState, 
            new GenericTransition("Es el fantasma más cerca a la mejor PP", in -> ((GhostInput)in).isEsElMasCercaAMejorPP()), 
            cercarPPState);

        fsmSuperior.ready(movAleatoriosState);
        CompoundState bloqueSuperior = new CompoundState("Estrategia Normal", fsmSuperior);

        // =========================================================================
        // 2. ESTADO COMPUESTO INFERIOR (ESTRATEGIA HUIDA)
        // =========================================================================
        FSM fsmInferior = new FSM("FSM_Inferior_" + ghost);

        SimpleState alejarseState = new SimpleState("Alejarse de Ms Pacman", huirMsPacmanAct);
        SimpleState protegerPPState = new SimpleState("Proteger PP", perseguirAct);
        SimpleState huidaEficienteState = new SimpleState("Huida eficiente", huirDispersosAct);

        fsmInferior.add(alejarseState, 
            new GenericTransition("Fantasma + cerca de PP", in -> ((GhostInput)in).getDistanciaMejorPP() != -1 && ((GhostInput)in).getDistanciaMejorPP() < ((GhostInput)in).getDistanciaMsPacman()), 
            protegerPPState);

        fsmInferior.add(alejarseState, 
            new GenericTransition("Comestible && distancia > 20", in -> ((GhostInput)in).getTiempoComestible() > 0 && ((GhostInput)in).getDistanciaMsPacman() > 20), 
            huidaEficienteState);

        fsmInferior.add(huidaEficienteState, 
            new GenericTransition("Comestible && distancia <= 20", in -> ((GhostInput)in).getTiempoComestible() > 0 && ((GhostInput)in).getDistanciaMsPacman() <= 20), 
            alejarseState);

        fsmInferior.add(protegerPPState, 
            new GenericTransition("Comestible && Ms Pacman cerca", in -> ((GhostInput)in).getTiempoComestible() > 0 && ((GhostInput)in).getDistanciaMsPacman() <= 20), 
            alejarseState);

        fsmInferior.ready(alejarseState);
        CompoundState bloqueInferior = new CompoundState("Estrategia Huida", fsmInferior);

        // =========================================================================
        // 3. ENSAMBLADO DE LA FSM PRINCIPAL
        // =========================================================================
        Transition comestibleTrans = new GenericTransition("Se vuelve comestible", in -> ((GhostInput)in).getTiempoComestible() > 0);
        Transition noComestibleTrans = new GenericTransition("Ya no es comestible", in -> ((GhostInput)in).getTiempoComestible() == 0);
        Transition comidoTrans = new ComidoTransition();
        Transition revividoTrans = new GenericTransition("Revivido en cueva", in -> !((GhostInput)in).isComido());

        // Manejo de muertes
        mainFSM.add(bloqueSuperior, comidoTrans, esperarState);
        mainFSM.add(perseguirState, comidoTrans, esperarState);
        mainFSM.add(bloqueInferior, comidoTrans, esperarState);
        mainFSM.add(esperarState, revividoTrans, bloqueSuperior);

        // Manejo de estado comestible (prioridad a huir)
        mainFSM.add(bloqueSuperior, comestibleTrans, bloqueInferior);
        mainFSM.add(perseguirState, comestibleTrans, bloqueInferior);
        mainFSM.add(bloqueInferior, noComestibleTrans, bloqueSuperior);

        // Transiciones normales entre estrategia superior y persecución
        mainFSM.add(bloqueSuperior, new MasCercaAMsPacmanQueAPPTransition(), perseguirState);
        
        mainFSM.add(perseguirState, new GenericTransition("Dejar de perseguir (Lógica/Exceso)", 
            in -> ((GhostInput)in).isDebeDejarDePerseguir() || 
                  (!((GhostInput)in).isPersecucionPermitida()) ||
                  (((GhostInput)in).getDistanciaMejorPP() != -1 && ((GhostInput)in).getDistanciaMsPacman() >= ((GhostInput)in).getDistanciaMejorPP())
        ), bloqueSuperior);

        mainFSM.ready(bloqueSuperior);
        return mainFSM;
    }

    @Override
    public void preCompute(String opponent) {
        for (FSM fsm : fsms.values()) fsm.reset();
    }

    @Override
    public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
        EnumMap<GHOST, MOVE> moves = new EnumMap<>(GHOST.class);
        for (GHOST ghost : GHOST.values()) {
            GhostInput input = new GhostInput(ghost, game);
            moves.put(ghost, fsms.get(ghost).run(input));
        }
        return moves;
    }
}