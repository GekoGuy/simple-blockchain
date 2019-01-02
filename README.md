# Simple Blockchain

Simple Blockchain Implementierung in Java. Enthält 3 Maven Module:
* simple-blockchain-common
* simple-blockchain-server 
* simple-blockchain-client 
Das Projekt dient mir dazu um die Blockchain besser zu verstehen und um praktische Erfahrungen mit Spring Boot zu sammeln und Java FX auszuprobieren.

## simple-blockchain-common
Enthält das Domain Model als Basis für den Client und die Server Implementierung.

## simple-blockchain-server
Enthält die Implementierung eines Server Nodes auf Basis von Spring Boot. In der aktuellen Version ist der Master Node automatisch der Node der unter http://localhost:8080 läuft. Weitere Knoten die beispielsweise unter einem anderen Port laufen, können Teil des Netztwerks werden.

In kommenden Versionen müssen die Transaktionen noch ausgebaut werden (Transaction Output und Input). Zudem muss die Verwaltung der Nodes weiter ausgebaut werden. 

## simple-blockchain-client
Einfach Java FX Application. Ist noch in Entwicklung.
