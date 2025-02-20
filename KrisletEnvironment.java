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
    /* We can make a 5-on-5 team if needed. */
    public static final int C_MAX_PLAYERS = 10;
    public static final int C_INVALID_PLAYER = -99;
    
    public static final boolean DEBUG_LOGGING = false;

    private Logger logger = Logger.getLogger("SYSC5103_Project.mas2j."+KrisletEnvironment.class.getName());
	private Krislet player[];
	
    /** Called before the MAS execution with the args informed in .mas2j */
    @Override
    public void init(String[] args) {
        jason.mas2j.parser.mas2j    parser;
	    MAS2JProject                project;
	    List<String>                names = new ArrayList<String>();
	    boolean                     isGoalie = false;
		String                      hostName = "localhost";
		String                      team = "MEMA";
		int                         port = 6000;
		int                         playerNumber = 0;
		int                         count = 0;
				
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
                     * If we need to generate the entire team ourselves, we can
                     * double the number of agents created in the .mas2j file
                     * and enable this code.
                     */
/*                     
                    if (count % 2 == 0)
                    {
                        team = "MEMA_Home";
                    }
                    else
                    {
                        team = "MEMA_Away";   
                    }
                    ++count;
*/
                    /**
                     * We want to notify the Soccer Server that this agent is to
                     * be designated as a goalie.
                     */
                    isGoalie = agName.equals("keeperAgent");

                    /**
                     * Stick the agent name into the Krislet object so we can
                     * associate it with what we get from the jason framework.
                     */
                    player[playerNumber] = new Krislet(InetAddress.getByName(hostName), 
                                                       port, 
                                                       team,
                                                       numberedAg,
                                                       isGoalie);
                    player[playerNumber].mainInit();
                    player[playerNumber].mainUpdate();

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

    /**
     * List of different actions that the agents will take
     */
	public static final Literal    enteringInTheField = Literal.parseLiteral("enteringInTheField");
	public static final Literal    turn20 = Literal.parseLiteral("turn20");
	public static final Literal    turn40 = Literal.parseLiteral("turn40");
	public static final Literal    turn120 = Literal.parseLiteral("turn120");
	public static final Literal    goToBall = Literal.parseLiteral("goToBall");
	public static final Literal    goToBallSlowly = Literal.parseLiteral("goToBallSlowly");
	public static final Literal    goToBallQuickly = Literal.parseLiteral("goToBallQuickly");
	public static final Literal    turnToBall = Literal.parseLiteral("turnToBall");
	public static final Literal    kickBall50 = Literal.parseLiteral("kickBall50");
	public static final Literal    noAction = Literal.parseLiteral("noAction");
	public static final Literal    runToDefGoal = Literal.parseLiteral("runTowardsDefGoal");
	public static final Literal    turnTowardsDefGoal = Literal.parseLiteral("turnTowardsDefGoal");
	public static final Literal    turnTowardsDefFlag = Literal.parseLiteral("turnTowardsDefFlag");
	public static final Literal    runTowardsDefFlag = Literal.parseLiteral("runTowardsDefFlag");
	public static final Literal    clearBall = Literal.parseLiteral("clearBall");
	public static final Literal    clearBallBackwards = Literal.parseLiteral("clearBallBackwards");
	public static final Literal    enteringInTheGoal = Literal.parseLiteral("enteringInTheGoal");
	
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
		{
		    if (DEBUG_LOGGING)
		    {
		        logger.info("*****\nError: " + action + ", failed to execute!\n*****");
		    }
		}
		else
		{
		    if (DEBUG_LOGGING)
		    {
		        logger.info("Executed: " + action);
			}
		}
		
        updatePlayer(player[playerNumber]);
        informAgsEnvironmentChanged();
        
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

    /*
     * This function takes as parameters:
     * @action: String for the action to be performed
     * @player: Krislet object to whom the action should be applied
     * returns true if the action as been performed and false otherwise
     */
    public boolean act(String action, Krislet player)
    {
 
    	ObjectInfo ball;
    	ObjectInfo attackingGoal;
    	ObjectInfo defendingGoal;
    	ObjectInfo defendingBox;
    	ObjectInfo defendingLine;
    	
		/**
		 * Get the information that a given player can see. The ball will be the
		 * same for both sides, but attacking/defending objects will be 
		 * different for each side.
		 */
    	ball = player.m_memory.getObject("ball");
    	if( player.m_side == 'l' )
    	{
    	    attackingGoal = player.m_memory.getObject("goal r");
    	    
    	    defendingGoal = player.m_memory.getObject("goal l");
    	    defendingBox = player.m_memory.getObject("flag p l c");
    	    defendingLine = player.m_memory.getObject("line l");
    	}
    	else
    	{
    	    attackingGoal = player.m_memory.getObject("goal l");
    	    
    	    defendingGoal = player.m_memory.getObject("goal r");
    	    defendingBox = player.m_memory.getObject("flag p r c");
    	    defendingLine = player.m_memory.getObject("line r");
    	}
		
		if(action.equals("enteringInTheField")) {
		    /**
		     * A non-goalie player enters the field in a random location on
		     * their side.
		     */
			player.inField = true;
			player.move( -Math.random()*52.5 , 34 - Math.random()*68.0 );
		}
		else if(action.equals("stopPlaying")) {
		    /**
		     * Players disconnect from the server
		     */
			player.bye();
		}
		else if(action.equals("turn20")) {
		    /**
		     * Player turns 20 degrees
		     */
			player.turn(20);
		}
		else if(action.equals("turn40")) {
		    /**
		     * Player turns 40 degrees
		     */
			player.turn(40);
		}
		else if(action.equals("turn80")) {
		    /**
		     * Player turns 80 degrees
		     */
			player.turn(80);
		}
		else if(action.equals("turn120")) {
		    /**
		     * Player turns 120 degrees
		     */
			player.turn(120);
		}
		else if(action.equals("turnToBall") && ball!=null) {
		    /**
		     * Player turns towards the ball, if the ball can be seen
		     */
			player.turn(ball.m_direction);
		}
		else if(action.equals("dash")) {
		    /**
		     * Player dashes forwards with a power of 100
		     */
			player.dash(100);
		}
		else if(action.equals("goToBall") && ball!=null) {
		    /**
		     * Player dashes towards the ball with a power of 10 * the distance
		     * from the ball, if the ball can be seen
		     */
			player.dash(10*ball.m_distance);
		}
		else if(action.equals("goToBallSlowly") && ball!=null) {
		    /**
		     * Player dashes towards the ball with a power of 0.5 * the distance
		     * from the ball, if the ball can be seen
		     */
			player.dash(0.5*ball.m_distance);
		}
		else if(action.equals("goToBallQuickly") && ball!=null) {
		    /**
		     * Player dashes towards the ball with a power of 20 * the distance
		     * from the ball, if the ball can be seen
		     */
			player.dash(20*ball.m_distance);
		}
		else if(action.equals("kickBall50") && attackingGoal!=null) {
		    /**
		     * Player kicks the ball with a power of 50 towards the attacking
		     * goal, if it can be seen.
		     */
			player.kick(50, attackingGoal.m_direction);
		}
		else if(action.equals("kickBall100") && attackingGoal!=null) {
		    /**
		     * Player kicks the ball with a power of 100 towards the attacking
		     * goal, if it can be seen.
		     */
			player.kick(100, attackingGoal.m_direction);
		}
		else if(action.equals("runTowardsDefGoal") && defendingGoal!=null) {
		    /**
		     * Player dashes towards the defending goal with a power of 100 * 
		     * the distance from the goal, if the goal can be seen
		     */
			player.dash(100 * defendingGoal.m_distance);
		}
		else if(action.equals("turnTowardsDefGoal") && defendingGoal!=null) {
		    /**
		     * Player turns towards the defending goal, if the goal can be seen
		     */
			player.turn(defendingGoal.m_direction);
		}
		else if(action.equals("runTowardsDefFlag") && defendingBox!=null) {
		    /**
		     * Player dashes towards the defending flag (middle flag of the 
		     * penalty box) with a power of 100 * the distance from the flag, 
		     * if the flag can be seen
		     */
			player.dash(100 * defendingBox.m_distance);
		}
		else if(action.equals("turnTowardsDefFlag") && defendingBox!=null) {
		    /**
		     * Player turns towards the defending flag (middle flag of the 
		     * penalty box), if the flag can be seen
		     */
			player.turn(defendingBox.m_direction);
		}
		else if(action.equals("clearBall")) {
		    /**
		     * Player kicks the ball with a power of 100 forwards
		     */
			player.kick(100, 0);
		}
		else if (action.equals("clearBallBackwards")) {
		    /**
		     * Player kicks the ball with a power of 100 backwards
		     */
		     player.kick(100, 180);
		}
		else if(action.equals("enteringInTheGoal")) {
		    /**
		     * A goalie player enters the field in front of their goal
		     */
			player.inField = true;
			player.move(-50, 0);
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
	private void updatePlayer(Krislet p)
	{
		try 
		{
			clearPercepts(p.m_name);

			ObjectInfo ball = p.m_memory.getObject("ball");
			ObjectInfo attackingGoal;
			ObjectInfo defendingGoal;
			ObjectInfo defendingBox;
			ObjectInfo defendingLine;

			if( p.m_side == 'l' )
			{
			    attackingGoal = p.m_memory.getObject("goal r");
			    
			    defendingGoal = p.m_memory.getObject("goal l");
			    defendingBox = p.m_memory.getObject("flag p l c");
			    defendingLine = p.m_memory.getObject("line l");
			}
			else
			{
			    attackingGoal = p.m_memory.getObject("goal l");
			    
			    defendingGoal = p.m_memory.getObject("goal r");
			    defendingBox = p.m_memory.getObject("flag p r c");
			    defendingLine = p.m_memory.getObject("line r");		
			}
		    
			if(/*Pattern.matches("^before_kick_off.*",m_playMode) &&*/ !p.inField) {
				p.inField= true;
				addPercept(p.m_name, ASSyntax.parseLiteral("readyToStart")); 
				if (DEBUG_LOGGING)
				{
				    logger.info("readyToStart");
				}
			}
			/*else if(//m_timeOver) {
		    	//return "TimeOver";
		    }*/
		    
		    if( ball == null ) {
				// If you don't know where is ball then find it
		    	//addPercept(p.m_name, ASSyntax.parseLiteral("noBall")); 
		    	if (DEBUG_LOGGING)
		    	{
		    	    logger.info("Add cannotSeeBall to " + p.m_name);
				}
			} else if(ball.m_distance > 1.0 && ball.m_direction != 0 ) {
				// If ball is too far to kick and we are not facing it
				addPercept(p.m_name, ASSyntax.parseLiteral("canSeeBall"));   
				if (DEBUG_LOGGING)
				{
				    logger.info("Add canSeeBall to " + p.m_name);
				}
			} else if (ball.m_distance > 1.0 && ball.m_direction == 0){
				// If ball is too far to kick and we are facing it
				addPercept(p.m_name, ASSyntax.parseLiteral("canSeeBall")); 
				addPercept(p.m_name, ASSyntax.parseLiteral("facingBall"));  
				if (DEBUG_LOGGING)
				{
				    logger.info("Add canSeeBall to " + p.m_name);
				    logger.info("Add facingBall to " + p.m_name);
                }
				
			} else {
				// Close enough to kick the ball
				addPercept(p.m_name, ASSyntax.parseLiteral("canSeeBall")); 
				addPercept(p.m_name, ASSyntax.parseLiteral("canKickBall"));   
				addPercept(p.m_name, ASSyntax.parseLiteral("clearBall")); 

                if (DEBUG_LOGGING)
                {
                    logger.info("Add canSeeBall to " + p.m_name);
                    logger.info("Add canKickBall to " + p.m_name);
                    logger.info("Add clearBall to " + p.m_name);
                }
			}    
		    if(p.inField && closestToBall(p,2)){
				addPercept(p.m_name, ASSyntax.parseLiteral("closestToBall"));   
				if (DEBUG_LOGGING)
				{
				    logger.info("Add closestToBall " + p.m_name);
                }
			}
			else{
			    if (DEBUG_LOGGING)
			    {
			        logger.info("Add not closestToBall" + p.m_name);
                }
			}
			// Look for attackingGoal
			if ( attackingGoal == null ) {
				//addPercept(p.m_name, ASSyntax.parseLiteral("noGoal"));   
				if (DEBUG_LOGGING)
				{
				    logger.info("Add cannotSeeAttackingGoal to " + p.m_name);
                }
		    } else {
				addPercept(p.m_name, ASSyntax.parseLiteral("canSeeAttackingGoal"));   
				if (DEBUG_LOGGING)
				{
				    logger.info("Add canSeeAttackingGoal to " + p.m_name);
                }
			}
			
			// Look for defendingGoal
			if (defendingGoal != null)
			{
			    /**
			     * Player can see the defending goal, but might not be aligned
			     * with it
			     */
			    addPercept(p.m_name, ASSyntax.parseLiteral("canSeeDefGoal")); 
			    if (DEBUG_LOGGING)
				{
				    logger.info("Add canSeeDefGoal to " + p.m_name);
                }
			    
			    if (defendingGoal.m_direction == 0)
			    {
			        /**
                     * Player is aligned with the defending goal
                     */
			        addPercept(p.m_name, ASSyntax.parseLiteral("facingDefGoal")); 
			        if (DEBUG_LOGGING)
                    {
                        logger.info("Add facingDefGoal to " + p.m_name);
                    }
			    }
			    
			    if (defendingGoal.m_distance < 5)
			    {
			        /**
                     * Player is at the defending goal
                     */
			        addPercept(p.m_name, ASSyntax.parseLiteral("atDefGoal"));
			        if (DEBUG_LOGGING)
                    {
                        logger.info("Add atDefGoal to " + p.m_name);
                    }
			    }
			}
			
			// Look for the flag at the center of the defending penalty box
			if (defendingBox != null)
			{
			    /**
			     * Player can see the defending flag, but might not be aligned
			     * with it
			     */
			    addPercept(p.m_name, ASSyntax.parseLiteral("canSeeDefFlag"));
			    if (DEBUG_LOGGING)
                {
                    logger.info("Add canSeeDefFlag to " + p.m_name);
                }
			    if (defendingBox.m_direction == 0)
			    {
			        /**
                     * Player is aligned with the defending flag
                     */
			        addPercept(p.m_name, ASSyntax.parseLiteral("facingDefFlag")); 
			        if (DEBUG_LOGGING)
                    {
                        logger.info("Add facingDefFlag to " + p.m_name);
                    }
			    }
			    
			    if (defendingBox.m_distance < 10)
			    {
			        /**
                     * Player is at the defending flag
                     */
			        addPercept(p.m_name, ASSyntax.parseLiteral("inPositionToDefend")); 
			        if (DEBUG_LOGGING)
                    {
                        logger.info("Add inPositionToDefend to " + p.m_name);
                    }
			    }
			}

			if ((ball != null) && (ball.m_distance < 15.0))
			{
			    // Ball is close enough to the goal to be dangerous
			    addPercept(p.m_name, ASSyntax.parseLiteral("dangerBall"));
			    if (DEBUG_LOGGING)
                {
                    logger.info("Add dangerBall to " + p.m_name);
                }
			}
			
			if (defendingLine == null)
			{
			    /**
                 * Player is facing forwards (goal is at its back)
                 */
			    addPercept(p.m_name, ASSyntax.parseLiteral("facingForwards")); 
			    if (DEBUG_LOGGING)
                {
                    logger.info("Add facingForwards to " + p.m_name);
                }
			}
			    		
		} catch (Exception e) {}
	}
    /*
     * This function takes as parameters:
     * @p: Krislet object to whom the action should be applied
     * @number: an int, if less than number player(s) (of my team) are "closest" to the ball than me, 
     * then I will run with "normal" speed to the ball, else I will reduced my seed
     * returns true if number of closest than me is < number, else false
     */
	private boolean closestToBall(Krislet p, int number){
		int closestThanMe = 0;
		LinkedList<PlayerInfo> team = getTeamPlayers(p);
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
     * This function takes as parameters:
     * @player: Krislet object
     * returns in a LinkedList<PlayerInfo> all the players that I can see and are in the same team
     */
	public LinkedList<PlayerInfo> getTeamPlayers(Krislet player) 
    {
	LinkedList<PlayerInfo> players = new LinkedList<PlayerInfo>();
	if( player.m_memory.getInfo() == null )
	    return null;

	for(int c = 0 ; c < player.m_memory.getInfo().m_objects.size() ; c ++)
	    {
		ObjectInfo object = (ObjectInfo)player.m_memory.getInfo().m_objects.elementAt(c);
		if(object.m_type.compareTo("player")==0){
		    PlayerInfo p = (PlayerInfo) object;
		    if(p.m_teamName.equals(player.getTeam())){
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