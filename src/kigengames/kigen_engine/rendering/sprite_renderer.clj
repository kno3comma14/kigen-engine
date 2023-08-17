(ns kigengames.kigen-engine.rendering.sprite-renderer
  (:require [kigengames.kigen-engine.data.component :as c])
  (:import (org.joml Vector4f Vector2f)))

(defprotocol SpriteRendererP
  (get-texture-coords [this]))

(defrecord SpriteRenderer [color sprite transform init-fn update-fn]
  SpriteRendererP
  (get-texture-coords [_] ;; I will remove this
    (let [t-coords [(Vector2f. 1.0 1.0)
                    (Vector2f. 1.0 0.0)
                    (Vector2f. 0.0 0.0)
                    (Vector2f. 0.0 1.0)]]
      t-coords)))

(defn create
  ([color transform init-fn update-fn]
   (let [sprite-renderer-instance (->SpriteRenderer color nil transform init-fn update-fn)]
     (c/create sprite-renderer-instance init-fn update-fn)))
  ([sprite transform]
   (let [init-fn (fn [_])
         update-fn (fn [_])
         sprite-renderer-instance (->SpriteRenderer (Vector4f. 1.0 1.0 1.0 1.0)  
                                                    sprite
                                                    transform 
                                                    init-fn 
                                                    update-fn)]
     (c/create sprite-renderer-instance init-fn update-fn))))