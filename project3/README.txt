Joseph Godlewski and Zhiting Zhu
joe3701 and zzt0215

CSE 461 Project 3 README

1) The System that we built was designed to provide a file sharing service. It consists of a client and a server. When a client connects to the server, it provides information about what files it is willing to share with others. The server will then tell the client what files are available for transfer. From this list, the user will tell the client which file it wants to receive. The client will ask the server for a source node that has the indicated file. The server responds with the IP of the node to ask. The client will then ask the source for the file, from which the source will give the file to the client. 

2) Our final result does what we set out to do. In our initial goal, we were planning on just using a command-line interface, however, in our final result we also added a Java Swing GUI to be used instead of the CLI.

3) There are three main parts of our design: Server, Client, and FileServer. 
   The Server is an application that is run on one computer and maintains a mapping of filenames (FileFinder.java) to nodes that contain the files and nodes to what files they are responsible for. When the server receives a connection from a client, it spawns a worker thread to handle the connection and take care of all transactions the client may want. There are 4 types of transactions that may be performed from client to server: update the list of files the client is willing to share, receive a list of all available files, receive an IP address for a node that is sharing a specific file, or terminate the connection. 
   The Client is an application that runs independently of the Server (but can also be run on the same machine as the Server). It maintains an MVC design for interracting with the user. The ClientModel class maintains the client's connection to the server and is what the main class (ClientMain.java) uses to interract with the Server, involving updating the files it wants to share, getting all the available files, requesting a file, and terminating the connection. The Client also chooses a view, either a CLI or GUI, based upon what is indicated in the configuration file. This view will interract with the user and let ClientMain know when a user wants a specific file. 
   The FileServer is a part of the Client and is spawned by the Client when it starts up. The FileServer's responsibility is to wait for other clients to connect to it asking for a specific file. The FileServer finds the location of the file on disk and reads it in chunks, sending these chunks to the requesting client over TCP. When the transfer is complete, the connection is terminated.

4) One interesting design decision we had to make was how to send files from one node to the next. We could have sent the file as one big chunk over TCP to the Client, however, that would not be taking advantage of the fact that the Client will be blocking while we are reading the file from disk, which would take a while. So we decided on spliting the file into chunks. By splitting the file into chunks the source could read a chunk, send it, and while it is reading the next chunk from disk, the client could receive the first chunk and write it to disk. The size of the chunk is decided based upon a field in the configuration file (see below for information on the configuration file).

Notes for use:
  Use the provided build.xml file to use ant to compile and jar the client and server.
Running ant will clean the project and compile two jar files, the client and the server. Each of these jars should be able to be run with: java -jar <Client or Server jar file>

Note that the jars need to be run with the configuration file in the same directory.
Also not that if an input or output directory is not specified in the config file, then there needs to be a directory called inputFiles in the same directory and a directory called receivedFiles in the same directory as the jar file.

Configuation File:
  Our project contains a configuation file called: config.txt that MUST be in the same directory as the jar to be executed. This file also has a specific format, so tampering with it except to change the serverIP is not advised. 
Its format is as follows (in this order):
  ServerIP: contains the IP address of the server to connect to. 
  ServerPort: contains the port that the server is listening for connections on.
  FileServerPort: contains the port that the fileServer is listening for connections on.
  ChunkSize: contains the size of chunks to be used when reading files and transmitting them.
  ViewType: contains the type of view to use in the client program. Can be only GUI or CMD, anything else will cause the client to not execute properly.
  InputDir: OPTIONAL FIELD. The path from this directory to the directory of files to share. Note that this directory must not have any sub-directories. The default value (if one is not specified, is ./inputFiles).
  OutputDir: OPTIONAL FIELD. The path from this directory to the directory to store received files. The default value (if one is not specified, is ./receivedFiles).

