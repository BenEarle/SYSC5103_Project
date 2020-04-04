// Agent PlayerAgent in project SYSC5103_Project.mas2j
/* Initial beliefs and rules */

/* Initial goals */
!beInTheField.
!scoreGoal.


/* Plans */
/******************************************************************************/
// We always want to score a goal!
// If the agent is in range and facing the goal it should kick the ball
+!scoreGoal: canKickBall & canSeeAttackingGoal <- kickBall100; !scoreGoal.
// If the agent is in range but not lined up it should locate the goal
+!scoreGoal: canKickBall & not canSeeAttackingGoal <- !findGoal; !scoreGoal.
// In all other cases the agent should run to the ball
+!scoreGoal: true <- !runToBall; !scoreGoal.
/******************************************************************************/
// This handles the pre-kickoff state
+!beInTheField: readyToStart <- enteringInTheField.
+!beInTheField: not readyToStart <- noAction.     
/******************************************************************************/
// Mission accomplished! We can kick the ball!
+!runToBall: canKickBall <- noAction.
// To run to the ball we must first see the ball , search until its in sight
+!runToBall: not canSeeBall <- turn40; !runToBall.
// The agent can see it but is not facing it, turn to face it
+!runToBall: canSeeBall & not facingBall <- turnToBall; !runToBall.
// The agent is inline with the ball and facing it, then we need to run forward
+!runToBall: canSeeBall & facingBall & not canKick & closestToBall<- goToBall; !runToBall.
+!runToBall: canSeeBall & facingBall & not canKick<- goToBallSlowly; !runToBall.
/******************************************************************************/
// Need to locate the goal, if we don't see it. No point in searching if we 
// cannot see the ball.
+!findGoal: canKickBall & not canSeeAttackingGoal <- turn20; !findGoal.
+!findGoal: true <- noAction.

