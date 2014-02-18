README
========
Automation Documentation


Info about the project
  The project build on jsystem, java, adb and uiautomator.
  Each cell contains server, that connect to the host by fifos(busybox nc), on the other side Jenkins --> Jsystem --> Java --> Java client(Jason Rpc) that connects to the server.
  Basicly the project is build on (Jsystem/Jenkins/Java user)client and (Uiautomator)server  


To Install in new enviroment
  1.	ADK - android developer kit, this kit contains inside the : eclipse, jdk, uiautomator.
  2.	Jsystem 6.0.03 and above
  3.	Ant


Jenkins
  The jenkins automation can be found in : http://build.vm.cellrox.com:8080/job/Automation_Nightly.
  This automation nightly job is downstream to the development jobs and it will trigger this job(for the time of the write of this document its Mako_Kit_Kat job). 
  After the run of the automation the jenkins will include the html document.  

  
Jsystem
  The Jsystem resurces containes few important files :
  1.	sut file - the wanted data to init the test with it.
  2.	scenario files - keeping the properties and the wanted tests to run.
  3.	jsystem properties - include the wanted sut file that will run, important to notice that in junit run it willl also will init the file from the sut file.
  
  
Java
  The main class that running the tests is CellroxDeviceOperations this class holds the deviceses manager and it holds the devices.
  For runing the tests locally usually there will be a nee in configue function and than to connect to the servers.
  The java Projects:
  1.	adb-controller-master - the adb controller.
  2.	cellrox-infra-project	- all the infra stracture of the test.
  3.	cellrox-tests-project	- all the tests itself, this class will be running the automation itself.
  4.	uiautomator-client-master - the client.
  5.	uiautomatorServer-${android version} - the servers.
  
  
Client Server
  1.	the client and the server are working with the JsonRpc.
  2.	There is a need for building fifo , nc and to activate the servers before of runing
  3.	The uiatomator server are activated from /data/containers/${persona}/data/local/tmp/bundle.jar and uiautomator-stub.jar,
	this jars are manifacture after ant build on the wanted uiautomatorServer-${number}
	
