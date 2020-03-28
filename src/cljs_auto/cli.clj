(ns cljs-auto.cli
  (:require [cljs-auto.util.file :refer [dir-exists?]]))

(def options
  [ ;; TODO
   ;; ["-m" "--merge" "Merge configs"]
   ;; ["-M" "--meta-merge" "Meta merge configs"]
   ;; ["-c" "--config" "Specifiy a cljs-auto config file"]

   ["-o" "--output PATH" "Output file"
    :missing "An output file must be specified using the `-o` option."]

   ["-p" "--edn-path PATH" "Root path of EDN files to merge"
    :validate [dir-exists? "Directory does not exist"]]

   ["-P" "--cljs-path PATH" "Root path of Clojurescript files to require"
    :validate [dir-exists? "Directory does not exist"]]

   ["-ns" "--namespace NAMESPACE" "Namespace of the generated cljs file"
    :default "cljs-auto.generated.config"]

   ["-w" "--watch" "Watch PATH to see if any files change and process them."]

   [nil "--integrant" "Use Integrant readers (ig/ref ig/refset)"]

   ["-h" "--help"]])

(defn show-help [{:keys [options summary]}]
  (when (:help options)
    (println summary)
    (System/exit 0)))

(defn validate-errors [errors]
  (when errors
    (println "cljs-auto: Failed to start\n")
    (doseq [error errors]
      (println "  " error))
    (System/exit 1)))
