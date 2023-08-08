(ns kigengames.kigen-engine.rendering.texture
  (:require [clojure.java.io :as io]
            [kigengames.kigen-engine.rendering.texture :as texture])
  (:import (org.lwjgl BufferUtils)
           (org.lwjgl.opengl GL46)
           (org.lwjgl.stb STBImage)))

(defn- set-texture-parameters 
  []
  (GL46/glTexParameteri GL46/GL_TEXTURE_2D GL46/GL_TEXTURE_WRAP_S GL46/GL_REPEAT)
  (GL46/glTexParameteri GL46/GL_TEXTURE_2D GL46/GL_TEXTURE_WRAP_T GL46/GL_REPEAT)
  (GL46/glTexParameteri GL46/GL_TEXTURE_2D GL46/GL_TEXTURE_MIN_FILTER GL46/GL_NEAREST)
  (GL46/glTexParameteri GL46/GL_TEXTURE_2D GL46/GL_TEXTURE_MAG_FILTER GL46/GL_NEAREST))

(defn- process-texture
  [image width height channels]
  (cond
    (= (.get channels 0) 3) (GL46/glTexImage2D GL46/GL_TEXTURE_2D 
                                               0 
                                               GL46/GL_RGB
                                               (.get width 0)
                                               (.get height 0)
                                               0
                                               GL46/GL_RGB
                                               GL46/GL_UNSIGNED_BYTE
                                               image)
    (= (.get channels 0) 4) (GL46/glTexImage2D GL46/GL_TEXTURE_2D
                                               0
                                               GL46/GL_RGBA
                                               (.get width 0)
                                               (.get height 0)
                                               0
                                               GL46/GL_RGBA
                                               GL46/GL_UNSIGNED_BYTE
                                               image)
    :else (Exception. (str "ERROR: Can't find the number of channels."))))

(defprotocol TextureP 
  (init [this]) 
  (bind [this])
  (unbind [this]))

(defn create-texture 
  [path]
  (let [t-id (GL46/glGenTextures)
        _ (GL46/glBindTexture GL46/GL_TEXTURE_2D t-id)
        _ (set-texture-parameters)
        width (BufferUtils/createIntBuffer 1)
        height (BufferUtils/createIntBuffer 1)
        channels (BufferUtils/createIntBuffer 1)
        complete-path (.getPath (io/resource path))
        image (STBImage/stbi_load complete-path width height channels 0)]
    (if (not= image nil)
      (process-texture image width height channels)
      (throw (Exception. (str "ERROR: Can't open the target image"))))
    (STBImage/stbi_image_free image)
    {:path path :texture-id t-id}))

(defn bind-texture
  [texture]
  (let [texture-id (:texture-id texture)]
    (GL46/glBindTexture GL46/GL_TEXTURE_2D texture-id)))

(defn unbind-texture
  [texture]
  (let [texture-id (:texture-id texture)]
    (GL46/glBindTexture GL46/GL_TEXTURE_2D 0)))
