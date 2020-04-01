// Agent PlayerAgent in project SYSC5103_Project.mas2j
/* Initial beliefs and rules */

/* Initial goals */
!beInTheField.
//!defendGoal.
!goToDefGoal.


/* Plans */
/******************************************************************************/
// This handles the pre-kickoff state
+!beInTheField: readyToStart <- enteringInTheGoal.
+!beInTheField: not readyToStart <- noAction.     

/******************************************************************************/
// Agent needs to be able to see the goal it needs to defend
+!goToDefGoal: not canSeeDefGoal <- turn40; !goToDefGoal.
// The agent can see it but is not facing it, turn to face it
+!goToDefGoal: canSeeDefGoal & not facingDefGoal <- turnTowardsDefGoal; !goToDefGoal.
// Once the Agent can see the goal, run towards it
+!goToDefGoal: not atDefGoal & canSeeDefGoal & facingDefGoal <- runTowardsDefGoal; !goToDefGoal.
// Agent is at the goal, needs to get into position to defend
+!goToDefGoal: atDefGoal <- noAction; !getIntoPosition.
// Default error handlers
+!goToDefGoal: true <- !goToDefGoal.
-!goToDefGoal: true <- !goToDefGoal.

// Agent is at the goal, but needs to get into position
+!getIntoPosition: not canSeeDefFlag & not facingDefFlag <- turn20; !getIntoPosition.
// Agent can see the flag, but is not facing it
+!getIntoPosition: canSeeDefFlag & not facingDefFlag <- turnTowardsDefFlag; !getIntoPosition.
// Once the Agent can see the goal, run towards it
+!getIntoPosition: not inPositionToDefend & canSeeDefFlag & facingDefFlag <- runTowardsDefFlag; !getIntoPosition.
// Agent is at the goal, needs to get into position to defend
+!getIntoPosition: inPositionToDefend <- noAction; !defendGoal.
// Default error handlers
+!getIntoPosition: true <- !getIntoPosition.
-!getIntoPosition: true <- !getIntoPosition.

// To defend wait until we can see the ball. We should be looking in the right direction already
+!defendGoal: not canSeeBall <- turn20; !defendGoal.
// The agent can see it but is not facing it, turn to face it
+!defendGoal: canSeeBall & not facingBall & not clearBall <- turnToBall; !defendGoal.
// The agent is inline with the ball and it is a danger
+!defendGoal: dangerBall & not clearBall <- goToBallQuickly; !defendGoal.
// The agent is inline with the ball and it is a danger
+!defendGoal: clearBall & facingForwards <- clearBall; .print("Facing Forwards: Clear Ball"); !goToDefGoal.
// The ball is behind the Agent, so we want to spin and then clear the ball
+!defendGoal: clearBall & not facingForwards <- turn120; .print("Turn: 120"); !defendGoal.


