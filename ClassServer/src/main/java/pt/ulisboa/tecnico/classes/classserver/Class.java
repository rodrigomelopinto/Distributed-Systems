package pt.ulisboa.tecnico.classes.classserver;

import java.util.logging.Logger;

import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import pt.ulisboa.tecnico.classes.DateHandler;

public class Class {
    private static final Logger LOGGER = Logger.getLogger(Class.class.getName());

    // Creating a singleton class.
    private static Class single_instance = null;

    ConcurrentHashMap<String,Student> enrolled;
    ConcurrentHashMap<String,Student> discarded;
    private boolean state;
    private boolean debug;
    private boolean openEnrollments;
    private boolean gossip;
    int capacity;
    String type;
    ConcurrentHashMap<String,Student> listUpdates;
    String closeTime;
    


    // Creating a new class object.
    private Class(boolean debug, String type){
        enrolled = new ConcurrentHashMap<>();
        discarded = new ConcurrentHashMap<>();
        this.state = true;
        this.debug = debug;
        this.type = type;
        this.gossip = true;
        listUpdates = new ConcurrentHashMap<>();
        this.closeTime = null;
    }

    /**
     * If the single_instance is null, create a new instance of Class, and then create a new timer that
     * will run the Gossip function every 30 seconds
     * 
     * @param debug true or false, if true, the program will print out debug messages
     * @param type the type of node you want to create.
     * @return The single instance of the class.
     */
    public static Class getInstance(boolean debug, String type){
        if (single_instance == null){
            single_instance = new Class(debug, type);
            Timer timer = new Timer();
            TimerTask gossip = new Gossip();
            timer.schedule(gossip,6000,30000);
        }
        return single_instance;
    }

    /**
     * Creating a static method that returns a Class object.
     * 
     * @return The class object itself.
     */
    public static Class getInstance(){
        return single_instance;
    }

    /**
     * This function returns the state of the class (active/true or inactive/false)
     * 
     * @return The state of the class.
     */
    public boolean getState(){
        return this.state;
    }

    /**
     * This function returns the value of the gossip variable (enable/true or disabled/false)
     * 
     * @return The value of the boolean variable gossip.
     */
    public boolean getGossip(){
        return this.gossip;
    }

    /**
     * This function sets the state of the server to true and returns a response code of OK.
     * 
     * @return A responsecode
     */
    public synchronized ResponseCode activate() {
        this.state = true;
        if(this.debug == true){
            LOGGER.info("Server has been activated;");
        }
        return ResponseCode.OK;
    }

    /**
     * This function deactivates the server.
     * 
     * @return A responsecode
     */
    public synchronized ResponseCode deactivate(){
        this.state = false;
        if(this.debug == true){
            LOGGER.info("Server has been deactivated;");
        }
        return ResponseCode.OK;
    }
    
