(ns kigengames.kigen-engine.rendering.batch-renderer
  (:require [kigengames.kigen-engine.rendering.shader-processor :as sp]
            [kigengames.kigen-engine.window :as w])
  (:import (org.lwjgl.opengl GL46)))

(def ^:private ^:const pos-size 2)
(def ^:private ^:const color-size 4)
(def ^:private ^:const tex-coords-size 2)
(def ^:private ^:const tex-id-size 1)

(def ^:private ^:const pos-offset 0)
(def ^:private ^:const color-offset (+ pos-offset (* pos-size Float/BYTES)))
(def ^:private ^:const tex-coords-offset (+ color-offset (* color-size Float/BYTES)))
(def ^:private ^:const tex-id-offset (+ tex-coords-offset (* tex-coords-size Float/BYTES)))
(def ^:private ^:const vertex-size 9)
(def ^:private ^:const vertex-size-bytes (* vertex-size Float/BYTES))
(def data-rebuffed? (atom false))

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
      (if (>= i max-batch-size)
        @elems
        (do
          (charge-element-indices elems i)
          (recur (inc i)))))))

(defn load-vertex-properties
  [index sprites vertices textures]
  (let [sprite (nth @sprites index)
        offset (atom (* 4 index vertex-size))
        color (:color sprite)
        tex-coords (.get-texture-coords sprite)
        x-add (atom 1.0)
        y-add (atom 1.0)
        tex-id (atom 0)
        tex-size (count @textures)] 
    (when (not= nil (:texture sprite))
     (loop [i 0]
       (if (.equals (nth @textures i) (:texture sprite))
         (reset! tex-id (inc i))
         (when (< i tex-size) 
          (recur (inc i))))))

    (loop [i 0]
      (when (< i 4)
        (cond
          (= i 1) (reset! y-add 0.0)
          (= i 2) (reset! x-add 0.0)
          (= i 3) (reset! y-add 1.0))
        ;; Vertex position
        (swap! vertices update @offset (fn [_] (+ (.x (get-in sprite [:transform :position])) (* @x-add (.x (get-in sprite [:transform :scale]))))))
        (swap! vertices update (+ @offset 1) (fn [_] (+ (.y (get-in sprite [:transform :position])) (* @y-add (.y (get-in sprite [:transform :scale]))))))
        ;; Colors
        (swap! vertices update (+ @offset 2) (fn [_] (.x color)))
        (swap! vertices update (+ @offset 3) (fn [_] (.y color)))
        (swap! vertices update (+ @offset 4) (fn [_] (.z color)))
        (swap! vertices update (+ @offset 5) (fn [_] (.w color)))
        ;; texture coordinates
        (swap! vertices update (+ @offset 6) (fn [_] (.x (nth tex-coords i))))
        (swap! vertices update (+ @offset 7) (fn [_] (.y (nth tex-coords i))))
        ;; texture id
        (swap! vertices update (+ @offset 8) (fn [_] @tex-id))

        (reset! offset (+ @offset vertex-size))
        (recur (inc i))))))

(defprotocol BatchRendererP
  (start [this])
  (add-sprite [this sr])
  (render [this]))

