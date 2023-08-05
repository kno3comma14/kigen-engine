(ns kigengames.kigen-engine.rendering.shader-processor
  (:require [clojure.string :as s]
            [kigengames.kigen-engine.util.file :as f])
  (:import (org.lwjgl.opengl GL11 GL46)))

(def splitter-token #"(#type)( )+([a-zA-Z]+)")

(defn extract-shader-programs
  [path]
  (let [target (f/get-content path)
        first-index (+ (s/index-of target "#type") 6)
        first-eol (s/index-of target "\n")
        first-clue (= "vertex" (subs target first-index first-eol))
        raw-programs (s/split target splitter-token)
        n-components (count raw-programs)]
    (when (= n-components 3)
      (if first-clue
        {:vertex (nth raw-programs 1)
         :fragment (nth raw-programs 2)}
        {:vertex (nth raw-programs 2)
         :fragment (nth raw-programs 1)}))))

(defn verify-shader-compilation-error
  [shader-id]
  (let [success (GL46/glGetShaderi shader-id GL46/GL_COMPILE_STATUS)]
    (when (= success GL11/GL_FALSE)
      (let [len (GL46/glGetShaderi shader-id GL46/GL_INFO_LOG_LENGTH)] ;; TODO add more logic
        (prn (GL46/glGetShaderInfoLog shader-id len))
        (throw (Exception. (str "ERROR: " shader-id " shader compilation failed.")))))))

(defn compile-shader
  [path]
  (let [source-map (extract-shader-programs path)
        vertex-source (:vertex source-map)
        fragment-source (:fragment source-map)
        vertex-id (GL46/glCreateShader GL46/GL_VERTEX_SHADER)
        fragment-id (GL46/glCreateShader GL46/GL_FRAGMENT_SHADER)]
    (GL46/glShaderSource vertex-id vertex-source)
    (GL46/glCompileShader vertex-id)
    (verify-shader-compilation-error vertex-id)
    (GL46/glShaderSource fragment-id fragment-source)
    (GL46/glCompileShader fragment-id)
    (verify-shader-compilation-error fragment-id)
    (let [shader-program-id (GL46/glCreateProgram)
          _ (GL46/glAttachShader shader-program-id vertex-id)
          _ (GL46/glAttachShader shader-program-id fragment-id)
          _ (GL46/glLinkProgram shader-program-id)
          success (GL46/glGetProgrami shader-program-id GL46/GL_LINK_STATUS)]
      (if (= success GL11/GL_FALSE) ;; TODO add more logic
        (throw (Exception. (str "ERROR: " shader-program-id " shader compilation failed.")))
        shader-program-id))))

(defn use-shader
  [shader-program-id]
  (GL46/glUseProgram shader-program-id))

(defn dettach []
  (GL46/glUseProgram 0))

