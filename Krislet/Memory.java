//
//	File:			Memory.java
//	Author:		Krzysztof Langner
//	Date:			1997/04/28
//

class Memory 
{
    private boolean memoryReady;
    
    //---------------------------------------------------------------------------
    // This constructor:
    // - initializes all variables
    public Memory()
    {
        memoryReady = false;
    }


    //---------------------------------------------------------------------------
    // This function puts see information into our memory
    public void store(VisualInfo info)
    {
        memoryReady = true;
        m_info = info;
    }
    
    /**
     * Returns if the player memory is ready to be accessed.
     */
    public boolean getMemoryReady()
    {
        return memoryReady;
    }

    //---------------------------------------------------------------------------
    // This function looks for specified object
    public ObjectInfo getObject(String name) 
    {
        if( m_info == null )
            return null;
    
        for(int c = 0 ; c < m_info.m_objects.size() ; c ++)
        {
            ObjectInfo object = (ObjectInfo)m_info.m_objects.elementAt(c);
            if(object.m_type.compareTo(name) == 0)
                return object;
        }												 
    
        return null;
    }


    //---------------------------------------------------------------------------
    // This function waits for new visual information
    public void waitForNewInfo() 
    {
        // first remove old info
        m_info = null;
        // now wait until we get new copy
        while(m_info == null)
	    {
            // We can get information faster then 75 miliseconds
            try
            {
                Thread.sleep(SIMULATOR_STEP);
            }
            catch(Exception e)
		    {
		    }
	    }
    }


    //===========================================================================
    // Private members
    volatile private VisualInfo	m_info;	// place where all information is stored
    final static int SIMULATOR_STEP = 100;
}

