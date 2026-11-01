package es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.depurar;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import jess.Fact;
import jess.JessException;
import jess.Rete;

/**
 * Prueba las reglas sin jugar: se escribe una situación a mano, como hechos,
 * y se mira qué ACTION gana. Cada caso es una pregunta con su respuesta esperada.
 */
public class ProbarReglas {

    static final String RUTA = "es/ucm/fdi/ici/c2627/practica3/grupo_JPC_SBV_APF_DGR/mspacman/";
    static int fallos = 0;

    public static void main(String[] args) throws JessException {
        for (String version : new String[] { "mspacman-v3.clp", "mspacman-v4.clp" }) {
            System.out.println("\n===== " + version);
            probar(version, version.equals("mspacman-v3.clp"));
        }
        System.out.println(fallos == 0 ? "\nTodos los casos bien." : "\n" + fallos + " casos MAL.");
    }

    static void probar(String fichero, boolean conComboListo) throws JessException {
        Rete jess = new Rete();
        jess.batch(RUTA + fichero);
        jess.eval("(watch rules)");           // Jess escribe cada regla que dispara

        caso(jess, "Pastilla mas cercana", "PastillaMasCercana",
                mspacman());

        caso(jess, "Pasillo con mas pastillas", "ComerPasilloConMasPastillas",
                mspacman("pasillosConMismasPastillas false"));

        caso(jess, "Ultimas pastillas", "IrAPorLasUltimasPastillas",
                mspacman("pasillosConMismasPastillas false", "quedanPocosPasillos true", "pasillosConPastillas 4"));

        caso(jess, "Esperar al combo", "EsperarPastillaDePoderHastaCombo",
                mspacman("pasillosConMismasPastillas false", "enCruceDePastillaDePoder true"));

        if (conComboListo)
            caso(jess, "Combo: comerse la pastilla", "IrAPorPastillaDePoder",
                    mspacman("pasillosConMismasPastillas false", "enCruceDePastillaDePoder true", "hayCombo true"));
        else
            caso(jess, "Combo: seguir comiendo", "ComerPasilloConMasPastillas",
                    mspacman("pasillosConMismasPastillas false", "enCruceDePastillaDePoder true", "hayCombo true"));

        caso(jess, "Perseguir comestible", "PerseguirFantasmaComestibleMasCercano",
                mspacman("pasillosConMismasPastillas false"),
                fantasma("BLINKY", "comestible true", "alcanzable true", "distancia 10", "tiempo 100"));

        caso(jess, "Comestible lejos: no perseguir", "ComerPasilloConMasPastillas",
                mspacman("pasillosConMismasPastillas false"),
                fantasma("BLINKY", "comestible true", "distancia 80", "tiempo 20"));

        caso(jess, "Interceptar comestible", "InterceptarFantasmaComestible",
                mspacman("intercepcionPosible true"),
                fantasma("BLINKY", "comestible true", "alcanzable true", "distancia 10", "tiempo 100"));

        caso(jess, "Cazar en cadena", "CazarEnCadena",
                mspacman("intercepcionPosible true", "cadenaPosible true"),
                fantasma("BLINKY", "comestible true", "alcanzable true", "distancia 10", "tiempo 100"),
                fantasma("PINKY", "comestible true", "alcanzable true", "distancia 20", "tiempo 100"));

        caso(jess, "Comestible con amenaza: huir", "HuirDelFantasmaMasCercano",
                mspacman("cadenaPosible true"),
                fantasma("BLINKY", "comestible true", "alcanzable true", "distancia 10", "tiempo 100"),
                fantasma("INKY", "amenaza true", "distancia 8"));

        caso(jess, "Huir del mas cercano", "HuirDelFantasmaMasCercano",
                mspacman("pasillosConMismasPastillas false"),
                fantasma("INKY", "amenaza true", "distancia 8"));

        caso(jess, "Cerca sin amenaza: no huir", "PastillaMasCercana",
                mspacman(),
                fantasma("INKY", "distancia 5"));

        caso(jess, "Huir de varios", "HuirDeVariosFantasmas",
                mspacman(),
                fantasma("INKY", "amenaza true", "distancia 8"),
                fantasma("SUE", "amenaza true", "distancia 12"));

        caso(jess, "Correr a la pastilla de poder", "IrAPorPastillaDePoder",
                mspacman("llegaAntesAPastillaDePoder true"),
                fantasma("INKY", "amenaza true", "distancia 8"),
                fantasma("SUE", "amenaza true", "distancia 12"));

        caso(jess, "Pastilla unica salida", "HuirHaciaPastillaDePoder",
                mspacman("pastillaUnicaSalida true", "movimientosSeguros 1"),
                fantasma("INKY", "amenaza true", "distancia 8"));

        caso(jess, "Encerrada: huir hacia la pastilla", "HuirHaciaPastillaDePoder",
                mspacman("riesgoDeEncierro true", "llegaAntesAPastillaDePoder true", "movimientosSeguros 1"),
                fantasma("INKY", "amenaza true", "distancia 8"),
                fantasma("SUE", "amenaza true", "distancia 12"));

        caso(jess, "Muerte segura", "MuerteSegura",
                mspacman("riesgoDeEncierro true", "llegaAntesAPastillaDePoder true", "pastillaUnicaSalida true",
                        "movimientosSeguros 0"),
                fantasma("INKY", "amenaza true", "distancia 8"),
                fantasma("SUE", "amenaza true", "distancia 12"));
    }

