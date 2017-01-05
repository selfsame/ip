(ns ip.impl
  (:import  #?(:cljr (System.IO Directory Path)
               :cljs (goog.string StringBuffer)))
  (:require #?(:cljr [clojure.clr.shell :as shell]
               :cljs [cljs.nodejs :as nodejs])))

#?(:cljs (def fs   (nodejs/require "fs")))
#?(:cljs (def path (nodejs/require "path")))
#?(:cljs (def process (nodejs/require "process")))
#?(:cljs (def exec (nodejs/require "child_process")))

(def shell #?(:cljr shell/sh
              :cljs (fn [& args] {:out (.toString (exec.execSync (reduce #(str %1 " " %2) args)))})))

(def pathdir  #?(:cljr #(Path/GetDirectoryName %)
                 :cljs path.dirname))
(def pathfile #?(:cljr #(Path/GetFileName %)
                 :cljs path.basename))
(def pathcat  #?(:cljr #(Path/Combine %1 %2)
                 :cljs path.join))
(def cd       #?(:cljr #(Directory/SetCurrentDirectory %)
                 :cljs process.chdir))
(def mkdir  #?(:cljr #(Directory/CreateDirectory %)
                 :cljs #(fs.mkdirSync % console.log)))
(def cwd      #?(:cljr #(Directory/GetCurrentDirectory %)
                 :cljs process.cwd))
(def dir-exists? #?(:cljr (fn [s] true)
                :cljs (fn [s] (fs.existsSync (pathcat (cwd) s)))))

(defmacro in-dir [dir & body]
 `(let [cwd# (cwd)]
    (try (if-not (dir-exists? ~dir) (mkdir ~dir)) 
         (cd ~dir) ~@body
         (finally (cd cwd#)))))

#?(:cljs (defn spit [f s] (fs.writeFileSync f (str s))))
#?(:cljs (defn slurp [f] (.toString (fs.readFileSync f))))