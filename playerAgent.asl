// Agent PlayerAgent in project SYSC5103_Project.mas2j



/* Initial beliefs and rules */



/* Initial goals */



!scoreGoal.



/* Plans */

//+!scoreGoal: readyToStart  <- enteringInTheField; !scoreGoal.
//+!scoreGoal: noBall  <- turn40; !scoreGoal.
+!scoreGoal: readyToStart  <- enteringInTheField; !findBall.
+!findBall: noBall  <- turn40; !findBall.
+!findBall: ballFarNoDirection  <- dash; !findBall.
+!findBall: true  <- turn40; !findBall.

/* 
+!findBall : not ball <- turn(40); .wait(500).
 

+!findBall : ball <- turn(dir(ball)).
*/