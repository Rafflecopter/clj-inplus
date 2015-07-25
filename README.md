# in+ [![Build Status][1]][2]

A clojure microlibrary to help with using deep nested objects.

[![Clojars Project](http://clojars.org/inplus/latest-version.svg)](http://clojars.org/inplus)

## Using

```clojure
(def obj
  {:foo {:bar [
    {:id "one"
     :baz 123}
    {:id "two"
     :baz 456}
    {:id "three"
     :baz 789}]}})

(require '[inplus.core :refer [get-in+ update-in+ assoc-in+ dissoc-in+]])

;; They work like regular *-in
(get-in+ obj [:foo :bar]) ; => [{:id "one" :baz 123} ...]
(update-in+ obj [:foo] assoc :bar :buzz) ; => {:foo {:bar :buzz}}
(assoc-in+ obj [:foo :bar] 88) ; => {:foo {:bar 88}}

;; A "new" function is dissoc-in+, which dissoc's the last key in the array
(dissoc-in+ obj [:foo :bar]) ; => {:foo {}}

;; They also can go deeply into collections via a "spec"
(get-in+ obj [:foo :bar {:id "one"}]) ; => {:id "one" :baz 123}
(get-in+ obj [:foo :bar {:id "one"} :baz]) ; => 123

;; update-in+ works the same, but will also create new structures if necessary
(update-in+ obj [:foo :bar {:id "two"} :baz] inc)
  ; => {... {:id "two" :baz 457} ...}
(update-in+ obj [:foo :bomb {:type "yarn"} :baz] inc)
  ; => {:foo {:bomb [{:type "yarn" :baz 1}] ...}}

;; assoc-in+ uses update-in+, so they support similar features
(assoc-in+ [:foo :bar {:id "four"} :baz] 1000)
  ; => {:foo {:bar [... {:id "four" :baz 1000}]}}
(assoc-in+ [:foo :bar {:id "five"}])
  ; => {:foo {:bar [... {:id "five"}]}}

;; dissoc-in+ supports removing from a collection as well as keys
(dissoc-in+ obj [:foo :bar {:id "one"} :baz])
  ; => {:foo {:bar [{:id "one"} ...]}}
(dissoc-in+ obj [:foo :bar {:id "one"}])
  ; => {:foo {:bar [{:id "two" :baz 456} ...]}}
```

## License

MIT license in LICENSE file.


[1]: https://travis-ci.org/Rafflecopter/clj-inplus.png?branch=master
[2]: http://travis-ci.org/Rafflecopter/clj-inplus
