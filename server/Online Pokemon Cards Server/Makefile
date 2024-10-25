JFLAGS = -g
JC = javac
JVM = java
CLASSPATH = .:lib/sqlite-jdbc-3.46.1.3.jar
SRCDIR = src/main/java/org/example

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) -cp $(CLASSPATH) $*.java
CLASSES = \
	$(SRCDIR)/ServerMain.java \
	$(SRCDIR)/ClientMain.java \
	$(SRCDIR)/DatabaseManager.java

default: classes

classes: $(CLASSES:.java=.class)

run_server:
	$(JVM) -cp $(CLASSPATH):$(SRCDIR) org.example.ServerMain

run_client:
	$(JVM) -cp $(CLASSPATH):$(SRCDIR) org.example.ClientMain localhost

clean:
	$(RM) $(SRCDIR)/*.class

