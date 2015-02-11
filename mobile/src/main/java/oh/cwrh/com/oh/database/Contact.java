package oh.cwrh.com.oh.database;

/**
 * Created by Nic on 1/31/2015.
 */
public class Contact {
    private long id;
    private String name;
    private String phone;

    public long getId(){return id;}
    public String getName(){return name;}
    public String getPhone(){return phone;}

    public Contact(long i,String n, String p){
        name = n;
        phone = p;
        id = i;
    }

}
