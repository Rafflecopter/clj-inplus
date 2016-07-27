(ns inplus.core)


;;;; Helpers for "indexing" into lists of objects

(defn- matches-spec? [spec m]
  (= spec (select-keys m (keys spec))))

;; TODO add a ++ version that gets/updates _all_ objects in a collection
(defn- get-all-matching
  "Finds all members (x) of coll where:
  - x contains all keys specified in `spec`
  - All of x's matching keys have the same value as prescribed in `spec`
  Example:
  spec: {:id :foo, :username :bar}
  matches: {:id :foo, :username :bar, :other-key 7}
  does not match {:id :foo, :username :qux, :other-key 9}"
  [coll spec]
  (filter #(matches-spec? spec %) coll))

(defn- get-matching
  "Like get-all-matching, but only returns the first match."
  [coll spec]
  (first (get-all-matching coll spec)))

(defn- assoc-matching
  "Using the same logic as get-all-matching, return a list where all matches
  are replaced with `new-value`.
  Returns a seq or vec, depending on coll's type."
  [coll spec new-value]
  ((if (vector? coll) vec identity)
   (if (get-matching coll spec)
    (map-indexed
      (fn [i val] (if (matches-spec? spec val)
                    new-value
                    val))
      coll)
    (conj coll (merge new-value spec)))))

(defn- remove-matching
  "Using the same logic as get-all-matching, returns a list with all
  matches removed.
  Returns a seq or vec, depending on coll's type."
  [coll spec]
  ((if (vector? coll) vec identity)
   (filter #(not (matches-spec? spec %)) coll)))


(defn get-in+
  "(get-in obj path) with extended behavior
  Handles path elements that are matching-schemas
  to match a single element in a collection"
  [obj [key & rest]]
  (if (nil? key)
    obj
    (let [subobj (if (map? key) (get-matching obj key) (get obj key))]
      (get-in+ subobj rest))))

(defn update-in+
  "(update-in obj path f & args) with extended behavior
  Handles path elements that are matching-schemas to match a single element in a collection.
  For empty paths, update-in+ calls (apply f obj args) instead of update-in's behavior.
  If a path element does not exist, a hash-map will be created.
  If a collection matching element does not exist, the spec will be copied and conjoined."
  [obj [key & rest] f & args]
  (if (nil? key)
    (apply f obj args)
    (cond
      (and (vector? obj)
           (not (map? key)))
      (throw (#?(:clj Exception. :cljs js/Error.)
              "spec must be provided for list collections"))

      (or (vector? obj)
          (seq? obj))
      (if-let [subobj (get-matching obj key)]
        (assoc-matching obj key (apply update-in+ subobj rest f args))
        (conj obj (apply update-in+ key rest f args)))

      :else (let [subobj (or (get obj key) (if (map? (first rest)) [] {}))
                  subobj (apply update-in+ subobj rest f args)]
              (assoc obj key subobj)))))

(defn assoc-in+
  "(assoc-in obj path val) with extended behavior
  Handles path elements that are matching-schemas to match a single element in a collection.
  If a path element does not exist, a hash-map will be created.
  If a collection matching element does not exist, the spec will be copied and conjoined."
  [obj path val]
  (if (map? (last path))
    (update-in+ obj (butlast path) #(-> % vec (assoc-matching (last path) val)))
    (update-in+ obj (butlast path) assoc (last path) val)))

(defn dissoc-in+
  "Dissociates keys and objects from deep inside structures.
  Handles path elements that are matching-schemas to match a single element in a collection"
  [obj path]
  (if (map? (last path))
    (update-in+ obj (butlast path) #(-> % vec (remove-matching (last path))))
    (update-in+ obj (butlast path) dissoc (last path) val)))
