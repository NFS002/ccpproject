package net.database;

import javax.security.auth.login.AccountException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.Json.AccountSerializer;
import net.Json.ContractSerializer;
import net.Json.ProductSerializer;
import net.accounts.Account;
import net.accounts.KYC;
import net.transactions.Contract;
import net.transactions.Product;

import java.awt.*;
import java.io.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;


/**
 * Created by nfs on 02/08/2017.
 */
public class CCPUtils {

    private Connection connection;
        
    private CCPUtils.SessionManager smg = new CCPUtils.SessionManager();

    public CCPUtils() throws Exception{
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
        this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ccp?verifyServerCertificate=false&useSSL=true", "root", "Flanger69");
    }

	public SessionManager getSmg() {
		return smg;
	}

	public String newAccountNumber() throws Exception {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append(random.nextInt(9) + 1);
        for (int i = 0; i < 4; i++)
            sb.append(random.nextInt(10));
        sb.append('-');
        int t = ((int) Math.floor(Math.cbrt(System.nanoTime())));
        sb.append(String.valueOf(t));
        String num = "A" + sb.toString();
        if (!checkNumberAvailability(num)) newAccountNumber();
        return num;
    }

    public String newContractNumber() throws Exception {
		Random random = new Random();
		int i = ((int) Math.cbrt(System.nanoTime() * random.nextInt()));
		String cid = String.valueOf(Math.abs(i));
        if (!checkCidAvailability(cid)) newContractNumber();
        return cid;
    }
    
    public String newProductNumber() throws Exception {
        Random random = new Random();
		int i = ((int) Math.cbrt(System.nanoTime() * random.nextInt()));
        String pid = String.valueOf(Math.abs(i));
        if (!checkPidAvailability(pid)) {
        	newProductNumber();
		}
        return pid;
    }

    
    public void saveAccount(Account account) throws Exception {
        String number = account.getId();
        String name = account.getName();
        Date dob = account.getDob();
		String username = account.getUsername();
		byte[] pwHash = account.getPwHash();
		Date dc = account.getDc();
		String email = account.getEmail();
		String phone = account.getPhoneNumber();
		String address = account.getAddress();
		String type = account.getType();
		KeyPair keypair = account.getKeypair();
		KYC kyc = account.getKyc();
        String q = "REPLACE into accounts (number,name,dob,uname,pwHash,dc,email,phone,address,type) values (?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement pstmt = connection.prepareStatement(q);
        pstmt.setString(1, number);
        pstmt.setString(2,name);
        pstmt.setDate(3,new java.sql.Date(dob.getTime()));
        pstmt.setString(4,username);
        pstmt.setBytes(5,pwHash);
        pstmt.setDate(6,new java.sql.Date(dc.getTime()));
        pstmt.setString(7,email);
        pstmt.setString(8,phone);
        pstmt.setString(9,address);
        pstmt.setString(10,type);
        pstmt.executeUpdate();
        if (keypair != null) saveKeyPair(account);
        if (kyc != null) saveKYC(account);
    }
    
    public void changeUserEmail(String aid, String email) throws Exception {
    		String q = "UPDATE accounts SET email = ? WHERE number = ? ";
    		PreparedStatement ps = connection.prepareStatement(q);
    		ps.setString(1,email);
    		ps.setString(2, aid); 
    		if (ps.executeUpdate() != 1) throw new AccountException();   		
    }
    
    public void changeUserPhone(String aid, String phone) throws Exception {
		String q = "UPDATE accounts SET phone =  ? WHERE number = ? ";
		PreparedStatement ps = connection.prepareStatement(q);
		ps.setString(1,phone);
		ps.setString(2, aid); 	
    }
    
    public void changeUserAddress(String aid, String address) throws Exception {
		String q = " UPDATE accounts SET address = ? WHERE number = ? ";
		PreparedStatement ps = connection.prepareStatement(q);
		ps.setString(1,address);
		ps.setString(2, aid); 
		if (ps.executeUpdate() != 1) throw new AccountException();
    }
    