    /**
     * The dump function prints out the contents of the class.
     * 
     * @return A responsecode
     */
    public ResponseCode dump(){
        return ResponseCode.OK;
    }

/**
 * The getClassState function returns the current classState.
 * 
 * @return The current classstate of the server
 */
    public ClassState.Builder getClassState(){
        ClassState.Builder classState = ClassState.newBuilder();
        if(this.debug == true){
            LOGGER.info("Server has sent the current classState;");
        }
        for(ConcurrentHashMap.Entry<String, Student> set :enrolled.entrySet()){
            classState.addEnrolled(set.getValue());
        }
        for(ConcurrentHashMap.Entry<String, Student> set :discarded.entrySet()){
            classState.addDiscarded(set.getValue());
        }
        synchronized (this){
            classState.setOpenEnrollments(openEnrollments);
            classState.setCapacity(capacity);
        }
        return classState;
    }

/**
 * The setClassState function updates the class state with the given ClassState object.
 * 
 * @param classState Update the class state
 * @param otherUpdates1 Store the updates that were made by the other server
 *
 * @return A responsecode object
 */
    public ResponseCode setClassState(ClassState classState, Map<String,Student> otherUpdates1){

        ConcurrentHashMap<String,Student> otherUpdates = new ConcurrentHashMap<>(otherUpdates1);
        for(int i = 0; i < classState.getEnrolledCount(); i++){
            if(enrolled.contains(classState.getEnrolledList().get(i))){
                continue;
            }
            enrolled.put(classState.getEnrolledList().get(i).getStudentId(),classState.getEnrolledList().get(i));
        }

        String last1 = null;
        String last2 = null;

        while(true){
            // Finding the last date in the otherUpdates map.
            for(ConcurrentHashMap.Entry<String, Student> set :otherUpdates.entrySet()){
                if(last2 == null){
                    last2 = set.getKey();
                }
                if(DateHandler.toLocalDateTime(set.getKey()).isAfter(DateHandler.toLocalDateTime(last2))){
                    last2 = set.getKey();
                }
            }
            // Removing students from the enrolled list if they are enrolled after the close time.
            if(closeTime != null){
                if(last2 == null){
                    break;
                }
                if(DateHandler.toLocalDateTime(last2).isBefore(DateHandler.toLocalDateTime(closeTime))){
                    break;
                }
                if(DateHandler.toLocalDateTime(last2).isAfter(DateHandler.toLocalDateTime(closeTime))){
                    enrolled.remove(otherUpdates.get(last2).getStudentId());
                    discarded.put(otherUpdates.get(last2).getStudentId(), otherUpdates.get(last2));
                    otherUpdates.remove(last2);
                }
                if(DateHandler.toLocalDateTime(last2).isEqual(DateHandler.toLocalDateTime(closeTime))){
                    enrolled.remove(otherUpdates.get(last2).getStudentId());
                    discarded.put(otherUpdates.get(last2).getStudentId(), otherUpdates.get(last2));
                    otherUpdates.remove(last2);
                }
            }
            else{
                break;
            }
            last2 = null;
        }

        // The below code is checking the capacity of the course and if the capacity is less than the
        // enrolled students, it will remove the student from the enrolled list and add it to the
        // discarded list if certain conditions are met.
        while(capacity < this.enrolled.size()){
            for(ConcurrentHashMap.Entry<String, Student> set :this.listUpdates.entrySet()){
                if(last1 == null){
                    last1 = set.getKey();
                }
                if(DateHandler.toLocalDateTime(set.getKey()).isAfter(DateHandler.toLocalDateTime(last1))){
                    last1 = set.getKey();
                }
            }
            for(ConcurrentHashMap.Entry<String, Student> set :otherUpdates.entrySet()){
                if(last2 == null){
                    last2 = set.getKey();
                }
                if(DateHandler.toLocalDateTime(set.getKey()).isAfter(DateHandler.toLocalDateTime(last2))){
                    last2 = set.getKey();
                }
            }
            // If the student in the other server enrolled first, it removes its student
            if(DateHandler.toLocalDateTime(last1).isAfter(DateHandler.toLocalDateTime(last2))){
                enrolled.remove(listUpdates.get(last1).getStudentId());
                discarded.put(listUpdates.get(last1).getStudentId(), listUpdates.get(last1));
                listUpdates.remove(last1);
                
            }
            // if the student in this server enrolled first, it discards theirs
            if(DateHandler.toLocalDateTime(last1).isBefore(DateHandler.toLocalDateTime(last2))){
                enrolled.remove(otherUpdates.get(last2).getStudentId());
                discarded.put(otherUpdates.get(last2).getStudentId(), otherUpdates.get(last2));
                otherUpdates.remove(last2);
            }
            // If this server is Primary and both students enrolled at the same type, this servers student has priority
            if(DateHandler.toLocalDateTime(last1).isEqual(DateHandler.toLocalDateTime(last2))){
                if(this.type.equals("S")){
                    enrolled.remove(listUpdates.get(last1).getStudentId());
                    discarded.put(listUpdates.get(last1).getStudentId(), listUpdates.get(last1));
                    listUpdates.remove(last1);
                }
                else{
                    enrolled.remove(otherUpdates.get(last2).getStudentId());
                    discarded.put(otherUpdates.get(last2).getStudentId(), otherUpdates.get(last2));
                    otherUpdates.remove(last2);
                }
            }
            last1 = null;
            last2 = null;

        }

        // Adding the discarded students to the discarded list.
        for(int i = 0; i < classState.getDiscardedCount(); i++){
            if(discarded.contains(classState.getDiscardedList().get(i))){
                continue;
            }
            discarded.put(classState.getDiscardedList().get(i).getStudentId(),classState.getDiscardedList().get(i));
        }

        // Removing all the students that are in the discarded list from the enrolled list.
        for(ConcurrentHashMap.Entry<String, Student> set :this.enrolled.entrySet()){
            if(this.discarded.containsKey(set.getKey())){
                enrolled.remove(set.getKey());
            }
        }
        
        if(this.type.equals("S")){
            this.openEnrollments = classState.getOpenEnrollments();
            this.capacity = classState.getCapacity();
        }

        if(this.debug == true){
            LOGGER.info("Server has been updated;");
        }

        return ResponseCode.OK;
    }

