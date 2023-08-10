(ns kigengames.kigen-engine.data.kosmo
  (:require [kigengames.kigen-engine.data.entity :as entity]
            [kigengames.kigen-engine.data.component :as component]
            [nano-id.core :refer [nano-id]]))

(defn- create-pairs [colls]
  (vec
   (map vec (if (empty? colls)
              '(())
              (for [more (create-pairs (rest colls))
                    x (first colls)]
                (cons x more))))))

(defn- filter-components-by-types 
  [input-types content] 
  (let [cmp-pairs (create-pairs [input-types content])
        values (map second (filter (fn [x] (= (type (:instance (second x))) (first x))) cmp-pairs))]
    (vec values)))

(defn filter-entities-by-types
  [input-types entities] 
  (reduce (fn [acc, item] (when (seq (filter-components-by-types input-types (:backpack item)))
                            (conj acc item)))
          []
          entities))

(defprotocol KosmoP 
  (create [this new-entities]) 
  (create-entity [this name] [this name components])
  (find-entities-with [this type-list]))

(defrecord Kosmo [entities]
 KosmoP
 (create [this new-entities]
  (if (empty? new-entities)
    (assoc this :entities (vector))
    (let [old-entities entities]
      (->Kosmo (vec (concat old-entities new-entities))))))
  
  (create-entity [this name]
    (let [old-entities entities
          new-entity (.init (entity/->Entity nil name true []))]
      (assoc this :entities (conj old-entities new-entity))))
  
  (create-entity [this name components]
    (let [old-entities entities
          new-entity (.init (entity/->Entity nil name true []))
          entity-with-components (.add-components new-entity components)]
      (assoc this :entities (conj old-entities entity-with-components))))
  
  (find-entities-with [this type-list]
    (filter-entities-by-types type-list entities)))


(comment
  (def sub-component-a "a")
  (def sub-component-b "b")
  (def component-a (atom (component/->Component nil sub-component-a)))
  (def component-b (atom (component/->Component nil sub-component-b)))
  (reset! component-a (.create @component-a))
  (reset! component-b (.create @component-b))
  
  (def entity-a (atom (entity/->Entity nil "entity-a" true nil)))
  (reset! entity-a (.init @entity-a))
  (def entity-b (atom (entity/->Entity nil "entity-b" true nil)))
  (reset! entity-b (.init @entity-b))

  )