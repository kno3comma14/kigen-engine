(ns kigengames.kigen-engine.rendering.batch-renderer
  (:require [kigengames.kigen-engine.rendering.shader-processor :as shader-processor])
  (:import (org.lwjgl.opengl GL46)))

(def ^:private ^:const pos-size 2)
(def ^:private ^:const color-size 4)

(def ^:private ^:const pos-offset 0)
(def ^:private ^:const color-offset (+ pos-offset (* pos-size Float/BYTES)))
(def ^:private ^:const vertex-size 6)
(def ^:private ^:const vertex-size-bytes (* vertex-size Float/BYTES))

;; Sprite Renderer specific values. Watch later for refactor
(def sprites (atom []))
(def number-of-sprites (atom 0))
(def has-capacity (atom true))
(def vertices (atom []))

;; GL stuff
(def vao-id (atom -1))
(def vbo-id (atom -1))

(def shader (atom 0))

(defn calculate-buffer-size
  [batch-size]
  (* batch-size 4 vertex-size Float/BYTES))

(defn charge-element-indices [elements index]
  (let [offset-array-index (* index 6)
        offset (* index 4)]
    (swap! elements update offset-array-index (fn [_] (+ 3 offset)))
    (swap! elements update (+ offset-array-index 1) (fn [_] (+ 2 offset)))
    (swap! elements update (+ offset-array-index 2) (fn [_] offset))
    
    (swap! elements update (+ offset-array-index 3) (fn [_] offset))
    (swap! elements update (+ offset-array-index 4) (fn [_] (+ 2 offset)))
    (swap! elements update (+ offset-array-index 5) (fn [_] (+ 1 offset)))))

(defn generate-indices 
  [max-batch-size]
  (let [elems (atom (vec (repeat (* 6 max-batch-size) 0)))]
    (loop [i 0]
      (if (i >= max-batch-size)
        @elems
        (do
          (charge-element-indices elems i)
          (recur (inc i)))))))

(defprotocol BatchRendererP
  (start [this]))

(defrecord BatchRenderer [max-batch-size]
  (start [_]
    (reset! vao-id (GL46/glGenVertexArrays))
    (GL46/glBindVertexArray vao-id)
    ;; Space for vertices
    (reset! vbo-id (GL46/glGenBuffers))
    (GL46/glBindBuffer GL46/GL_ARRAY_BUFFER vbo-id)
    (GL46/glBufferData GL46/GL_ARRAY_BUFFER (calculate-buffer-size max-batch-size) GL46/GL_DYNAMIC_DRAW)
    ;; Load indices buffer
    (let [ebo-id (GL46/glGenBuffers)
          indices (generate-indices max-batch-size)]
      (GL46/glBindBuffer GL46/GL_ELEMENT_ARRAY_BUFFER ebo-id)
      (GL46/glBufferData GL46/GL_ELEMENT_ARRAY_BUFFER (to-array indices) GL46/GL_STATIC_DRAW))
    
    (GL46/glVertexAttribPointer 0 pos-size GL46/GL_FLOAT false vertex-size-bytes pos-offset)
    (GL46/glEnableVertexAttribArray 0)

    (GL46/glVertexAttribPointer 1 color-size GL46/GL_FLOAT false vertex-size-bytes color-offset)
    (GL46/glEnableVertexAttribArray 1))
  (add-sprite-renderer [this sr])
  (render [this]))

(defn create
  [max-batch-size]
  (let [instance (->BatchRenderer max-batch-size)]
    (reset! shader (shader-processor/compile-shader "shaders/default.glsl"))
    (reset! vertices (vec (repeat (* max-batch-size 4 vertex-size) 0.0)))))