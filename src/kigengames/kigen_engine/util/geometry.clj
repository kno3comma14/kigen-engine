(ns kigengames.kigen-engine.util.geometry
  (:require [kigengames.kigen-engine.data.validation :as v]))

(defprotocol TransformP
  (validate [this]) 
  (translate [this new-position]) 
  (change-scale [this new-scale])
  (equals? [this other]))

(defrecord Transform [position scale]
  TransformP
  (validate [this]
    (if (not (v/validate-transform this))
      (throw (Exception. "Transform position and scale can only contain org.joml.Vector2f values"))
      this))
  (translate [this new-position]
    (validate (assoc this :position new-position))) 
  (change-scale [this new-scale]
    (validate (assoc this :scale new-scale)))
  (equals? [_ other]
    (and (= position (:position other))
         (= scale (:scale other)))))

(defn create-transform
  [position scale]
  (->Transform position scale)) ;; Improvement needed