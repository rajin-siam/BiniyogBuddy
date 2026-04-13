# Understanding MessageSource, Classpath, and JAR Loading

**Date:** April 7, 2026
**Context:** BiniyogBuddy was deployed to Render (production). User registration was failing with `NoSuchMessageException` even though the `.properties` file existed. The same code worked perfectly in IntelliJ (development).

---

## The Problem

After deploying to Render, calling the `/api/v1/auth/register` endpoint threw:

```
NoSuchMessageException: No message found under code 'auth.error.email.duplicate' for locale 'en_US'
```

The file `messages/auth.properties` existed and had the key. So why couldn't Spring find it?

---

## How IDE Runs a Java Application

When we click the **Run** or **Debug** button in IntelliJ, three things happen:

### 1. Compile

IntelliJ (via Gradle) takes all `.java` files and **compiles** them into `.class` files. Java doesn't understand `.java` files. It only understands `.class` files.

```
BEFORE (source code - we understand this):
src/main/java/
    AuthService.java
    AuthController.java

AFTER (compiled - Java understands this):
build/classes/java/main/
    AuthService.class
    AuthController.class
```

### 2. Copy Resources

IntelliJ **copies** resource files (`.yaml`, `.properties`) to the build folder. No compilation needed for these - they are just copied as-is.

```
BEFORE:
src/main/resources/
    application.yaml
    messages/auth.properties

AFTER (just copied):
build/resources/main/
    application.yaml
    messages/auth.properties
```

**Important:** These are still **normal files on the hard drive**. Nothing is zipped or compressed.

### 3. Build the Classpath and Run

IntelliJ tells Java where to find everything using a **classpath**. The classpath is just a list of folders where Java should look for files.

```
java -classpath 
    build/classes/java/main          <-- "Your .class files are HERE"
    build/resources/main             <-- "Your .properties files are HERE"
    common/build/classes/java/main   <-- "Common module is HERE"
    libs/auth/build/classes/...      <-- "Auth module is HERE"
    ...spring-boot.jar               <-- "Spring framework is HERE"
  com.biniyogbuddy.api.BiniyogBuddyApplication   <-- "Start from this class"
```

**Result:** Everything is normal files on disk. Easy to find. Easy to read.

---

## How Production (Render) Runs the Application

### 1. Compile (same as IDE)

`.java` files are compiled into `.class` files. Same process.

### 2. Copy Resources (same as IDE)

Resource files are copied to the build folder. Same process.

### 3. Package into JAR (DIFFERENT from IDE)

This is where it changes. Gradle takes ALL compiled classes, ALL resources, ALL libraries and **packages them into a single file** called a **JAR**.

A JAR file is basically a **ZIP file**. Everything is compressed into one file.

```
BEFORE packaging (many files and folders):
build/classes/java/main/AuthService.class
build/classes/java/main/AuthController.class
build/resources/main/application.yaml
build/resources/main/messages/auth.properties
+ spring-boot.jar
+ postgresql.jar
+ ...hundreds of library files

AFTER packaging (one single file):
app.jar
```

Inside the JAR (ZIP), the structure looks like:

```
app.jar (ZIP file)
    BOOT-INF/
        classes/
            com/biniyogbuddy/
                AuthService.class
                AuthController.class
            application.yaml
            messages/
                auth.properties         <-- Inside the ZIP now!
                general.properties      <-- Inside the ZIP now!
        lib/
            spring-boot-4.0.3.jar
            postgresql-42.7.10.jar
            ...
    META-INF/
        MANIFEST.MF
```

### 4. Run the JAR

Render runs:

```
java -jar app.jar
```

Now the classpath is NOT a list of folders on disk. The classpath is **inside the JAR file**. The `.properties` files are no longer normal files on the hard drive - they are **compressed inside a ZIP**.

---

## Why Reading Files Inside a JAR is Different

### Reading a normal file on disk (Dev)

Java uses the `File` class to read normal files. This is simple and straightforward:

```java
File file = new File("messages/auth.properties");
FileInputStream stream = new FileInputStream(file);
// Just opens the file and reads it. Like opening a text file with Notepad.
```

### Reading a file inside a JAR (Prod)

The file is inside a ZIP. We can't just "open" it like a normal file. Java needs to:

