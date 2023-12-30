(ns move-files-module.core
  (:require [clojure.java.io :as io]
            [move-files-module.helpers :as helpers]))


(defn loop-first-level-subfolders [source-path target-path process-fn]
  (let [subfolders (->> (file-seq (io/file source-path))
                        (filter #(and (.isDirectory %)
                                      (.getParentFile %)    ;; Check if the parent is the source-path
                                      (.getCanonicalPath (.getParentFile %)) ;; Canonicalize the parent path
                                      (.equals source-path (.getCanonicalPath (.getParentFile %))))) ;; Compare with source-path
                        (remove #(clojure.string/starts-with? (-> % (.getName) (.toLowerCase)) "[done]"))
                        (map #(.getAbsolutePath %))
                        )]
    (doseq [subfolder subfolders]
      (println "will process:" subfolder)
      (process-fn subfolder target-path)
      (helpers/rename-folder subfolder)
      )))

(defn process-subfolder [source-folder target-folder]
  (let [current-author (helpers/get-current-folder source-folder)
        target-folder-with-author (str target-folder "/" current-author)]
    (if (helpers/folder-contains-subfolder? target-folder current-author)
      (let [files-from-author-folder (helpers/get-epub-files source-folder)]
        (doseq [file files-from-author-folder]
          (helpers/copy-file file target-folder-with-author)
          ))
      (do
        (println "on else")
        (helpers/create-directory-and-copy-files source-folder target-folder)
        (let [files-from-author-folder (helpers/get-epub-files source-folder)]
          (doseq [file files-from-author-folder]
            (helpers/copy-file file target-folder-with-author)
            ))))))