    /**
     * If the server is active, return OK, otherwise return INACTIVE_SERVER
     * 
     * @return The response code is being returned.
     */
    public synchronized ResponseCode listClass(){
        if(this.state == false){
            if(this.debug == true){
                LOGGER.info("INACTIVE_SERVER exception thrown;");
            }
            return ResponseCode.INACTIVE_SERVER;
        }
        else{
            return ResponseCode.OK;
        }
    }

    /**
     * It checks if the student is already enrolled, if the class is full, if the enrollments are
     * closed, if the student is discarded, if the server is active, if the student's id is valid, if
     * the student's name is valid, and if everything is ok, it enrolls the student
     * 
     * @param student The student to be enrolled in the class.
     * @return The method returns a ResponseCode object.
     */
    public ResponseCode enroll(Student student){
        synchronized (this){
            if(this.state == false){
                if(this.debug == true){
                    LOGGER.info("INACTIVE_SERVER exception thrown;");
                }
                return ResponseCode.INACTIVE_SERVER;
            }
        }
        if(student.getStudentId().length()!= 9 || student.getStudentId().startsWith("aluno") == false){
            if(this.debug == true){
                LOGGER.info("INPUT_NOT_SUPPORTED exception thrown;");
            }
            return ResponseCode.INPUT_NOT_SUPPORTED;
        }
        try{
            Integer.parseInt(student.getStudentId().substring(5, 8));
        }
        catch(NumberFormatException e){
            if(this.debug == true){
                LOGGER.info("INPUT_NOT_SUPPORTED exception thrown;");
            }
            return ResponseCode.INPUT_NOT_SUPPORTED;
        }
        if(student.getStudentName().length() <= 3 || student.getStudentName().length() >= 30){
            if(this.debug == true){
                LOGGER.info("INPUT_NOT_SUPPORTED exception thrown;");
            }
            return ResponseCode.INPUT_NOT_SUPPORTED;
        }

        synchronized (this){
            if(this.openEnrollments== false){
                if(this.debug == true){
                    LOGGER.info("ENROLLMENTS_ALREADY_CLOSED exception thrown;");
                }
                return ResponseCode.ENROLLMENTS_ALREADY_CLOSED;
            }
        }

        if(this.enrolled.contains(student)){
            if(this.debug == true){
                LOGGER.info("STUDENT_ALREADY_ENROLLED exception thrown;");
            }
            return ResponseCode.STUDENT_ALREADY_ENROLLED;
        }

        synchronized (this){
            if(this.capacity == this.enrolled.size()){
                if(this.debug == true){
                    LOGGER.info("FULL_CLASS exception thrown;");
                }
                return ResponseCode.FULL_CLASS;
            }
        }

        if(this.discarded.contains(student)){
            this.discarded.remove(student.getStudentId());
        }

        this.enrolled.put(student.getStudentId(),student);
        if(this.debug == true){
            LOGGER.info("A student enrolled in the class;");
        }
        return ResponseCode.OK;
    }

    /**
     * This function opens enrollments for a course
     * 
     * @param capacity the maximum number of students that can be enrolled in the course.
     * @return ResponseCode
     */
    public synchronized ResponseCode openEnrollments(int capacity){
        if(this.state == false){
            if(this.debug == true){
                LOGGER.info("INACTIVE_SERVER exception thrown;");
            }
            return ResponseCode.INACTIVE_SERVER;
        }
        if(type.equals("S")){
            if(this.debug == true){
                LOGGER.info("WRITING_NOT_SUPPORTED exception thrown;");
            }
            return ResponseCode.WRITING_NOT_SUPPORTED;
        }
        if(this.openEnrollments == true){
            if(this.debug == true){
                LOGGER.info("ENROLLMENTS_ALREADY_OPENED exception thrown;");
            }
            return ResponseCode.ENROLLMENTS_ALREADY_OPENED;
        }
        if(this.enrolled.size() > capacity){
            if(this.debug == true){
                LOGGER.info("FULL_CLASS exception thrown;");
            }
            return ResponseCode.FULL_CLASS;
        }
        this.openEnrollments =true;
        this.capacity = capacity;
        this.closeTime = null;
        if(this.debug == true){
            LOGGER.info("Enrollments were oppened;");
        }
        return ResponseCode.OK;
    }

