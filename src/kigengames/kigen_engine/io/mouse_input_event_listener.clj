(ns kigengames.kigen-engine.io.mouse-input-event-listener
  (:import (org.lwjgl.glfw GLFW
                           GLFWCursorPosCallbackI
                           GLFWScrollCallbackI
                           GLFWMouseButtonCallbackI)))

(defonce mouse-listener (atom nil))

(defn init
  []
  (reset! mouse-listener {:scroll-x 0.0
                          :scroll-y 0.0
                          :x-pos 0.0
                          :y-pos 0.0
                          :last-x 0.0
                          :last-y 0.0
                          :mouse-button-pressed (vec (boolean-array 3)) ;; Consider dynamic quantity of buttons
                          :is-dragging false}))

(defn provide-listener
  []
  (if (nil? @mouse-listener)
    (init)
    @mouse-listener))

#_{:clj-kondo/ignore [:unused-binding]}
(def mouse-position-callback
  (reify GLFWCursorPosCallbackI
    (invoke [this window x-pos y-pos]
      (let [ml (provide-listener)
            updated-map {:last-x (:last-x ml)
                         :last-y (:last-y ml)
                         :x-pos x-pos
                         :y-pos y-pos
                         :is-dragging (not-every? false? (:mouse-button-pressed ml))}]
        (reset! mouse-listener (merge ml updated-map))))))

#_{:clj-kondo/ignore [:unused-binding]}
(def mouse-scroll-callback
  (reify GLFWScrollCallbackI
    (invoke [this window x-offset y-offset]
      (let [ml (provide-listener)
            updated-map {:scroll-x x-offset
                         :scroll-y y-offset}]
        (reset! mouse-listener (merge ml updated-map))))))

#_{:clj-kondo/ignore [:unused-binding]}
(def mouse-button-callback
  (reify GLFWMouseButtonCallbackI
    (invoke [this window button action mods]
      (let [ml (provide-listener)
            available-buttons (count (:mouse-button-pressed ml))]
        (cond
          (= action GLFW/GLFW_PRESS) (when (< button available-buttons)
                                       (reset! mouse-listener (update-in ml [:mouse-button-pressed button] (fn [_] true))))
          (= action GLFW/GLFW_RELEASE) (when (< button available-buttons)
                                         (reset! mouse-listener (update-in ml [:mouse-button-pressed button] (fn [_] false)))
                                         (reset! mouse-listener (update (provide-listener) :is-dragging (fn [_] false)))))))))

(defn end-frame
  []
  (let [ml (provide-listener)
        updated-map {:scroll-x 0
                     :scroll-y 0
                     :last-x (:last-x ml)
                     :last-y (:last-y ml)}]
    (reset! mouse-listener (merge ml updated-map))))

(defn get-position
  []
  (let [ml (provide-listener)
        x (:x-pos ml)
        y (:y-pos ml)]
    {:x x :y y}))

(defn get-differential-position
  []
  (let [ml (provide-listener)
        dx (- (:last-x ml) (:x-pos ml))
        dy (- (:last-y ml) (:y-pos ml))]
    {:dx dx :dy dy}))

(defn get-scroll-x ;; Probably will be removed
  []
  (let [ml (provide-listener)
        scroll-x (:scroll-x ml)]
    scroll-x))

(defn dragging?
  []
  (let [ml (provide-listener)]
    (:is-dragging ml)))

(defn button-down?
  [button]
  (let [ml (provide-listener)
        buttons (:mouse-button-pressed ml)
        n-buttons (count buttons)]
    (if (< button n-buttons)
      (nth buttons button)
      false)))
