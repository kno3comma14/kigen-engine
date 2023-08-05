(ns kigengames.core
  (:gen-class)
  (:require [kigengames.kigen-engine.window :as w]
            [kigengames.kigen-engine.scene :as scene]
            [kigengames.kigen-engine.rendering.shader-processor :as sp]
            [kigengames.kigen-engine.camera :as camera]
            [kigengames.kigen-engine.util.buffer :as buffer])
  (:import (org.lwjgl.opengl GL46) 
           (org.joml Vector2f Matrix4f)))

(def vertex-vector
  ;; POS              ;; RGB
  [100.5  0.5  0.0     1.0  0.0  0.0  1.0
   0.5  100.5  0.0     0.0  1.0  0.0  1.0
   100.5 100.5  0.0     1.0  0.0  1.0  1.0
   0.5  0.5 0.0     1.0  1.0  0.0  1.0])

(def element-vector
  [2, 1, 0
   0, 1, 3])

(def program-id (atom nil))
(def vao-id (atom nil))
(def vbo-id (atom nil))
(def ebo-id (atom nil))

(def main-camera (atom (camera/->Camera 0
                                        "camera0"
                                        (Vector2f. -200.0 -300.0)
                                        (fn [pos dt]
                                          (Vector2f. (- (.x pos) (* 50.0 dt)) (- (.y pos) (* 20.0 dt))))
                                        (Matrix4f.)
                                        (Matrix4f.))))

(def scene0 (scene/->Scene 0
                           "bla0"
                           (fn []
                             (reset! program-id (sp/compile-shader "shaders/default.glsl"))
                             (reset! vao-id (GL46/glGenVertexArrays))
                             (GL46/glBindVertexArray @vao-id)
                             (let [vertex-buffer (buffer/float-array->float-buffer vertex-vector)
                                   _ (reset! vbo-id (GL46/glGenBuffers))
                                   _ (GL46/glBindBuffer GL46/GL_ARRAY_BUFFER @vbo-id)
                                   _ (GL46/glBufferData GL46/GL_ARRAY_BUFFER vertex-buffer GL46/GL_STATIC_DRAW)
                                   element-buffer (buffer/int-array->int-buffer element-vector)
                                   _ (reset! ebo-id (GL46/glGenBuffers))
                                   _ (GL46/glBindBuffer GL46/GL_ELEMENT_ARRAY_BUFFER @ebo-id)
                                   _ (GL46/glBufferData GL46/GL_ELEMENT_ARRAY_BUFFER element-buffer GL46/GL_STATIC_DRAW)
                                   position-size 3
                                   color-size 4
                                   float-size-bytes 4
                                   vertex-size-bytes (* float-size-bytes (+ position-size color-size))
                                   _ (GL46/glVertexAttribPointer 0 position-size GL46/GL_FLOAT false vertex-size-bytes 0)
                                   _ (GL46/glEnableVertexAttribArray 0)
                                   _ (GL46/glVertexAttribPointer 1 color-size GL46/GL_FLOAT false vertex-size-bytes (* position-size float-size-bytes))
                                   _ (GL46/glEnableVertexAttribArray 1)]))
                           (fn [dt]
                             (sp/use-shader @program-id)
                             (sp/upload-matrix4f @program-id "uProjection" (.get-projection-matrix @main-camera))
                             (sp/upload-matrix4f @program-id "uView" (.get-view-matrix @main-camera))
                             (GL46/glBindVertexArray @vao-id)
                             (GL46/glEnableVertexAttribArray 0)
                             (GL46/glEnableVertexAttribArray 1)
                             (GL46/glDrawElements GL46/GL_TRIANGLES (count element-vector) GL46/GL_UNSIGNED_INT 0)
                             (GL46/glDisableVertexAttribArray 0)
                             (GL46/glDisableVertexAttribArray 1)
                             (GL46/glBindVertexArray 0)
                             (sp/dettach))
                           main-camera))

(defn -main
  [& _args]
  (w/run 1920 1080 "Sup!!!" scene0))
