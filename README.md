Getting Started with JSystem
=============================

1. Install Java JDK 
JSystem is a pure Java framework and it needs Oracle JVM for running. To create and compile tests the Java compiler and tools that are part of the JDK package are also required. JSystem is running with Oracle’s JDK 1.7. 
2. Download and Install JSystem
Go to the downloads page and install relevant version
If you are not using Eclipse, or if you prefer to work with the command line, you can follow the instructions that are described in the ‘Creating JSystem Projects From the Command Line’ blog instead of sections 3 to 7.
3. Setup Eclipse
In Eclipse, open window->Preferences and select Maven->Arechtypes
 
Click on Add Remote Catalog. In the catalog file add the catalog URL and appropriate description
url = http://maven.top-q.co.il/content/groups/public/archetype-catalog.xml
 
Click on the OK button.
4. Create System Objects Project
In Eclipse, click on File->New->Other. In the opened dialog select Maven->Maven Project
 
In the New Maven Project dialog, leave the fields with the default values and click on the Next button

Select the Archetype catalog that you added in the previous section and select the jsystem-so-archetype from the archetype list.
If you want the latest archetypes snapshots, check the ‘Include Snapshot archetypes’’ check box

Click on the next button.
You will asked to provide your project group and artifact ids.
 
Click on the Finish button
5. Create Tests Project
Creating a tests project is very much alike creating a system objects project.
In Eclipse, click on File->New->Other. In the opened dialog select Maven->Maven Project
 
In the New Maven Project dialog, leave the fields with the default values and click on the Next button
 
Select the Archetype catalog that you added in the previous section and select the jsystem-tests-archetype from the archetype list.
If you want the latest archetypes snapshots, check the ‘Include Snapshot archetypes’’ check box
 
 Click on the next button.
 You will be asked to provide your project group and artifact ids.
 
Click on the Finish button
6. Project Structure
The structure of the system objects project is pretty much follows the Maven project structure convention. Your system objects and other infrastructure classes should all be in the src/main/java source folder.
You may want to use your artifact <groupId> as your project root package, but this is not mandatory.
The tests project also follows the Maven convention. All your tests should be in the src/main/java source folder. Notice that although JSystem tests are based on JUnit, we don’t want them to be executed in the Maven tests phase. This is good enough reason to not include them in the src/main/tests source folder.
All other files that are part of the project but are not sources like the scenarios and SUT files are located in the src/main/resources source folder.

Project Structure
7. Add the System Object Project as a Dependency
Your test project should probably make use of the system object project, so we need to add it as a dependency.
Open the tests project, (my-tests-proj) POM.xml file. You can Eclipse POM Maven Editor or use your favorite text editor. Find the Dependencies section and add a section that describes your system object GAV coordinated (Group Id, Artifact ID and version).
 

<dependencies>
    <dependency>
        <groupId>org.jsystem</groupId>
        <artifactId>my-infra-proj</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
</dependencies>
 
8. Build Projects
Maven allows us to easily compile and build our projects. If you want to build directly from Eclipse, right click on the system objects root folder, select ‘Run As’ and click on ‘Maven Install’. If the build fails check that default JRE that is used by Eclipse is a JDK. After successful build,  repeat the process on the tests project. 
If Maven is installed on your machine and it is included in your path environment variable, you can also perform the previous operations from the command line. Open your operating system console, change directory to the project root folder and type:
> mvn clean install
9. Lib folder
JSystem does not and should not work with Maven dependency mechanism. For that reason, every time you build your tests project the Maven assembly plugin is configured to create lib folder under your tests root project and copy all the dependencies which are not already included in JSystem to this folder.
When JSystem starts to execute the projects tests it includes all the jars that are located in the lib folder to the Java class path.
If you want to exclude some of the jars from being copied to the lib folder, for example, your tests project jar, edit the create-lib.xml file that is located in src/main/assembly folder.
10. Using JSystem UI
JSystem comes with a UI that allows easy creation of test scenarios, parameters configuration, execution and management. Once your test project is created you are two steps away from running your tests.
Open the JSystem UI by running the “run.exe” or “run” located in the <jsystem_install_path>/runner folder.

Now just navigate to your test project root path: “File”->”Switch Project”.

