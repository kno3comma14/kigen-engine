(ns kigengames.kigen-engine.data.kosmo
  (:require [nano-id.core :refer [nano-id]]))

(defprotocol ComponentP
  (create [this]))

(defrecord Component [id instance]
  ComponentP
  (create 
    [_]
    (let [new-id (nano-id)]
      (->Component new-id instance))))

(defn- create-pairs [colls]
  (vec
   (map vec (if (empty? colls)
              '(())
              (for [more (create-pairs (rest colls))
                    x (first colls)]
                (cons x more))))))

(defn- study-components
  [target]
  (let [is-list? (instance? clojure.lang.PersistentVector target)
        is-valid? (if (not is-list?)
                    (instance? Component target)
                    (every? (fn [c] (instance? Component c)) target))]
    (if is-valid?
      is-list?
      (throw (Exception. "Entities can save component or vectors of components")))))

(defn- filter-components-by-types 
  [input-types content]
  (let [cmp-pairs (create-pairs [input-types content])
        values (map second (filter (fn [x] (= (type (second x)) (first x))) cmp-pairs))]
    (vec values)))

(defn filter-entities-by-types
  [input-types entities]
  (reduce (fn [acc, item] (when (seq(filter-components-by-types input-types item))
                            (conj acc item)))
          []
          entities))

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
    (let [is-list? (study-components components)
          updated-backpack (if is-list?
                             (reduce (fn [acc, item] (conj item acc))
                                     backpack
                                     components)
                             (conj backpack components))]
      (assoc this :backpack updated-backpack))))

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
          new-entity (.init (->Entity nil name true []))]
      (assoc this :entities (conj old-entities new-entity))))
  
  (create-entity [this name components]
    (let [old-entities entities
          new-entity (.init (->Entity nil name true []))
          entity-with-components (.add-components new-entity components)]
      (assoc this :entities (conj old-entities entity-with-components))))
  
  (find-entities-with [this type-list]
    (filter-entities-by-types type-list entities)))
