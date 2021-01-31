(ns app.app
  (:require [reagent.core :as r])
  (:require [clojure.string  :as string]))

(def state
  (r/atom {:target nil
           :digits nil
           :result nil
           :include {1 true
                     2 true
                     3 true
                     4 true
                     5 true
                     6 true
                     7 true
                     8 true
                     9 true}
           :repeat-digits false}))

(defn docalc [target acc digits possible repeat-digits]
  (let [sum (reduce + acc)]
    (cond
      (and (= digits 0) (= sum target)) #{(sort acc)}
      (or (> sum target) (= digits 0)) #{}
      :else (let [result (atom #{})]
              (doseq [d possible]
                (let [newAcc (conj acc d)
                      res (docalc target newAcc (dec digits) (if repeat-digits possible (remove #(= % d) possible)) repeat-digits)]
                  (swap! result #(set (concat % res)))))
              @result))))

(defn calc [target digits available repeat-digits]
  (docalc target [] digits available repeat-digits))

(defn calculate []
  [:button.fond-semibold.mt-5.bg-blue-400.px-2.py-1.rounded-lg.text-white.w-52
   {:on-click
    #(swap! state assoc
            :result (calc
                     (:target @state)
                     (:digits @state)
                     (->> (:include @state)
                          (filter (fn [[_ selected]] selected))
                          (map (fn [[digit _]] digit)))
                     (:repeat-digits @state)))}
   :Calculate])

(defn styled-input [props]
  (let [added {:class [:text-center :px-2 :py-1 :rounded-lg :w-52 :focus-ring-2 :focus-ring-blue-400]
               :on-focus #(-> % .-target .select)}]
    (into [:input] (list (into props added)))))

(defn update-num [state key]
  (fn [event]
    (let [n (-> event .-target .-value js/Number)]
      (when-not (js/isNaN n)
        (swap! state assoc key n)))))

(defn target []
  [styled-input {:value (:target @state)
                 :on-change (update-num state :target)}])

(defn digits []
  [styled-input
   {:value (:digits @state)
    :on-change (update-num state :digits)}])

(defn num [n on toggle]
  [:button
   {:on-click toggle
    :class [:font-semibold :font-sans
            :text-white :mx-1 :w-10 :h-10 :rounded-full :w-4
            :flex :justify-center :items-center :px-3
            (if on :bg-blue-400 :bg-gray-400)]}
   n])

(defn include []
  [:div.flex.justify-start
   (->> (:include @state)
        (map (fn [[key val]]
               ^{:key key} [num  key val #(swap! state assoc-in [:include  key] (not val))])))])

(defn label [text]
  [:label.font-medium.mt-3.mb-1.text-gray-500 text])

(defn result-item [result]
  [:p.text-2xl.flex.justify-center
   (string/join "-" (seq result))])

(defn result []
  [:div.mt-3
   (when-let [result (:result @state)]
     [:<>
      [label :Result]
      (map-indexed (fn [i r] ^{:key i} [result-item r]) result)])])

(defn toggle [val on-toggle]
  [:div.w-14.h-8.flex.items-center.rounded-full.p-1.duration-300.ease-in-out.cursor-pointer
   {:class (if val :bg-blue-400 :bg-gray-400)
    :on-click #(on-toggle (not val))}
   [:div.bg-white.w-6.h-6.rounded-full.shadow-md.transform.duration-300.ease-in-out
    {:class (if val :translate-x-6 :translate-x-0)}]])

(defn repeat-digits []
  [toggle (:repeat-digits @state) #(swap! state assoc :repeat-digits %)])

(defn app []
  [:div.container.flex.justify-center.items-center.flex-col.pt-3
   [label :Target]
   [target]
   [label :Digits]
   [digits]
   [label :Include]
   [include]
   [label "Repeat Digits"]
   [repeat-digits]
   [calculate]
   [result]])


