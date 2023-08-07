(ns kigengames.kigen-engine.rendering.shader-processor
  (:require [clojure.string :as s]
            [kigengames.kigen-engine.util.file :as f])
  (:import (org.lwjgl.opengl GL11 GL46)
           (org.lwjgl BufferUtils)))

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
  (GL46/glUseProgram shader-program-id)) ;; Research conditional use - no big priority anyways

(defn dettach []
  (GL46/glUseProgram 0))

(defn upload-matrix4f 
  [shader-program-id variable-name input-matrix]
  (let [variable-location (GL46/glGetUniformLocation shader-program-id variable-name)
        _ (use-shader shader-program-id)
        matrix-buffer (BufferUtils/createFloatBuffer 16)
        _ (.get input-matrix matrix-buffer)]
    (GL46/glUniformMatrix4fv variable-location false matrix-buffer)))

(defn upload-matrix3f 
  [shader-program-id variable-name input-matrix]
  (let [variable-location (GL46/glGetUniformLocation shader-program-id variable-name)
        _ (use-shader shader-program-id)
        matrix-buffer (BufferUtils/createFloatBuffer 9)
        _ (.get input-matrix matrix-buffer)]
    (GL46/glUniformMatrix3fv variable-location false matrix-buffer)))

(defn upload-vec4f
  [shader-program-id variable-name input-vector]
  (let [variable-location (GL46/glGetUniformLocation shader-program-id variable-name)
        _ (use-shader shader-program-id)]
    (GL46/glUniform4f variable-location (.x input-vector) (.y input-vector) (.z input-vector) (.w input-vector))))

(defn upload-vec3f 
  [shader-program-id variable-name input-vector]
  (let [variable-location (GL46/glGetUniformLocation shader-program-id variable-name)
        _ (use-shader shader-program-id)]
    (GL46/glUniform3f variable-location (.x input-vector) (.y input-vector) (.z input-vector))))

(defn upload-vec2f
  [shader-program-id variable-name input-vector]
  (let [variable-location (GL46/glGetUniformLocation shader-program-id variable-name)
        _ (use-shader shader-program-id)]
    (GL46/glUniform2f variable-location (.x input-vector) (.y input-vector))))

(defn upload-float
  [shader-program-id variable-name input-float]
  (let [variable-location (GL46/glGetUniformLocation shader-program-id variable-name)
        _ (use-shader shader-program-id)]
    (GL46/glUniform1f variable-location input-float)))

(defn upload-int
  [shader-program-id variable-name input-int]
  (let [variable-location (GL46/glGetUniformLocation shader-program-id variable-name)
        _ (use-shader shader-program-id)]
    (GL46/glUniform1i variable-location input-int)))

(defn upload-texture
  [shader-program-id variable-name input-slot]
  (let [variable-location (GL46/glGetUniformLocation shader-program-id variable-name)
        _ (use-shader shader-program-id)]
    (GL46/glUniform1i variable-location input-slot)))







