(ns app.test-cards
  (:require [reagent.core :as r]
            [devcards.core :as dc :refer [defcard deftest]]
            [cljs.test :include-macros true :refer [is]]
            ["@testing-library/react" :refer [render cleanup fireEvent]]
            [app.hello :refer [click-counter hello]]))

(defcard
  "This is a live interactive development environment using [Devcards](https://github.com/bhauman/devcards).
   You can use it to design, test, and think about parts of your app in isolation.
   
   The two 'cards' below show the two components in this app.")

(defcard hello-card
  (dc/reagent hello))