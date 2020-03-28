(ns cljs-auto.edn
  (:require
   [clojure.string :as string]
   [clojure.edn :as edn]
   [cljs-auto.util.file :refer [files-in]]
   [cljs-auto.extension.integrant :as extension.integrant]))

(defn readers
  [options]
  (merge (when (:integrant options) extension.integrant/readers)))

(defn read-edn-files [options]
  (->> (files-in (:edn-path options))
       (filter #(string/ends-with? % ".edn"))
       (map slurp)
       (map (partial edn/read-string {:readers (readers options)}))
       (apply merge)))
