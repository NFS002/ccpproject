package net.transactions;

import net.Json.Identifiable;

import java.util.ArrayList;
import java.util.Date;



public class Product implements Identifiable {
		
		private final String productNum;
		
		private final Date dc;
		 
		private final String description;
	
		private final String size;

	    private final String RRP;
	    
	    private String owner;

	    private ArrayList<Contract> contracts;
	     
	    public Product(String productNum, Date dc, String description, String size, String RRP, String owner,
				ArrayList<Contract> contracts) {
			this.productNum = productNum;
			this.dc = dc;
			this.description = description;
			this.size = size;
			this.RRP = RRP;
			this.owner = owner;
			this.contracts = contracts;
		}
	    
		public ArrayList<Contract> getContracts() {
			return contracts;
		}

		public void setContracts(ArrayList<Contract> contracts) {
			this.contracts = contracts;
		}
		
		public void addContracts(Contract contract) {
			this.contracts.add(contract);
		}

		public String getId() {
			return productNum;
		}

		public Date getDc() {
			return dc;
		}

		public String getDescription() {
			return description;
		}

		public String getSize() {
			return size;
		}

		public String getRRP() {
			return RRP;
		}

		public String getOwner() {
			return owner;
		}

		public void setOwner(String ownerNumber) {
			this.owner = ownerNumber;
		}

	    @Override
	    public boolean equals(Object obj) {
	        return obj instanceof Product && getId().equals(((Product) obj).getId());
	    }
}