    /**
     * If the server is active, and the server is not a S server, and the server is currently
     * open for enrollments, then close the server for enrollments
     * 
     * @return ResponseCode.OK
     */
    public synchronized ResponseCode closeEnrollments(){
        if(this.state == false){
            if(this.debug == true){
                LOGGER.info("INACTIVE_SERVER exception thrown;");
            }
            return ResponseCode.INACTIVE_SERVER;
        }
        if(type.equals("S")){
            if(this.debug == true){
                LOGGER.info("WRITING_NOT_SUPPORTED exception thrown;");
            }
            return ResponseCode.WRITING_NOT_SUPPORTED;
        }
        if(this.openEnrollments == false){
            if(this.debug == true){
                LOGGER.info("ENROLLMENTS_ALREADY_CLOSED exception thrown;");
            }
            return ResponseCode.ENROLLMENTS_ALREADY_CLOSED;
        }
        this.openEnrollments = false;
        if(this.debug == true){
            LOGGER.info("Enrollments were closed;");
        }
        return ResponseCode.OK;
    }

    /**
     * It cancels an enrollment of a student in the course
     * 
     * @param studentId The student's id.
     * @return The method returns a ResponseCode.
     */
    public ResponseCode cancelEnrollment(String studentId){
        synchronized (this){
            if(this.state == false){
                if(this.debug == true){
                    LOGGER.info("INACTIVE_SERVER exception thrown;");
                }
                return ResponseCode.INACTIVE_SERVER;
            }
            if(type.equals("S")){
                if(this.debug == true){
                    LOGGER.info("WRITING_NOT_SUPPORTED exception thrown;");
                }
                return ResponseCode.WRITING_NOT_SUPPORTED;
            }  
        } 
        if(studentId.length()!= 9 || studentId.startsWith("aluno") == false){
            if(this.debug == true){
                LOGGER.info("INPUT_NOT_SUPPORTED exception thrown;");
            }
            return ResponseCode.INPUT_NOT_SUPPORTED;
        }
        try{
            Integer.parseInt(studentId.substring(5, 8));
        }
        catch(NumberFormatException e){
            if(this.debug == true){
                LOGGER.info("INPUT_NOT_SUPPORTED exception thrown;");
            }
            return ResponseCode.INPUT_NOT_SUPPORTED;
        }
        for(ConcurrentHashMap.Entry<String, Student> set :enrolled.entrySet()){
            if(set.getKey().equals(studentId) == true){
                discarded.put(studentId,set.getValue());
                enrolled.remove(studentId);
                if(this.debug == true){
                    LOGGER.info("An enrollment was cancelled;");
                }
                return ResponseCode.OK;
            }
        }
        if(this.debug == true){
            LOGGER.info("NON_EXISTING_STUDENT exception thrown;");
        }
        return ResponseCode.NON_EXISTING_STUDENT;
    }

    /**
     * Activate gossiping.
     * 
     * @return ResponseCode.OK
     */
    public synchronized ResponseCode activateGossip() {
        this.gossip = true;
        if(this.debug == true){
            LOGGER.info("Gossip has been activated;");
        }
        return ResponseCode.OK;
    }

    /**
     * This function deactivates the gossip protocol
     * 
     * @return ResponseCode.OK
     */
    public synchronized ResponseCode deactivateGossip() {
        this.gossip = false;
        if(this.debug == true){
            LOGGER.info("Gossip has been deactivated;");
        }
        return ResponseCode.OK;
    }

    /**
     * The function Gossip() is a synchronized function that creates a new Gossip object and runs it, forcibly propagating the class states
     * 
     * @return ResponseCode.OK
     */
    public synchronized ResponseCode Gossip() {
        int aux = 0;
        if(this.gossip == false){
            this.gossip = true;
            aux = 1;
        }
        Gossip gossip = new Gossip();
        gossip.run();
        if(aux == 1){
            this.gossip = false;
        }
        return ResponseCode.OK;
    }

    /**
     * It takes in a
     * time and a student object and adds them to the listUpdates hashmap
     * 
     * @param time The time at which the student was updated.
     * @param student The student object that is being updated.
     */
    public synchronized void getUpdateTime(String time,Student student){
        listUpdates.put(time,student);
    }

    /**
     * > This function returns the list of updates
     * 
     * @return A reference to the listUpdates object.
     */
    public synchronized ConcurrentHashMap<String,Student> getListUpdates(){
        return this.listUpdates;
    }

    /**
     * This function clears the list of updates
     */
    public synchronized void resetUpdates(){
        this.listUpdates.clear();
    }

    /**
     * This function sets the close time of the classe enrollments.
     * 
     * @param time The time to close the door.
     */
    public synchronized void getCloseTime(String time){
        this.closeTime = time;
    }

    /**
     * This function returns the type of the server.
     * 
     * @return The type of the object.
     */
    public synchronized String getType(){
        return this.type;
    }
}