    public void changeUserPassword(String aid, String pw) throws Exception {
		String q = "UPDATE accounts SET pwHash = ? WHERE number = ? ";
		PreparedStatement ps = connection.prepareStatement(q);
		ps.setBytes(1,hash(pw));
		ps.setString(2, aid);
		ps.executeUpdate();
		if (ps.executeUpdate() != 1) throw new AccountException();
    }
    
    
    public void saveKeyPair(Account account) throws Exception {	
		String q = "REPLACE into Keypairs (number,public,private) values (?,?,?)";
		PreparedStatement pstmt = connection.prepareStatement(q);
		String number = account.getId();
		KeyPair keypair = account.getKeypair();
		pstmt.setString(1, number);
		pstmt.setBinaryStream(2, serialize(keypair.getPublic()));
		pstmt.setBinaryStream(3, serialize(keypair.getPrivate()));
		pstmt.executeUpdate();
    }
    
    
    public void saveKYC(Account account) throws Exception {
    		String q = "REPLACE into KYC (number,image,address) values (?,?,?)";
    		PreparedStatement pstmt = connection.prepareStatement(q);
    		String number = account.getId();
    		KYC kyc = account.getKyc();
    		pstmt.setString(1, number);
    		pstmt.setBinaryStream(2, serialize(kyc.getId()));
    		pstmt.setBinaryStream(3, serialize(kyc.getProofOfAddress()));
    		pstmt.executeUpdate();
 
    }
    
    
    public void saveNewContract(Contract c) throws Exception {
		String q = "REPLACE INTO contracts (cid,numberTo,numberFrom,dc,value,confirmed) values (?,?,?,?,?,?)";
		PreparedStatement pstmt = connection.prepareStatement(q);
		String cid = c.getId();
		String nT = c.getAcctNumTo();
		String nF = c.getAcctNumFrom();
		Date dc = c.getCreated();
		String value = c.getValue();
		boolean confirmed = c.getConfirmed();
		pstmt.setString(1, cid);
		pstmt.setString(2, nT);
		pstmt.setString(3, nF);
		pstmt.setDate(4, new java.sql.Date(dc.getTime()));
		pstmt.setString(5,value);
		pstmt.setBoolean(6, confirmed);
		pstmt.executeUpdate();	    
		//Part 2
		ArrayList<Product> products = c.getProducts();
		q = "REPLACE INTO ContractsJProducts (PID,CID) VALUES (?,?)";
		pstmt = connection.prepareStatement(q);
		pstmt.setString(2, cid);
		for(Product product:products) {
			String pid = product.getId();
			pstmt.setString(1, pid);
			pstmt.executeUpdate();
		}
     }
    
    
    public void registerNewProduct(Product p) throws Exception {
    	String q = "REPLACE into products (pid,dc,description,size,rrp,owner) values (?,?,?,?,?,?)";
		PreparedStatement pstmt = connection.prepareStatement(q);
		String pid = p.getId();
		Date dc = p.getDc();
		String description = p.getDescription();
		String size = p.getSize();
		String rrp = p.getRRP();
		String owner = p.getOwner();
		pstmt.setString(1, pid);
		pstmt.setDate(2, new java.sql.Date(dc.getTime()));
		pstmt.setString(3, description);
		pstmt.setString(4,size);
		pstmt.setString(5, rrp);
		pstmt.setString(6, owner);
		pstmt.executeUpdate();       
     }
    
    public void createAndSaveNewProduct(String aid, String pid, String size, String rrp, String description) throws SQLException {
    		String q = "REPLACE into products (pid,dc,description,size,rrp,owner) values (?,?,?,?,?,?)";
		PreparedStatement pstmt = connection.prepareStatement(q);
		pstmt.setString(1, pid);
		pstmt.setDate(2, new java.sql.Date(new Date().getTime()));
		pstmt.setString(3, description);
		pstmt.setString(4,size);
		pstmt.setString(5, rrp);
		pstmt.setString(6, aid);
		pstmt.executeUpdate();   
    }

	public void updateProductOwner(String pid,String owner) throws Exception {
		if (checkPidAvailability(pid)) throw new AccountException("The product " + pid + " has not been registered, and so cannot be updated");
		if (checkNumberAvailability(owner)) throw new AccountException("The Account " + owner + " is not valid");
		String q = "UPDATE products SET owner = ? WHERE pid = ?";
		PreparedStatement pstmt = connection.prepareStatement(q);
		pstmt.setString(1,owner);
		pstmt.setString(2,pid);
		pstmt.executeUpdate();
	}
    
    //TODO: account verification
    
