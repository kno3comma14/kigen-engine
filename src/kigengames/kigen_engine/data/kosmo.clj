(ns kigengames.kigen-engine.data.kosmo
  (:require [nano-id.core :refer [nano-id]]))

(defprotocol EntityP
  (init [this name])
  (remove [this])
  (add-component [this kosmo-id]))

(defrecord Entity [id name enabled?]
 EntityP
 (init 
  [_ name]
  (let [id (nano-id)
        is-enabled? true]
    (->Entity id name is-enabled?)))
  (remove 
    [this]
    (assoc this :enabled false)))

(defprotocol ComponentP)

(defprotocol SystemP)

(defprotocol KosmoP
  (init [this])
  (create-entity [this components])
  (create-entity [this]))
