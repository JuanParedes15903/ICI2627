;;; Los cuatro fantasmas con la misma base de reglas: v2, con G3, roles, cárcel y limpieza de prioridades

(deftemplate FANTASMA
   (slot nombre         (type SYMBOL))
   (slot comestible     (type SYMBOL))
   (slot tiempoComestible (type NUMBER)) 
   (slot enCarcel       (type SYMBOL))
   (slot distPacman     (type NUMBER))
   (slot distMejorPP    (type NUMBER)))  

(deftemplate MSPACMAN
   (slot distPPill      (type NUMBER))
   (slot numPP          (type NUMBER))   
   (slot numPills       (type NUMBER)))  

;; ---- Y uno más, distinto en cada motor: quién soy yo --------------------
(deftemplate YO (slot nombre (type SYMBOL)))

(deftemplate ROL (slot nombre (type SYMBOL)) (slot rol (type SYMBOL)))

(deftemplate ACTION
   (slot id) 
   (slot info (default "")) 
   (slot priority (type NUMBER)))

;; =========================================================================
;; ---- ASIGNACIÓN DE ROLES (Nativo Jess) ----------------------------------
;; =========================================================================

;; 0 fantasmas más cercanos -> Acosador
(defrule rolAcosador
   (YO (nombre ?yo))
   (FANTASMA (nombre ?yo) (comestible false) (enCarcel false) (distPacman ?d))
   (not (FANTASMA (nombre ?otro&~?yo) (comestible false) (enCarcel false) (distPacman ?d2&:(< ?d2 ?d))))
   =>
   (assert (ROL (nombre ?yo) (rol acosador)))
)

;; Exactamente 1 fantasma más cercano -> Bloqueador
(defrule rolBloqueador
   (YO (nombre ?yo))
   (FANTASMA (nombre ?yo) (comestible false) (enCarcel false) (distPacman ?d))
   
   ;; Existe 1 fantasma distinto más cercano
   (FANTASMA (nombre ?g1&~?yo) (comestible false) (enCarcel false) (distPacman ?d1&:(< ?d1 ?d)))
   
   ;; PERO NO existe un segundo fantasma distinto más cercano
   (not (FANTASMA (nombre ?g2&~?yo&~?g1) (comestible false) (enCarcel false) (distPacman ?d2&:(< ?d2 ?d))))
   =>
   (assert (ROL (nombre ?yo) (rol bloqueador)))
)

;; Exactamente 2 fantasmas más cercanos -> Emboscador
(defrule rolEmboscador
   (YO (nombre ?yo))
   (FANTASMA (nombre ?yo) (comestible false) (enCarcel false) (distPacman ?d))
   
   ;; Existen 2 fantasmas distintos más cercanos
   (FANTASMA (nombre ?g1&~?yo) (comestible false) (enCarcel false) (distPacman ?d1&:(< ?d1 ?d)))
   (FANTASMA (nombre ?g2&~?yo&~?g1) (comestible false) (enCarcel false) (distPacman ?d2&:(< ?d2 ?d)))
   
   ;; PERO NO existe un tercer fantasma distinto más cercano
   (not (FANTASMA (nombre ?g3&~?yo&~?g1&~?g2) (comestible false) (enCarcel false) (distPacman ?d3&:(< ?d3 ?d))))
   =>
   (assert (ROL (nombre ?yo) (rol emboscador)))
)

;; 3 fantasmas más cercanos -> Dispersor
(defrule rolDispersor
   (YO (nombre ?yo))
   (FANTASMA (nombre ?yo) (comestible false) (enCarcel false) (distPacman ?d))
   
   ;; Existen 3 fantasmas distintos más cercanos
   (FANTASMA (nombre ?g1&~?yo) (comestible false) (enCarcel false) (distPacman ?d1&:(< ?d1 ?d)))
   (FANTASMA (nombre ?g2&~?yo&~?g1) (comestible false) (enCarcel false) (distPacman ?d2&:(< ?d2 ?d)))
   (FANTASMA (nombre ?g3&~?yo&~?g1&~?g2) (comestible false) (enCarcel false) (distPacman ?d3&:(< ?d3 ?d)))
   =>
   (assert (ROL (nombre ?yo) (rol dispersor)))
)

;; =========================================================================
;; ---- ACCIONES SEGÚN EL ROL (Sustituyen a G1) ----------------------------
;; =========================================================================

(defrule accionAcosador
   (YO (nombre ?yo))
   (ROL (nombre ?yo) (rol acosador))
   =>
   (assert (ACTION (id perseguir) (priority 30) (info "R1: acosador, voy a por ella")))
)

(defrule accionBloqueador
   (YO (nombre ?yo))
   (ROL (nombre ?yo) (rol bloqueador))
   =>
   (assert (ACTION (id interceptar) (priority 20) (info "R2: bloqueador, le corto el paso")))
)

(defrule accionDispersor
   (YO (nombre ?yo))
   (ROL (nombre ?yo) (rol dispersor))
   =>
   (assert (ACTION (id patrullar) (priority 10) (info "R3: dispersor, cubro otra zona")))
)

