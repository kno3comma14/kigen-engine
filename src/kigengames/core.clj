(ns kigengames.core
  (:gen-class)
  (:require [kigengames.kigen-engine.window :as w]
            [kigengames.kigen-engine.scene :as scene]
            [kigengames.kigen-engine.rendering.shader-processor :as sp])
  (:import (org.lwjgl.opengl GL46)
           (java.nio ByteBuffer ByteOrder)))

(def vertex-vector
  ;; POS              ;; RGB
  [0.5  -0.5  0.0     1.0  0.0  0.0  1.0
   -0.5  0.5  0.0     0.0  1.0  0.0  1.0
   0.5   0.5  0.0     1.0  0.0  1.0  1.0
   -0.5  -0.5 0.0     1.0  1.0  0.0  1.0])

(def element-vector
  [2, 1, 0
   0, 1, 3])

(def program-id (atom nil))
(def vao-id (atom nil))
(def vbo-id (atom nil))
(def ebo-id (atom nil))

(defn float-array->float-buffer
  [target]
  (let [t-array (float-array target)
        byte-buffer (ByteBuffer/allocateDirect (* 4 (count target)))
        _ (.order byte-buffer (ByteOrder/nativeOrder))
        float-buffer (.asFloatBuffer byte-buffer)
        _ (.put float-buffer t-array)
        _ (.position float-buffer 0)]
    float-buffer))

(defn int-array->int-buffer
  [target]
  (let [t-array (int-array target)
        byte-buffer (ByteBuffer/allocateDirect (* 4 (count target)))
        _ (.order byte-buffer (ByteOrder/nativeOrder))
        int-buffer (.asIntBuffer byte-buffer)
        _ (.put int-buffer t-array)
        _ (.position int-buffer 0)]
    int-buffer))

(def scene0 (scene/->Scene 0
                           "bla0"
                           (fn []
                             (reset! program-id (sp/compile-shader "shaders/default.glsl"))
                             (reset! vao-id (GL46/glGenVertexArrays))
                             (GL46/glBindVertexArray @vao-id)
                             (let [vertex-buffer (float-array->float-buffer vertex-vector)
                                   _ (reset! vbo-id (GL46/glGenBuffers))
                                   _ (GL46/glBindBuffer GL46/GL_ARRAY_BUFFER @vbo-id)
                                   _ (GL46/glBufferData GL46/GL_ARRAY_BUFFER vertex-buffer GL46/GL_STATIC_DRAW)
                                   element-buffer (int-array->int-buffer element-vector)
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
                             (GL46/glBindVertexArray @vao-id)
                             (GL46/glEnableVertexAttribArray 0)
                             (GL46/glEnableVertexAttribArray 1)
                             (GL46/glDrawElements GL46/GL_TRIANGLES (count element-vector) GL46/GL_UNSIGNED_INT 0)
                             (GL46/glDisableVertexAttribArray 0)
                             (GL46/glDisableVertexAttribArray 1)
                             (GL46/glBindVertexArray 0)
                             (sp/dettach))))

(defn -main
  [& _args]
  (w/run 300 300 "Sup!!!" scene0))
