package PJ3;

import java.util.*;
import java.io.*;

// You may add new functions or data in this class 
// You may modify any functions or data members here
// You must use Customer, Cashier and CheckoutArea
// to implement your simulator

class CheckoutAreaSimulator {

  // input parameters
  private int numCashiers, customerQLimit;
  private int simulationTime, dataSource;
  private int chancesOfArrival, maxCheckoutTime;

  // statistical data
  private int numGoaway, numServed, totalWaitingTime;

  // internal data
  private int customerIDCounter;
  private CheckoutArea checkoutarea; // checkout area object
  private Scanner dataFile;	     // get customer data from file
  private Random dataRandom;	     // get customer data using random function

  // most recent customer arrival info, see getCustomerData()
  private boolean anyNewArrival;  
  private int checkoutTime;

  // initialize data fields
  private CheckoutAreaSimulator()
  {
      numCashiers = 0;
      customerQLimit = 0;
      simulationTime = 0;
      dataSource = 0;
      chancesOfArrival = 0;
      maxCheckoutTime = 0;
      numGoaway = 0;
      numServed = 0;
      totalWaitingTime = 0;
      customerIDCounter = 0; 
      checkoutarea = null;
      dataFile = null;
      dataRandom= new Random();
      anyNewArrival = false;
      checkoutTime = 0;
      
  }

  private void setupParameters()
  {
	// add statements, read input parameters
	// setup dataFile or dataRandom
      Scanner input = new Scanner(System.in);
      do{
          System.out.println("ENTER NUMBER OF CASHIERS BETWEEN 1-10: ");
          numCashiers= input.nextInt();
      }while((numCashiers<1) || (numCashiers>10));
      
      do{
          System.out.println("ENTER A SIMULATION TIME BETWEEN 1-10,000 UNITS: ");
          simulationTime= input.nextInt();
      }while((simulationTime<1) || (simulationTime>10000)); 
      
      
      do{
          System.out.println("ENTER A CUSTOMER LINE LIMIT BETWEEN 1-50: ");
          customerQLimit= input.nextInt();
      }while((customerQLimit<1) || (customerQLimit>50));
      
      do{
          System.out.println("ENTER A MAXIMUM CHECKOUT TIME BETWEEN 1-500: ");
          maxCheckoutTime= input.nextInt();
      }while((maxCheckoutTime<1) || (maxCheckoutTime>500));
      
      do{
          System.out.println("ENTER A CUSTOMER'S CHANCE OF ARRIVAL BETWEEN 1-100%: ");
          chancesOfArrival= input.nextInt();
      }while((chancesOfArrival<1) || (chancesOfArrival>100));
          
      
      do{
          System.out.println("ENTER 1/0 TO GET CUSTOMER DATA FROM FILE/RANDOM: ");
          dataSource= input.nextInt();
      }while((dataSource<0) || (dataSource>1));
      
      if (dataSource == 1){    
          
          System.out.println("Enter filename:");
          //this asks the user to enter a filename
          String file;
          Scanner scanner = new Scanner(System.in);
          file = scanner.nextLine();
          //checks if the filename by the user exists
          try{
              dataFile = new Scanner(new File(file)); //scan the file
          }
          catch (FileNotFoundException e){
              System.out.println("Cannot open the file: " + file);
          }
          scanner.close();
      }
      input.close();
      
  }

  private void getCustomerData() 
  {
	// get next customer data : from file or random number generator
	// set anyNewArrival and checkoutTime
      
    // Data Source is from File
    if (dataSource == 1){
    int data1 = dataFile.nextInt(); // get the data for anyNewArrival
    int data2 = dataFile.nextInt(); //get the data for checkoutTime
      
      anyNewArrival = (((data1%100)+1)<= chancesOfArrival);
      checkoutTime= (data2 % maxCheckoutTime)+1;
  
    // Data Source is from Random Number Generator
    }else { 
        anyNewArrival = ((dataRandom.nextInt(100)+1) <= chancesOfArrival); // new Customer created when anyNewArrival is true
        checkoutTime = dataRandom.nextInt(maxCheckoutTime)+1; // checkoutTime is between 1 and maxCheckoutTime
        }
  }

