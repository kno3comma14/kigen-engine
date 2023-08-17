(ns kigengames.kigen-engine.rendering.sprite 
  (:import (org.joml Vector2f)))

(defprotocol SpriteP)

(defrecord Sprite [texture coordinates]
  SpriteP)

(defn ->Sprite
  ([texture]
   (let [instance (->Sprite texture [(Vector2f. 1.0 1.0)
                                     (Vector2f. 1.0 0.0)
                                     (Vector2f. 0.0 0.0)
                                     (Vector2f. 0.0 1.0)])]
     instance))
  ([texture coordinates]
   (let [instance (->Sprite texture coordinates)]
     instance)))