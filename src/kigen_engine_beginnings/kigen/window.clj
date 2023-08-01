(ns kigen-engine-beginnings.kigen.window
  (:import (org.lwjgl.glfw GLFW))
  (:require [taoensso.timbre :as timbre :refer [warn]]))

(defonce _window-entity (atom nil))

(defn create-window
  [width height title origin-x origin-y]
  (if @_window-entity
    (warn "Only one instance of GLFW window can be created.")
    (let [window (GLFW/glfwCreateWindow width height title origin-x origin-y)]
      (if (zero? window)
        (throw (RuntimeException. "Failed to create the GLFW window"))
        (reset! _window-entity window)))))

(defn provide-window
  []
  (if (nil? @_window-entity)
    (throw (RuntimeException. "Failed to create the GLFW window"))
    @_window-entity))

(defn _init
  [])

(defn _loop
  [])

(defn run
  [])


