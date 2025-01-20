(ns kigengames.kigen-engine.io.keyboard-input-event-listener
  (:import (org.lwjgl.glfw GLFW GLFWKeyCallbackI)))

(defonce key-listener (atom nil))

#_{:clj-kondo/ignore [:unused-binding]}
(def process-key-callback
  (reify GLFWKeyCallbackI
    (invoke [this window key scancode action mods]
      (cond (= action GLFW/GLFW_PRESS) (reset! key-listener (assoc-in @key-listener [:keys-pressed key] true))
            (= action GLFW/GLFW_RELEASE) (reset! key-listener (assoc-in @key-listener [:keys-pressed key] false))))))

(defn init
  []
  (let [key-switchs (vec (boolean-array 350))]
    (reset! key-listener {:keys-pressed key-switchs})))

(defn is-key-pressed
  [key-code]
  (nth (:keys-pressed @key-listener) key-code))
