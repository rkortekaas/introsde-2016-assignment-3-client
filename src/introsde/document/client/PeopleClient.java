package introsde.document.client;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.transform.TransformerException;
import javax.xml.ws.Holder;

import org.w3c.dom.Document;

import introsde.document.ws.PeopleService;
import introsde.document.ws.People;
import introsde.document.ws.Person;
import introsde.document.ws.ReadPerson;
import introsde.document.ws.ReadPersonListResponse;
import introsde.document.ws.ReadPersonResponse;
import introsde.document.ws.UpdatePerson;
import introsde.document.ws.UpdatePersonResponse;
import introsde.document.ws.CreatePerson;
import introsde.document.ws.CreatePersonResponse;
import introsde.document.ws.Measure;
import introsde.document.ws.ParseException_Exception;

public class PeopleClient{
	
	private static String output;
	private static PrintStream print;
	private static String doc;
	private static PeopleService service;
	private static People people;
	private static Measure measure;
	
    private static void initialize() throws FileNotFoundException{
    	FileOutputStream file = new FileOutputStream("output.log");
        print = new PrintStream(file);
        service = new PeopleService();
        people = service.getPeopleImplPort();
    }
    
    public static void main(String[] args) throws Exception {
    	initialize();
    	readPersonList();
    	readPerson(11); 
    	updatePerson(11);
    	createPerson();
    	deletePerson(1);
    	readPersonHistory(11, "weight");
    	readMeasureTypes();
    	readPersonMeasure(11, "weight", 757);
    	savePersonMeasure(11);
    	measure = savePersonMeasure(11);
    	updatePersonMeasure(11, measure);
    	System.out.println("All queries are executed and output is saved to output.log");
    }
    
    // Method 1 - Read Person List
    public static String readPersonList(){
    	PrintStream stream = print;
    	List<Person> pList = people.readPersonList();
        stream.println("====================================");
        stream.println("1. Reading List of persons");
    	stream.println("====================================");
    	stream.println("There are "+pList.size()+" persons in the system\n");
    	output = "";
    	for (int i=0;i<pList.size()-1;i++){
    		output += outputPerson(pList.get(i))+"===========================================\n";
    	}
    	output += outputPerson(pList.get(pList.size()-1));
    	stream.println(output);
    	return output;
    }
    
    // Method 2 - Read Person with ID=11
    public static String readPerson(int id) {
    PrintStream stream = print;
    stream.println("====================================");
    stream.println("2. Read Person with id=11");
    stream.println("====================================");
    Person p = people.readPerson(id);
    
    if (p.equals(null)){
    	return "\n Person with id="+p.getIdPerson()+"is not found";
    }
    else {
    	output = outputPerson(p);
    	stream.println(output);
        return output; 
    }
    }
    
    // Method 3 - Update Person with ID=11
    public static String updatePerson(int id) {
    PrintStream stream = print;
    stream.println("====================================");
    stream.println("3. Update Person with ID=11");
    stream.println("====================================");
    Person p = people.readPerson(id);
    stream.println("Before update:");
    stream.println(p.getName()+ " "+ p.getLastname());
    stream.println("Appending BLA to lastname:");
    p.setLastname(p.getLastname()+"BLA");
    p.setName("Joke");
    p.setCurrentHealth(null); // no health profile updates
    
    Holder<Person> holder=new Holder<Person>(p);
    people.updatePerson(holder);
    Person joke = holder.value;
    stream.println("\nAfter Update: \n");
    output = outputPerson(joke);
    stream.println(output);
    return output;
    }
    
    // Method 4 - Create Person with id=1
    public static String createPerson()  {
    	PrintStream stream = print;
    	stream.println("====================================");
    	stream.println("4. Create New Person with id=1");
    	stream.println("====================================");
    	Person p = new Person();
    	p.setIdPerson(1);
		p.setName("Benno");
		p.setLastname("De Grote");
		p.setEmail("benno@tvelden.nl");
		p.setBirthdate("10/10/1989"); 
		Measure m = new Measure();
		m.setType("weight");
		m.setValue("110");
		m.setValueType("integer");
		m.setDate("01/01/2016");
		
		Person.CurrentHealth cp = new Person.CurrentHealth();
		cp.getMeasure().add(m);
		p.setCurrentHealth(cp);
		
		Holder<Person> holder=new Holder<Person>(p);
		people.createPerson(holder);
		p=holder.value;

        output = outputPerson(p);
        stream.println(output);
        return output;
    }
    
