# simple-blockchain-parent

Simple Blockchain Implementierung in Java. Enthält 3 Maven Module simple-blockchain-common, simple-blockchain-server und simple-blockchain-client.

# simple-blockchain-common
Enthält das Domain Model als Basis für den Client und die Server Implementierung.

# simple-blockchain-server
Enthält die Implementierung eines Server Nodes auf Basis von Spring Boot. In der aktuellen Version ist der Master Node automatisch der Node der unter localhost:8080 läuft. Weitere Knoten die beispielsweise unter einem anderen Port laufen, können Teil des Netztwerks werden.
In kommenden Versionen müssen die Transaktionen noch ausgebaut werden (Transaction Output und Input).

# simple-blockchain-client
Einfach Java FX Application. Ist noch in Entwicklung.
