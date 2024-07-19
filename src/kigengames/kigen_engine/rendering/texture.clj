(ns kigengames.kigen-engine.rendering.texture
  (:require [clojure.java.io :as io]
            [kigengames.kigen-engine.data.component :as component])
  (:import (org.lwjgl BufferUtils)
           (org.lwjgl.opengl GL46)
           (org.lwjgl.stb STBImage)))

(defn- set-texture-default-parameters 
  []
  (GL46/glTexParameteri GL46/GL_TEXTURE_2D GL46/GL_TEXTURE_WRAP_S GL46/GL_REPEAT)
  (GL46/glTexParameteri GL46/GL_TEXTURE_2D GL46/GL_TEXTURE_WRAP_T GL46/GL_REPEAT)
  (GL46/glTexParameteri GL46/GL_TEXTURE_2D GL46/GL_TEXTURE_MIN_FILTER GL46/GL_NEAREST)
  (GL46/glTexParameteri GL46/GL_TEXTURE_2D GL46/GL_TEXTURE_MAG_FILTER GL46/GL_NEAREST))

(defn- prepare-texture
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

(defn- load-texture 
  [path]
  (let [t-id (GL46/glGenTextures)
        _ (GL46/glBindTexture GL46/GL_TEXTURE_2D t-id)
        _ (set-texture-default-parameters)
        width (BufferUtils/createIntBuffer 1)
        height (BufferUtils/createIntBuffer 1)
        channels (BufferUtils/createIntBuffer 1)
        _ (STBImage/stbi_set_flip_vertically_on_load true) 
        complete-path (.getPath (io/resource path))
        image (STBImage/stbi_load complete-path width height channels 0)]
    (if (not= image nil)
      (prepare-texture image width height channels)
      (throw (Exception. (str "ERROR: Can't open the target image"))))
    (STBImage/stbi_image_free image)
    {:path path :texture-id t-id :width (float (.get width 0)) :height (float (.get height 0))}))

(defprotocol TextureP
  (bind [this])
  (unbind [this]))

(defrecord Texture [texture-id path width height]
  TextureP
  (bind [_]
    (GL46/glBindTexture GL46/GL_TEXTURE_2D texture-id))
  (unbind [_] 
    (GL46/glBindTexture GL46/GL_TEXTURE_2D 0)))

(defn create [path init-fn update-fn] 
  (let [tx-map (load-texture path)
        tx-id (:texture-id tx-map)
        width (:width tx-map)
        height (:height tx-map)
        tx-instance (->Texture tx-id path width height)]
    (component/create tx-instance init-fn update-fn)))
