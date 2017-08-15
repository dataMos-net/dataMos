(ns datamos.msg-functions)

(defn retrieve-sender
  "Returns component-settings value for key :datamos-cfg/module-uri. If option is :type
  key will be set to :datamos-cfg/module-type"
  ([component-settings] (retrieve-sender component-settings nil))
  ([component-settings option]
   (select-keys component-settings [(case option
                                      :type :dmsfn-def/module-type
                                      :datamos-cfg/module-uri)])))

(defn compose-message
  "Returns full message, with values for :datamos/logistic and :datamos/rdf-content.
  Settings is used to retrieve sender. Content is the RDF message to be sent. RCPT is the recepient of the message.
  RCPT is a datamos function defined as a keyword. Example :datamos-fn/registry"
  [component-settings content rcpt]
  (let [s (retrieve-sender component-settings)]
    {:datamos/logistic {:dms-def/rcpt-fn rcpt
                        :dms-def/sender  (vals s)}
     :datamos/config   content}))

(defn get-message-id
  "Returns message-id from a message"
  [message]
  (:dms-def/message-id (:dms-def/message (:datamos/logistic message))))