(ns kigengames.kigen-engine.data.validation
  (:require [malli.core :as m])
  (:import (org.joml Vector2f)))

(def transform-schema
  [:and 
   [:map 
    [:position any?] 
    [:scale any?]] 
   [:fn (fn [{:keys [position scale]}] 
          (and (= (type position) Vector2f)
               (= (type scale) Vector2f)))]])

(defn validate-transform 
  [input-transform]
  (m/validate transform-schema input-transform))

