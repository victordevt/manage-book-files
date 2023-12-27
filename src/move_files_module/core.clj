(ns move-files-module.core
  (:require [clojure.java.io :as io]
            [move_files_module.helpers :as helpers]))


(defn process-first-level-subfolders [source-path target-path process-fn]
  (let [subfolders (->> (file-seq (io/file source-path))
                        (filter #(and (.isDirectory %)
                                      (.getParentFile %) ;; Check if the parent is the source-path
                                      (.getCanonicalPath (.getParentFile %)) ;; Canonicalize the parent path
                                      (.equals source-path (.getCanonicalPath (.getParentFile %))))) ;; Compare with source-path
                        (map #(.getAbsolutePath %)))]
    (doseq [subfolder subfolders]
      (process-fn subfolder target-path))))

(defn process-subfolder [source-folder target-folder]
  (println "Processing subfolder:" source-folder))

(process-first-level-subfolders "/Users/victorteianu/Documents/books/_packs/BEST Romanian ebooks PACK - ePub format-NoGrp/V" "/Users/victorteianu/Documents/books/_packs/ebooks pack)" process-subfolder)


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
