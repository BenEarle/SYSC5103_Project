import java.io.*;
import java.net.*;

import jason.mas2j.*;
import jason.asSyntax.*;
import jason.environment.*;

import java.util.*;
import java.util.logging.*;
import java.util.regex.Pattern;


public class KrisletEnvironment extends Environment 
{
    public static final int C_MAX_PLAYERS = 5;
    public static final int C_INVALID_PLAYER = -99;

    private Logger logger = Logger.getLogger("SYSC5103_Project.mas2j."+KrisletEnvironment.class.getName());
	private Krislet player[];
	
    /** Called before the MAS execution with the args informed in .mas2j */

    @Override

    public void init(String[] args) {
        jason.mas2j.parser.mas2j    parser;
	    MAS2JProject                project;
	    List<String>                names = new ArrayList<String>();
		String                      hostName = "localhost";
		String                      team = new String("Krislet3");
		int                         port = 6000;
		int                         playerNumber = 0;
				
        super.init(args);
        player = new Krislet[C_MAX_PLAYERS];
        
		try	
		{
		    /**
		     * Parse the project file to get the list of agents that exist. When
		     * we create our Krislet player, we will assign them that name,
		     * since that's what jason will refer to them as when it executes
		     * actions.
		     */
            parser = new jason.mas2j.parser.mas2j(new FileInputStream(args[0]));
            project = parser.mas();
            
            // Get the names from the project
            for (AgentParameters ap : project.getAgents()) 
            {
                String agName = ap.name;
                for (int cAg = 0; cAg < ap.getNbInstances(); cAg++) 
                {
                    String numberedAg = agName;
                    if (ap.getNbInstances() > 1) 
                    {
                       numberedAg += (cAg + 1);
                    }
                    
                    /**
                     * Stick the agent name into the Krislet object so we can
                     * associate it with what we get from the jason framework.
                     */
                    player[playerNumber] = new Krislet(InetAddress.getByName(hostName), 
                                                       port, 
                                                       team,
                                                       numberedAg);
                    player[playerNumber].mainInit();
                    player[playerNumber].mainUpdate();
                    
                    
                    /**
                     * NOTE: We will probably only want to do this beore the 
                     * kickoff only
                     */
                    /* Put the player somewhere on the field */
                    player[playerNumber].move(-Math.random()*52.5 , 34 - Math.random()*68.0);
                    updatePlayer(player[playerNumber]);
        
                    ++playerNumber;
                }
            }
		} 
		catch (Exception e) 
		{
		    e.printStackTrace();
		    return;
		}
    }

	public static final Literal    enteringInTheField = Literal.parseLiteral("enteringInTheField");
	public static final Literal    turn40 = Literal.parseLiteral("turn40");
	public static final Literal    goToBall = Literal.parseLiteral("goToBall");
	public static final Literal    turnToBall = Literal.parseLiteral("turnToBall");
	public static final Literal    kickBall50 = Literal.parseLiteral("kickBall50");
	public static final Literal    noAction = Literal.parseLiteral("noAction");

    @Override
    public boolean executeAction(String agName, Structure action) {
        int playerNumber = C_INVALID_PLAYER;
	/*	String temp = action.toString();
		String[] arrOfStr = temp.split("(");
		String comm = arrOfStr[0];
		String param = arrOfStr[1];
		param = param.replaceAll(")","");
		switch(comm){
			case "turn":
			player[playerNumber].turn(Double.parseDouble(param));
			break;
		}*/
		
		playerNumber = getPlayerNumber(agName);
		if (playerNumber == C_INVALID_PLAYER)
		{
		     return false;   
		}
		
		try{
		    player[playerNumber].mainUpdate();
		}catch(Exception e){
			logger.info(e.getMessage());
		}
		if(action.equals(enteringInTheField)){
			act("enteringInTheField", player[playerNumber]);
		}
		else if(action.equals(turn40)){
			act("turn40", player[playerNumber]);
		}
		else if(action.equals(goToBall)){
			act("goToBall", player[playerNumber]);
		}
		else if(action.equals(turnToBall)){
			act("turnToBall", player[playerNumber]);
		}
		else if(action.equals(kickBall50)){
			act("kickBall50", player[playerNumber]);
		}
		else if(action.equals(noAction)){
			//
		}
		else {
			logger.info("executing: "+action+", but not implemented!");
        }
		if (true) { // you may improve this condition
			updatePlayer(player[playerNumber]);
            informAgsEnvironmentChanged();
        }
        
		try
		{
			player[playerNumber].mainUpdate();
		} 
		catch(Exception e) 
		{ 
		    e.printStackTrace();
		    return false;
		}

        return true; // the action was executed with success

    }


    public String act(String action, Krislet player)
    {
    	ObjectInfo ball;
    	ObjectInfo goal;
		
    	ball = player.m_memory.getObject("ball");
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
    				//player.m_memory.waitForNewInfo();
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
    			else if(action.equals("turnToBall") && ball!=null) {
    				player.turn(ball.m_direction);
    				return action;
    			}
    			else if(action.equals("dash")) {
					player.dash(100);
					return action;
				}
    			else if(action.equals("goToBall") && ball!=null) {
					player.dash(10*ball.m_distance);
					return action;
				}
				else if(action.equals("kickBall50") && goal!=null) {
					player.kick(50, goal.m_direction);
					return action;
				}
				else if(action.equals("kickBall100") && goal!=null) {
					player.kick(100, goal.m_direction);
					return action;
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
		    if( ball == null )
			    {
				// If you don't know where is ball then find it
		    	addPercept(ASSyntax.parseLiteral("noBall"));   
				logger.info("noBall");
			    }
			if( ball != null && ball.m_distance > 1.0 && ball.m_direction != 0 )
			    {
				// If ball is too far then
				// turn to ball or 
				// if we have correct direction then go to ball
				
				addPercept(ASSyntax.parseLiteral("ballFarKnowDirection"));   
				logger.info("ballFarKnowDirection");
				}
			if (ball != null && ball.m_distance > 1.0 && ball.m_direction == 0){
					addPercept(ASSyntax.parseLiteral("ballFarNoDirection"));   
					logger.info("ballFarNoDirection");
					}
			if (ball != null && ball.m_distance < 1.0){
				addPercept(ASSyntax.parseLiteral("ballClose"));   
				logger.info("ballClose");
				}    
				// We know where is ball and we can kick it
				// so look for goal
			if ( goal == null )
				    {
					addPercept(ASSyntax.parseLiteral("noGoal"));   
					logger.info("noGoal");
				    }
			if ( goal != null ){
					addPercept(ASSyntax.parseLiteral("seeGoal"));   
					logger.info("seeGoal");
				}
				   
			    		
		} catch (Exception e) {}
	}
	
	/**
	 * Name: getPlayerNumber
	 *
	 * Parameters:    agentName - String passed from the jason framework identifying a specific Agent
	 *
	 * Takes an Agent name given to us from the jason framework and sees if there is a Krislet
	 * object with that name. If so, returns the player number assigned to that player,
	 * otherwise will return an invalid number.
	 *
	 * Returns: Player number associated with the name, C_INVALID_PLAYER otherwise.
	 */
    private int getPlayerNumber(String agentName)
    {
        if (agentName == null)
        {
            return C_INVALID_PLAYER;   
        }
        
        for (int i = 0; i < C_MAX_PLAYERS; i++)
        {            
            if (agentName.equals(player[i].m_name))
            {
                return i;   
            }
        }
        
        return C_INVALID_PLAYER;
    }

}


