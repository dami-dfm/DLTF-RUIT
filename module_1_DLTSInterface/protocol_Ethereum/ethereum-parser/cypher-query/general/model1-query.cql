// Conta il numero di nodi senza relazioni
MATCH (n) WHERE size(labels(n)) = 0 RETURN count(n)

// Ritrorna la lista di account che ha effettuato piu transazioni (ordine decrescente)
MATCH (n:Account)-[:sent]->(t:Transaction)
WITH n, COUNT(t) AS numTransactions
ORDER BY numTransactions DESC
RETURN n, numTransactions;


// Ritorna i vicini dei nodi verso i quali i primi 1000 nodi hanno fatto transazioni
PROFILE MATCH (n:Account)-[r:sent]-(t:Transaction)-[:transfered]->(b:Account)
where n <> b
WITH n, COUNT(r) AS numTransactions
ORDER BY numTransactions DESC
LIMIT 100 // Limita ai nodi con il maggior numero di transazioni
MATCH (n)-[*6]->(neighbor) // Espandi fino ai vicini dei vicini
WHERE neighbor <> n and (neighbor: Account)// Assicurati che i vicini non siano i nodi stessi
RETURN count (distinct neighbor);


match (a:Account {address: '0xfffbca3a38c3c5fcb3adbb8e63c04c3e629aafce'})
MATCH (a)-[*6]->(neighbor: Account {account_type: 'EOA'})
where a<>neighbor
return neighbor
order by neighbor.address DESC