    public Account getAccount(String username, String password) throws Exception {
		 Account account = null;
         byte[] pwHashIn = hash(password);
         String q = "SELECT * FROM accounts WHERE uname = ?";
         PreparedStatement ps = connection.prepareStatement(q);
         ps.setString(1,username);
         ResultSet rs = ps.executeQuery();
         if (rs.first()) {
        	 	byte[] pwHash = trimBytes(rs.getBytes("pwHash"));
        	 	if (Arrays.equals(pwHashIn,pwHash)) {
					String number = rs.getString("number");
					String name = rs.getString("name");
        	 		Date dob = rs.getDate("dob");
					Date dc = rs.getDate("dc");
					String email = rs.getString("email");
					String phone = rs.getString("phone");
					String address = rs.getString("address");
					String type = rs.getString("type");
					KYC kyc = getKYC(number);
					KeyPair keypair = getKeyPair(number);
					account = new Account(number, name, dob, username, pwHashIn, dc, email, phone, address, type, keypair, kyc, new ArrayList<Contract>(), new ArrayList<Product>());
					this.smg.addRef(number, account);
					account.setContracts(getAllAccountContracts(number));
					account.setProducts(getAllAccountProducts(number));
				}
          }
        return account;
    } 
    
    public Contract getContractDetails(String cid) throws Exception {
    		Contract contract = null;
        String q = "SELECT * FROM contracts WHERE cid = ?";
        PreparedStatement ps = connection.prepareStatement(q);
        ps.setString(1,cid);
        ResultSet rs = ps.executeQuery();
        if (rs.first()) {
			 String numberTo = rs.getString("numberTo");
			 String numberFrom = rs.getString("numberFrom");
       	 	 Date dc = rs.getDate("dc");
			 String value = rs.getString("value");
			 boolean confirmed = rs.getBoolean("confirmed");
			 ArrayList<Product> products = this.getProductsInfoOfContract(cid);
			 contract = new Contract(cid,numberTo,numberFrom,dc,value,confirmed,products);
         }
       return contract;
   } 

    public Account getAccount(String number) throws Exception {
		Account account = null;
		ArrayList<Contract> contracts = new ArrayList<Contract>();
		ArrayList<Product> products = new ArrayList<Product>();
         String q = "SELECT * FROM accounts WHERE number = ? ";
         PreparedStatement ps = connection.prepareStatement(q);
         ps.setString(1,number);
         ResultSet rs = ps.executeQuery();
         if (rs.first()) {
        	 	String name = rs.getString("name");
        	 	String uname = rs.getString("uname");
        	 	byte[] pwHash = rs.getBytes("pwHash");
        	 	Date dob = rs.getDate("dob");
        	 	Date dc = rs.getDate("dc");     	 	
			    String email = rs.getString("email");
            	String phone = rs.getString("phone");
            	String address = rs.getString("address");
            	String type = rs.getString("type");
            	KYC kyc = getKYC(number);
            	KeyPair keypair = getKeyPair(number);
            	account = new Account(number,name,dob,uname,pwHash,dc,email,phone,address,type,keypair,kyc,contracts,products);
            	this.smg.addRef(number, account);
            	account.setContracts(getAllAccountContracts(number));
            account.setProducts(getAllAccountProducts(number));
          }
        return account;
    }
     
    public KYC getKYC(String number) throws Exception {
    		KYC kyc = null;
    		String q = "SELECT * FROM KYC where number = ?";
    		PreparedStatement ps = connection.prepareStatement(q);
    		ps.setString(1, number);
    		ResultSet rs = ps.executeQuery();
    		if (rs.first()) {
    			Image id = ((Image) deserialize(rs.getBinaryStream("image")));
    			Image address = ((Image) deserialize(rs.getBinaryStream("address")));
    			kyc = new KYC(number,id,address);
    		}
    		return kyc;
    }
   
    public KeyPair getKeyPair(String number) throws Exception {
		KeyPair keypair = null;
		String q = "SELECT * FROM keypairs where number = ?";
		PreparedStatement ps = connection.prepareStatement(q);
		ps.setString(1, number);
		ResultSet rs = ps.executeQuery();
		if (rs.first()) {
			PublicKey pub = ((PublicKey) deserialize(rs.getBinaryStream("public")));
			PrivateKey priv = ((PrivateKey) deserialize(rs.getBinaryStream("private")));
			keypair = new KeyPair(pub,priv);
		}
		else throw new AccountException("Cannot retrieve keypair for account");		
		return keypair;
    }
      
