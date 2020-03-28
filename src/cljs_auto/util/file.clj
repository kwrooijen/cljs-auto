(ns cljs-auto.util.file
  (:require
   [clojure.java.io :as io]))

(defn dir? [v]
  (.isDirectory v))

(defn dir-exists? [v]
  (dir? (io/file v)))

(defn files-in [dir]
  (->> (io/file dir)
       (tree-seq dir? #(.listFiles %))
       (remove dir?)
       (map #(.getPath %))))
