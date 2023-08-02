(ns kigen-engine-beginnings.kigen.mouse-input-event-listener
  (:import (org.lwjgl.glfw GLFW GLFWKeyCallbackI)))

(defonce mouse-listener (atom nil))

(defn init
  []
  (reset! mouse-listener {:scroll-x 0.0
                          :scroll-y 0.0
                          :x-pos 0.0
                          :y-pos 0.0
                          :last-x 0.0
                          :last-y 0.0
                          :mouse-button-pressed (vec (boolean-array 3))
                          :is-dragging false}))

(defn provide-listener
  []
  (if (nil? @mouse-listener)
    (init)
    (@mouse-listener)))

(def mouse-position-callback
  (reify GLFWKeyCallbackI
    (invoke [this window x-pos y-pos]
      (let [updated-map {:last-x (:last-x @mouse-listener)
                         :last-y (:last-y @mouse-listener)
                         :x-pos x-pos
                         :y-pos y-pos
                         :is-dragging (not-every? false? (:mouse-button-pressed @mouse-listener))}]
        (reset! mouse-listener (@mouse-listener updated-map))))))

(def mouse-scroll-callback
  (reify GLFWKeyCallbackI
    (invoke [this window x-offset y-offset]
      (let [updated-map {:scroll-x x-offset
                         :scroll-y y-offset}]
        (reset! mouse-listener (@mouse-listener updated-map))))))