    public ArrayList<Contract> getAllAccountContracts(String number) throws Exception {
    	ArrayList<Contract> contracts = new ArrayList<>();
		ArrayList<Product> products = new ArrayList<>();
		String sql = "SELECT * FROM contracts WHERE numberTo = ? OR numberFrom = ?";
		PreparedStatement ps = connection.prepareStatement(sql);
		ps.setString(1, number);
		ps.setString(2,number);
		ResultSet rs = ps.executeQuery();
		while (rs.next()){
			String cid = rs.getString("cid");
			String numberTo = rs.getString("numberTo");
			String numberFrom = rs.getString("numberFrom");
			Date dc = rs.getDate("dc");
			String value = rs.getString("value");
			boolean confirmed = rs.getBoolean("confirmed");
			Contract contract = this.smg.referencedContracts.get(cid);
			if (contract == null) {
				contract = new Contract(cid,numberTo,numberFrom,dc,value,confirmed,products);
				smg.addRef(cid, contract);
				contract.setProducts(getProductsOfContract(cid));
			}
			contracts.add(contract);
			products = getProductsOfContract(cid);
		}
		return contracts;
   }
        
    public ArrayList<Product> getAllAccountProducts(String number) throws Exception {
		ArrayList<Product> products = new ArrayList<>();
		String sql = "SELECT * FROM PRODUCTS WHERE OWNER = ?";
		PreparedStatement ps = connection.prepareStatement(sql);
		ps.setString(1, number);
		ResultSet rs = ps.executeQuery();
		while (rs.next()){
			String pid = rs.getString("pid");
			Date dc = rs.getDate("dc");
			String description = rs.getString("description");
			String size = rs.getString("Size");
			String rrp = rs.getString("RRP");
			Product product = this.smg.referencedProducts.get(pid);
			if (product == null) {
				product = new Product(pid,dc,description,size,rrp,number,new ArrayList<Contract>());
				smg.addRef(pid, product);
				product.setContracts(getContractsOfProduct(pid));
			}			
			products.add(product);
		}
		return products;
    }
    
    
    public ArrayList<Contract> getContractsOfProduct(String pid) throws Exception {
    		String q = "SELECT cid from ContractsJProducts where pid = ? ";
    		PreparedStatement ps = connection.prepareStatement(q);
    		ps.setString(1, pid);
    		ArrayList<String> cids = new ArrayList<>();
    		ResultSet rs  = ps.executeQuery();
    		while (rs.next()) {
    			String cid = rs.getString("cid");
    			cids.add(cid);
    		}
    		ArrayList<Contract> contracts =  new ArrayList<>();
    		q = "SELECT * FROM contracts where cid = ? ";
    		ps = connection.prepareStatement(q);
    		for (String cid: cids) {
    			ps.setString(1, cid);
    			rs = ps.executeQuery();
    			rs.first();
    			String numberTo = rs.getString("numberTo");
    			String numberFrom = rs.getString("numberFrom");  			
    			Date dc = rs.getDate("dc");
    			String value = rs.getString("value");
    			boolean confirmed = rs.getBoolean("confirmed");
    			Contract contract = smg.referencedContracts.get(cid);
    			if (contract == null) {
    				contract = new Contract(cid,numberTo,numberFrom,dc,value,confirmed,new ArrayList<Product>());
    				smg.addRef(cid, contract);
    				contract.setProducts(this.getProductsOfContract(cid));
    			}
    			contracts.add(contract);  			
    		}
		return contracts;
	}

