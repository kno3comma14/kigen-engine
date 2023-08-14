(ns kigengames.kigen-engine.rendering.renderer
  (:require [kigengames.kigen-engine.rendering.batch-renderer :as br]))

(def ^:private ^:const max-batch-size 1000)

(defprotocol RendererP
  (add-drawable [this target])
  (render [this]))

(defrecord Renderer [batches]
  RendererP
  (add-drawable 
    [_ target] 
    (let [added (atom false)
          batch (first (filter (fn [item] @(:has-capacity item)) @batches))]
      (when (not= batch nil) 
        (.add-sprite batch target)
        (reset! added true)) 
      (when (not @added)
        (let [new-br (br/create max-batch-size)] 
          (.start new-br)
          (.add-sprite new-br target)
          (swap! batches conj new-br)))))

  (render [_]
    (reduce (fn [_, item] 
              (.render item)) 
            nil 
            @batches)))

(defn create []
  (let [batches (atom [])]
    (->Renderer batches)))



