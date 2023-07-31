(ns kigen-engine-beginnings.kigen.window
  (:import 
           (org.lwjgl.glfw GLFW)))

(defn create-window
  [width height title origin-x origin-y]
  (let [window (GLFW/glfwCreateWindow width height title origin-x origin-y)]
    (if (zero? window)
      (throw (RuntimeException. "Failed to create the GLFW window"))
      window)))
