(ns move_files_module.helpers
  (:require [clojure.java.io :as io]
            [clojure.set]
            [clojure.string :as str]))

(defn extract-author-name [book-name]
  (if-let [matches (re-matches (re-pattern #"(.*?) - .*\.epub$") book-name)]
    (second matches)
    nil))

(defn extract-extension [file]
  (if-let [matches (re-matches #".*\.(\w+)$" file)]
    (second matches)
    nil))

(defn copy-file [source-path destination-path]
  (io/copy (io/file source-path) (io/file destination-path)))

(defn file-exists? [path]
  (.isFile (io/file path)))

(defn file-exists-in-directory? [filename directory]
  (some #(= filename (.getName %))
        (filter #(.isFile %) (file-seq (io/file directory)))))

(defn folder-contains-subfolder? [folder subfolder]
  (some #(and (.isDirectory %)
              (not (.isHidden %))
              (= subfolder (.getName %)))
        (file-seq (io/file folder))))

(defn file-contains-string? [substring directory]
  (some #(if (.isFile %)
           (let [file-name (.getName %)]
             (if (clojure.string/includes? file-name substring)
               file-name)))
        (file-seq (io/file directory))))

(defn create-directory [path]
  (let [dir (io/file path)]
    (.mkdir dir)
    dir))

;; part 2
(defn same-title? [title1 title2]
  (= (clojure.string/lower-case title1)
     (clojure.string/lower-case title2)))

; get list of files with the name ending in .epub from a directory, including subdirectories
; /Users/victorteianu/Documents/books/_packs/BEST Romanian ebooks PACK - ePub format-NoGrp/A/Albert Camus


;; create a function to copy file from source to target, if exists with the same filename, add suffix
(defn copy-file [source-file target-folder]
  (let [source-path (io/as-file source-file)
        target-path (io/as-file target-folder)
        target-file (io/file target-path (.getName source-path))
        target-file-exists? (.exists target-file)]
    (if target-file-exists?
      (let [source-file-size (.length source-path)
            target-file-size (.length target-file)]
        (if (not= source-file-size target-file-size)
          (let [target-file-name (.getName target-file)
                target-file-extension (.substring target-file-name (.lastIndexOf target-file-name "."))
                target-file-name-with-copy (str (.getName target-file) " (Copy)" target-file-extension)]
            (io/copy source-path (io/file target-path target-file-name-with-copy)))
          (io/copy source-path target-file)))
      (io/copy source-path target-file))))

(defn rename-folder [folder-path]
  (let [folder (io/as-file folder-path)
        parent-folder (io/file (.getParent folder) (str "[done] " (.getName folder)))]
    (.renameTo folder parent-folder)))

(defn get-current-folder [full-path]
  (->>
    (str/split full-path #"/")
    last))

; final function


(defn get-epub-files [directory]
  (->> (file-seq (io/file directory))
       (filter #(and (.isFile %) (.endsWith (str %) ".epub")))
       (map #(.getCanonicalPath %))
       ))

(defn create-directory-and-copy-files [source-directory destination-directory]
  (let [files (->> (io/file source-directory)
                   io/file
                   .listFiles
                   (filter #(re-matches #".* - .*\.epub$" (.getName %)))
                   (map #(.getName %)))]
    (doseq [file files]
      (let [source-file (io/file source-directory file)
            author-name (extract-author-name file)
            destination-dir (io/file destination-directory author-name)]
        (when-not (file-exists? destination-dir)
          (create-directory destination-dir))
        (let [destination-file (io/file destination-dir file)]
          (copy-file source-file destination-file))))))

;; Example usage
;(let [source-dir "/Users/victorteianu/Documents/books/_packs/Lada.cu.cărţi.5217.cărţi.epub.în.limba.română"
;      destination-dir "/Volumes/MacOSssd/Users/victorteianu/Documents/books/_packs/ebooks pack"]
;  (create-directory-and-copy-files source-dir destination-dir))


(defn count-files [path]
  (let [file-seq (file-seq (io/file path))
        files (filter #(and (.isFile %) (.endsWith (str %) ".epub")) file-seq)
        file-names (map #(.getName %) files)]
    {:count (count files)
     :files file-names}
    (count files)))

(.isDirectory (io/file "/Users/victorteianu/Documents/books/_packs/BEST Romanian ebooks PACK - ePub format-NoGrp/V/Victor Kernbach/Victor Kernbach - Luntrea Sublima/Victor Kernbach - Luntrea Sublima.epub"))

(defn epub-files-delta [source-dir target-dir]
  (let [source-files (->> (file-seq (io/file source-dir))
                          (filter #(and (.isFile %) (.endsWith (str %) ".epub")))
                          (map #(.getName %))
                          set)
        target-files (->> (file-seq (io/file target-dir))
                          (filter #(and (.isFile %) (.endsWith (str %) ".epub")))
                          (map #(.getName %))
                          set)
        differing-files (clojure.set/difference source-files target-files)]
    (println "source nr files:" (count source-files))
    (println "target nr files:" (count target-files))

    (doseq [file-name differing-files]
      (println file-name))

    (println "Number of differing files:" (count differing-files))))