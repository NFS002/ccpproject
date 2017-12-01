package net.transactions;

import net.Json.Identifiable;

import java.util.ArrayList;
import java.util.Date;

import javax.security.auth.login.AccountException;

public class Contract implements Identifiable {

	
	private final String CID;
	
	private final String numberTo;
	
	private final String numberFrom;
	
	private final Date dc;

    private final String value;
    
    boolean confirmed;

    private ArrayList<Product> products;

    public Contract(String CID, String numberTo, String numberFrom, Date dc, String value, boolean confirmed, ArrayList<Product> products) {
        this.CID = CID;
    		this.products = products;
        this.value = value;
        this.numberTo = numberTo;
        this.numberFrom = numberFrom;
        this.confirmed = confirmed;
        this.dc = dc;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void addProduct(Product product) {
		this.products.add(product);
	}
    
    public String getValue() {
        return value;
    }

    public String getAcctNumFrom() {
        return numberFrom;
    }

    public String getAcctNumTo() {
        return numberTo;
    }

    public Date getCreated() {
        return dc;
    }

    public boolean getConfirmed() {
        return confirmed;
    }

    public String getId() {
		return CID;
	}

    public void setProducts(ArrayList<Product> products) {
    		this.products = products;
    }
    
	public void confirm() throws Exception {
        if (this.confirmed) throw new AccountException("Contract has already been confirmed");
        this.confirmed = true;
    }
    
    @Override
   	public boolean equals(Object obj) {
   		return (obj instanceof Contract && (((Contract) obj).getId().equals(getId())));
   	}
}
