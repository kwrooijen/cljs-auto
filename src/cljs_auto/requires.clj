(ns cljs-auto.requires
  (:require
   [cljs-auto.extension.integrant :as extension.integrant]
   [clojure.edn :as edn]
   [clojure.string :as string]
   [cljs-auto.util.file :refer [files-in]]))

(defn file-ns
  [file]
  (-> (slurp file)
      (edn/read-string)
      (second)))

(defn cljs-requires [options]
  (if (:cljs-path options)
    (->> (files-in (:cljs-path options))
         (filter #(string/ends-with? % ".cljs"))
         (mapv (comp vector file-ns)))
    []))

(defn requires
  [options]
  (->> (cljs-requires options)
       (concat (when (:integrant options)
                 extension.integrant/requires))
       (remove nil?)))
