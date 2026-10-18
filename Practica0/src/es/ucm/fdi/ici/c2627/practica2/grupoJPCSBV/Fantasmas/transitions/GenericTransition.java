package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.Fantasmas.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.fsm.Transition;
import java.util.function.Predicate;

public class GenericTransition implements Transition {

    private final String description;
    private final Predicate<Input> condition;

    public GenericTransition(String description, Predicate<Input> condition) {
        this.description = description;
        this.condition = condition;
    }

    @Override
    public boolean evaluate(Input in) {
        return condition.test(in);
    }

    @Override
    public String toString() {
        return description;
    }
}