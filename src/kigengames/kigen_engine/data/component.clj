(ns kigengames.kigen-engine.data.component
  (:require [nano-id.core :refer [nano-id]]))

(defprotocol ComponentP 
  (init [this])
  (process [this dt]))

(defrecord Component [instance init-fn update-fn]
  ComponentP 
  (init [this]
    (init-fn this))
  (process [this dt]
    (update-fn this dt)))

(defn create 
  [instance init-fn update-fn]
  (->Component instance init-fn update-fn))

(defn study-components
  [target] 
  (if (sequential? target)
    (every? (fn [c] (instance? Component c)) target)
    (instance? Component target)))