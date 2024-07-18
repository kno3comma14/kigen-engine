(ns kigengames.kigen-engine.rendering.sprite-renderer
  (:require [kigengames.kigen-engine.data.component :as c])
  (:import (org.joml Vector4f Vector2f)))

(defprotocol SpriteRendererP
  (get-texture-coords [this])
  (process [this sr dt]))

(defrecord SpriteRenderer [color texture-coords texture transform init-fn update-fn is-dirty?]
  SpriteRendererP
  (get-texture-coords [_] ;; I will remove this
    (let [t-coords [(Vector2f. 1.0 1.0)
                    (Vector2f. 1.0 0.0)
                    (Vector2f. 0.0 0.0)
                    (Vector2f. 0.0 1.0)]]
      t-coords))
  (process [_ sr dt]
    (let [new-spr (update-fn sr dt)
          changed? (or (not= (:color new-spr) color) (not (.equals? (:transform new-spr) transform)))]
      (update-fn (update new-spr :is-dirty? (fn [_] changed?)) dt))))

(defn create
  ([color transform init-fn update-fn]
   (let [sprite-renderer-instance (->SpriteRenderer color nil nil transform init-fn update-fn false)]
     (c/create sprite-renderer-instance init-fn update-fn)))
  ([texture-coords texture transform init-fn update-fn]
   (let [sprite-renderer-instance (->SpriteRenderer (Vector4f. 1.0 1.0 1.0 1.0) 
                                                    texture-coords 
                                                    (:instance texture)
                                                    transform 
                                                    init-fn 
                                                    update-fn
                                                    false)]
     (c/create sprite-renderer-instance init-fn update-fn))))