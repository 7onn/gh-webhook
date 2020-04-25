(ns webhook.core-test
  (:use midje.sweet)
  (:use [webhook.core])
)

(facts "about `-main`"
  (fact "it normally returns 1"
    (-main) => 1
  )
)