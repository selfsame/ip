(ns ip.impl
  (:import (System.IO Directory Path))
  (:require [clojure.clr.shell :as shell]))

(def shell shell/sh)

(def pathdir  #(Path/GetDirectoryName %))
(def pathfile #(Path/GetFileName %))
(def pathcat  #(Path/Combine %1 %2))
(def setdir   #(Directory/SetCurrentDirectory %))
(def makedir  #(Directory/CreateDirectory %))
(def getdir   #(Directory/GetCurrentDirectory))

(defmacro in-dir [dir & body]
 `(let [cwd# (getdir)]
    (try (makedir ~dir) (setdir ~dir) ~@body
         (finally (setdir cwd#)))))