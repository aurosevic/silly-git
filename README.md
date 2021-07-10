# Silly Git

Silly git is a decentralized version control system where files are distributed across the network of active Nodes. It
is maintaining the history as long as the Nodes are running.

# Architecture and How to run

## Silly File

Silly File can be a file or a directory and based on it's hash it is being stored on a Node that is responsible for it.

## Bootstrap Server

Bootstrap's role is to welcome the Nodes into the system. It only delegates the coming Node to another random Node that
is already in the network. If the Node is the first one in the network, Bootstrap is responsible for running it. In
order to start the system, You need to start a BootstrapServer.java first giving it ip address and port it will be
listening on as a program arguments.
<p><i>Properties example: localhost 2000</i></p>

## Node

Node is the origin for the Silly Files. It is responsible for storing only Silly Files that have a hash belonging to
that Node. To run the Node you need to give it two properties: file where it's details are described, and the node id.
<p><i>Properties example: chord/servent_list.properties 0</i></p>

## Supported Operations

### Add

<p><i>Usage example: </i> add file</p>

Use add to add a new Silly File to the system. After adding the Silly File, the hash for it will be generated and it
will be stored on one of the active Nodes.

### Pull

<p><i>Usage example: </i> pull file [version]</p>

Use pull to get the Silly File from the system to Your local storage. Version argument is not mandatory, and if it is
specified the required version is pulled if it exists.

### Commit

<p><i>Usage example: </i> commit file</p>

Use commit to replace the Silly File content on the origin with the one in Your storage. If there is a conflict while
doing the commit command You can choose to view the content from the origin, replace it with the file in Your storage,
or replace Your file with the one from the origin. After the commit command, the version of the file in Your storage and
origin are updated.

### Remove

<p><i>Usage example: </i> remove file</p>

Use remove command to remove the Silly File form the origin.