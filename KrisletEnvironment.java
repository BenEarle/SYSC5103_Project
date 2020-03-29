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
	public static final Literal    goToBallSlowly = Literal.parseLiteral("goToBallSlowly");
	public static final Literal    turnToBall = Literal.parseLiteral("turnToBall");
	public static final Literal    kickBall50 = Literal.parseLiteral("kickBall50");
	public static final Literal    noAction = Literal.parseLiteral("noAction");

    @Override
    public boolean executeAction(String agName, Structure action) {
        int playerNumber = C_INVALID_PLAYER;
		// Check which player is execing an action
		playerNumber = getPlayerNumber(agName);
		if (playerNumber == C_INVALID_PLAYER)
		     return false;   
		try{
		    player[playerNumber].mainUpdate();
		}catch(Exception e){
			logger.info(e.getMessage());
		}
		// Call act with the given command, if it fails log the result.
		if(!act(action.toString(),player[playerNumber]))
			logger.info("*****\nError: " + action + ", failed to execute!\n*****");
		else
			logger.info("Executed: " + action);
		if (true) { // Always update the player's environment
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


    public boolean act(String action, Krislet player)
    {
    	ObjectInfo ball;
    	ObjectInfo goal;
		// Get relavent information
    	ball = player.m_memory.getObject("ball");
    	if( player.m_side == 'l' )
    	    goal = player.m_memory.getObject("goal r");
    	else
    	    goal = player.m_memory.getObject("goal l");
		// Handle the action
		if(action.equals("enteringInTheField")) {
			player.inField = true;
			player.move( -Math.random()*52.5 , 34 - Math.random()*68.0 );
		}
		else if(action.equals("stopPlaying")) {
			player.bye();
		}
		else if(action.equals("turn20")) {
			player.turn(20);
			//player.m_memory.waitForNewInfo();
		}
		else if(action.equals("turn40")) {
			player.turn(40);
			//player.m_memory.waitForNewInfo();
		}
		else if(action.equals("turn80")) {
			player.turn(80);
		//	player.m_memory.waitForNewInfo();
		}
		else if(action.equals("turn120")) {
			player.turn(120);
		//	player.m_memory.waitForNewInfo();
		}
		else if(action.equals("turnToBall") && ball!=null) {
			player.turn(ball.m_direction);
		}
		else if(action.equals("dash")) {
			player.dash(100);
		}
		else if(action.equals("goToBall") && ball!=null) {
			player.dash(10*ball.m_distance);
		}
		else if(action.equals("goToBallSlowly") && ball!=null) {
			player.dash(0.5*ball.m_distance);
		}
		else if(action.equals("kickBall50") && goal!=null) {
			player.kick(50, goal.m_direction);
		}
		else if(action.equals("kickBall100") && goal!=null) {
			player.kick(100, goal.m_direction);
		}
		
		else if(action.equals("noAction")) {
		}
		else {
			// If the action was unrecognized then return false
			return false;
		}
		// Otherwise the action was properly handled, return true.
    	return true;
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
			clearPercepts(p.m_name);

			ObjectInfo ball = p.m_memory.getObject("ball");
			ObjectInfo goal;
			if( p.m_side == 'l' )
			    goal = p.m_memory.getObject("goal r");
			else
			    goal = p.m_memory.getObject("goal l");
		    
			if(/*Pattern.matches("^before_kick_off.*",m_playMode) &&*/ !p.inField) {
				p.inField= true;
				addPercept(p.m_name, ASSyntax.parseLiteral("readyToStart"));   
				//logger.info("readyToStart");
			}
			/*else if(//m_timeOver) {
		    	//return "TimeOver";
		    }*/
		    if( ball == null ) {
				// If you don't know where is ball then find it
		    	//addPercept(p.m_name, ASSyntax.parseLiteral("noBall"));   
				logger.info("Add cannotSeeBall to " + p.m_name);
			} else if(ball.m_distance > 1.0 && ball.m_direction != 0 ) {
				// If ball is too far to kick and we are not facing it
				addPercept(p.m_name, ASSyntax.parseLiteral("canSeeBall"));   
				logger.info("Add canSeeBall to " + p.m_name);
			} else if (ball.m_distance > 1.0 && ball.m_direction == 0){
				// If ball is too far to kick and we are facing it
				addPercept(p.m_name, ASSyntax.parseLiteral("canSeeBall")); 
				addPercept(p.m_name, ASSyntax.parseLiteral("facingBall"));   
				logger.info("Add facingBall to " + p.m_name);
				if(closestToBall(p,2)){
					addPercept(p.m_name, ASSyntax.parseLiteral("closestToBall"));   
					logger.info("Add closestToBall " + p.m_name);
				}
				else{
					logger.info("Add not closestToBall" + p.m_name);
				}
			} else {
				// Close enough to kick the ball
				addPercept(p.m_name, ASSyntax.parseLiteral("canKickBall"));   
				logger.info("Add canKickBall to " + p.m_name);
			}    
			// Look for goal
			if ( goal == null ) {
				//addPercept(p.m_name, ASSyntax.parseLiteral("noGoal"));   
				logger.info("Add cannotSeeGoal to " + p.m_name);
		    } else {
				addPercept(p.m_name, ASSyntax.parseLiteral("canSeeGoal"));   
				logger.info("Add canSeeGoal to " + p.m_name);
			}
				   
			    		
		} catch (Exception e) {}
	}
	/*
	 * 
	 */
	private boolean closestToBall(Krislet p, int number){
		int closestThanMe = 0;
		LinkedList<PlayerInfo> team = getTeamPlayers(p,p.getTeam());
		ObjectInfo ball = p.m_memory.getObject("ball");
		if (ball==null){
			return false;
		}
		float distance = 0;
		float my_distance = ball.m_distance;
		for(int i = 0; i < team.size(); i++){
			float partner_distance = team.get(i).m_distance;
			if(partner_distance < my_distance){
					closestThanMe++;
				}
		}
		
		return closestThanMe < number;
	}
	/*
	 * 
	 */
	public LinkedList<PlayerInfo> getTeamPlayers(Krislet player, String teamName) 
    {
	LinkedList<PlayerInfo> players = new LinkedList<PlayerInfo>();
	if( player.m_memory.getInfo() == null )
	    return null;

	for(int c = 0 ; c < player.m_memory.getInfo().m_objects.size() ; c ++)
	    {
		ObjectInfo object = (ObjectInfo)player.m_memory.getInfo().m_objects.elementAt(c);
		if(object.m_type.compareTo("player")==0){
		    PlayerInfo p = (PlayerInfo) object;
		    if(p.m_teamName.equals(teamName)){
		    players.add(p);	
		    }
		    }
	    }												 
	return players;
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