(ns kigengames.kigen-engine.rendering.shader-processor
  (:require [clojure.string :as s]))

(defn extract-shader-programs
  [target]
  (let [first-index (+ (s/index-of target "#type") 6)
        first-eol (s/index-of target "\n")
        first-clue (= "vertex" (subs target first-index first-eol))
        raw-programs (s/split target #"(#type)( )+([a-zA-Z]+)")
        n-components (count raw-programs)]
    (when (= n-components 3)
      (if first-clue
        {:vertex (nth raw-programs 1)
         :fragment (nth raw-programs 2)}
        {:vertex (nth raw-programs 2)
         :fragment (nth raw-programs 1)}))))

