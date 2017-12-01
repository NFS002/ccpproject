# ccpproject
Blockchain backed product tracking 

The ccp network, although inspired and powered by Blockchain concepts such as Open Transaction ledgers and total database durability, is in actuality a centralised system. Several varieties of ctyptography are also involved in this project such 
as private keys and hashing to protect user anonymity and ensure the reliability of the data transmitted on the ccp network.

The goal of the project is to provide all users of the network with a system for tracking the history, usage, ownership and location of physical goods, for example second hand cars or electronics. Physical goods in reality are represented by a cryptographic token on the ccp network. 

To explain how the system works, please look at the two diagrams in the /imgs directory. In essence, physical goods are represnted by digital tokens, each signed by the private key of the user who created the token. Products are sent via 'Contracts' that link two different Accounts, the reciever and the sender. Each contract can be linked with multiple products, and each product can be linked with multiple contracts, so the database that stores these references (currently written in SQL server) can be seen as a continous spectrum that perists data about these accounts, contracts and products over time, and so each new transaction stored in the database is dependent on the consistency of the previous ones, and any altering of the database would result in an inconsistency of the products and contracts that constitue it. 

A common usage of this system may be second hand cars, so a potential buiyer could have total confidence  about every aspect of the product they are about to purchase, providing that this vehicle was represented on the network of course. Both the seller and the consumer do have an incentive to ensure this is the case though, and as such this sytem proviudes security an reliability to all consumers of second hand goods.

The sever application itself is written in Java, using the JDK 1.8, and deployed on Apache Tomcat server 7. I have also written a web interface to the program, along with a Javascript API for interacting with the application. The web interface is written in the form of a 7 page user website, so you muist first create an account with a username and a password, along with
address and d.o.b information. At this point you may log in, and do things such as write contracts, create products, view your own contracts, as well as virewing a particular product history.

