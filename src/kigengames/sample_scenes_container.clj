(ns kigengames.sample-scenes-container
  (:require [kigengames.kigen-engine.scene :as scene] 
            [kigengames.kigen-engine.camera :as camera] 
            [kigengames.kigen-engine.geometry :as g]
            [kigengames.kigen-engine.rendering.renderer :as renderer]
            [kigengames.kigen-engine.rendering.sprite-renderer :as sr]
            [kigengames.kigen-engine.rendering.texture :as texture])
  (:import (org.joml Vector2f Matrix4f Vector4f)))

(def main-camera (atom (camera/->Camera "camera0"
                                        (g/->Transform (Vector2f. -250.0 0.0) (Vector2f. 1.0 1.0))
                                        (fn [tr _dt]
                                          tr)
                                        (Matrix4f.)
                                        (Matrix4f.))))

(def renderer0 (renderer/create))

(def scene0 (scene/->Scene 0
                           "bla0"
                           (fn [this]
                             (let [x-offset 10.0
                                   y-offset 10.0
                                   total-width (float (- 600.0 (* 2.0 x-offset))) 
                                   total-height (float (- 300.0 (* 2.0 y-offset))) 
                                   size-x (/ total-width 100.0) 
                                   size-y (/ total-height 100.0)
                                   padding 0.0]
                               (loop [x 0]
                                 (when (< x 100)
                                   (loop [y 0]
                                     (when (< y 100)
                                       (let [x-pos (+ x-offset (* x size-x) (* padding x))
                                             y-pos (+ y-offset (* y size-y) (* padding y))
                                             r (:renderer this) 
                                             sr1 (sr/create (Vector4f. (/ x-pos total-width)
                                                                       (/ y-pos total-height)
                                                                       1.0
                                                                       1.0)
                                                            (g/create-transform (Vector2f. x-pos y-pos) (Vector2f. size-x size-y))
                                                            (fn [_])
                                                            (fn [_]))] 
                                         (.add-drawable r (:instance sr1))) 
                                       (recur (inc y)))) 
                                   (recur (inc x))))))
                           (fn [this dt]
                             (prn (str "Frames per second(FPS): " (/ 1.0 dt)))
                             (let [r (:renderer this)] 
                               (.render r)))
                           main-camera
                           renderer0))

(def scene1 (scene/->Scene 1
                           "bla1"
                           (fn [this]
                             (let [r (:renderer this)
                                   sr0 (sr/create [(Vector2f. 1.0 1.0)
                                                   (Vector2f. 1.0 0.0)
                                                   (Vector2f. 0.0 1.0)
                                                   (Vector2f. 0.0 0.0)]
                                                  (texture/create "textures/img0.png" (fn [_]) (fn [_]))
                                                  (g/create-transform (Vector2f. 100.0 100.0) (Vector2f. 256.0 256.0))
                                                  (fn [_])
                                                  (fn [_]))
                                   sr1 (sr/create [(Vector2f. 1.0 1.0)
                                                   (Vector2f. 1.0 0.0)
                                                   (Vector2f. 0.0 1.0)
                                                   (Vector2f. 0.0 0.0)]
                                                  (texture/create "textures/img1.png" (fn [_]) (fn [_]))
                                                  (g/create-transform (Vector2f. 400.0 100.0) (Vector2f. 256.0 256.0))
                                                  (fn [_])
                                                  (fn [_]))]
                               (.add-drawable r (:instance sr1))
                               (.add-drawable r (:instance sr0))
                               ))
                           (fn [this dt] 
                             (let [r (:renderer this)]
                               (.render r)))
                           main-camera
                           renderer0))