(defrecord BatchRenderer [max-batch-size sprites number-of-sprites has-capacity vertices textures vao-id vbo-id shader]
  BatchRendererP
  (start
   [_]
   (reset! vao-id (GL46/glGenVertexArrays))
   (GL46/glBindVertexArray @vao-id)
    ;; Space for vertices
   (reset! vbo-id (GL46/glGenBuffers))
   (GL46/glBindBuffer GL46/GL_ARRAY_BUFFER @vbo-id)
   (GL46/glBufferData GL46/GL_ARRAY_BUFFER (calculate-buffer-size max-batch-size) GL46/GL_DYNAMIC_DRAW)
    ;; Load indices buffer
   (let [ebo-id (GL46/glGenBuffers)
         indices (int-array (generate-indices max-batch-size))]
     (GL46/glBindBuffer GL46/GL_ELEMENT_ARRAY_BUFFER ebo-id)
     (GL46/glBufferData GL46/GL_ELEMENT_ARRAY_BUFFER indices GL46/GL_STATIC_DRAW))

   (GL46/glVertexAttribPointer 0 pos-size GL46/GL_FLOAT false vertex-size-bytes pos-offset)
   (GL46/glEnableVertexAttribArray 0)

   (GL46/glVertexAttribPointer 1 color-size GL46/GL_FLOAT false vertex-size-bytes color-offset)
   (GL46/glEnableVertexAttribArray 1)
   
   (GL46/glVertexAttribPointer 2 tex-coords-size GL46/GL_FLOAT false vertex-size-bytes tex-coords-offset)
   (GL46/glEnableVertexAttribArray 2)
   
   (GL46/glVertexAttribPointer 3 tex-id-size GL46/GL_FLOAT false vertex-size-bytes tex-id-offset)
   (GL46/glEnableVertexAttribArray 3))
  
  (add-sprite
   [_ sr]
   (let [index @number-of-sprites]
     (swap! sprites update index (fn [_] sr))
     (swap! number-of-sprites inc)

     (when (and (not= nil (:texture sr))
                (not-any? (fn [t] (.equals t (:texture sr))) @textures))
       (swap! textures conj (:texture sr)))

     (load-vertex-properties index sprites vertices textures)
     (when (>= @number-of-sprites max-batch-size)
       (reset! has-capacity false))))
  (render
   [_]
   (reduce (fn [acc, _] 
             (when (get-in @sprites [acc :is-dirty?]) 
               (load-vertex-properties acc sprites vertices textures)
               (swap! sprites update-in [acc :is-dirty?] (fn [_] false))
               (reset! data-rebuffed? true))
             (inc acc))
           0
           @sprites)
   
   (when @data-rebuffed?
     (GL46/glBindBuffer GL46/GL_ARRAY_BUFFER @vbo-id)
     (GL46/glBufferSubData GL46/GL_ARRAY_BUFFER 0 (float-array @vertices)))
   
   (sp/use-shader @shader)
   (sp/upload-matrix4f @shader "uProjection" (.get-projection-matrix @(:camera @w/current-scene)))
   (sp/upload-matrix4f @shader "uView" (.get-view-matrix @(:camera @w/current-scene)))
   (reduce (fn [acc, _]
             (GL46/glActiveTexture (+ GL46/GL_TEXTURE0 (+ acc 1))) 
             (.bind (nth @textures acc))
             (inc acc))
           0
           @textures)
   (sp/upload-int-array @shader "uTextures" (vec (range 7))) ;; using vec because I want to normalize only vectors for collections
   (GL46/glBindVertexArray @vao-id)
   (GL46/glEnableVertexAttribArray 0)
   (GL46/glEnableVertexAttribArray 1)
   (GL46/glDrawElements GL46/GL_TRIANGLES (* @number-of-sprites 6) GL46/GL_UNSIGNED_INT 0)
   (GL46/glDisableVertexAttribArray 0)
   (GL46/glDisableVertexAttribArray 1)
   (GL46/glBindVertexArray 0)

   (reduce (fn [acc, _]
             (GL46/glActiveTexture (+ GL46/GL_TEXTURE0 (inc acc)))
             (.unbind (nth @textures acc))
             (inc acc))
           0
           @textures)

   (sp/dettach)))

(defn create
  [max-batch-size]
  (let [shader (atom (sp/compile-shader "shaders/default.glsl"))
        vertices (atom (vec (repeat (* max-batch-size 4 vertex-size) 0.0)))
        textures (atom [])
        sprites (atom (vec (repeat max-batch-size nil)))
        number-of-sprites (atom 0)
        has-capacity (atom true)
        vao-id (atom -1)
        vbo-id (atom -1)]
    (->BatchRenderer max-batch-size sprites number-of-sprites has-capacity vertices textures vao-id vbo-id shader)))