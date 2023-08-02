(ns kigen-engine-beginnings.kigen.window
  (:import (org.lwjgl.glfw GLFW GLFWErrorCallback Callbacks)
           (org.lwjgl.opengl GL GL33))
  (:require [taoensso.timbre :as timbre :refer [warn]]
            [kigen-engine-beginnings.kigen.keyboard-input-event-listener :as kl]
            [kigen-engine-beginnings.kigen.mouse-input-event-listener :as ml]))

(defonce _window-entity (atom nil))

(defn create-window
  [width height title]
  (if @_window-entity
    (warn "Only one instance of GLFW window can be created.")
    (let [window (GLFW/glfwCreateWindow width height title 0 0)]
      (if (zero? window)
        (throw (RuntimeException. "Failed to create the GLFW window"))
        (reset! _window-entity window)))))

(defn provide-window
  []
  (if (nil? @_window-entity)
    (throw (RuntimeException. "You need to create a GLFW window first"))
    @_window-entity))

(defn- init
  [width height title]
  (.set (GLFWErrorCallback/createPrint (System/err)))
  (when (not (GLFW/glfwInit))
    (throw (IllegalStateException. "Unable to initialize GLFW")))
  (GLFW/glfwDefaultWindowHints)
  (GLFW/glfwWindowHint GLFW/GLFW_VISIBLE GLFW/GLFW_FALSE)
  (GLFW/glfwWindowHint GLFW/GLFW_RESIZABLE GLFW/GLFW_TRUE)
  ;;(GLFW/glfwWindowHint GLFW/GLFW_MAXIMIZED GLFW/GLFW_TRUE)
  (create-window width height title)
  (GLFW/glfwMakeContextCurrent @_window-entity)
  (GLFW/glfwShowWindow @_window-entity)
  (GL/createCapabilities)
  (kl/init)
  (ml/init)
  (GLFW/glfwSetKeyCallback @_window-entity kl/process-key-callback)
  (GLFW/glfwSetMouseButtonCallback @_window-entity ml/mouse-button-callback)
  (GLFW/glfwSetCursorPosCallback @_window-entity ml/mouse-position-callback)
  (GLFW/glfwSetScrollCallback @_window-entity ml/mouse-scroll-callback))

(defn- draw []

  (GL33/glClearColor 1.0 0.0 0.0 0.0)

  ; clear the framebuffer
  (GL33/glClear (bit-or GL33/GL_COLOR_BUFFER_BIT GL33/GL_DEPTH_BUFFER_BIT))

  ; swap the color buffers
  (GLFW/glfwSwapBuffers @_window-entity)

  ; Poll for window events. The key callback above will only be
  ; invoked during this call.
  (GLFW/glfwPollEvents))

(defn- game-loop
  []
  (while (not (GLFW/glfwWindowShouldClose @_window-entity))
    (draw)))

(defn run
  [width height title]
  (init width height title)
  (game-loop)
  (let [window (provide-window)]
    (Callbacks/glfwFreeCallbacks window)
    (GLFW/glfwDestroyWindow window)
    (GLFW/glfwTerminate)
    (-> (GLFW/glfwSetErrorCallback nil) (.free))))
