(ns kigen-engine-samples.basic-samples
  (:require [kigengames.kigen-engine.camera :as camera]
            [kigengames.kigen-engine.geometry :as g]
            [kigengames.kigen-engine.rendering.renderer :as renderer]
            [kigengames.kigen-engine.rendering.sprite :as sprite]
            [kigengames.kigen-engine.rendering.sprite-renderer :as sr]
            [kigengames.kigen-engine.rendering.texture :as texture]
            [kigengames.kigen-engine.scene :as scene]
            [kigengames.kigen-engine.rendering.spritesheet :as ss])
  (:import (org.joml Matrix4f Vector2f Vector4f)))

(def main-camera (atom (camera/->Camera "camera0"
                                        (g/->Transform (Vector2f. -250.0 0.0) (Vector2f. 1.0 1.0))
                                        (fn [tr _dt]
                                          tr)
                                        (Matrix4f.)
                                        (Matrix4f.))))

(def renderer0 (renderer/create))
(def sr2 (atom nil))

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
                                             r (:renderer this)]

                                         (reset! sr2 (sr/create (Vector4f. (/ x-pos total-width)
                                                                           (/ y-pos total-height)
                                                                           1.0
                                                                           1.0)
                                                                nil
                                                                (g/create-transform (Vector2f. x-pos y-pos) (Vector2f. size-x size-y))
                                                                (fn [_])
                                                                (fn [sr dt]
                                                                  (let [new-sr (update-in sr
                                                                                          [:transform]
                                                                                          (fn [t] (.translate t (Vector2f. (- (.x (:position t)) (* 10.0 dt)) 100.0))))]
                                                                    new-sr))))
                                         (.add-drawable r @sr2))
                                       (recur (inc y))))
                                   (recur (inc x))))))
                           (fn [this dt]
                             (let [r (:renderer this)]
                               (.render r dt)))
                           main-camera
                           renderer0))


(def sr0 (atom nil))
(def sr1 (atom nil))
(def sr3 (atom nil))
(def sr4 (atom nil))


(def scene1 (scene/->Scene 1
                           "bla1"
                           (fn [this]
                             (let [r (:renderer this)
                                   texture0 (texture/create "textures/img0.png" (fn [_]) (fn [_]))
                                   texture1 (texture/create "textures/img1.png" (fn [_]) (fn [_]))]
                               (reset! sr0 (sr/create nil
                                                      (sprite/create texture0)
                                                      (g/create-transform (Vector2f. 100.0 100.0) (Vector2f. 256.0 256.0))
                                                      (fn [_])
                                                      (fn [sr dt]
                                                        (let [new-sr (update-in sr
                                                                                [:transform]
                                                                                (fn [t] (.translate t (Vector2f. (- (.x (:position t)) (* 10.0 dt)) 100.0))))]
                                                          new-sr))))
                               (reset! sr1 (sr/create nil
                                                      (sprite/create texture1)
                                                      (g/create-transform (Vector2f. 400.0 100.0) (Vector2f. 256.0 256.0))
                                                      (fn [_])
                                                      (fn [sr dt]
                                                        (let [new-sr (update-in sr
                                                                                [:transform]
                                                                                (fn [t] (.translate t (Vector2f. (+ (.x (:position t)) (* 10.0 dt)) 100.0))))]
                                                          new-sr))))
                               (.add-drawable r @sr0)
                               (.add-drawable r @sr1)))
                           (fn [this dt]
                             (let [r (:renderer this)]
                               (.render r dt)))
                           main-camera
                           renderer0))

(def scene2 (scene/->Scene 2
                           "bla2"
                           (fn [this]
                             (let [r (:renderer this)
                                   test-texture (texture/create "textures/kigen-basic-sprites.png" (fn [_]) (fn [_]))
                                   test-spritesheet (ss/create (:instance test-texture) 32 32 0 40)
                                   aux-transform (g/create-transform (Vector2f. 100.0 100.0) (Vector2f. 256.0 256.0))
                                   aux-transform2 (g/create-transform (Vector2f. 120.0 100.0) (Vector2f. 256.0 256.0))
                                   aux-init-fn (fn [_])
                                   aux-update-fn (fn [sr dt]
                                                   (let [new-sr (update-in sr
                                                                           [:transform]
                                                                           (fn [t] (.translate t (Vector2f. (+ (.x (:position t)) (* 10.0 dt)) 100.0))))] 
                                                     new-sr))]
                               
                               (reset! sr3 (.attach-to-sprite-renderer test-spritesheet aux-transform 0 aux-init-fn (fn [sr _dt] sr)))
                               (reset! sr4 (.attach-to-sprite-renderer test-spritesheet aux-transform2 1 aux-init-fn aux-update-fn))
                               (.add-drawable r @sr3)
                               (.add-drawable r @sr4))) 
                           (fn [this dt]
                             (let [r (:renderer this)]
                               (.render r dt)))
                           main-camera
                           renderer0))