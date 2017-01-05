(ns ip.fs
  #?(:cljs (:require [ip.impl :refer-macros [in-dir]]))
  (:use 
    [ip.impl :only [shell pathfile pathdir cwd mkdir cd dir-exists? #?@(:cljs [spit slurp])]]
    #?(:cljs [cljs.reader :only [read-string]])))

(def ns-rx #"^\W*\(\W*ns[^\(\[]+")
(def fn-rx #"(.+)\..*$")
(def linkx #"Qm.{44}")

(defn- forms [s] (read-string (str "(" s ")")))
(defn- ns? [form] (first (map #(= 'ns %) form)))

(defn- get-link [link] 
  (in-dir ".ipfs"
    (shell "ipfs" "get" link) 
    (slurp link)))

(defn fetch [sym s]
  (when-let [txt (get-link (str s))]
    (let [code (forms txt)
          file (pathfile sym)
          nsn  (last (re-find fn-rx file))]
        (in-dir (pathdir sym)
          (spit file 
            (if (ns? (first code))
                (clojure.string/replace txt ns-rx (str "(ns " nsn "\n  "))
                (str "(ns " nsn ")\n" txt)))))))

(defn pin [m] 
  (in-dir ".ipfs" 
    (spit "_" (prn-str m))
    (->> (shell "ipfs" "add" "./_") 
      :out str (re-find linkx))))