(defrule accionEmboscador
   (YO (nombre ?yo))
   (ROL (nombre ?yo) (rol emboscador))
   =>
   (assert (ACTION (id emboscar) (priority 15) (info "R4: emboscador, intercepto 2 nodos por delante de su direccion actual")))
)

;; R5 · Cortar el camino a la PP: el no-comestible más cercano a la PP se interpone
(defrule cortarCaminoPP
   (YO (nombre ?yo))
   (MSPACMAN (numPP ?n&:(> ?n 0)) (distPPill ?dp&:(< ?dp 240)))
   (FANTASMA (nombre ?yo) (comestible false) (enCarcel false)
             (distMejorPP ?dg&:(< ?dg ?dp)))
   ;; solo el más cercano a la PP (el resto no hace nada especial)
   (not (FANTASMA (nombre ?otro&~?yo) (comestible false) (enCarcel false)
                  (distMejorPP ?do&:(< ?do ?dg))))
   =>
   (assert (ACTION (id cortar_pp) (priority 38)
                   (info "R5: me interpongo entre Ms. Pac-Man y la PP"))))
                   
;; R6 · Cerco: hay al menos otro fantasma libre cerca de Ms. Pac-Man
(defrule cerrarCerco
   (YO (nombre ?yo))
   (FANTASMA (nombre ?yo) (comestible false) (enCarcel false)
             (distPacman ?d&:(< ?d 200)))
   (exists (FANTASMA (nombre ?otro&~?yo) (comestible false) (enCarcel false)
                     (distPacman ?d2&:(< ?d2 200))))
   =>
   (assert (ACTION (id cerco) (priority 25)
                   (info "R6: cierro una salida de Ms. Pac-Man"))))
;; =========================================================================
;; ---- SUPERVIVENCIA Y ESTADOS CRÍTICOS (Mandan sobre el rol) -------------
;; =========================================================================

;; G4 · Si estoy en la cárcel, mi única acción válida es salir (Prioridad 100)
(defrule accionCarcel
   (YO (nombre ?yo))
   (FANTASMA (nombre ?yo) (enCarcel true))
   =>
   (assert (ACTION (id salir_carcel) (priority 100) (info "G4: Estoy en la carcel, intentando salir")))
)

;; ---- G2: Supervivencia de fantasmas comestibles -------------------------

;; G2a · Peligro inminente: la prioridad absoluta es alejarse de Ms. Pac-Man
(defrule huirComestiblePanico
   (YO (nombre ?yo))
   (FANTASMA (nombre ?yo) (comestible true) (distPacman ?d&:(< ?d 40))) ; 40 es un umbral de ejemplo
   =>
   (assert (ACTION (id huir) (info "G2a: comestible y muy cerca, huir directo") (priority 50)))
)

;; G2b · Distancia de seguridad: huir intentando no agruparse con otros fantasmas
(defrule huirComestibleDisperso
   (YO (nombre ?yo))
   (FANTASMA (nombre ?yo) (comestible true) (distPacman ?d&:(>= ?d 40)))
   =>
   (assert (ACTION (id huir_disperso) (info "G2b: comestible, huir separandose del grupo") (priority 50)))
)

;; G2c · Deja de huir si está lo suficientemente lejos de Ms. Pac-man
(defrule comestibleSeguro
   (YO (nombre ?yo))
   (FANTASMA (nombre ?yo) (comestible true)
             (tiempoComestible ?t) (distPacman ?d&:(> ?d (+ ?t 10))))
   =>
   (assert (ACTION (id perseguir) (priority 55) (info "G2c: seguro hasta que acabe el efecto"))))

;; G3 · si Ms. Pac-Man está junto a una píldora de poder, no acercarse (Prioridad 40)
;; Solo aplica si estoy libre y no soy comestible
(defrule huirDeLaPPill
   (YO (nombre ?yo))
   (FANTASMA (nombre ?yo) (enCarcel false) (comestible false))
   (MSPACMAN (distPPill ?d&:(< ?d 15)))
   =>
   (assert (ACTION (id huir) (priority 40) (info "G3: Ms. Pac-Man junto a una PPill")))
)

;; Si quedan pocas pildoras y no quedan pildoras de poder van todos a por Ms. PacMan
(defrule finalSinPP
   (YO (nombre ?yo))
   (MSPACMAN (numPills ?n&:(< ?n 20)) (numPP 0))
   (FANTASMA (nombre ?yo) (comestible false) (enCarcel false))
   =>
   (assert (ACTION (id perseguir) (priority 35) (info "Fase final: presión total"))))
;; =========================================================================
;; ---- LIMPIEZA DE ACCIONES: Solo sobrevive la de mayor prioridad ---------
;; =========================================================================
(defrule seleccionar-mejor-accion
   (declare (salience -10)) ; Se ejecuta al final, cuando ya se han propuesto todas las acciones
   ?accionMenor <- (ACTION (priority ?pMenor))
   (ACTION (priority ?pMayor&:(> ?pMayor ?pMenor)))
   =>
   (retract ?accionMenor)
)