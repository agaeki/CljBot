(ns cljBot
  (:require clojure.contrib.string)
  (:require clojure.contrib.trace))

(defmacro dbg [& body]
  `(let [x# ~@body]
     (println (str "dbg: " (quote ~@body) "=" x#))
     x#))

(defn pong [x] -1)

(defn command-say [x] (let [y (seq (.split x " " 2))] (str "PRIVMSG " (nth y 0) " :" (nth y 1 "Hi!"))))

(defn command-join [x] (let [y (seq (.split x " " 2))] (str "JOIN " (nth y 1 ""))))

(defn command-quit [x] (let [y (seq (.split x " " 2))] (str "QUIT " (nth y 1 "I can't let you do thaHEY, NO FAIR"))))

(defn command-nick [x] (let [y (seq (.split x " " 2))] (str "NICK " (nth y 1 "cljBot"))))

(defn process-PRIVMSG [x] (let [msg (dbg (seq (.split x " " 3)))] (if (= (str (nth (str (nth msg 1)) 1)) "?")
                                                        (try (eval (read-string (str "(#'cljBot/command-" (.toLowerCase (.substring (nth msg 1) 2)) " \"" (nth msg 0) " " (nth msg 2)"\")"))) (catch Exception e (.printStackTrace e)))
                                                        (str "-1"))))