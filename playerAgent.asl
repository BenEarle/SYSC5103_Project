// Agent PlayerAgent in project SYSC5103_Project.mas2j



/* Initial beliefs and rules */



/* Initial goals */



!scoreGoal.



/* Plans */
/* 
+!scoreGoal: readyToStart  <- enteringInTheField; !scoreGoal.
+!scoreGoal: noBall  <- turn40; !scoreGoal.
+!scoreGoal: ballFarNoDirection  <- turnToBall; !scoreGoal.
+!scoreGoal: ballFarKnowDirection  <- goToBall; !scoreGoal.
+!scoreGoal: ballCloseNoGoal  <- turn40; !scoreGoal.
+!scoreGoal: ballCloseSeeGoal  <- kickBall50; !scoreGoal.
*/

+!scoreGoal : true <- !beInTheField; !findBall; !beNearBall; !findGoal; kickBall50; !scoreGoal.
+!beInTheField: readyToStart <- enteringInTheField.
+!beInTheField: not readyToStart <- noAction.
+!findBall: noBall <- turn40; !findBall.
+!findBall: not noBall <- noAction.
+!beNearBall: ballFarNoDirection <- turnToBall; !beNearBall.
+!beNearBall: ballFarKnowDirection <- goToBall; !beNearBall.
+!beNearBall: true <- noAction.
+!findGoal: ballClose & noGoal <- turn40; !findGoal.
+!findGoal: ballClose & seeGoal <- noAction.
+!findGoal: not ballClose <- noAction.