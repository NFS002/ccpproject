package net.accounts;

import java.util.Date;
import java.security.KeyPair;
import java.util.ArrayList;

import net.Json.Identifiable;
import net.transactions.Contract;
import net.transactions.Product;

public class Account implements Identifiable {
    
	private final String accountId;
	
	private String name;
	
	private Date dob;
	
	private String username;

    private byte[] pwHash;
    
    private Date dc;
    
    private String email;

    private String phoneNumber;
    
    private String address;

    private String type;
    
    private KeyPair keypair;
    
    private KYC kyc;
    
    private ArrayList<Contract> contracts;
    
    private ArrayList<Product> products;
    
    public Account(String accountId, String name, Date dob, String username, byte[] pwHash, Date dc, String email,
			String phoneNumber, String address, String type,KeyPair keypair, KYC kyc, ArrayList<Contract> contracts, ArrayList<Product> products) {
    	
		this.accountId = accountId;
		this.name = name;
		this.dob = dob;
		this.username = username;
		this.pwHash = pwHash;
		this.dc = dc;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.type = type;
		this.keypair = keypair;
		this.kyc = kyc;
		this.contracts = contracts;
		this.products = products;
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public byte[] getPwHash() {
		return pwHash;
	}
	
	public void setPwHash(byte[] pwHash) {
		this.pwHash = pwHash;
	}
	
	public Date getDc() {
		return dc;
	}

	public void setDc(Date dc) {
		this.dc = dc;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return accountId;
	}

	public ArrayList<Product> getProducts() {
		return products;
	}

	public void setProducts(ArrayList<Product> products) {
		this.products = products;
	}
	
	public ArrayList<Contract> getContracts() {
		return contracts;
	}

	public void setContracts(ArrayList<Contract> contracts) {
		this.contracts = contracts;
	}

	public KeyPair getKeypair() {
		return keypair;
	}

	public void setKeypair(KeyPair keypair) {
		this.keypair = keypair;
	}

	public KYC getKyc() {
		return kyc;
	}

	public void setKyc(KYC kyc) {
		this.kyc = kyc;
	}

    public void transferProductIn(Product p) {
        if(getProducts().contains(p)) return; 
        getProducts().add(p);
    }

    public boolean removeProductOut(Product p){
        if (!getProducts().contains(p)) return false;
        getProducts().remove(p);
        return true;
    }

	
    @Override
	public boolean equals(Object obj) {
		return (obj instanceof Account && (((Account) obj).getId().equals(getId())));
	}

    
}
