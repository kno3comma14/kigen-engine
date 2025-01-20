(ns kigengames.kigen-engine.rendering.camera
  (:require [kigengames.kigen-engine.data.component :as component])
  (:import (org.joml  Vector3f)))

(defprotocol CameraP
  (init [this])
  (update-position [this new-transform dt])
  (get-view-matrix [this])
  (get-projection-matrix [this]))

(defrecord Camera [name transform update-fn projection-matrix view-matrix]
  CameraP
  (init [_]
    (.identity projection-matrix)
    (.ortho projection-matrix 0.0 (* 32.0 40.0) 0.0 (* 32.0 21.0) 0.0 100.0))

  (update-position [_ new-transform dt]
    (->Camera name (update-fn new-transform dt) update-fn  projection-matrix view-matrix))

  (get-view-matrix [_]
    (let [front-camera-position (Vector3f. 0.0 0.0 -1.0)
          upper-camera-position (Vector3f. 0.0 1.0 0.0)
          position (:position transform)
          _ (.identity view-matrix)
          _ (.lookAt view-matrix 
                     (Vector3f. (.x position) (.y position) 20.0)
                     (.add front-camera-position (.x position) (.y position) 0.0)
                     upper-camera-position)]
      view-matrix))

  (get-projection-matrix [_]
    projection-matrix))

(defn create 
  [name transform update-fn projection-matrix view-matrix]
  (let [cam-instance (->Camera name transform update-fn projection-matrix view-matrix)]
    (component/create cam-instance (fn [_this] (.init cam-instance)) update-fn)))


