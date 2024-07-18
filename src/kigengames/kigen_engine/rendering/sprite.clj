(ns kigengames.kigen-engine.rendering.sprite 
  (:import (org.joml Vector2f)))

(defprotocol SpriteP)

(defrecord Sprite [texture texture-coords]
  SpriteP)

(defn create
  ([texture]
  (->Sprite texture [(Vector2f. 1.0 1.0) (Vector2f. 1.0 0.0) (Vector2f. 0.0 0.0) (Vector2f. 0.0 1.0)]))
  ([texture texture-coords]
  (->Sprite texture texture-coords)))