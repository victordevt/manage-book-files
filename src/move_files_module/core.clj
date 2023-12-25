(ns move-files-module.core
  (:require [move_files_module.helpers :as helpers]))

(let [directory "/Users/victorteianu/Documents/books/_packs/Lada.cu.cărţi.5217.cărţi.epub.în.limba.română"]
  (println (helpers/count-files directory)))

(let [directory "/Volumes/MacOSssd/Users/victorteianu/Documents/books/_packs/ebooks pack"]
  (println (helpers/count-files directory)))

(helpers/file-exists-in-directory? "Dama de pica" "/Users/victorteianu/Documents/books/_packs/Lada.cu.cărţi.5217.cărţi.epub.în.limba.română")

(helpers/file-contains-string "Pica" "/Users/victorteianu/Documents/books/_packs/Lada.cu.cărţi.5217.cărţi.epub.în.limba.română")

(helpers/epub-files-delta "/Users/victorteianu/Documents/books/_packs/Lada.cu.cărţi.5217.cărţi.epub.în.limba.română" "/Volumes/MacOSssd/Users/victorteianu/Documents/books/_packs/ebooks pack")

(helpers/count-files "/Users/victorteianu/Documents/books/_packs/Lada.cu.cărţi.5217.cărţi.epub.în.limba.română")


