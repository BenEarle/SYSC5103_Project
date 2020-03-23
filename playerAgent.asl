// Agent PlayerAgent in project SYSC5103_Project.mas2j

/* Initial beliefs and rules */

/* Initial goals */

!findBall.

/* Plans */

+!findBall : not ball <- turn(40); .wait(500). 

+!findBall : ball <- turn(dir(ball)).
