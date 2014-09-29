////////////////////////////////////////////////////////////////////////////////
// Command proccesor interface.
//
// This interface will be called by CommandQueue to process commands.
//
// Author: zackchiu@realtek.com
//
// Created: 11/19/2010
////////////////////////////////////////////////////////////////////////////////

#ifndef __COMMAND_PROCESSOR__
#define __COMMAND_PROCESSOR__



template <typename T> class CommandProcessor
{
    public:
        // Execute command
        //
        //  Parameters:
        //      1. cmd: The command wants to be executed.
        //
        //  Return:
        //      true --> Success.
        //      false--> Failed.
        //
        virtual bool Execute(T cmd) = 0;
        
    public:
        virtual ~CommandProcessor() { ; };
};



#endif
