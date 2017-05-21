Algorithm discussion
====================

Given: n points as a set of (time, position, direction)
Expected: group the points into k clusters, determine the general
direction of movement for each cluster and point each member towards a
safe location based on others' prior experience

Phase 1 (normal/pre-emergency state)
------------------------------------

- Android
  * listen on socket from Spark server which tells when to switch to
    Phase 2
  * listen on Significant Motion Sensor events
    * send these to Spark
- Spark
  * determine direction of movement
  * determine if directions are concentric
    * if they are, and speed is above certain threshold, broadcast
      emergency state to all other groups and switch to phase 2

Phase 2 (emergency)
-------------------

* determine which escape paths are successful
    * can be manual (when a user presses the "I'm safe" button in the
      Android app)
    * can be automatic (when a group disperses calmly, at low speeds)
* determine a score for each successful emergency exit path
    * safety
        * determine the people left behind from a particular group (-)
        * determine percentage of people from group that clicked "I'm safe"
    	  (+)
    * crowded (-)
* choose the viable escape paths for a group
    * based on proximity, safety and crowdedness
    * if multiple paths, do load balancing (split group)

Phase 3 (post-emergency)
------------------------

* show info about people left behind
* find congested groups that are trapped

Spark implementation details
----------------------------

* all people (time, position, direction) =
    safe people + people in danger + people left behind + rescuers
