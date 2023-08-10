(ns kigengames.kigen-engine.data.entity
  (:require [kigengames.kigen-engine.data.component :as component]
            [nano-id.core :refer [nano-id]]))

(defprotocol EntityP
  (init [this])
  (disable [this])
  (add-components [this components]))

(defrecord Entity [id name enabled? backpack]
  EntityP
  (init
    [_]
    (let [id (nano-id)
          is-enabled? true]
      (->Entity id name is-enabled? (vector))))

  (disable
    [this]
    (assoc this :enabled false))
  
  (add-components [this components]
    (let [is-list? (component/study-components components)
          updated-backpack (if is-list?
                             (reduce (fn [acc, item] (conj acc item))
                                     backpack
                                     components)
                             (conj backpack components))]
      (assoc this :backpack updated-backpack))))