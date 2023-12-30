(ns move-files-module.helpers
  (:require [clojure.java.io :as io]
            [clojure.set]
            [clojure.string :as str]))


(defn get-current-folder [full-path]
  (->>
    (str/split full-path #"/")
    last))

(defn file-exists? [path]
  (.isFile (io/file path)))

(defn create-directory [path]
  (let [dir (io/file path)]
    (.mkdir dir)
    dir))

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
                target-file-name-without-extension (.substring target-file-name 0 (.lastIndexOf target-file-name "."))
                target-file-extension (.substring target-file-name (.lastIndexOf target-file-name "."))
                target-file-name-with-copy (str target-file-name-without-extension " (Copy)" target-file-extension)]
            (println "target file name" target-file-name)
            (println "target file name without extension" target-file-name-without-extension)
            (println "target file name with copy" target-file-name-with-copy)
            (println "target file extension" target-file-extension)
            (io/copy source-path (io/file target-path target-file-name-with-copy))

            )
          (io/copy source-path target-file)))
      (io/copy source-path target-file))))
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
                   (map #(.getName %)))
        author-name (get-current-folder source-directory)
        destination-dir (io/file destination-directory author-name)]
    (when-not (file-exists? destination-dir)
      (println "destination dir to create: " destination-dir)
      (create-directory destination-dir))
    (println files)
    (doseq [file files]
      (let [source-file (io/file source-directory file)]
        (let [destination-file (io/file destination-dir file)]
          (copy-file source-file destination-file))))))

(defn rename-folder [folder-path]
  (let [folder (io/as-file folder-path)
        parent-folder (io/file (.getParent folder) (str "[done] " (.getName folder)))]
    (.renameTo folder parent-folder)))

(defn folder-contains-subfolder? [folder subfolder]
  (some #(and (.isDirectory %)
              (not (.isHidden %))
              (= subfolder (.getName %)))
        (file-seq (io/file folder))))

(defn file-exists-in-directory? [filename directory]
  (some #(= filename (.getName %))
        (filter #(.isFile %) (file-seq (io/file directory)))))
(folder-contains-subfolder? "/Users/victorteianu/Documents/books/_packs/ebooks pack" "Albert Camus")

(defn file-contains-string? [substring directory]
  (some #(if (.isFile %)
           (let [file-name (.getName %)]
             (if (clojure.string/includes? file-name substring)
               file-name)))
        (file-seq (io/file directory))))

(defn same-title? [title1 title2]
  (= (clojure.string/lower-case title1)
     (clojure.string/lower-case title2)))

(defn check-file-exists [filename path]
  (let [lowercase-filename (.toLowerCase filename)
        files (io/file path)]
    (some #(= lowercase-filename (.toLowerCase (name %)))
          files)))

(defn test-get-name [source-path]
  (let [name (.getName source-path)]
    (println name)))

(defn extract-author-name [book-name]
  (if-let [matches (re-matches (re-pattern #"(.*?) - .*\.epub$") book-name)]
    (second matches)
    nil))

(defn extract-extension [file]
  (if-let [matches (re-matches #".*\.(\w+)$" file)]
    (second matches)
    nil))

(defn count-files [path]
  (let [file-seq (file-seq (io/file path))
        files (filter #(and (.isFile %) (.endsWith (str %) ".epub") (not (clojure.string/includes? (str %) "(Copy)"))) file-seq)
        file-names (map #(.getName %) files)]
    {:count (count files)
     :files file-names}
    (count files)))

(defn epub-files-delta [source-dir target-dir]
  (let [source-files (->> (file-seq (io/file source-dir))
                          (filter #(and (.isFile %) (.endsWith (str %) ".epub")))
                          (map #(.getName %))
                          set)
        target-files (->> (file-seq (io/file target-dir))
                          (filter #(and (.isFile %) (.endsWith (str %) ".epub")))
                          (map #(.getName %))
                          set)
        lowercase-source (set (map clojure.string/lower-case source-files))
        lowercase-target (set (map clojure.string/lower-case target-files))

        differing-files (clojure.set/difference lowercase-source lowercase-target)]
    (println "source nr files:" (count lowercase-source))
    (println "target nr files:" (count lowercase-target))

    (doseq [file-name (sort differing-files)]
      (println file-name))

    (println "Number of differing files:" (count differing-files))))