	private void sign(Contract c) throws Exception {
    	c.confirm();
    	String cid = c.getId();
    	String q = "UPDATE contracts SET confirmed = ? WHERE cid = ?";
    	PreparedStatement ps = connection.prepareStatement(q);
    	ps.setBoolean(1,true);
    	ps.setString(2,cid);
    	ps.executeUpdate();
	}
    

    
    public ArrayList<Product> getProductsOfContract(String cid) throws Exception {
    	String q = "SELECT pid FROM ContractsJProducts where CID = ? ";
		PreparedStatement ps = connection.prepareStatement(q);
		ps.setString(1, cid);
		ResultSet rs = ps.executeQuery();
		ArrayList<String> pids = new ArrayList<String>();
		while (rs.next()) {
			String pid = rs.getString("pid");
			pids.add(pid);
		}
		ArrayList<Product> products =  new ArrayList<>();
		q = "SELECT * FROM products where pid = ? ";
		ps = connection.prepareStatement(q);
		for (String pid: pids) {
			ps.setString(1, pid);
			rs = ps.executeQuery();
			rs.first();
			Date dc = rs.getDate("dc");
			String description = rs.getString("description");
			String size = rs.getString("Size");
			String rrp = rs.getString("RRP");
			String owner = rs.getString("owner"); 
			Product product = this.smg.referencedProducts.get(pid);
			if (product == null)  {
				product = new Product(pid,dc,description,size,rrp,owner,new ArrayList<>());
				this.smg.addRef(pid, product);
				product.setContracts(getContractsOfProduct(pid));
			}
			products.add(product); 			
		}
		return products;
    }
    
    public ArrayList<Product> getProductsInfoOfContract(String cid) throws Exception {
    	String q = "SELECT pid FROM ContractsJProducts where CID = ? ";
		PreparedStatement ps = connection.prepareStatement(q);
		ps.setString(1, cid);
		ResultSet rs = ps.executeQuery();
		ArrayList<String> pids = new ArrayList<String>();
		while (rs.next()) {
			String pid = rs.getString("pid");
			pids.add(pid);
		}
		ArrayList<Product> products =  new ArrayList<>();
		q = "SELECT * FROM products where pid = ? ";
		ps = connection.prepareStatement(q);
		for (String pid: pids) {
			ps.setString(1, pid);
			rs = ps.executeQuery();
			rs.first();
			Date dc = rs.getDate("dc");
			String description = rs.getString("description");
			String size = rs.getString("Size");
			String rrp = rs.getString("RRP");
			String owner = rs.getString("owner"); 
			products.add(new Product(pid,dc,description,size,rrp,owner,new ArrayList<>()));
			}	
		 return products;
		}
    
    public ArrayList<Contract> getContractInfoOfProducts(String pid) throws Exception {
    	String q = "SELECT cid FROM ContractsJProducts where PID = ? ";
		PreparedStatement ps = connection.prepareStatement(q);
		ps.setString(1, pid);
		ResultSet rs = ps.executeQuery();
		ArrayList<String> cids = new ArrayList<String>();
		while (rs.next()) {
			String cid = rs.getString("cid");
			cids.add(cid);
		}
		ArrayList<Contract> contracts =  new ArrayList<>();
		q = "SELECT * FROM contracts where cid = ? ";
		ps = connection.prepareStatement(q);
		for (String cid: cids) {
			ps.setString(1, cid);
			rs = ps.executeQuery();
			rs.first();
			String numberTo = rs.getString("numberTo");
			String numberFrom = rs.getString("numberFrom");
      	 	Date dc = rs.getDate("dc");
			String value = rs.getString("value");
			boolean confirmed = rs.getBoolean("confirmed");
			contracts.add(new Contract(cid,numberTo,numberFrom,dc,value,confirmed,new ArrayList<>()));
			}	
		 return contracts;
		}
    
    public Product getProductDetails(String pid) throws Exception {
    		Product product = null;
    		String q = "SELECT * FROM products WHERE pid = ?";
    		PreparedStatement ps = connection.prepareStatement(q);
    		ps.setString(1,pid);
    		ResultSet rs = ps.executeQuery();
    		if (rs.first()) {
    			String description = rs.getString("description");
    			String size = rs.getString("size");
    			Date dc = rs.getDate("dc");
    			String owner = rs.getString("owner");
    			String rrp = rs.getString("rrp");
    			ArrayList<Contract> contracts = this.getContractInfoOfProducts(pid);
    			product = new Product(pid, dc, description, size, rrp, owner, contracts);
     }
   return product;
} 
		
    
    public Account createAccount(String name, String dob, String email, String phoneNumber, String username, String password, String address) throws Exception {
        String accountNum = newAccountNumber();
        KeyPair keypair = generateKeyPair();
		Date dateOfBirth = parseDate(dob);
        checkUnameAvailability(username);
        byte[] pwHash = hash(password);
        ArrayList<Contract> contracts = new ArrayList<>();
        ArrayList<Product> products = new ArrayList<>();
        return new Account(accountNum,name,dateOfBirth,username, pwHash,new Date(),email,phoneNumber,address,"Standard",keypair,null,contracts,products);
    }

