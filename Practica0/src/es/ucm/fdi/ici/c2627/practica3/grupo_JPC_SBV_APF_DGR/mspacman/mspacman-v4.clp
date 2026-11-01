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

(deftemplate PELIGRO)
(deftemplate VARIOSPELIGROS)

(deftemplate ACTION
   (slot id) (slot info (default "")) (slot priority (type NUMBER))
   (slot fantasma (type SYMBOL)))

;; ---- Hechos intermedios ------------------------------------------------
(defrule peligro
   (declare (salience 10))
   (FANTASMA (comestible false) (amenaza true))
   =>
   (assert (PELIGRO)))

(defrule variosPeligros
   (declare (salience 10))
   (FANTASMA (nombre ?a) (comestible false) (amenaza true))
   (FANTASMA (nombre ?b&~?a) (comestible false) (amenaza true))
   =>
   (assert (VARIOSPELIGROS)))

;; ---- Lo normal (0-19) -------------------------------------------------
(defrule pastillaMasCercana
   (MSPACMAN)
   =>
   (assert (ACTION (id PastillaMasCercana) (priority 10) (info "por defecto"))))

;; ---- Oportunidades (20-59) --------------------------------------------
(defrule comerPasillo
   (MSPACMAN (pasillosConMismasPastillas false))
   =>
   (assert (ACTION (id ComerPasilloConMasPastillas) (priority 20)
                   (info "pasillos con distinto numero de pastillas"))))

(defrule ultimasPastillas
   (MSPACMAN (quedanPocosPasillos true))
   =>
   (assert (ACTION (id IrAPorLasUltimasPastillas) (priority 30)
                   (info "quedan pocos pasillos con pastillas"))))

(defrule esperarCombo
   (not (PELIGRO))
   (MSPACMAN (enCruceDePastillaDePoder true) (hayCombo false))
   =>
   (assert (ACTION (id EsperarPastillaDePoderHastaCombo) (priority 50)
                   (info "en el cruce de la pastilla de poder, esperar al combo"))))

;; ---- Ataque (60-79) ---------------------------------------------------
(defrule perseguirComestible
   (not (PELIGRO))
   (FANTASMA (comestible true) (alcanzable true))
   =>
   (assert (ACTION (id PerseguirFantasmaComestibleMasCercano) (priority 60)
                   (info "comestible al que llego"))))

(defrule interceptar
   (not (PELIGRO))
   (MSPACMAN (intercepcionPosible true))
   =>
   (assert (ACTION (id InterceptarFantasmaComestible) (priority 62)
                   (info "llego al cruce de un comestible"))))

(defrule cazarEnCadena
   (not (PELIGRO))
   (MSPACMAN (cadenaPosible true))
   =>
   (assert (ACTION (id CazarEnCadena) (priority 65)
                   (info "cadena de comestibles posible"))))

;; ---- Supervivencia (80-99) --------------------------------------------
(defrule huirDelMasCercano
   (PELIGRO)
   (not (VARIOSPELIGROS))
   =>
   (assert (ACTION (id HuirDelFantasmaMasCercano) (priority 80)
                   (info "un fantasma peligroso cerca"))))

(defrule huirDeVarios
   (VARIOSPELIGROS)
   =>
   (assert (ACTION (id HuirDeVariosFantasmas) (priority 82)
                   (info "varios fantasmas peligrosos cerca"))))

(defrule correrALaPastilla
   (PELIGRO)
   (MSPACMAN (llegaAntesAPastillaDePoder true))
   =>
   (assert (ACTION (id IrAPorPastillaDePoder) (priority 85)
                   (info "peligro y llego antes a la pastilla de poder"))))

(defrule pastillaUnicaSalida
   (PELIGRO)
   (MSPACMAN (pastillaUnicaSalida true))
   =>
   (assert (ACTION (id HuirHaciaPastillaDePoder) (priority 88)
                   (info "el camino de la pastilla de poder es la unica salida segura"))))

(defrule encerrada
   (VARIOSPELIGROS)
   (MSPACMAN (riesgoDeEncierro true) (llegaAntesAPastillaDePoder true))
   =>
   (assert (ACTION (id HuirHaciaPastillaDePoder) (priority 89)
                   (info "varios fantasmas, riesgo de encierro y llego antes a la pastilla"))))

(defrule muerteSegura
   (MSPACMAN (movimientosSeguros 0))
   =>
   (assert (ACTION (id MuerteSegura) (priority 95)
                   (info "ningun movimiento seguro"))))
