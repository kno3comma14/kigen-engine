(ns kigengames.kigen-engine.data.component
  (:require [nano-id.core :refer [nano-id]]))

(defprotocol ComponentP
  (create [this]))

(defrecord Component [id instance]
  ComponentP
  (create
    [_]
    (let [new-id (nano-id)]
      (->Component new-id instance))))

(defn study-components
  [target]
  (let [is-list? (instance? clojure.lang.PersistentVector target)
        is-valid? (if (not is-list?)
                    (instance? Component target)
                    (every? (fn [c] (instance? Component c)) target))]
    (if is-valid?
      is-list?
      (throw (Exception. "Entities can save component or vectors of components")))))