# Dstributed-System-Prototype


Problem: Your system cannot host a huge volume of information and data records and accept and answer queries from clients. 
Business Inquiries: ijespinoza00@gmail.com

Solution: Implement a scalable distributed query system.

Blueprint: 
       
- System Units - the query system is composed of the following units:  
    - Client, the machine that a client can use to build and send a search query then display results back. 
    - Peer Machines, the different machines in your system that host resources (data records), accept queries, and 
      respond accordingly.

- Peer Machine – the host of resources in the system: 
    - Each machine has a unique name/ID and a local directory (a folder). 
    - Each machine gets queries only from the client, the machine answers the query from the hosted files, and 
      responds back to the user. 
    - Machine’s response are JSON-formatted. 
    - Upon receiving the query, the machine employes the actor model to divide the work (the files and the 
      query) on the workers for parallel computation.  

- Client– the user interface to the system 
    - A user using this machine and the available interface can connect to the system, build a query command, and 
      wait for response. 
    - Client’s request are JSON-formatted 

- Communication
    - A global/public MQTT broker (no need to import a local broker or implement a broker) 

- System requirement – because of time limitation  
    - The system must be able to run a Java file. 

------------------------------------------------------------------------------------------------------------------------------------





