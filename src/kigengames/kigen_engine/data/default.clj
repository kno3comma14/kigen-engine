(ns kigengames.kigen-engine.data.default
  (:require [kigengames.kigen-engine.rendering.shader-processor :as sp]
            [kigengames.kigen-engine.util.buffer :as buffer]
            [kigengames.kigen-engine.util.time :as time])
  (:import (org.lwjgl.opengl GL46)))

(def default-vertex-vector
   ;; POS                ;; RGB                  ;; UV coords                   
  [100.5  0.5    0.0     1.0  0.0  0.0  1.0      1, 1
   0.5    100.5  0.0     0.0  1.0  0.0  1.0      0, 0
   100.5  100.5 0.0      1.0  0.0  1.0  1.0      1, 0
   0.5    0.5   0.0      1.0  1.0  0.0  1.0      0, 1])

(def element-vector
  [2, 1, 0
   0, 1, 3])

(def program-id (atom nil))
(def vao-id (atom nil))
(def vbo-id (atom nil))
(def ebo-id (atom nil))

(defn init-default-context
  [load-elements-fn]
  (reset! program-id (sp/compile-shader "shaders/default.glsl"))
  (reset! vao-id (GL46/glGenVertexArrays))
  (GL46/glBindVertexArray @vao-id)
  (load-elements-fn)
  (let [vertex-buffer (buffer/float-array->float-buffer default-vertex-vector)
        _ (reset! vbo-id (GL46/glGenBuffers))
        _ (GL46/glBindBuffer GL46/GL_ARRAY_BUFFER @vbo-id)
        _ (GL46/glBufferData GL46/GL_ARRAY_BUFFER vertex-buffer GL46/GL_STATIC_DRAW)
        element-buffer (buffer/int-array->int-buffer element-vector)
        _ (reset! ebo-id (GL46/glGenBuffers))
        _ (GL46/glBindBuffer GL46/GL_ELEMENT_ARRAY_BUFFER @ebo-id)
        _ (GL46/glBufferData GL46/GL_ELEMENT_ARRAY_BUFFER element-buffer GL46/GL_STATIC_DRAW)
        position-size 3
        color-size 4
        uv-size 2
        vertex-size-bytes (* Float/BYTES (+ position-size color-size uv-size))
        _ (GL46/glVertexAttribPointer 0 position-size GL46/GL_FLOAT false vertex-size-bytes 0)
        _ (GL46/glEnableVertexAttribArray 0)
        _ (GL46/glVertexAttribPointer 1 color-size GL46/GL_FLOAT false vertex-size-bytes (* position-size Float/BYTES))
        _ (GL46/glEnableVertexAttribArray 1)
        _ (GL46/glVertexAttribPointer 2 uv-size GL46/GL_FLOAT false vertex-size-bytes (* Float/BYTES (+ position-size color-size)))
        _ (GL46/glEnableVertexAttribArray 2)]))

(defn process-default-context 
  [main-camera]
  (sp/upload-texture @program-id "TEX_SAMPLER" 0)
   (GL46/glActiveTexture GL46/GL_TEXTURE0)
   (sp/upload-matrix4f @program-id "uProjection" (.get-projection-matrix @main-camera))
   (sp/upload-matrix4f @program-id "uView" (.get-view-matrix @main-camera))
   (sp/upload-float @program-id "uTime" (time/get-time))
   (GL46/glBindVertexArray @vao-id)
   (GL46/glEnableVertexAttribArray 0)
   (GL46/glEnableVertexAttribArray 1)
   (GL46/glDrawElements GL46/GL_TRIANGLES (count element-vector) GL46/GL_UNSIGNED_INT 0)
   (GL46/glDisableVertexAttribArray 0)
   (GL46/glDisableVertexAttribArray 1)
   (GL46/glBindVertexArray 0)
   (sp/dettach))

