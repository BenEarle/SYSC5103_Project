// Agent PlayerAgent in project SYSC5103_Project.mas2j

/* Initial beliefs and rules */

/* Initial goals */

!environmentReady.

/* Plans */
+!environmentReady : not environment_ready
	<-	.print("Waiting for environment");
		.wait(500);
		initialize;
		!environmentReady.

+!environmentReady : environment_ready
	<-	.print("Environment is ready. Start playing soccer.");
		!findBall.
		
+!findBall : not ball_found 
	<-	.print("Cannot find ball");
		turn(40); 
		.wait(200);
		!findBall.

+!findBall : ball_found
	<- 	.print("Found the ball").

