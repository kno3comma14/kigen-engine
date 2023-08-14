(ns kigengames.kigen-engine.data.component
  (:require [nano-id.core :refer [nano-id]]))

(defprotocol ComponentP 
  (init [this])
  (process [this dt]))

(defrecord Component [id instance init-fn update-fn]
  ComponentP 
  (init [this]
    (init-fn this))
  (process [this dt]
    (update-fn this dt)))

(defn create 
  [instance init-fn update-fn]
  (->Component (nano-id) instance init-fn update-fn))

(defn study-components
  [target]
  (let [is-list? (instance? clojure.lang.PersistentVector target)
        is-valid? (if (not is-list?)
                    (instance? Component target)
                    (every? (fn [c] (instance? Component c)) target))]
    (if is-valid?
      is-list?
      (throw (Exception. "Entities can save component or vectors of components")))))