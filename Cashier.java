// DO NOT ADD NEW METHODS OR DATA FIELDS!

package PJ3;

class Cashier {

   // define constants for representing intervals
   static int BUSY = 1;
   static int FREE = 0;

   // start time and end time of current interval
   private int startTime;
   private int endTime;

   // cashier id and current customer which is served by this cashier 
   private int cashierID;
   private Customer currentCustomer;

   // for keeping statistical data
   private int totalFreeTime;
   private int totalBusyTime;
   private int totalCustomers;

   // Constructor
   Cashier()
   {
	this(0);
   }


   // Constructor with cashier id
   Cashier(int cashierId)
   {
	cashierID = cashierId;
        startTime = 0;
        endTime = 0;
        totalFreeTime = 0;
        totalBusyTime = 0;
        totalCustomers = 0;
	currentCustomer= null;
   }

   // accessor methods

   int getCashierID () 
   {
	return cashierID;
   }

   Customer getCustomer() 
   {
	return currentCustomer;
   }



   // need this to setup priority queue
   int getEndBusyIntervalTime() 
   {
	  return endTime; 
   }



   // functions for state transition
   // FREE -> BUSY :
   void freeToBusy (Customer currentCustomer, int currentTime)
   {
  	// Main goal  : switch from free interval to busy interval
  	//
  	// end free interval, start busy interval
  	// steps	: update totalFreeTime
  	// 		  set startTime, endTime, currentCustomer, 
  	// 		  update totalCustomers


	totalFreeTime+= (currentTime- endTime);
	this.currentCustomer= currentCustomer; // this.currentCustomer is a datafield
	startTime=currentTime; //change startTime to the currentTime
	endTime= startTime + this.currentCustomer.getCheckoutDuration();
	totalCustomers++;
	
   }

   // BUSY -> FREE :
   Customer busyToFree()
   {
   	// Main goal : switch from busy interval to free interval
   	// 
  	// steps     : update totalBusyTime 
  	// 	       	set startTime 
  	//             return currentCustomer

	
	totalBusyTime= totalBusyTime + (endTime - startTime);
	startTime= endTime;
	
	return this.getCustomer();
   }



   // need this method at the end of simulation to update cashier data
   void setEndIntervalTime (int endsimulationtime, int intervalType)
   {
  	// for end of simulation
  	// set endTime, 
  	// for FREE interval, update totalFreeTime
  	// for BUSY interval, update totalBusyTime

	endTime= endsimulationtime;
	if(FREE == intervalType){
		totalFreeTime+= (endTime-startTime);
	}else{
		totalBusyTime+= (endTime-startTime);
		
	}
    }

   // functions for printing statistics :
   void printStatistics () 
   {
  	// print cashier statistics, see project statement

  	System.out.println("\t\tCashier ID             : "+cashierID);
  	System.out.println("\t\tTotal free time        : "+totalFreeTime);
  	System.out.println("\t\tTotal busy time        : "+totalBusyTime);
  	System.out.println("\t\tTotal # of customers   : "+totalCustomers);
       if (totalCustomers > 0){
  	    System.out.format("\t\tAverage checkout time  : %.2f%n\n",(totalBusyTime*1.0)/totalCustomers);
       }
       System.out.println();
   }

   public String toString()
   {
	return "Cashier:"+cashierID+":"+startTime+"-"+endTime+":Customer:"+currentCustomer;
   }

   public static void main(String[] args) {
        // quick check
        Customer mycustomer = new Customer(20,30,40);
	Cashier mycashier = new Cashier(5);
        mycashier.freeToBusy (mycustomer, 13); 
        System.out.println(mycashier);

   }

}

