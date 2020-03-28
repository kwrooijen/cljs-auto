(ns cljs-auto.extension.integrant)

(def requires
  '[[integrant.core :as ig]])

(def readers
  {'ig/ref    (fn [v] `(~'ig/ref ~v))
   'ig/refset (fn [v] `(~'ig/refset ~v))})
