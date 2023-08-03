(ns kigengames.kigen-engine.util.file
  (:require [clojure.java.io :as io]))

(defn get-content 
  [file-path]
  (let [data (io/resource file-path)]
    (slurp data)))