// DO NOT ADD NEW METHODS OR DATA FIELDS!

package PJ3;

class Customer
{
    private int customerID;
    private int checkoutDuration;
    private int arrivalTime;
    //Default Constructor
    Customer()
    {
        this(0,0,0);
    }
    // Constructor 
    Customer(int customerid, int checkoutduration, int arrivaltime)
    {
        customerID= customerid;
        checkoutDuration= checkoutduration;
        arrivalTime= arrivaltime;
    }

    int getCheckoutDuration() 
    {
        return checkoutDuration;
    }

    int getArrivalTime() 
    {
        return arrivalTime;
    }

    int getCustomerID() 
    {
        return customerID; 
    }

    public String toString()
    {
    	return ""+customerID+":"+checkoutDuration+":"+arrivalTime;

    }

    public static void main(String[] args) {
        // quick check!
	Customer mycustomer = new Customer(20,30,40);
	System.out.println("Customer Info:"+mycustomer);

    }
}
