(ns cljs-auto.core
  (:gen-class)
  (:require
   [cljs-auto.requires :refer [requires]]
   [cljs-auto.util.hawk :as util.hawk]
   [cljs-auto.cli]
   [clojure.java.io :as io]
   [clojure.tools.cli :refer [parse-opts]]
   [cljs-auto.edn]
   [comb.template :as template ]
   [hawk.core :as hawk]))

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

(defn full-process [options]
  (when (:edn-path options)
    (binding [*print-namespace-maps* false
              *print-meta* true]
      (-> (cljs-auto.edn/read-edn-files options)
          (output-config options)))))

(defn compile? [e]
  (or (util.hawk/edn-file? e)
      (and (util.hawk/cljs-file? e)
           (util.hawk/created? e))))

(defn hawk-handler [options _ctx e]
  (when (compile? e)
    (try (full-process options)
         (catch Exception e
           (println (.getMessage e))))))

(defn watch-files [options]
  (hawk/watch!
   [{:paths
     (remove nil?
             [(:edn-path options)
              (:cljs-path options)])
     :handler (partial hawk-handler options)}]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [{:keys [errors options]} (parse-opts args cljs-auto.cli/options)]
    (cljs-auto.cli/validate-errors errors)
    (full-process options)
    (when (:watch options)
      (println "Starting edn-cljs Watcher...")
      (watch-files options))))
