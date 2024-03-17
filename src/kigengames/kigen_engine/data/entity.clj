(ns kigengames.kigen-engine.data.entity
  (:require [kigengames.kigen-engine.data.component :as component]
            [nano-id.core :refer [nano-id]]))

(defprotocol EntityP 
  (disable [this])
  (add-components [this components]))

(defrecord Entity [id name enabled backpack]
  EntityP 
  (disable
    [this]
    (assoc this :enabled false))
  
  (add-components [this components] ;; Addition of validity pending
    (let [updated-backpack (if (sequential? components)
                             (reduce (fn [acc, item] (conj acc item))
                                     backpack
                                     components)
                             (conj backpack components))]
      (assoc this :backpack updated-backpack))))

(defn create
  [name]
  (let [id (nano-id)
        is-enabled? true]
    (->Entity id name is-enabled? (vector))))