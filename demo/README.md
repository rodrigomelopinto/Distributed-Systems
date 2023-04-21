# Prerequisites

The Project is configured with Java 17 (which is only compatible with Maven >= 3.8), but if you want to use Java 11 you
can too, just downgrade the version in the POMs.

To confirm that you have them installed and which versions they are, run in the terminal:

```s
javac -version
mvn -version
```

# Setup

To compile and install all modules run this commmand in the root directory:

```s
mvn clean install
```

Go to /A51-Turmas/NamingServer and run the following command:
```s
mvn compile exec:java
```
if you want to run with debug activated use the command:
```s
mvn compile exec:java -Dexec.args="-debug"
```

## To run the Server

Go to /A51-Turmas/ClassServer and run the following command:

```s
mvn compile exec:java -Dexec.args="localhost 8080 P"
```

if you want to run with debug activated use the command:

```s
mvn compile exec:java -Dexec.args="localhost 8080 P -debug"
```

where the first two arguments are the hostname and the port number where the server will run they can be changed if needed
to run the secondary server run the same command replacing P with S

## To run the Admin Client

Go to /A51-Turmas/Admin and run the following command:

```s
mvn compile exec:java
```
To run the admin commands you have to put the name of the command, type of the server and port in which the server is running.
Example: 
```
activate P 8080
``` 
Interaction example with Admin

```
> activate P 8080
> The action completed successfully.
> deactivate P 8080
> The action completed successfully.
> dump P 8080
> ClassState{
	capacity=0,
	openEnrollments=false,
	enrolled=[],
	discarded=[]
}
> exit
```

## To run the Professor Client

Go to /A51-Turmas/Professor and run the following command:

```s
mvn compile exec:java
```
Interaction example with Professor

```
> openEnrollments 10
> The action completed successfully.
> list
> ClassState{
	capacity=10,
	openEnrollments=true,
	enrolled=[],
	discarded=[]
}
> closeEnrollments
> The action completed successfully.
> list
> ClassState{
	capacity=10,
	openEnrollments=false,
	enrolled=[],
	discarded=[]
}
> cancelEnrollment <studentId>
> The student does not exist.
> exit
```

## To run the Student Client

Go to /A51-Turmas/Student and run the following command:

```s
mvn compile exec:java -Dexec.args="<studentId> <studentName>"
```
Interaction example with Professor

```
> list
> ClassState{
	capacity=10,
	openEnrollments=true,
	enrolled=[],
	discarded=[]
}
> enroll
> The action completed successfully.
> list
> ClassState{
	capacity=10,
	openEnrollments=true,
	enrolled=[
		Student{
			Id='aluno1000',
			Name='Cristina Ferreira'
		}],
	discarded=[]
}
> exit
```
## Run tests
 - Run the naming server
 - Run the server
 - Go to tests directory in demo
 - Run the command 'bash run.bash' 

