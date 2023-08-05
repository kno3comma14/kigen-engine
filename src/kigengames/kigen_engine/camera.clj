(ns kigengames.kigen-engine.camera
  (:import (org.joml  Vector3f)))

(defprotocol CameraP
  (init [this])
  (update-position [this actual-position dt])
  (get-view-matrix [this])
  (get-projection-matrix [this]))

(defrecord Camera [id name position update-position-fn projection-matrix view-matrix]
  CameraP
  (init [_]
    (.identity projection-matrix)
    (.ortho projection-matrix 0.0 (* 32.0 40.0) 0.0 (* 32.0 21.0) 0.0 100.0))

  (update-position [_ actual-position dt]
    (->Camera id name (update-position-fn actual-position dt) update-position-fn  projection-matrix view-matrix))

  (get-view-matrix [_]
    (let [front-camera (Vector3f. 0.0 0.0 -1.0)
          upper-camera (Vector3f. 0.0 1.0 0.0)
          _ (.identity view-matrix)
          _ (.lookAt view-matrix 
                     (Vector3f. (.x position) (.y position) 20.0)
                     (.add front-camera (.x position) (.y position) 0.0)
                     upper-camera)]
      view-matrix))

  (get-projection-matrix [_]
    projection-matrix))


