(ns kigengames.kigen-engine.rendering.spritesheet
  (:require [kigengames.kigen-engine.rendering.sprite :as spr]
            [kigengames.kigen-engine.rendering.sprite-renderer :as sr])
  (:import (org.joml Vector2f)))

(defprotocol SpritesheetP
  (get-sprite [this index])
  (attach-to-sprite-renderer [this tansform index init-fn update-fn]))

(defrecord Spritesheet [texture sprite-list sprite-width sprite-height number-of-sprites spacing]
  SpritesheetP
  (get-sprite [_ index] 
    (nth sprite-list index))
  (attach-to-sprite-renderer [_ transform index init-fn update-fn]
    (let [target-sprite (get-sprite _ index)
          _ (println transform)
          result-renderer (sr/create nil target-sprite transform init-fn update-fn)]
      result-renderer)))

(defn create [texture sprite-width sprite-height spacing number-of-sprites] ;; Improvement needed
  (let [current-x (atom 0.0)
        current-y (atom (- (:height texture) sprite-height))
        sprite-list (reduce (fn [acc, _]
                              (let [top-y (/ (+ @current-y sprite-height) (float (:height texture)))
                                    right-x (/ (+ @current-x sprite-width) (float (:width texture)))
                                    left-x (/ @current-x (float (:width texture)))
                                    bottom-y (/ @current-y (float (:height texture)))
                                    text-coords [(Vector2f. right-x top-y) 
                                                 (Vector2f. right-x bottom-y) 
                                                 (Vector2f. left-x bottom-y) 
                                                 (Vector2f. left-x top-y)] 
                                    new-sprite (spr/create texture text-coords)] 
                                (reset! current-x (+ @current-x sprite-width spacing))
                                (when (>= @current-x (:width texture))
                                  (reset! current-x 0.0)
                                  (reset! current-y (- @current-y (+ sprite-height spacing))))
                                (conj acc new-sprite)))
                            []
                            (range number-of-sprites))] 
    (->Spritesheet texture sprite-list sprite-width sprite-height number-of-sprites spacing)))
  