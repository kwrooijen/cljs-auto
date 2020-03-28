(ns cljs-auto.core
  (:gen-class)
  (:require
   [clojure.string :as string]
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [clojure.tools.cli :refer [parse-opts]]
   [comb.template :as template ]
   [hawk.core :as hawk]))


(defn dir? [v]
  (.isDirectory v))

(defn dir-exists? [v]
  (dir? (io/file v)))

(def cli-options
  [ ;; TODO
   ;; ["-m" "--merge" "Merge configs"]
   ;; ["-M" "--meta-merge" "Meta merge configs"]
   ;; ["-c" "--config" "Specifiy a edn-cljs config file"]

   ["-o" "--output PATH" "Output file"
    :missing "An output file must be specified using the `-o` option."]

   ["-p" "--path PATH" "Root path of the EDN files"
    :validate [dir-exists? "Directory does not exist"]
    :missing "An output directory must be specified using the `-p` option."]

   ["-ns" "--namespace NAMESPACE" "Namespace of the generated cljs file"
    :default "edn-cljs.generated.config"]

   ["-w" "--watch" "Watch PATH to see if any files change and process them."]

   [nil "--integrant" "Use Integrant readers (ig/ref ig/refset)"]

   ["-h" "--help"]])

(defn files-in [dir]
  (->> (io/file dir)
       (tree-seq dir? #(.listFiles %))
       (remove dir?)
       (map #(.getPath %))))

(def require-integrant
  "[integrant.core :as ig]")

(def readers-integrant
  {'ig/ref    (fn [v] `(~'ig/ref ~v))
   'ig/refset (fn [v] `(~'ig/refset ~v))})

(defn readers
  [options]
  (merge (when (:integrant options) readers-integrant)))

(defn requires
  [options]
  (remove nil?
          [(when (:integrant options) require-integrant)]))

(defn read-edn-files [options]
  (->> (files-in (:path options))
       (filter #(string/ends-with? % ".edn"))
       (map slurp)
       (map (partial edn/read-string {:readers (readers options)}))
       (apply merge)))

(defn template-opts [config options]
  {:requires (requires options)
   :config (pr-str config)
   :namespace (:namespace options)})

(defn process-template
  [config options]
  (-> (io/resource "template_cljs.cljs")
      (slurp)
      (template/eval (template-opts config options))))

(defn output-config [config options]
  (spit (:output options)
        (process-template config options)))

(defn validate-errors [errors]
  (when errors
    (println "edn-cljs: Failed to start\n")
    (doseq [error errors]
      (println "  " error))
    (System/exit 1)))

(defn full-process [options]
  (binding [*print-namespace-maps* false
            *print-meta* true]
    (-> (read-edn-files options)
        (output-config options))))

(defn watch-files [options]
  (hawk/watch!
   [{:paths [(:path options)]
     :handler (fn [ctx _e]
                (try
                  (full-process options)
                  (catch Exception e
                    (println (.getMessage e))))
                ctx)}]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [{:keys [errors options]} (parse-opts args cli-options)]
    (validate-errors errors)
    (full-process options)
    (when (:watch options)
      (println "Starting edn-cljs Watcher...")
      (watch-files options))))
