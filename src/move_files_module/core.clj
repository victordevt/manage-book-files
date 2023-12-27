(ns move-files-module.core
  (:require [clojure.java.io :as io]
            [move_files_module.helpers :as helpers]))


(defn loop-first-level-subfolders [source-path target-path process-fn]
  (let [subfolders (->> (file-seq (io/file source-path))
                        (filter #(and (.isDirectory %)
                                      (.getParentFile %)    ;; Check if the parent is the source-path
                                      (.getCanonicalPath (.getParentFile %)) ;; Canonicalize the parent path
                                      (.equals source-path (.getCanonicalPath (.getParentFile %))))) ;; Compare with source-path
                        (map #(.getAbsolutePath %)))]
    (doseq [subfolder subfolders]
      (process-fn subfolder target-path)
      (helpers/rename-folder subfolder)
      )))

(defn process-subfolder [source-folder target-folder]
  (let [current-author (helpers/get-current-folder source-folder)
        target-folder-with-author (str target-folder "/" current-author)]
    (when (helpers/folder-contains-subfolder? target-folder current-author)
      ;(println (str "found: " current-author "target folder with author: " target-folder-with-author))
      (println "source folder:" source-folder)
      (let [files-from-author-folder (helpers/get-epub-files source-folder)]
        (doseq [file files-from-author-folder]
          (helpers/copy-file file target-folder-with-author)
          )))))

; todo: check for equal filenames, without caps
(loop-first-level-subfolders "/Users/victorteianu/Documents/books/_packs/BEST Romanian ebooks PACK - ePub format-NoGrp/B" "/Users/victorteianu/Documents/books/_packs/ebooks pack" process-subfolder)

(helpers/folder-contains-subfolder? "/Users/victorteianu/Documents/books/_packs/ebooks pack" "Albert Camus")

(helpers/rename-folder "/Users/victorteianu/Documents/books/_packs/ebooks pack/[done]_test")
(helpers/get-epub-files "/Users/victorteianu/Documents/books/_packs/BEST Romanian ebooks PACK - ePub format-NoGrp/A")

(helpers/copy-file "/Users/victorteianu/Documents/books/_packs/BEST Romanian ebooks PACK - ePub format-NoGrp/V/Victor Kernbach/Victor Kernbach - Luntrea Sublima/Victor Kernbach - Luntrea Sublima.epub" "/Users/victorteianu/Documents/books/_packs/ebooks pack/_test")

;(let [directory "/Users/victorteianu/Documents/books/_packs/Lada.cu.cărţi.5217.cărţi.epub.în.limba.română"]
;  (println (helpers/count-files directory)))
;
;(let [directory "/Volumes/MacOSssd/Users/victorteianu/Documents/books/_packs/ebooks pack"]
;  (println (helpers/count-files directory)))
;
;(helpers/file-exists-in-directory? "Dama de pica" "/Users/victorteianu/Documents/books/_packs/Lada.cu.cărţi.5217.cărţi.epub.în.limba.română")
;
;(helpers/file-contains-string? "Pica" "/Users/victorteianu/Documents/books/_packs/Lada.cu.cărţi.5217.cărţi.epub.în.limba.română")
;
;(helpers/epub-files-delta "/Users/victorteianu/Documents/books/_packs/Lada.cu.cărţi.5217.cărţi.epub.în.limba.română" "/Volumes/MacOSssd/Users/victorteianu/Documents/books/_packs/ebooks pack")
;
;(helpers/count-files "/Users/victorteianu/Documents/books/_packs/Lada.cu.cărţi.5217.cărţi.epub.în.limba.română")
;
;(helpers/copy-file "/Users/victorteianu/Documents/books/_packs/BEST Romanian ebooks PACK - ePub format-NoGrp/A/A.S. Puskin/A.S. Puskin - Dama de pica/A.S. Puskin - Dama de pica.epub" "/path/to/target/file.txt")
;
;
