
import java.io.*;
import java.net.*;

import jason.asSyntax.*;
import jason.environment.*;

import java.util.logging.*;
import java.util.regex.Pattern;


public class KrisletEnvironment extends Environment {



    private Logger logger = Logger.getLogger("SYSC5103_Project.mas2j."+KrisletEnvironment.class.getName());
	private Krislet player;
	

    /** Called before the MAS execution with the args informed in .mas2j */

    @Override

    public void init(String[] args) {
		String	hostName = "localhost";
		int			port = 6000;
		String	team = new String("Krislet3");
		
        super.init(args);
		
		try	{		
			//System.out.println("Creating Krislet player");
			player = new Krislet(InetAddress.getByName(hostName), port, team);
			//System.out.println("Calling main init");
			player.mainInit();
			//System.out.println("Calling the update function");
			player.mainUpdate();
		} catch (Exception e) {	}
		//System.out.println("Updating player");
		updatePlayer(player);
		//System.out.println("End init");
    }


	public static final Literal    enteringInTheField = Literal.parseLiteral("enteringInTheField");
	public static final Literal    turn40 = Literal.parseLiteral("turn40");
	public static final Literal    dash = Literal.parseLiteral("dash");
	public static final Literal    kick = Literal.parseLiteral("kick");
    @Override

    public boolean executeAction(String agName, Structure action) {
	/*	String temp = action.toString();
		String[] arrOfStr = temp.split("(");
		String comm = arrOfStr[0];
		String param = arrOfStr[1];
		param = param.replaceAll(")","");
		switch(comm){
			case "turn":
			player.turn(Double.parseDouble(param));
			break;
		}*/
		try{player.mainUpdate();}catch(Exception e){
			logger.info(e.getMessage());
		}
		if(action.equals(enteringInTheField)){
			act("enteringInTheField");
		}
		else if(action.equals(turn40)){
			act("turn40");
		}
		else if(action.equals(dash)){
			act("dash");
		}
		else {
			logger.info("executing: "+action+", but not implemented!");
        }
		if (true) { // you may improve this condition
			updatePlayer(player);
            informAgsEnvironmentChanged();

        }
		
		

        return true; // the action was executed with success

    }

    public String act(String action){//, ArrayList<String[]> list) {
    	ObjectInfo ball = player.m_memory.getObject("ball");
    	ObjectInfo goal;
    	if( player.m_side == 'l' )
    	    goal = player.m_memory.getObject("goal r");
    	else
    	    goal = player.m_memory.getObject("goal l");
    	//for (String[] s: list){
    		//if(s[0].equals(st)) {
    			if(action.equals("enteringInTheField")) {
    				player.inField = true;
    				player.move( -Math.random()*52.5 , 34 - Math.random()*68.0 );
    			}
    			else if(action.equals("stopPlaying")) {
    				player.bye();
    				return action;
    			}
    			else if(action.equals("turn40")) {
    				player.turn(40);
    			//	player.m_memory.waitForNewInfo();
    				return action;
    			}
    			else if(action.equals("turn80")) {
    				player.turn(80);
    			//	player.m_memory.waitForNewInfo();
    				return action;
    			}
    			else if(action.equals("turn120")) {
    				player.turn(120);
    			//	player.m_memory.waitForNewInfo();
    				return action;
    			}
    			else if(action.equals("turnToBall")) {
    				player.turn(ball.m_direction);
    				return action;
    			}
    			else if(action.equals("dash")) {
					player.dash(100);
					return action;
				}
    			else if(action.equals("goToBall")) {
					player.dash(10*ball.m_distance);
					return action;
				}
				else if(action.equals("kickBall50")) {
					player.kick(50, goal.m_direction);
					return action;
				}
				else if(action.equals("kickBall100")) {
					player.kick(100, goal.m_direction);
					return action;
			//	}
    			//m_krislet
    		//}
    	}
    	return "NoAction";
    }

    /** Called before the end of MAS execution */

    @Override

    public void stop() {
		
        super.stop();

    }
	
	/** Private helper functions*/
	private void updatePlayer(Krislet p){
		try {
			// Specify agent name !!!
			clearPercepts();
			//addPercept(ASSyntax.parseLiteral("percept(demo)"));
			//addPercept(ASSyntax.parseLiteral("ball"));   
			//logger.info("Ball was seen");
			ObjectInfo ball = p.m_memory.getObject("ball");
			ObjectInfo goal;
			if( p.m_side == 'l' )
			    goal = p.m_memory.getObject("goal r");
			else
			    goal = p.m_memory.getObject("goal l");
		    
			
			
			
			if(/*Pattern.matches("^before_kick_off.*",m_playMode) &&*/ !p.inField) {
				p.inField= true;
				addPercept(ASSyntax.parseLiteral("readyToStart"));   
				logger.info("readyToStart");
			}
			/*else if(//m_timeOver) {
		    	//return "TimeOver";
		    }*/
		    else if( ball == null )
			    {
				// If you don't know where is ball then find it
		    	addPercept(ASSyntax.parseLiteral("noBall"));   
				logger.info("noBall");
			    }
			else if( ball.m_distance > 1.0 )
			    {
				// If ball is too far then
				// turn to ball or 
				// if we have correct direction then go to ball
				if( ball.m_direction != 0 ){
				addPercept(ASSyntax.parseLiteral("ballFarKnowDirection"));   
				logger.info("ballFarKnowDirection");
				}
				else{
					addPercept(ASSyntax.parseLiteral("ballFarNoDirection"));   
					logger.info("ballFarNoDirection");
					}
			    }
			else 
			    {
				// We know where is ball and we can kick it
				// so look for goal
				if( goal == null )
				    {
					addPercept(ASSyntax.parseLiteral("ballCloseNoGoal"));   
					logger.info("ballCloseNoGoal");
				    }
				else{
					addPercept(ASSyntax.parseLiteral("ballCloseSeeGoal"));   
					logger.info("ballCloseSeeGoal");
				}
				   
			    }		
		} catch (Exception e) {}
	}

}


