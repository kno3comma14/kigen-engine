(ns kigen-engine-beginnings.core
  (:gen-class)
  (:require [kigen-engine-beginnings.kigen.window :as w]
            [nrepl.server :refer [start-server stop-server]])
  (:import (org.lwjgl Version)
           (org.lwjgl.glfw GLFWErrorCallback GLFW GLFWKeyCallbackI Callbacks)
           (org.lwjgl.opengl GL GL33)
           (org.lwjgl.system MemoryStack)))

(defonce server (start-server :port 7888))

(defn -main
  [& args]
  (w/run 300 300 "Sup!!!")
  ;; (init)
  ;; (main-loop)
  (let [window (w/provide-window)]
    (Callbacks/glfwFreeCallbacks window)
    (GLFW/glfwDestroyWindow window)

    (GLFW/glfwTerminate)
    (-> (GLFW/glfwSetErrorCallback nil) (.free))))

;; (defn init []

;;   ; Setup an error callback. The default implementation
;;   ; will print the error message in System.err.
;;   (-> (GLFWErrorCallback/createPrint System/err) (.set))

;;   ; Initialize GLFW. Most GLFW functions will not work before doing this.
;;   (when (not (GLFW/glfwInit))
;;     (throw (IllegalStateException. "Unable to initialize GLFW")))

;;   ; Configure GLFW
;;   (GLFW/glfwDefaultWindowHints)                             ; optional, the current window hints are already the default
;;   (GLFW/glfwWindowHint GLFW/GLFW_VISIBLE GLFW/GLFW_FALSE)   ; the window will stay hidden after creation
;;   (GLFW/glfwWindowHint GLFW/GLFW_RESIZABLE GLFW/GLFW_TRUE)  ; the window will be resizable

;;   ; Create the window
;;   (w/create-window 300 300 "Sup!!!")
;;   (def window (w/provide-window))

;;   ; Setup a key callback. It will be called every time a key is pressed, repeated or released.
;;   (GLFW/glfwSetKeyCallback window (reify GLFWKeyCallbackI
;;                                     (invoke [this window key scancode action mods]
;;                                       (when (and (= key GLFW/GLFW_KEY_ESCAPE)
;;                                                  (= action GLFW/GLFW_RELEASE))
;;                                         ; We will detect this in the rendering loop
;;                                         (GLFW/glfwSetWindowShouldClose window true)))))

;;   ; Get the thread stack and push a new frame
;;   (let [stack (MemoryStack/stackPush)
;;         p-width (.mallocInt stack 1)
;;         p-height (.mallocInt stack 1)]

;;     ; Get the window size passed to glfwCreateWindow
;;     (GLFW/glfwGetWindowSize ^long window p-width p-height)
;;     (let [vidmode (-> (GLFW/glfwGetPrimaryMonitor)          ; Get the resolution of the primary monitor
;;                       (GLFW/glfwGetVideoMode))
;;           xpos (/ (- (.width vidmode)
;;                      (.get p-width 0))
;;                   2)
;;           ypos (/ (- (.height vidmode)
;;                      (.get p-height 0))
;;                   2)]
;;       (GLFW/glfwSetWindowPos window xpos ypos))             ; Center the window
;;     (MemoryStack/stackPop)                                  ; pop stack frame
;;     )

;;   (GLFW/glfwMakeContextCurrent window)                      ; Make the OpenGL context current
;;   (GLFW/glfwSwapInterval 1)                                 ; Enable v-sync
;;   (GLFW/glfwShowWindow window))                             ; Make the window visible

;; (defn main-loop []

;;   ; This line is critical for LWJGL's interoperation with GLFW's
;;   ; OpenGL context, or any context that is managed externally.
;;   ; LWJGL detects the context that is current in the current thread,
;;   ; creates the GLCapabilities instance and makes the OpenGL
;;   ; bindings available for use.
;;   (GL/createCapabilities)

;;   ; Set the clear color
;;   (GL33/glClearColor 1.0 0.0 0.0 0.0)

;;   ; Run the rendering loop until the user has attempted to close
;;   ; the window or has pressed the ESCAPE key.
;;   (while (not (GLFW/glfwWindowShouldClose window))
;;     (draw)))

;; (defn draw []

;;   (GL33/glClearColor 1.0 0.0 0.0 0.0)

;;   ; clear the framebuffer
;;   (GL33/glClear (bit-or GL33/GL_COLOR_BUFFER_BIT GL33/GL_DEPTH_BUFFER_BIT))

;;   ; swap the color buffers
;;   (GLFW/glfwSwapBuffers window)

;;   ; Poll for window events. The key callback above will only be
;;   ; invoked during this call.
;;   (GLFW/glfwPollEvents))