    public Account createManagerAccount(String name, String dob, String email, String phoneNumber, String username, String password, String address) throws Exception {
		String accountNum = newAccountNumber();
        KeyPair keypair = generateKeyPair();
		Date dateOfBirth = parseDate(dob);
        checkUnameAvailability(username);
        byte[] pwHash = hash(password);
        ArrayList<Contract> contracts = new ArrayList<>();
        ArrayList<Product> products = new ArrayList<>();
        return new Account(accountNum,name,dateOfBirth,username, pwHash,new Date(),email,phoneNumber,address,"Manager",keypair,new KYC(accountNum,null,null),contracts,products);
    }
    
    public Contract writeContract(String cid,String accNumFrom, String acctNumTo, String value,String[] pids) throws Exception {
    	ArrayList<String> pidsList = new ArrayList<>();
    	pidsList.addAll(Arrays.asList(pids));
    	ArrayList<Product> products = new ArrayList<>();
    	Account from = getAccount(accNumFrom);
    	if (checkNumberAvailability(acctNumTo) || (acctNumTo.equals(accNumFrom)))
			throw new AccountException("The receiving account number is not valid");
    for (Product p:from.getProducts()) {  
        if (pidsList.contains(p.getId())) {
        	   products.add(p);
        	}
      }
    for (Product p:products) {
       if (!from.getProducts().contains(p)) {
             throw new AccountException("You cannot write a contract to send products you dont own");
        }
    }
     return new Contract(cid, acctNumTo,  accNumFrom, new Date(),value, false, products);
	}
    
    private void process(Contract contract) throws Exception {
        if (!contract.getConfirmed()) throw new AccountException("The contract must be signed by both the sender and the receiver before it can be processed");
    	Account send = getAccount(contract.getAcctNumFrom());
        Account rec = getAccount(contract.getAcctNumTo());
        ArrayList<Product> products = contract.getProducts();
        for (Product p:products) {
            if (!send.removeProductOut(p)) {
                throw new AccountException("Products are no longer available");
            }
        }
        for (Product p:products) {
        	rec.transferProductIn(p);
        	updateProductOwner(p.getId(),rec.getId());
		}
        //send message to send account saying contract has been confirmed
        //change respective balances of accounts
    }
    
    public void signAndProcess(Contract c, String pw) throws Exception {
         byte[] pwHash = findPwHash(c.getAcctNumTo());
         if (Arrays.equals(hash(pw),pwHash)) {
             sign(c);
             process(c);
         }
         else throw new AccountException("Password is incorrect,Cannot sign contract");
     }
    
    public void signAndProcess(String cid, String pw) throws Exception {
    		Contract c = this.getContractDetails(cid);
        byte[] pwHash = findPwHash(c.getAcctNumTo());
        if (Arrays.equals(hash(pw),pwHash)) {
            sign(c);
            process(c);
        }
        else throw new AccountException("Password is incorrect,Cannot sign contract");
    }
        
    public Product createProduct(Account account,String description,String size,String RRP) throws Exception {
    		if (!account.getType().contains("Manager")) throw new AccountException("Only a managers account can create products");
    		String PID = newProductNumber();
    		return new Product(PID,new Date(),description, size, RRP,account.getId(),new ArrayList<>());
    }

    public void verifyAccount(Account account, Image id, Image proofOfAddress) throws Exception{
        checkKYCdocs(id, proofOfAddress);
        readKYCdocs(account,id,proofOfAddress);
    }
    


    private void checkKYCdocs(Image id, Image proofOfAddress) throws Exception{

    }

    private void readKYCdocs(Account account,Image id, Image proofOfAddress) throws Exception{

    }



    private Date parseDate(String date) throws Exception{
        SimpleDateFormat formatter = new SimpleDateFormat("dd/mm/yyyy");
        Date dob = formatter.parse(date);
        return dob;
    }
    
    