  private void doSimulation()
  {

	// Initialize CheckoutArea, Create numCashier objects in the cashierQ
      checkoutarea = new CheckoutArea( numCashiers, customerQLimit, 1);

	// Time driver simulation loop
  	for (int currentTime = 0; currentTime < simulationTime; currentTime++) {
        //Print out the current time:
        System.out.println("Time : "+ currentTime);
        
        // Get the customer's chance of arrival and checkout time from File/Random
        getCustomerData();
        //Check the probability of arrival
        if (anyNewArrival) {
            
            // Create a new customer, customerIDcounter starts from 1
            Customer client= new Customer((customerIDCounter+1),checkoutTime,currentTime);
            // Print new customer's statistics
            System.out.println("\tcustomer #"+(customerIDCounter+1)+" arrives with checkout time "+checkoutTime+" units");
            // Increment the total number of customers that have arrived
            customerIDCounter++;
            
            // If the customer line is NOT too long, then add customer to Customer Queue
            if(!checkoutarea.isCustomerQTooLong()){
                checkoutarea.insertCustomerQ(client);
            // Otherwise customer Goes away because line is too long
            } else {
                System.out.println("\tCustomer goes away because line is too long");
                numGoaway++; //Increment the number of Customers who Don't enter the long line
            }    
                
        }else {
                System.out.println("\tNo new customer!");
        }
        
        // check endtime of all busy cashiers who's endtime is done, remove busy from queue, busytofree, add to freeCashierQ
        int numB= checkoutarea.numBusyCashiers();
        for(int j= 0; j<numB; j++){
          
            Cashier busyCashier= checkoutarea.getFrontBusyCashierQ(); //peek, return null if No busy cashier
          if((busyCashier.getEndBusyIntervalTime() == currentTime)&& (busyCashier!=null)) {
              busyCashier= checkoutarea.removeBusyCashierQ();
              Customer doneCustomer= busyCashier.busyToFree(); // returns a customer
              // Print this Customer is done
              System.out.println("\tcustomer #"+doneCustomer.getCustomerID()+" is done");
              //Print this Cashier is free
              System.out.println("\tcashier #"+busyCashier.getCashierID()+" is free");
              //Add the busyCashier to the freeCashierQ
              checkoutarea.insertFreeCashierQ(busyCashier);
           }
          
        }
      
        // remove all free Cashiers, remove customers from queue, freetobusy, add cashiers to busyQ one at a time
        int numF= checkoutarea.numFreeCashiers();
        for(int i=0; ((i<numF) && (!checkoutarea.emptyCustomerQ())) ; i++){
            
            Cashier removedCashier= checkoutarea.removeFreeCashierQ();
            Customer removedCustomer= checkoutarea.removeCustomerQ();
            //Update the Total waiting time for each customer to get a cashier to help them
            totalWaitingTime+= currentTime - removedCustomer.getArrivalTime();
            removedCashier.freeToBusy(removedCustomer, currentTime);
            // Print this Customer gets a cashier
            System.out.println("\tcustomer #"+removedCustomer.getCustomerID()+" gets a cashier");
            // Print this Cashier starts serving a Customer
            System.out.println("\tcashier #"+removedCashier.getCashierID()+" starts serving customer #"+removedCustomer.getCustomerID()+" for "+removedCustomer.getCheckoutDuration()+ " units");
            // Increment the total number of customers being served
            numServed++;
            checkoutarea.insertBusyCashierQ(removedCashier);
        }
    
    }
      
  }

  private void printStatistics()
  {
	// print out simulation results
	// see the given example in project statement
    // you need to display all free and busy cashiers
      double aveWaitTime= 0.0;
      System.out.println("\nEnd of Simulation Report:");
      System.out.println("\t# total arrival customers  : "+customerIDCounter);
      System.out.println("\t# customers gone-away      : "+numGoaway);
      System.out.println("\t# customers served         : "+numServed);
      
      System.out.println("\n*** Current Cashiers Info. ***");
      checkoutarea.printStatistics();
      System.out.println("\tTotal waiting time         : "+totalWaitingTime);
      if(numServed==0){
          aveWaitTime= 0.0;
      }else{
          aveWaitTime= totalWaitingTime/numServed;
      }
      System.out.println("\tAverage waiting time       : "+ aveWaitTime);
      
      System.out.println("Busy Cashiers:");
      // Get the number of Busy Cashiers in the Queue
      int numB=checkoutarea.numBusyCashiers();
      for(int i=0;i<numB; i++){
          Cashier leftoverBusyCashier= checkoutarea.removeBusyCashierQ();
          leftoverBusyCashier.setEndIntervalTime (simulationTime, 1);
          leftoverBusyCashier.printStatistics();
      }
                
      System.out.println("Free Cashiers:");
      // Get the number of Free Cashiers in the Queue
      int numF= checkoutarea.numFreeCashiers();
      for(int i=0;i<numF; i++){
          Cashier leftoverFreeCashier= checkoutarea.removeFreeCashierQ();
          leftoverFreeCashier.setEndIntervalTime (simulationTime, 0);
          leftoverFreeCashier.printStatistics();
      }
      
  }

  // *** main method to run simulation ****

  public static void main(String[] args) {
   	CheckoutAreaSimulator runCheckoutAreaSimulator=new CheckoutAreaSimulator();
   	runCheckoutAreaSimulator.setupParameters();
   	runCheckoutAreaSimulator.doSimulation();
   	runCheckoutAreaSimulator.printStatistics();
  }

}
