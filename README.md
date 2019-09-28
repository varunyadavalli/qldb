# AmazonQLDB FABCAR POC


## Requirements

### Basic Configuration

You need to set up your AWS security credentials before the sample code is able
to connect to AWS. You can do this by creating a file named "config" at `~/.aws/` 
(`C:\Users\USER_NAME\.aws\` for Windows users) and saving the following lines in the file:

    [default]
    aws_access_key_id = <your access key id>
    aws_secret_access_key = <your secret key>
    region = us-east-1 <or other region>

Alternatively, us the [AWS CLI](https://aws.amazon.com/cli/) and run `aws configure` to 
step through a setup wizard for the config file.

See the [Security Credentials](http://aws.amazon.com/security-credentials) page
for more information on getting your keys.

### Java 8 and Gradle

The examples are written in Java 8 using the Gradle build tool. Java 8 must be installed to build the examples, however 
the Gradle wrapper is bundled in the project and does not need to be installed. Please see the link below for more 
detail to install Java 8 and information on Gradle:

* [Java 8 Installation](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html)
* [Gradle]()
* [Gradle Wrapper](https://docs.gradle.org/3.3/userguide/gradle_wrapper.html)

# Running the Application

1). Create fabcar Ledger

```
./gradlew run -Dtutorial=CreateLedger
```

2). List the ledgers available

```
./gradlew run -Dtutorial=ListLedgers
```

3). Create Cars table in ledger

```
./gradlew run -Dtutorial=CreateTable
```

4). Create Index "CarId" on Cars Table

```
./gradlew run -Dtutorial=CreateIndex
```

5). Insert Sample data to the ledger

```
./gradlew run -Dtutorial=InsertDocument
```

5). List cars available in ledger

```
./gradlew run -Dtutorial=ListAllCars
```

6). Transfer car from 'Alice' from 'Teja' for Car ID = "IN001"

```
./gradlew run -Dtutorial=AssignNewOwner
```

7). Check the car history for Car ID = "IN001"

```
./gradlew run -Dtutorial=CheckCarHistory
```