1. Find the JAR file
2. Open the JAR (it's a ZIP)
3. Search through the ZIP contents
4. Find the right file inside
5. Extract it
6. Read it

The `File` class **cannot do this**. It only knows how to read normal files on disk.

To read from inside a JAR, Java needs the **ClassLoader**. ClassLoader is a special built-in Java class that knows how to find files **everywhere** - on disk AND inside JAR files.

```java
ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
InputStream stream = classLoader.getResourceAsStream("messages/auth.properties");
// This works for files on disk AND files inside JARs
```

---

## What Actually Went Wrong

### The YAML Configuration

```yaml
spring:
  messages:
    basename: messages/auth, messages/general
```

When Spring Boot reads this, it uses **auto-configuration** to create a `MessageSource` bean automatically. We never wrote any code for this - Spring does it behind the scenes.

### What Spring's Auto-Config Did Internally

Spring's auto-config code (which we never see) did something like:

```java
// Spring wrote this code internally, not us
Resource resource = findResource("messages/auth.properties");

if (resource not found) {
    // Give up silently, create a dummy MessageSource
    return new DelegatingMessageSource();  // <-- Does nothing!
}

// If found, create a real MessageSource
return new ResourceBundleMessageSource();
```

### In Dev (IDE)

```
Spring's auto-config: "Let me find messages/auth.properties"
Looks on disk: /home/rajin/.../build/resources/main/messages/auth.properties
Found it! (normal file on disk)
Creates a real ResourceBundleMessageSource
messageSource.getMessage("auth.register.success") --> "User registered successfully"
RESULT: Works!
```

### In Prod (JAR on Render)

```
Spring's auto-config: "Let me find messages/auth.properties"
Looks on disk: ... not found (it's inside the JAR, not on disk!)
NOT FOUND!
Gives up silently, creates DelegatingMessageSource (dummy)
messageSource.getMessage("auth.register.success") --> "I don't know this key!"
RESULT: NoSuchMessageException!
```

**The key point:** We never wrote bad code. Spring's auto-configuration wasn't smart enough to find files inside JAR. It used a simple file lookup that only works on disk. When the file was inside a JAR, the lookup failed, and Spring silently created a dummy MessageSource that knows nothing.

---

## The Fix

Instead of letting Spring guess how to create MessageSource (auto-config), we created an **explicit bean** that uses `ResourceBundleMessageSource`:

```java
@Configuration
public class MessageSourceConfig {

    @Bean(name = "messageSource")
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages/auth");
        messageSource.addBasenames("messages/general");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(3600);
        return messageSource;
    }
}
```

### Why This Works

`ResourceBundleMessageSource` uses `ClassLoader` internally. ClassLoader knows how to read files from both disk and JAR files.

- In Dev (disk): ClassLoader finds the file on disk and reads it
- In Prod (JAR): ClassLoader opens the JAR, finds the file inside, extracts and reads it

### Why We Also Removed YAML Message Config

Since the explicit bean handles everything, we removed the YAML message configuration from both `application-dev.yaml` and `application-prod.yaml` to avoid confusion and redundancy.

---

## Summary Table

| Concept | Dev (IDE) | Prod (JAR on Render) |
|---------|-----------|---------------------|
| How app runs | `java -classpath folder1:folder2 MainClass` | `java -jar app.jar` |
| Where are files | Normal files on disk | Compressed inside JAR (ZIP) |
| File class | Can read them | Cannot read them |
| ClassLoader | Can read them | Can read them |
| YAML auto-config | Works (file on disk) | Fails (file in JAR) |
| Explicit bean (ResourceBundleMessageSource) | Works | Works |

## Key Classes to Remember

| Class | What it does | Works on disk? | Works in JAR? |
|-------|-------------|---------------|---------------|
| `File` / `FileInputStream` | Reads normal files | Yes | No |
| `ClassLoader` | Finds files everywhere on classpath | Yes | Yes |
| `ResourceBundleMessageSource` | Uses ClassLoader to load .properties files | Yes | Yes |
| `DelegatingMessageSource` | Dummy MessageSource that does nothing | N/A | N/A |

## Key Takeaway

When deploying Spring Boot to production, don't rely on YAML auto-configuration for MessageSource. Use an explicit `ResourceBundleMessageSource` bean that uses `ClassLoader` internally, so it works in both development (files on disk) and production (files inside JAR).
