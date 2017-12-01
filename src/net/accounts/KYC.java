package net.accounts;

import java.awt.Image;

public class KYC {
	
	private final String number;
	
	private final Image id;

    private final Image proofOfAddress;

    public KYC(String number,Image id, Image proofOfAddress) throws Exception {
        this.number = number;
    	    this.id = id;
        this.proofOfAddress = proofOfAddress;
    }

	
    public String getNumber() {
		return number;
	}


	public Image getId() {
		return id;
	}

	
    public Image getProofOfAddress() {
		return proofOfAddress;
	}

    

}
