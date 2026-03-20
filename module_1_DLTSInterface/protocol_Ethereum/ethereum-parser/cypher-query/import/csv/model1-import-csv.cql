// USE FOR DELETE
:auto match (n) call {with n detach delete n} in transactions of 10000 rows;

// CONSTRAINT
CREATE CONSTRAINT IF NOT EXISTS FOR (a: Account) REQUIRE a.address IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (t:Transaction) REQUIRE t.hash IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (block: Block) REQUIRE block.hash IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (block: Block) REQUIRE block.number IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (log: Log) REQUIRE log.hash IS UNIQUE;

// QUERY 1, EOA TRANSACTIONS
:auto LOAD CSV WITH HEADERS FROM "file:///output/model1-data/eoa-transactions/eoa-transactions-1.csv" AS transaction
call {
    WITH transaction
    // Create the transaction
    MERGE (t:Transaction {hash: transaction.hash})
    ON CREATE
        SET t = properties(transaction)

    // Create the node
    MERGE (fromNode: Account {address: transaction.fromAddress})
    ON CREATE 
        SET fromNode.account_type = 'EOA'

    MERGE (toNode: Account {address: transaction.toAddress})
    ON CREATE 
        SET toNode.account_type = 'EOA'
        
    MERGE (block: Block {hash: transaction.blockHash})

    // Create the relation
    CREATE (t)-[:contained_in]->(block)
    CREATE (t)<-[:sent]-(fromNode)
    CREATE (t)-[:transfered]->(toNode)

} IN TRANSACTIONS OF 10000 ROWS;


// QUERY 2, SMART CONTRACT CREATION TRANSACTIONS
:auto LOAD CSV WITH HEADERS FROM "file:///output/model1-data/contract-creation/contract-creation-1.csv" AS transaction
call {
    WITH transaction
    CREATE (t: Transaction {hash: transaction.hash})
    SET t = properties(transaction)

    MERGE (fromNode: Account {address: transaction.fromAddress})
    ON CREATE
        SET fromNode.account_type = 'EOA'

    MERGE (toContract: Account {address: transaction.contractAddress})
    ON CREATE
        SET toContract.account_type = 'SC'

    MERGE (block: Block {hash: transaction.blockHash})

    MERGE (fromNode)-[:sent]->(t)
    MERGE (toContract)<-[:created]-(t)
    MERGE (t)-[:contained_in]->(block)

} IN TRANSACTIONS OF 10000 ROWS;


// QUERY 3, CONTRACT INVOCATION TRANSACTIONS
:auto LOAD CSV WITH HEADERS FROM "file:///output/model1-data/contract-transactions/contract-transactions-1.csv" AS transaction
call {
    WITH transaction
    MERGE (t: Transaction {hash: transaction.hash})
    ON CREATE
        SET t = properties(transaction)

    MERGE (fromNode: Account {address: transaction.fromAddress})
    ON CREATE
        SET fromNode.account_type = 'EOA'

    MERGE (toContract: Account {address: transaction.toAddress})
    ON CREATE
        SET toContract.account_type = 'SC'

    MERGE (block: Block {hash: transaction.blockHash})

    MERGE (fromNode)-[:sent]->(t)
    MERGE (toContract)<-[:invoked]-(t)
    MERGE (t)-[:contained_in]->(block)

} IN TRANSACTIONS OF 10000 ROWS;

//QUERY 4, CONTRACT LOGS
:auto LOAD CSV WITH HEADERS FROM "file:///output/model1-data/contract-logs/contract-logs-1.csv" AS log
call {
    WITH log
    CREATE (event: Event)
    SET event = properties(log)
    SET event.topics = SPLIT(log.topics,';')

    WITH event, log.transactionHash as transactionHash
    MATCH (transaction: Transaction {hash: transactionHash})
    CREATE (transaction)-[:emitted]->(event)

} IN TRANSACTIONS OF 10000 ROWS;

//QUERY 5, UNKNOWN TRANSACTIONS
:auto LOAD CSV WITH HEADERS FROM "file:///output/model1-data/unknown-transactions/unknown-transactions-1.csv" AS transaction
call {
    WITH transaction

    MERGE (t: Transaction {hash: transaction.hash})
    ON CREATE
        SET t = properties(transaction)

    MERGE (fromNode: Account {address: transaction.fromAddress})
    ON CREATE
        SET fromNode.account_type = "EOA"
    
    MERGE (block: Block {hash: transaction.blockHash})

    MERGE (toNode: Account {address: transaction.toAddress})
    ON CREATE
        SET toNode.account_type = "UNK"

    WITH t, fromNode, toNode, block,
    CASE
        WHEN toNode.account_type = "UNK" THEN "to"
        WHEN toNode.account_type = "EOA" THEN "transfered"
        WHEN toNode.account_type = "SC"  THEN "invoked"
    END AS relationType
    call apoc.create.relationship(t, relationType, {}, toNode) YIELD rel
    //call apoc.merge.relationship(t,relationType,{},{},toNode,{}) YIELD rel // Avoid to create multiple times same relationship

    // The from node relation is always sent
    MERGE (fromNode)-[:sent]->(t)
    MERGE (t)-[:contained_in]->(block)

} IN TRANSACTIONS OF 10000 ROWS;

// QUERY 6, BLOCKS
:auto LOAD CSV WITH HEADERS FROM "file:///output/model1-data/blocks/blocks-1.csv" AS block
call {
    WITH block
    MERGE (b: Block {hash: block.hash})
    set b = properties(block)

    MERGE (parent: Block {hash: block.parentHash})

    MERGE (b)-[:son_of]->(parent)

} IN TRANSACTIONS OF 10000 ROWS;