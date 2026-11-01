;; ---- Plantillas -------------------------------------------------------
(deftemplate MSPACMAN
   (slot pastillaDePoderComida           (type SYMBOL))
   (slot poderActivo                     (type SYMBOL))
   (slot fantasmasComestiblesAlcanzables (type NUMBER))
   (slot enPeligro                       (type SYMBOL))
   (slot enCruceDePastillaDePoder        (type SYMBOL))
   (slot hayCombo                        (type SYMBOL))
   (slot pasillosConMismasPastillas      (type SYMBOL))
   (slot pasillosConPastillas            (type NUMBER))
   (slot quedanPocosPasillos             (type SYMBOL))
   (slot cadenaPosible                   (type SYMBOL))
   (slot intercepcionPosible             (type SYMBOL))
   (slot fantasmasCerca                  (type NUMBER))
   (slot riesgoDeEncierro                (type SYMBOL))
   (slot pastillaUnicaSalida             (type SYMBOL))
   (slot llegaAntesAPastillaDePoder      (type SYMBOL))
   (slot pastillaDePoderCerca            (type SYMBOL))
   (slot movimientosSeguros              (type NUMBER)))

(deftemplate FANTASMA
   (slot nombre     (type SYMBOL))
   (slot comestible (type SYMBOL))
   (slot distancia  (type NUMBER))
   (slot tiempo     (type NUMBER))
   (slot amenaza    (type SYMBOL))
   (slot alcanzable (type SYMBOL)))

(deftemplate ACTION
   (slot id) (slot info (default "")) (slot priority (type NUMBER))
   (slot fantasma (type SYMBOL)))

;; ---- Lo normal (0-19) -------------------------------------------------
(defrule pastillaMasCercana
   (MSPACMAN)
   =>
   (assert (ACTION (id PastillaMasCercana) (priority 10)
                   (info "por defecto: pastilla mas cercana"))))

;; ---- Oportunidades (20-59) --------------------------------------------
(defrule comerPasillo
   (MSPACMAN (pasillosConMismasPastillas false))
   =>
   (assert (ACTION (id ComerPasilloConMasPastillas) (priority 20)
                   (info "los pasillos tienen distinto numero de pastillas"))))

(defrule ultimasPastillas
   (MSPACMAN (quedanPocosPasillos true))
   =>
   (assert (ACTION (id IrAPorLasUltimasPastillas) (priority 30)
                   (info "quedan pocos pasillos con pastillas"))))

(defrule esperarCombo
   (MSPACMAN (pastillaDePoderCerca true) (hayCombo false) (fantasmasCerca 0))
   =>
   (assert (ACTION (id EsperarPastillaDePoderHastaCombo) (priority 50)
                   (info "pastilla de poder cerca, esperar a que haya combo"))))

;; ---- Ataque (60-79) ---------------------------------------------------
(defrule perseguirComestible
   (MSPACMAN (fantasmasComestiblesAlcanzables ?n&:(> ?n 0)) (fantasmasCerca 0))
   =>
   (assert (ACTION (id PerseguirFantasmaComestibleMasCercano) (priority 60)
                   (info "hay comestibles a los que llego"))))

(defrule interceptar
   (MSPACMAN (intercepcionPosible true) (fantasmasCerca 0))
   =>
   (assert (ACTION (id InterceptarFantasmaComestible) (priority 62)
                   (info "llego al cruce de un comestible"))))

(defrule cazarEnCadena
   (MSPACMAN (cadenaPosible true) (fantasmasCerca 0))
   =>
   (assert (ACTION (id CazarEnCadena) (priority 65)
                   (info "cadena de comestibles posible"))))

(defrule comboListo
   (MSPACMAN (hayCombo true) (fantasmasCerca 0))
   =>
   (assert (ACTION (id IrAPorPastillaDePoder) (priority 70)
                   (info "hay combo: comerse la pastilla"))))

;; ---- Supervivencia (80-99) --------------------------------------------
(defrule huirDelMasCercano
   (MSPACMAN (fantasmasCerca 1))
   =>
   (assert (ACTION (id HuirDelFantasmaMasCercano) (priority 80)
                   (info "un fantasma peligroso cerca"))))

(defrule huirDeVarios
   (MSPACMAN (fantasmasCerca ?n&:(> ?n 1)))
   =>
   (assert (ACTION (id HuirDeVariosFantasmas) (priority 82)
                   (info "varios fantasmas peligrosos cerca"))))

(defrule correrALaPastilla
   (MSPACMAN (fantasmasCerca ?n&:(> ?n 0)) (llegaAntesAPastillaDePoder true))
   =>
   (assert (ACTION (id IrAPorPastillaDePoder) (priority 85)
                   (info "peligro y llego antes a la pastilla de poder"))))

(defrule huirHaciaPastilla
   (MSPACMAN (riesgoDeEncierro true) (pastillaDePoderCerca true))
   =>
   (assert (ACTION (id HuirHaciaPastillaDePoder) (priority 88)
                   (info "riesgo de encierro con pastilla de poder cerca"))))

(defrule muerteSegura
   (MSPACMAN (movimientosSeguros 0))
   =>
   (assert (ACTION (id MuerteSegura) (priority 95)
                   (info "no queda ningun movimiento seguro"))))