(ns kigengames.kigen-engine.window
  (:import (org.lwjgl.glfw GLFW GLFWErrorCallback Callbacks)
           (org.lwjgl.opengl GL GL46))
  (:require [taoensso.timbre :as timbre :refer [warn]]
            [kigengames.kigen-engine.keyboard-input-event-listener :as kl]
            [kigengames.kigen-engine.mouse-input-event-listener :as ml] 
            [kigengames.kigen-engine.scene :as scene]))

(defonce _window-entity (atom nil))

(def current-scene (atom nil))

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

(defn change-scene
  [input-scene]
  (reset! current-scene input-scene)
  (scene/init @current-scene))

(defn- init
  [width height title initial-scene]
  (.set (GLFWErrorCallback/createPrint (System/err)))
  (when (not (GLFW/glfwInit))
    (throw (IllegalStateException. "Unable to initialize GLFW")))
  (GLFW/glfwDefaultWindowHints)
  (GLFW/glfwWindowHint GLFW/GLFW_VISIBLE GLFW/GLFW_FALSE)
  (GLFW/glfwWindowHint GLFW/GLFW_RESIZABLE GLFW/GLFW_TRUE)
  (GLFW/glfwWindowHint GLFW/GLFW_CONTEXT_VERSION_MAJOR 3)
  (GLFW/glfwWindowHint GLFW/GLFW_CONTEXT_VERSION_MINOR 3)
  (GLFW/glfwWindowHint GLFW/GLFW_OPENGL_FORWARD_COMPAT GLFW/GLFW_TRUE)
  (GLFW/glfwWindowHint GLFW/GLFW_OPENGL_PROFILE GLFW/GLFW_OPENGL_CORE_PROFILE)
  (GLFW/glfwWindowHint GLFW/GLFW_MAXIMIZED GLFW/GLFW_TRUE)
  (create-window width height title)
  (GLFW/glfwMakeContextCurrent @_window-entity)
  (GLFW/glfwSwapInterval 1)
  (GLFW/glfwShowWindow @_window-entity)
  (GL/createCapabilities)
  (change-scene initial-scene)
  (kl/init)
  (ml/init)

  (GLFW/glfwSetKeyCallback @_window-entity kl/process-key-callback)
  (GLFW/glfwSetMouseButtonCallback @_window-entity ml/mouse-button-callback)
  (GLFW/glfwSetCursorPosCallback @_window-entity ml/mouse-position-callback)
  (GLFW/glfwSetScrollCallback @_window-entity ml/mouse-scroll-callback))

(defn- draw [dt]
  (GLFW/glfwPollEvents)
  (GL46/glClearColor 0.0 0.0 0.0 0.0)

  (GL46/glClear (bit-or GL46/GL_COLOR_BUFFER_BIT GL46/GL_DEPTH_BUFFER_BIT))

  (when (and (not= @current-scene nil) (>= @dt 0))
    (scene/process @current-scene @dt))

  (GLFW/glfwSwapBuffers (provide-window)))

(defn- game-loop
  []
  (let [begin-time (atom (float (GLFW/glfwGetTime)))
        end-time (atom (float (GLFW/glfwGetTime)))
        dt (atom -1.0)]
    (while (not (GLFW/glfwWindowShouldClose @_window-entity))
      (draw dt)
      (reset! end-time (float (GLFW/glfwGetTime)))
      (reset! dt (- @end-time @begin-time))
      (reset! begin-time @end-time))))

(defn run
  [width height title initial-scene]
  (init width height title initial-scene)
  (game-loop)
  (let [window (provide-window)]
    (Callbacks/glfwFreeCallbacks window)
    (GLFW/glfwDestroyWindow window)
    (GLFW/glfwTerminate)
    (-> (GLFW/glfwSetErrorCallback nil) (.free))))
