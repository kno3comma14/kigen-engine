(ns kigengames.sample-scenes-container
  (:require [kigengames.kigen-engine.scene :as scene] 
            [kigengames.kigen-engine.camera :as camera] 
            [kigengames.kigen-engine.rendering.texture :as texture]) 
  (:import (org.joml Vector2f Matrix4f)))

(def main-camera (atom (camera/->Camera 0
                                        "camera0"
                                        (Vector2f. -100.0 -300.0)
                                        (fn [pos dt]
                                          (Vector2f. (- (.x pos) (* 50.0 dt)) (- (.y pos) (* 20.0 dt))))
                                        (Matrix4f.)
                                        (Matrix4f.))))

(def test-texture (atom nil))

(defn load-textures []
  (reset! test-texture (texture/create-texture "textures/Kulsa_V2.png")))

(def scene0 (scene/->Scene 0
                           "bla0"
                           (fn [])
                           load-textures
                           (fn [_dt])
                           main-camera
                           {}))