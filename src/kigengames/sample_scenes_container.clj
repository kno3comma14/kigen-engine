(ns kigengames.sample-scenes-container
  (:require [kigengames.kigen-engine.scene :as scene] 
            [kigengames.kigen-engine.camera :as camera] 
            [kigengames.kigen-engine.rendering.texture :as texture]
            [kigengames.kigen-engine.geometry :as g])
  (:import (org.joml Vector2f Matrix4f)))

(def main-camera (atom (camera/->Camera 0
                                        "camera0"
                                        (g/->Transform (Vector2f. -100.0 -300.0) (Vector2f. 1.0 1.0))
                                        (fn [tr dt] 
                                          (let [pos (:position tr)]
                                            (g/->Transform (Vector2f. (- (.x pos) (* 50.0 dt)) (- (.y pos) (* 20.0 dt)))
                                                           (Vector2f. 1.0 1.0))))
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
                           main-camera))