    public void checkUnameAvailability(String username) throws Exception{
        String q = "SELECT * FROM accounts WHERE uname = ? ";
        PreparedStatement ps = connection.prepareStatement(q);
        ps.setString(1,username);
        ResultSet rs = ps.executeQuery();
        if (rs.first()) throw new AccountException("This username is already in use, usernames must be unique");
     }
    

 
    public boolean checkNumberAvailability(String number) throws Exception {
		String q = "SELECT dc FROM Accounts WHERE number = ?";
		PreparedStatement ps = connection.prepareStatement(q);
		ps.setString(1, number);
		ResultSet rs = ps.executeQuery();
		return !rs.first();
		
    }
    
    private boolean checkCidAvailability(String cid) throws Exception {
    		String q = "SELECT dc FROM Contracts WHERE cid = ?";
    		PreparedStatement ps = connection.prepareStatement(q);
    		ps.setString(1, cid);
    		ResultSet rs = ps.executeQuery();
    		return !rs.first();
    		
    }

    private boolean checkPidAvailability(String pid) throws Exception {
		String q = "SELECT dc FROM Products WHERE pid = ?";
		PreparedStatement ps = connection.prepareStatement(q);
		ps.setString(1, pid);
		ResultSet rs = ps.executeQuery();
		return !rs.first();
		
    }

	public byte[] findPwHash(String number) throws Exception {
		String q = "SELECT pwHash FROM accounts WHERE number = ?";
		PreparedStatement ps = connection.prepareStatement(q);
		ps.setString(1,number);
		ResultSet rs = ps.executeQuery();
		if (rs.first()) return trimBytes(rs.getBytes("pwHash"));
		else throw new AccountException("Could not find Account " + number);
	}
    
        
 
    public KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("DSA");
        return generator.genKeyPair();
    }
    

    
    public InputStream serialize(Object o) throws Exception {
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        	ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(o);
        out.close();
        return new ByteArrayInputStream(baos.toByteArray());
     }

	public Gson createCCPJsonSerializer() {
		 GsonBuilder builder = new GsonBuilder();
		 builder.registerTypeAdapter(Account.class,new AccountSerializer());
		 builder.registerTypeAdapter(Contract.class,new ContractSerializer());
		 builder.registerTypeAdapter(Product.class,new ProductSerializer());
		 return builder.create();
	 }
    
    public Object deserialize(InputStream is) throws Exception {
        ObjectInput in = new ObjectInputStream(is);    
        return in.readObject();
    }


    public byte[] hash(String i) throws Exception{
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.reset();
        md.update(i.getBytes("UTF-8"));
        return md.digest();
    }

	public byte[] trimBytes(byte[] in) throws Exception {
		int i = in.length - 1;
		while (in[i] == 0)
		{
			i--;
		}
		return Arrays.copyOf(in, i+1);
	}

    public void restartSession() {
    		this.smg = new CCPUtils.SessionManager();
    }

    	public class SessionManager {
    		
    			public HashMap<String,Account> referencedAccounts;
    		
    			public HashMap<String,Product> referencedProducts;
    		
    			public HashMap<String,Contract> referencedContracts;

			public SessionManager(HashMap<String, Account> referencedAccounts,
					HashMap<String, Product> referencedProducts, HashMap<String, Contract> referencedContracts) {
				super();
				this.referencedAccounts = referencedAccounts;
				this.referencedProducts = referencedProducts;
				this.referencedContracts = referencedContracts;
				
			}
			
			public SessionManager() {
				this.referencedAccounts = new HashMap<>();
				this.referencedProducts = new HashMap<>();
				this.referencedContracts = new HashMap<>();
			}
			
			void addRef(String number,Account account) throws Exception {
				if (this.referencedAccounts.keySet().contains(number))  {
					System.out.println("ERROR: SM ACCOUNT DUPLICATES");
					throw new AccountException("You are attempting to reference the same account twice");
				}
				else
					this.referencedAccounts.put(number, account);
			}
						
			void addRef(String cid,Contract contract) throws Exception {
				if (this.referencedContracts.keySet().contains(cid))  {
					System.out.println("ERROR: SM CONTRACT DUPLICATES");
					throw new AccountException("You are attempting to reference the same account twice");
				}
				else
					this.referencedContracts.put(cid, contract);
			}
			
			void addRef(String pid,Product product) throws Exception {
				if (this.referencedProducts.keySet().contains(pid)) {
					System.out.println("ERROR: SM PRODUCT DUPLICATES");
					throw new AccountException("You are attempting to reference the same product twice");
				}
				else
					this.referencedProducts.put(pid, product);
			}
    	}
}