    // Method 5 - Delete person with id=1
    public static void deletePerson(int id) {
    	PrintStream stream = print;
    	stream.println("===================================="); 
    	stream.println("5. Delete Person with ID=1:");
    	stream.println("====================================");
    	 int output= people.deletePerson(id);
         if (output==0)
         	stream.println("Person with id "+id+" was deleted ");
         else
        	stream.println("Person with id "+id+" was NOT deleted");
    }
    
    // Method 6 - Read history of person with id=11
    public static String readPersonHistory(int id, String type) {
    	PrintStream stream = print;
    	stream.println("====================================");
    	stream.println("6. Read History of Person with ID=11");
    	stream.println("====================================");
    	List<Measure> m = people.readPersonHistory(id, type);
    	output = "";
    	for (int i = 0;i < m.size(); i++){
    		output += outputMeasure(m.get(i))+"\n";
    	}
    	stream.println(output);
    	return output;
    }
    
    // Method 7 - Return list of measures
    public static String readMeasureTypes() {
    	PrintStream stream = print;
    	stream.println("====================================");
    	stream.println("7. Return list of measures");
    	stream.println("====================================");
    	List<Measure> m = people.readMeasureTypes();
    	output = "";
    	for (int i = 0;i < m.size(); i++){
    		output += outputMeasure(m.get(i))+"\n";
    	}
    	stream.println(output);
    	return output;
    }
    
    // Method 8 - Return measures with mid 757 for weight of person with id=11
    public static String readPersonMeasure(int id, String measureType, int mid) {
    	PrintStream stream = print;
    	stream.println("====================================");
    	stream.println("8. Return Measure MID=757 for Weight of Person with ID=11");
    	stream.println("====================================");
    	Measure m = people.readPersonMeasure(id, measureType, mid);
    	output = outputMeasure(m);
    	stream.println(output);
    	return output;
    }
    
    // Method 9 - Save a new measure object for person with id=11
    public static Measure savePersonMeasure(int id) throws ParseException_Exception {
    	PrintStream stream = print;
    	stream.println("====================================");
    	stream.println("9. Save a new measure object for Person with ID=11");
    	stream.println("====================================");
    	Measure mm = new Measure();
    	mm.setDate("10/10/1989");
    	mm.setType("weight");
    	mm.setValue("88");
    	mm.setValueType("integer");
    	
		Holder<Measure> holder=new Holder<Measure>(mm);
		people.savePersonMeasure(id, holder);
		mm=holder.value;
		output = outputMeasure(mm);
		stream.println(output);
		return mm;
    }
    
    // Method 10 - update measure with mid= for person with id=11
    public static String updatePersonMeasure(int id, Measure m) {
    	PrintStream stream = print;
    	stream.println("====================================");
    	stream.println("10. Update measure identified by " +m.getIdMeasure() +" for person with id=11");
    	stream.println("====================================");
    	
    	m.setType("height");
    	m.setValue("1.90");
    	
		Holder<Measure> holder=new Holder<Measure>(m);
		people.updatePersonMeasure(id, holder);
		m=holder.value;
		
		output = outputMeasure(m);
		stream.println(output);
		return output;
    	
    }
    
    public static String outputPerson(Person p) {
    String persondata = "- PersonID: "+p.getIdPerson()+"\n"; 
	persondata += "- Name: "+p.getName()+"\n";
	persondata += "- Lastname: "+p.getLastname()+"\n";
	persondata += "- Email: "+p.getEmail()+"\n";
	persondata += "- Birthdate: "+p.getBirthdate()+"\n";
	if (p.getCurrentHealth().getMeasure().size()!=0){
		persondata += "\n*** HealthProfile *** \n";
		List<Measure> m = p.getCurrentHealth().getMeasure();
		for (int i = 0;i < m.size(); i++){
			persondata += outputMeasure(m.get(i))+"\n";
		}
	}
	return persondata;
    }
    
    public static String outputMeasure(Measure m) {
	String measuredata = "- MeasureID: "+m.getIdMeasure()+"\n";
	measuredata += "- MeasureType: "+m.getType()+"\n";
	measuredata += "- Value: "+m.getValue()+"\n";
	measuredata += "- ValueType :"+m.getValueType()+"\n";
	measuredata += "- Data of registration: "+m.getDate()+"\n";
	return measuredata;
    }
    
}