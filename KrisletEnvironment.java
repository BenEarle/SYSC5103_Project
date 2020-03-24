// Environment code for project SYSC5103_Project.mas2j

import java.io.*;
import java.net.*;
import jason.asSyntax.*;
import jason.environment.*;
import jason.mas2j.*;
import java.util.logging.*;
import java.util.*;

public class KrisletEnvironment extends Environment 
{
    public static final int C_MAX_PLAYERS = 5;
    public static final int C_INVALID_PLAYER = -99;
    
    public static final Term    init = Literal.parseLiteral("initialize");
	public static final Term    turn40 = Literal.parseLiteral("turn(40)");

    private Logger logger = Logger.getLogger("SYSC5103_Project.mas2j." + KrisletEnvironment.class.getName());
	private Krislet[] player;

    /** Called before the MAS execution with the args informed in .mas2j */
    @Override
    public void init(String[] args) 
	{
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

    @Override
    public boolean executeAction(String agName, Structure action) 
	{
	    int playerNumber = C_INVALID_PLAYER;
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
		
		playerNumber = getPlayerNumber(agName);
		if (playerNumber == C_INVALID_PLAYER)
		{
		     return false;   
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
		
		if (action.equals(init))
		{
		    /**
		     * The player needs to wait for its memory to initialize before it
		     * can do anything useful.
		     */
		}
		else if (action.equals(turn40))
		{
			player[playerNumber].turn(40);
		} 
        else 
		{
			logger.info("executing: " + action + ", but not implemented!");
			return false;
        }

		updatePlayer(player[playerNumber]);
		informAgsEnvironmentChanged(agName);
		
        return true; // the action was executed with success
    }

	
    /** Called before the end of MAS execution */
    @Override
    public void stop() 
	{
        super.stop();
    }
	
	/** Private helper functions*/
	private void updatePlayer(Krislet p)
	{
		try 
		{
			clearPercepts(p.m_name);
			
			/* Check to see if everything has been initialized. */
			if (p.m_memory.getMemoryReady())
			{
			    /**
			     * The Krislet object has received its first visual update, so
			     * we should be able to start figuring out where things are.
			     */
			    addPercept(p.m_name, ASSyntax.parseLiteral("environment_ready"));
			}
			
			ObjectInfo object = p.m_memory.getObject("ball");
			if (object != null)
			{
			    /**
			     * This player knows that a ball exists.
			     */
			    addPercept(p.m_name, ASSyntax.parseLiteral("ball_found"));
			}
			
			
//			if( object == null )
//			{
				/**
				 * This player doesn't know where the ball is, so remove its
				 * perception of the ball.
				 */
//				removePercept(ASSyntax.parseLiteral("ball"));
//				logger.info("Ball was not seen");
//			} 
//			else 
//			{
//				addPercept(p.m_name, ASSyntax.parseLiteral("ball"));   
		    
//				logger.info("Ball was seen");
				/*if( object.m_distance > 1.0 ) {
					// The ball is out of range
					if( object.m_direction != 0 ){
						// Not facing the ball
					} else{
						// Facing the ball
					}
				}*/
//			}
			//System.out.println("Got Ball info");
			// Look for the goal
/*			if( p.m_side == 'l' )
				object = p.m_memory.getObject("goal r");
			else
				object = p.m_memory.getObject("goal l");
	
			if( object == null ) {
				// Cannot see the goal
			} else {
				// Can see the goal	
			}		
	*/	
	    }
	    catch (Exception e) 
	    {
	        e.printStackTrace();
		    return;
	    }
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