    static String fantasma(String nombre, String... cambios) {
        Map<String, String> slots = new LinkedHashMap<>();
        slots.put("nombre", nombre);
        slots.put("comestible", "false");
        slots.put("distancia", "100");
        slots.put("tiempo", "0");
        slots.put("amenaza", "false");
        slots.put("alcanzable", "false");
        return hecho("FANTASMA", slots, cambios);
    }

    static String mspacman(String... cambios) {
        Map<String, String> slots = new LinkedHashMap<>();
        slots.put("pastillaDePoderComida", "false");
        slots.put("poderActivo", "false");
        slots.put("fantasmasComestiblesAlcanzables", "0");
        slots.put("enPeligro", "false");
        slots.put("enCruceDePastillaDePoder", "false");
        slots.put("hayCombo", "false");
        slots.put("pasillosConMismasPastillas", "true");
        slots.put("pasillosConPastillas", "20");
        slots.put("quedanPocosPasillos", "false");
        slots.put("cadenaPosible", "false");
        slots.put("intercepcionPosible", "false");
        slots.put("fantasmasCerca", "0");
        slots.put("riesgoDeEncierro", "false");
        slots.put("pastillaUnicaSalida", "false");
        slots.put("llegaAntesAPastillaDePoder", "false");
        slots.put("pastillaDePoderCerca", "false");
        slots.put("movimientosSeguros", "2");
        return hecho("MSPACMAN", slots, cambios);
    }

    static String hecho(String plantilla, Map<String, String> slots, String... cambios) {
        for (String cambio : cambios) {
            String[] partes = cambio.split(" ");
            if (!slots.containsKey(partes[0])) throw new IllegalArgumentException("Slot desconocido: " + partes[0]);
            slots.put(partes[0], partes[1]);
        }
        StringBuilder hecho = new StringBuilder("(" + plantilla);
        slots.forEach((slot, valor) -> hecho.append(" (").append(slot).append(' ').append(valor).append(')'));
        return hecho.append(')').toString();
    }

    static void caso(Rete jess, String nombre, String esperada, String... hechos) throws JessException {
        System.out.println("\n--- " + nombre);
        jess.reset();
        for (String h : hechos) jess.assertString(h);
        jess.run();

        Fact gana = null;                       // lo mismo que hace RuleEngine: la de más priority
        for (Iterator<?> it = jess.listFacts(); it.hasNext(); ) {
            Fact f = (Fact) it.next();
            if (f.getName().equals("MAIN::ACTION") && (gana == null
                    || f.getSlotValue("priority").intValue(null) > gana.getSlotValue("priority").intValue(null)))
                gana = f;
        }
        String id = gana == null ? "(ninguna)" : gana.getSlotValue("id").toString();
        boolean bien = id.equals(esperada);
        if (!bien) fallos++;
        System.out.printf("%s  gana %s, esperaba %s%n", bien ? "BIEN" : "MAL ", id, esperada);
    }
}
