(ns kigengames.kigen-engine.util.buffer
  (:import (java.nio ByteBuffer ByteOrder)))

(defn float-array->float-buffer
  [target]
  (let [t-array (float-array target)
        byte-buffer (ByteBuffer/allocateDirect (* 4 (count target)))
        _ (.order byte-buffer (ByteOrder/nativeOrder))
        float-buffer (.asFloatBuffer byte-buffer)
        _ (.put float-buffer t-array)
        _ (.position float-buffer 0)]
    float-buffer)) ;; candidate to refactor - move to another namespace

(defn int-array->int-buffer
  [target]
  (let [t-array (int-array target)
        byte-buffer (ByteBuffer/allocateDirect (* 4 (count target)))
        _ (.order byte-buffer (ByteOrder/nativeOrder))
        int-buffer (.asIntBuffer byte-buffer)
        _ (.put int-buffer t-array)
        _ (.position int-buffer 0)]
    int-buffer))