(ns cljs-auto.util.hawk
  (:require
   [clojure.string :as string]))

(defn edn-file? [e]
  (-> (:file e)
      (.getName)
      (string/ends-with? ".edn")))

(defn cljs-file? [e]
  (-> (:file e)
      (.getName)
      (string/ends-with? ".cljs")))

(defn created? [e]
  (= :create (:kind e)))
