//model1-eoa-transaction-1
call apoc.load.jsonArray("file:///model1-data/eoa-transactions/eoa-transactions-1.json")
YIELD value as transaction
CREATE (t:Transaction)
set t = properties(transaction)

WITH t, transaction.blockHash AS blockHash, transaction.toAddress AS toAddress, transaction.fromAddress AS fromAddress
MERGE (fromNode: Account {address: fromAddress})
SET fromNode.address = fromAddress

MERGE (toNode: Account {address: toAddress})
SET toNode.address = toAddress

MERGE (block: Block {hash: blockHash})

CREATE (t)-[:contained_in]->(block)
CREATE (t)<-[:sent]-(fromNode)
CREATE (t)-[:transfered]->(toNode)

return t;


//model1-contract-creation-2
CALL apoc.load.jsonArray("file:///model1-data/contract-creation/contract-creation-1.json")
YIELD value AS creation
CREATE (t: Transaction)
SET t = properties(creation)

WITH t, creation.fromAddress as creator, creation.contractAddress as contractAddress
MERGE (fromNode: Account {address: creator})
MERGE (toContract: SmartContract {address: contractAddress})

CREATE (fromNode)-[:sent]->(t)
CREATE (toContract)<-[:created]-(t)

return t;


//model1-contract-transaction-3
CALL apoc.load.jsonArray("file:///model1-data/contract-transactions/contract-transactions-1.json")
YIELD value AS transaction
CREATE (t: Transaction)
SET t = properties(transaction)

WITH t, transaction.fromAddress as fromAddress, transaction.toAddress as toAddress, transaction.blockHash as blockHash
MERGE (fromNode: Account {address: fromAddress})
MERGE (toContract: SmartContract {address: toAddress})
MERGE (block: Block {hash: blockHash})

CREATE (fromNode)-[:sent]->(t)
CREATE (toContract)<-[:invoked]-(t)

return t;


//model1-contract-log-4
CALL apoc.load.jsonArray("file:///model1-data/contract-logs/contract-logs-1.json")
YIELD value AS log
CREATE (event: Event)
SET event = properties(log)

WITH event, log.transactionHash as transactionHash
MATCH (transaction: Transaction {hash: transactionHash})
CREATE (transaction)-[:emitted]->(event)

return event;