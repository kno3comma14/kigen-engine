(ns kigengames.kigen-engine.rendering.renderer
  (:require [kigengames.kigen-engine.rendering.batch-renderer :as br]))

(def ^:private ^:const max-batch-size 1000)

(defprotocol RendererP
  (add-drawable [this target])
  (render [this dt]))

(defrecord Renderer [batches]
  RendererP
  (add-drawable
    [_ comp_target]
    (let [added (atom false)
          batch (first (filter (fn [item] @(:has-capacity item)) @batches))
          target (:instance comp_target)]
      (when (not= batch nil)
        (.add-sprite batch target)
        (reset! added true))
      (when (not @added)
        (let [new-br (br/create max-batch-size)]
          (.start new-br)
          (.add-sprite new-br target)
          (swap! batches conj new-br)))))

  (render [_ dt]
    (reduce (fn [_, item]
              (if (empty? @(:textures item))
                (.render item)
                (let [sprite-list (filter (fn [s] (not (nil? s))) @(:sprites item))]
                  (reduce (fn [acc, _]
                            (let [sprite-renderer (nth sprite-list acc)
                                  new-sprite-renderer (.process sprite-renderer sprite-renderer dt)]
                              (swap! (:sprites item) assoc acc new-sprite-renderer)
                              (.render item))
                            (inc acc))
                          0
                          sprite-list))))
            nil
            @batches)))

(defn create []
  (let [batches (atom [])]
    (->Renderer batches)))



