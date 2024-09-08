# DatabaseEngine

DatabaseEngine is a Java-based project that provides a simple and flexible way to interact with different types of databases, including JSON and MongoDB.

## Features

- **JSON Database**: Store and retrieve data in JSON format.
- **MongoDB Database**: Store and retrieve data using MongoDB.
- **Utility Methods**: Print all entities in the databases.

## Getting Started

### Prerequisites

- Java 21
- Gradle
- MongoDB (for MongoDB database functionality)

### Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/whynotmax/DatabaseEngine.git
    cd DatabaseEngine
    ```

2. Build the project using Gradle:
    ```sh
    gradle build
    ```

### Usage

#### JSON Database

```java
JSONDatabase<Person> jsonDatabase = new JSONDatabase<>("data.json", Person.class);
Person person1 = new Person("1", "John Doe");
jsonDatabase.save(person1);
System.out.println("Saved to JSON: " + jsonDatabase.getEntity("1"));
```

#### MongoDB Database

```java
MongoDBDatabase<Person> mongoDBDatabase = new MongoDBDatabase<>(DatabaseCredentials.createMongoDatabase(connectionUrl), database, collection, Person.class);
Person person2 = new Person("2", "Jane Doe");
mongoDBDatabase.save(person2);
System.out.println("Saved to MongoDB: " + mongoDBDatabase.getEntity("2"));
mongoDBDatabase.close();
```

#### MySQL Database

```java
MySQLDatabase<Person> mySQLDatabase = new MySQLDatabase<Person>(DatabaseCredentials.createMySQLDatabase(host, port, database, username, password), tableName, Person.class);
Person person3 = new Person("3", "Alice Smith");
mySQLDatabase.save(person3);
System.out.println("Saved to MySQL: " + mySQLDatabase.getEntity("3"));
mySQLDatabase.close();
```

Please make sure that the tables are created in the MySQL database before running the code. Alternatively, you can use the following code to create the table:

```java
mySQLDatabase.createTable(TableBuilder);
```

#### SQLite Database

```java
SQLiteDatabase<Person> sqLiteDatabase = new SQLiteDatabase<Person>(DatabaseCredentials.createSQLiteDatabase(database), tableName, Person.class);
Person person4 = new Person("4", "Bob Smith");
sqLiteDatabase.save(person4);
System.out.println("Saved to SQLite: " + sqLiteDatabase.getEntity("4"));
sqLiteDatabase.close();
```

Please make sure that the tables are created in the SQLite database before running the code. Alternatively, you can use the following code to create the table:

```java
sqLiteDatabase.createTable(TableBuilder);
```

## License

Distributed under the MIT License. See `LICENSE` for more information.

## Contact

E-Mail: max@keinesecrets.de

