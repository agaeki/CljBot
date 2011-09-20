(ns cljBot)

(defn process-PRIVMSG [x] (let [msg (.split x " " 3)] (if (and (= (str (nth (nth msg 1) 1)) "?") (= (str (nth msg 1)) ":?say"))
                                                        (str "PRIVMSG " (nth msg 0) " :" (nth msg 2))
                                                        (str "-1"))))

(defn pong [x] -1)